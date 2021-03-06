package com.abc.editorserver.manager;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.abc.editorserver.config.EditorConfig;
import com.abc.editorserver.config.EditorConst;
import com.abc.editorserver.db.JedisManager;
import com.abc.editorserver.module.JSONModule.ExcelConfig;
import com.abc.editorserver.module.JSONModule.ExcelTrigger;
import com.abc.editorserver.module.user.User;
import com.abc.editorserver.msg.GameActionJson;
import com.abc.editorserver.net.RequestData;
import com.abc.editorserver.utils.EditorUtils;
import com.abc.editorserver.utils.Task;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.abc.editorserver.support.LogEditor;
import com.alibaba.fastjson.parser.Feature;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import com.abc.editorserver.utils.Timer;
import org.tmatesoft.svn.core.SVNCommitInfo;

/**
 * 数据管理器
 */
public class DataManager {

    /** 记录表格是否有未持久化至Excel的改动 */
    private Map<String, Timer> tableTimers = new HashMap<>();

    /** 记录表格是否有未提交至SVN的改动 */
    private Map<String, Boolean> tableModifiedSinceLastCommit = new HashMap<>();

    /** 记录表格的锁定情况 */
    private Map<String, User> tableLocks = new ConcurrentHashMap<>();

    /** Excel文件路径 */
    private static StringBuilder excelPathPrefix = new StringBuilder(EditorConfig.svn_export).append('/');

    /** CSV文件路径 */
    private static StringBuilder csvPathPrefix = new StringBuilder(EditorConfig.svn_export).append("/data/csv/");

    /** 数据持久化时间间隔 */
    private final long dataPersistInterval = Timer.HALF_MINUTE;

    /** ExcelConfig配置文件名 */
    private static final String ConfigPath = "ExcelConfig.json";

    /** 是否需要提交CSV文件 */
    private boolean shouldCommitCSV = true;

    /** Redis中表格名 -> 表格中所有列名的映射 */
    private static Map<String, List<String>> columnSeqMap = new HashMap<>();

    /** Trigger表格数据缓存 */
    private Map<String, Map<String, JSONObject>> triggerData = new HashMap<>();

    /** 管理类实例 */
    private static DataManager mgr = new DataManager();

    private DataManager() {}
    public static DataManager getInstance(){ return mgr; }

    /**
     * 初始化方法
     */
    public void init() {
        LogEditor.serv.info("======初始化Excel配置数据======");
        loadExcelConfig();

        LogEditor.serv.info("======开始加载Trigger表======");
        initTriggers();

        LogEditor.serv.info("======初始化计时器中======");
        initTimers(dataPersistInterval);

        LogEditor.serv.info("======从Excel写入Redis中======");
        excelToRedis();
    }

    /*
     * 在数据被更新/发生改动之后重新写入本地缓存及Redis数据
     */
    public void reloadDataAfterUpdate() {
        LogEditor.serv.info("重新写入本地缓存及Redis数据");

        // 重新加载Triggers表格
        initTriggers();

        // 从Excel写入Redis中
        excelToOverrideRedis();
    }

    /**
     * 读取ExcelConfig.json配置文件，解析成excelConfig对象
     * @return
     */
    private void loadExcelConfig() {
        String configPath = EditorConfig.CONFIG_PATH + ConfigPath;
        try (InputStream input = new FileInputStream(configPath)) {
            byte[] buff = input.readAllBytes();
            String jsonStr = new String(buff);
            JSONObject jo = JSON.parseObject(jsonStr);
            ExcelManager.setInstance(jo.toJavaObject(ExcelManager.class));
        } catch(Exception e) {
            LogEditor.config.error("读取Excel配置失败：", e);
        }
    }

    /**
     * 为每个表格初始化计时器
     */
    private void initTimers(long interval) {
        ExcelConfig[] excelConfigs = ExcelManager.getInstance().getConfigs();

        for (ExcelConfig config : excelConfigs) {
            tableTimers.put(config.getRedis_table(), new Timer(interval));
        }
    }

    /**
     * 停止指定表格对应的计时器
     * @param tableName
     */
    public void stopTimerOfTable(String tableName) {
        for (Map.Entry<String, Timer> entry : tableTimers.entrySet()) {
            if (entry.getKey().equals(tableName)) {
                entry.getValue().stopTimer();
                break;
            }
        }
    }

    /**
     * 读取ExcelConfig中配置的Trigger表，缓存表格内容
     */
    private void initTriggers() {
        // 获取Trigger表的配置文件
        ExcelTrigger[] triggers = ExcelManager.getInstance().getTriggers();

        for (ExcelTrigger trigger : triggers) {
            String excelName = trigger.getExcel();
            String sheetName = trigger.getSheet();
            String triggerName = excelName.substring(0, excelName.lastIndexOf('.')) + "_" + sheetName;
            excelName = EditorConfig.svn_export + "/" + excelName;

            // TODO DEBUG USE REMOVE
            LogEditor.serv.info("Processing excel: " + excelName + ", sheet: " + sheetName);

            try (FileInputStream fis = new FileInputStream(excelName)) {
                Workbook workbook = WorkbookFactory.create(fis);
                Sheet sheet = workbook.getSheet(sheetName);

                // 获取表格所有的列名
                List<String> colNames = new ArrayList<>();
                Row row = sheet.getRow(0);

                for (int colIndex = 0; colIndex < row.getLastCellNum(); colIndex++) {
                    Cell cell = row.getCell(colIndex);

                    if (cell == null) {
                        colNames.add(String.valueOf(colIndex));
                    } else {
                        colNames.add(cell.toString());
                    }
                }

                // 缓存表格数据
                for (int rowIndex = 1; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
                    List<String> rowValues = new ArrayList<>();
                    row = sheet.getRow(rowIndex);

                    String indexColumn = row.getCell(0).toString();

                    for (int colIndex = 0; colIndex < row.getLastCellNum(); colIndex++) {
                        Cell cell = row.getCell(colIndex);

                        if (cell == null) {
                            rowValues.add("");
                        } else {
                            rowValues.add(convertFromBR(cell.toString()));
                        }
                    }

                    while (rowValues.size() < colNames.size()) {
                        rowValues.add("");
                    }

                    triggerData.computeIfAbsent(triggerName, s -> new LinkedHashMap<>())
                            .put(indexColumn, JSONObject.parseObject(stringToJSON(colNames, rowValues)));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 根据配置信息将对应的excel表写入Redis
     */
    public void excelToRedis(boolean shouldOverride) {
        for (ExcelConfig conf: ExcelManager.getInstance().getConfigs()) {
            String fileName = EditorConfig.svn_export + "/" + conf.getExcel();
            String sheetName = conf.getSheet();

            // TODO DEBUG USE REMOVE
            LogEditor.serv.info("Processing excel: " + fileName + ", sheet: " + sheetName);

            try (FileInputStream fis = new FileInputStream(fileName)) {
                Workbook workbook = WorkbookFactory.create(fis);
                Sheet sheet = workbook.getSheet(sheetName);

                // 获取表格每一列的英文名称（列名）
                List<String> colNames = new ArrayList<>();
                Row nameRow = sheet.getRow(2);                  // 第2行定义了所有的英文列名
                for (int i = 0; i < nameRow.getLastCellNum(); i++) {
                    if (nameRow.getCell(i)==null) {
                        colNames.add(Integer.toString(i));
                    } else {
                        colNames.add(nameRow.getCell(i).toString());
                    }
                }

                columnSeqMap.put(conf.getRedis_table(),colNames);

                // 清除数据
                if (shouldOverride) {
                    JedisManager.del(conf.getRedis_table());
                }

                // 更新最大SN表
                JedisManager.hdel(EditorConst.TABLE_SN_MAX, conf.getRedis_table());

                // 逐行遍历表格
                int maxSnAtCurrTable = 0, currSn = 0;
                for (int i = 0; i <= sheet.getLastRowNum(); i++) {
                    List<String> valueList = new ArrayList<>();
                    Row row = sheet.getRow(i);
                    for (int j = 0; j < row.getLastCellNum(); j++) {
                        Cell cell = row.getCell(j);
                        if (null == cell) {
                            valueList.add("");
                        } else {
                            valueList.add(convertFromBR(cell.toString()));
                        }
                    }
                    while (valueList.size() < colNames.size()) {
                        valueList.add("");
                    }

                    String key = valueList.get(0);
                    if (i < ExcelManager.getInstance().getDefaultNames().length) {
                        key = ExcelManager.getInstance().getDefaultNames()[i];
                    }

                    // 获取当前表格中最大的SN
                    try {
                        currSn = Integer.parseInt(key);
                    } catch (Exception e) {
                        currSn = 0;
                    } finally {
                        maxSnAtCurrTable = Math.max(maxSnAtCurrTable, currSn);
                    }

                    // 记录表格数据
                    JedisManager.hset(conf.getRedis_table(), key, stringToJSON(colNames, valueList));
                }

                // 记录表格的最大SN
                JedisManager.hset(EditorConst.TABLE_SN_MAX, conf.getRedis_table(), String.valueOf(maxSnAtCurrTable));

            } catch (Exception e) {
                LogEditor.config.error("Excel写入Redis失败：", e);
            }
        }
    }

    /**
     * 根据配置信息，清空Redis中的数据，并将对应的excel表写入Redis
     */
    public void excelToOverrideRedis() {
        excelToRedis(true);
    }

    /**
     * 根据配置信息，保留Redis中的数据，并将对应的excel表写入Redis（追加数据）
     */
    public void excelToRedis() {
        excelToRedis(false);
    }

    private String convertFromBR(String value) {
        if (value.contains("\n") || value.contains("\r\n")) {
            return value.replaceAll("\\n", "@n@");
        }
        if (value.contains("\"")) {
            return value.replaceAll("\"", "@q@");
        }
        if(value.endsWith(".0")){
            value = value.substring(0,value.length()-2);
        }

        Pattern ptr = Pattern.compile("^\\d+[.]\\d+[E]\\d+");   ///匹配科学计数格式表达的ID/SN
        Matcher matcher = ptr.matcher(value);

        if (matcher.find()) {
            /// 统一数字格式
            return String.valueOf(String.format("%.0f", Double.parseDouble(value)));
        }

        return value;
    }

    private String convertToBR(String value) {
        if (value.contains("@n@")) {
            return value.replaceAll("@n@", "\n");
        }
        if (value.contains("@q@")) {
            return value.replaceAll("@q@", "\"");
        }
        if(value.endsWith(".0")){
            value = value.substring(0,value.length()-2);

            /// 统一数字格式
            return String.valueOf(String.format("%.0f", Double.parseDouble(value)));
        }

        return value;
    }

    /**
     * 根据excel一行的值返回json串
     * @param keys
     * @param values
     * @return
     * @throws Exception
     */
    private String stringToJSON(List<String> keys, List<String> values) throws Exception {
        // 检查提供values的内容是否包括不可解析的字符
        List<String> parsedValues = new ArrayList<>();
        for (String value : values) {
            parsedValues.add(formatStringForJSON(value));
        }

        int length = keys.size();
        StringBuilder sb = new StringBuilder();

        sb.append("{\"");
        for (int i = 0; i < length - 1; i++) {
            sb.append(keys.get(i));
            sb.append("\":\"");
            sb.append(parsedValues.get(i));
            sb.append("\",\"");
        }
        sb.append(keys.get(length - 1));
        sb.append("\":\"");
        sb.append(parsedValues.get(length - 1));
        sb.append("\"}");

        return sb.toString();
    }

    /**
     * 检查传入字符，替换不可被FastJSON解析的内容，与resolveStringForJSON方法配合使用
     * @param value
     * @return
     */
    private String formatStringForJSON(String value) {
        if (value == null || value.isBlank()) {
            return value;
        }
        // 避免JSON解析时遇到"\"字符时抛出异常的问题
        value = value.replaceAll("\\\\", "#@#");
        return value;
    }

    /**
     * 解析通过formatStringForJSON方法处理过的字符，与resolveStringForJSON方法配合使用
     * @param value
     * @return
     */
    private String resolveStringForJSON(String value) {
        if (value == null || value.isBlank()) {
            return value;
        }
        // 与formatStringForJSON中的操作互反
        value = value.replaceAll("#@#", "\\");
        return value;
    }

    /**
     * 读取redis中的数据，保存到Excel表中
     */
    private void redisToExcel(){
        FileOutputStream fos = null;
        try {
            for (ExcelConfig conf : ExcelManager.getInstance().getConfigs()) {
                //根据原表名和页签sheet名生成新表名,目前格式为：原表名_页签英文名.xlsx
                String fileName = EditorConfig.svn_export + "/" + conf.getExcel();
                if (fileName.endsWith(".xlsx") || fileName.endsWith(".xlsm")) {
                    fileName = fileName.substring(0, fileName.length() - 5);
                } else {
                    throw new Exception("要写入的目标文件不是xlsx或xlsm格式：" + conf.getExcel());
                }
                String sheetName = conf.getSheet();
                String[] sArr = sheetName.split("\\|");
                fileName += "_" + sArr[1] + ".xlsx";
                File file = new File(fileName);
                if(!file.exists()) {
                    file.createNewFile();
                }

                Map<String, String> mapAll = JedisManager.hgetAll(conf.getRedis_table());
                XSSFWorkbook workbook = new XSSFWorkbook();
                XSSFSheet sheet  = workbook.createSheet(sheetName);
                String[] defaultName = ExcelManager.getInstance().getDefaultNames();
                int rowCount = 0;
                for(String name : defaultName) {
                    String value = mapAll.get(name);
                    makeRow(value, sheet, rowCount++, conf.getRedis_table());
                    mapAll.remove(name);
                }
                for(String key : mapAll.keySet()) {
                    String value = mapAll.get(key);
                    makeRow(value, sheet, rowCount++, conf.getRedis_table());
                }

                fos = new FileOutputStream(fileName);
                workbook.write(fos);
            }
        }
        catch(Exception e){
            LogEditor.config.error("Redis写入Excel失败：", e);
        }
        finally {
            try{
                if(fos != null){
                    fos.flush();
                    fos.close();
                }
            }
            catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    /**
     * 写入Excel的一行数据
     * @param value
     * @param sheet
     * @param rowNum
     * @param tableName
     */
    private void makeRow(String value, XSSFSheet sheet, int rowNum, String tableName){
        try{
            XSSFRow row = sheet.createRow(rowNum);
            JSONObject jo = JSON.parseObject(value);
            int cellNum = 0;
            XSSFCell cell;
            while (jo.size() > 0) {
                String columnKey = columnSeqMap.get(tableName).get(cellNum);
                cell = row.createCell(cellNum);
                cell.setCellValue(convertToBR(jo.getString(columnKey) == null ? "" : jo.getString(columnKey)));
                cellNum++;
                jo.remove(columnKey);
            }
        }
        catch(Exception e){
            LogEditor.serv.error("表名" + tableName + " 行号：" + rowNum, e);
        }
    }

    /**
     * 获取数据表数据
     * @param tableName
     * @return
     */
    public JSONArray getTableData(String tableName) {
        ExcelConfig config = ExcelManager.getInstance().getConfig(tableName);
        JSONArray ret = new JSONArray();
        if (config == null) {
            return ret;
        }
        Map<String, String> datas = JedisManager.hgetAll(config.getRedis_table());

        //在返回数据前去掉属性行
        String[] defaultName = ExcelManager.getInstance().getDefaultNames();
        for(String name : defaultName) {
            datas.remove(name);
        }
        for (String data : datas.values()) {
            ret.add(data);
        }
        return ret;
    }

    /**
     * 获取数据表列名
     * @param tableName
     * @return
     */
    public JSONArray getTableColumnName(String tableName) {
        String value = JedisManager.hget(tableName,"cnName");
        JSONObject jo = JSONObject.parseObject(value);
        JSONArray ret = new JSONArray();
        List<String> columnName = columnSeqMap.get(tableName);

        for(String name : columnName){
            ret.add("{\"prop\":\"" + name +"\",\"label\":\""+ convertToBR(jo.getString(name))+ "\"}");
        }

        return ret;
    }


    /**
     * 获取指定Trigger表的全表数据
     * @param excelName Excel文件名
     * @param sheetName 表格名
     * @return
     */
    public JSONArray getAllTriggerTableData(String excelName, String sheetName) {
        int excelNameEndsAt = excelName.lastIndexOf('.');
        String triggerKey = excelName.substring(0, excelNameEndsAt == -1 ? excelName.length() : excelNameEndsAt)
                + "_" + sheetName;
        Map<String, JSONObject> data = triggerData.get(triggerKey);
        Iterator<Map.Entry<String, JSONObject>> iter = data.entrySet().iterator();

        JSONArray result = new JSONArray();

        while(iter.hasNext()) {
            result.add(iter.next().getValue());
        }

        return result;
    }

    /**
     * 获取Trigger表指定行的整行数据
     * @param excelName Excel文件名
     * @param sheetName 表格名
     * @param row 行ID
     * @return
     */
    public JSONObject getTriggerTableDataAtRow(String excelName, String sheetName, String row) {
        int excelNameEndsAt = excelName.lastIndexOf('.');
        String triggerKey = excelName.substring(0, excelNameEndsAt == -1 ? excelName.length() : excelNameEndsAt)
                + "_" + sheetName;

        return triggerData.get(triggerKey).get(row);
    }

    /**
     * 获取Trigger表在指定行&指定列位置的数据
     * @param excelName Excel文件名
     * @param sheetName 表格名
     * @param row 行ID
     * @param columnName 列ID
     * @return
     */
    public String getTriggerTableDataAtRowColumn(String excelName, String sheetName, String row, String columnName) {
        int excelNameEndsAt = excelName.lastIndexOf('.');
        String triggerKey = excelName.substring(0, excelNameEndsAt == -1 ? excelName.length() : excelNameEndsAt)
                + "_" + sheetName;

        JSONObject jsonObject = triggerData.get(triggerKey).get(row);

        if (jsonObject != null) {
            return jsonObject.get(columnName).toString();
        } else {
            //TODO: LOG
            return null;
        }
    }

    /**
     * 更新数据表数据
     * @param tableName
     * @param sn
     * @param field
     * @param value
     * @param shouldIncrementVerNum: 是否需要更新版本号
     * @return
     */
    public long updateTableData(String tableName, String sn, String field, String value, boolean shouldIncrementVerNum,
                                User user) {
        // 判断表格数据是否被锁定
        User currLockOwner = getCurrentTableDataLockOwner(tableName, sn);
        if (currLockOwner != null && !currLockOwner.getUid().equals(user.getUid())) {
            return -1;
        }

        String json = JedisManager.hget(tableName, sn);
        JSONObject jo;

        if (json == null) {
            jo = new JSONObject();
        } else {
            jo = JSON.parseObject(json);
        }
        jo.put(field, value);

        JedisManager.hset(tableName, sn, jo.toJSONString());

        return onTableDataChanged(tableName, sn, shouldIncrementVerNum, false);
    }

    /**
     * 更新数据表数据（不更新版本号）
     * @param table
     * @param sn
     * @param field
     * @param value
     * @return
     */
    public long updateTableData(String table, String sn, String field, String value, User user) {
        return updateTableData(table, sn, field, value, false, user);
    }

    /**
     * 获取数据表某条数据
     * @param tableName
     * @return
     */
    public String getTableDataBySn(String tableName, String sn) {
        ExcelConfig config = ExcelManager.getInstance().getConfig(tableName);
        if (config == null) {
            return null;
        }

        // 读取表数据
        JSONObject dataInfo = new JSONObject();
        String tableData = JedisManager.hget(config.getRedis_table(), sn);
        dataInfo.put("tableData", tableData);

        // 获取锁信息
        User lockOwner = getCurrentTableDataLockOwner(tableName, sn);

        if (lockOwner != null) {
            dataInfo.put("lockInfo", lockOwner.getName());
        }

        return dataInfo.toString();
    }

    /**
     * 获取数据表所有行指定列的数据
     * @param tableName
     * @param colNames
     * @return
     */
    public JSONArray getTableDataAtColumns(String tableName, List<String> colNames) {
        JSONArray allTableData = getTableData(tableName);
        JSONArray partialTableData = new JSONArray();

        for (Object data : allTableData) {
            JSONObject rowData = JSONObject.parseObject((String)data);
            JSONObject partialRowData = new JSONObject();
            for (String colName : colNames) {
                partialRowData.put(colName, rowData.get(colName));
            }
            partialTableData.add(partialRowData);
        }

        return partialTableData;
    }

    /**
     * 检查表中是否有对应SN的记录
     * @param tableName
     * @param sn
     * @return
     */
    public boolean SnExistsInTable(String tableName, String sn) {
        return JedisManager.hexists(tableName, sn);
    }

    /**
     * 获取指定表格下一个可用的SN
     * @param tableName
     * @return
     */
    public String getAndIncrNextAvailableSn(String tableName) {
        ExcelConfig config = ExcelManager.getInstance().getConfig(tableName);

        if (config == null) {
            LogEditor.serv.error("提供的表名错误，无法获取到下一个可用的SN");
            return null;
        }

        // 自增下一个可用的SN
        return String.valueOf(JedisManager.hincrBy(EditorConst.TABLE_SN_MAX, config.getRedis_table(), 1L));
    }

    /**
     * 在数据表中新增一条数据
     * @param tableName
     * @param params
     * @param user
     * @return
     */
    public int addTableData(String tableName, JSONObject params, User user) {
        // 判断表格数据是否被锁定
        User currLockOwner = getCurrentTableDataLockOwner(tableName, "SN_PLACEHOLDER");
        if (currLockOwner != null && !currLockOwner.getUid().equals(user.getUid())) {
            return -2;
        }

        ExcelConfig config = ExcelManager.getInstance().getConfig(tableName);
        if (config == null) {
            return -1;
        }

        // 新增表数据
        String newTableData = params.toString();
        long opResult = JedisManager.hsetNx(config.getRedis_table(), params.getString("sn"), newTableData);

        if (opResult == 0L) {
            LogEditor.serv.error("添加表数据失败：Redis中表格" + tableName + "已有重复的SN " + params.getString("sn"));
            return 0;
        }

        // 数据变动回调方法
        String currSn = params.getString("sn");
        onTableDataChanged(config.getRedis_table(), currSn, false, true);

        return 1;
    }

    /**
     * 浅复制一条表数据
     * @param tableName
     * @param sn
     * @param user
     * @return
     */
    private JSONObject shallowCopyTableData(String tableName, String sn, User user) {
        JSONObject replyMsg = new JSONObject();
        String originalData = JedisManager.hget(tableName, sn);
        if (originalData == null) {
            String hint = "浅复制表数据" + tableName + ":" + sn + "时无法找到指定的源数据";
            LogEditor.serv.error(hint);
            replyMsg.put("result", EditorConst.RESULT_FAILED);
            replyMsg.put("hint", hint);
        }

        // 执行浅复制前锁定源数据，避免用户再复制期间修改数据
        User defaultUser = new User();
        defaultUser.setUid("DEFAULT_USER");
        tryLockingTableData(tableName, sn, defaultUser);

        String copiedDataSn = getAndIncrNextAvailableSn(tableName);
        JSONObject copiedData = JSONObject.parseObject(originalData);
        copiedData.put("sn", copiedDataSn);
        addTableData(tableName, copiedData, user);

        // 解锁源数据
        tryUnLockingTableData(tableName, sn, defaultUser);

        replyMsg.put("result", EditorConst.RESULT_OK);
        replyMsg.put("data", copiedData.toString());
        return replyMsg;
    }

    /**
     * 深复制一条表数据
     * @param tableName
     * @param sn
     * @param user
     * @return
     */
    public JSONObject deepCopyTableData(String tableName, String sn, User user) {
        JSONObject replyMsg = new JSONObject();
        ExcelConfig config = ExcelManager.getInstance().getConfig(tableName);
        if (config == null) {
            String hint = "找不到对应的配置文件，请检查后重试";
            LogEditor.serv.error(hint);
            replyMsg.put("result", EditorConst.RESULT_FAILED);
            replyMsg.put("hint", hint);
            return replyMsg;
        }

        // 浅复制当前表格指定的数据
        JSONObject shallowCopyResult = shallowCopyTableData(tableName, sn, user);
        if (shallowCopyResult.get("result") == EditorConst.RESULT_FAILED) {
            return shallowCopyResult;
        }

        JSONObject shallowCopiedData = JSONObject.parseObject(shallowCopyResult.getString("data"));
        LogEditor.serv.info("浅复制了表格" + tableName + "中SN为" + sn + "的数据，新数据SN为" +
                shallowCopiedData.getString("sn") + "，开始进行深复制");

        // 查询表格关联信息，执行深复制
        String refs = config.getRefs();
        if (refs != null && refs.length() > 0) {
            JSONArray refsAsArray = JSONObject.parseArray(refs);
            for (int i = 0; i < refsAsArray.size(); i++) {
                JSONObject refObj = refsAsArray.getJSONObject(i);

                // 遍历所有的引用列
                for (String refColName : refObj.keySet()) {
                    String refTableName = refObj.getString(refColName);
                    String[] refTableSns = shallowCopiedData.getString(refColName).split(",");
                    List<String> updatedCopiedRefSns = new ArrayList<>(refTableSns.length);

                    // 引用列中可能有多个数据（用逗号分隔），逐个进行复制
                    for (String refTableSn : refTableSns) {
                        // 处理引用字段为空的情况
                        if (refTableSn.isBlank()) {
                            continue;
                        }

                        // 递归进行深度复制
                        JSONObject deepCopyResult = deepCopyTableData(refTableName, refTableSn, user);
                        LogEditor.serv.info("完成表格" + refTableName + "中SN为" + refTableSn + "数据的深复制");

                        // 记录深复制后的新SN值
                        JSONObject deepCopiedData = JSONObject.parseObject(deepCopyResult.getString("data"));
                        updatedCopiedRefSns.add(deepCopiedData.getString("sn"));

                        // 一旦失败则停止复制
                        if (deepCopyResult.get("result") == EditorConst.RESULT_FAILED) {
                            return deepCopyResult;
                        }
                    }

                    // 更新上级复制数据引用列的值
                    String shallowCopiedDataSn = shallowCopiedData.getString("sn");
                    shallowCopiedData.put(refColName, String.join(",", updatedCopiedRefSns));
                    JedisManager.hset(tableName, shallowCopiedDataSn, shallowCopiedData.toString());
                    shallowCopyResult.put("data", shallowCopiedData.toString());

                    // 调用数据变化回调方法（无需再更新SN池，在浅复制时已经进行）
                    onTableDataChanged(tableName, shallowCopiedDataSn, false, false);
                }
            }
        }

        replyMsg.put("result", EditorConst.RESULT_OK);
        replyMsg.put("data", shallowCopyResult.getString("data"));
        return replyMsg;
    }

    /**
     * 在数据表中删去一条数据
     * @param tableName
     * @param sn
     * @return
     */
    public int deleteTableData(String tableName, String sn, User user) {
        // 判断表格数据是否被锁定
        User currLockOwner = getCurrentTableDataLockOwner(tableName, sn);
        if (currLockOwner != null && !currLockOwner.getUid().equals(user.getUid())) {
            return -1;
        }

        ExcelConfig config = ExcelManager.getInstance().getConfig(tableName);
        if (config == null) {
            return -1;
        }

        LogEditor.serv.info("删除了" + tableName + "表中SN为" + sn + "的记录");

        // 从数据库中删除数据
        JedisManager.hdel(config.getRedis_table(), sn);

        // 数据变动回调方法
        onTableDataChanged(config.getRedis_table(), sn, false, false);
        return 1;
    }

    /**
     * 尝试给指定表格中的某个数据上锁
     * @param tableName
     * @param sn
     * @param currUser
     * @return
     */
    public JSONObject tryLockingTableData(String tableName, String sn, User currUser) {
        String dataTag = tableName + ":" + sn;
        JSONObject reply = new JSONObject();

        User lockOwner = tableLocks.get(dataTag);
        if (lockOwner != null) {
            reply.put("result", EditorConst.RESULT_FAILED);
            reply.put("msg", "加锁失败，当前表格数据已被" + lockOwner.getName() + "锁定！");
        } else {
            tableLocks.put(dataTag, currUser);
            reply.put("result", EditorConst.RESULT_OK);
            reply.put("msg", "加锁成功");
        }

        return reply;
    }

    /**
     * 尝试为指定表格中的某个数据解锁
     * @param tableName
     * @param sn
     * @param currUser
     * @return
     */
    public JSONObject tryUnLockingTableData(String tableName, String sn, User currUser) {
        String dataTag = tableName + ":" + sn;
        JSONObject reply = new JSONObject();

        User lockOwner = tableLocks.get(dataTag);
        if (lockOwner == null) {
            reply.put("result", EditorConst.RESULT_OK);
            reply.put("msg", "表格数据未上锁");
        } else if (!lockOwner.getUid().equals(currUser.getUid())) {
            reply.put("result", EditorConst.RESULT_FAILED);
            reply.put("msg", "解锁失败，当前表格数据被" + lockOwner.getName() + "锁定！");
        } else {
            tableLocks.remove(dataTag);
            reply.put("result", EditorConst.RESULT_OK);
            reply.put("msg", "解锁成功");
        }

        return reply;
    }

    /**
     * 尝试对指定表格上锁
     * @param tableName
     * @param currUser
     * @return
     */
    public JSONObject tryLockingTable(String tableName, User currUser) {
        String dataTag = tableName;
        JSONObject reply = new JSONObject();

        User lockOwner = tableLocks.get(dataTag);
        if (lockOwner != null) {
            reply.put("result", EditorConst.RESULT_FAILED);
            reply.put("msg", "加锁失败，当前表格已被" + lockOwner.getName() + "锁定！");
        } else {
            tableLocks.put(dataTag, currUser);
            reply.put("result", EditorConst.RESULT_OK);
            reply.put("msg", "加锁成功");
        }

        return reply;
    }

    /**
     * 尝试对指定表格解锁
     * @param tableName
     * @param currUser
     * @return
     */
    public JSONObject tryUnlockingTable(String tableName, User currUser) {
        String dataTag = tableName;
        JSONObject reply = new JSONObject();

        User lockOwner = tableLocks.get(dataTag);
        if (lockOwner == null) {
            reply.put("result", EditorConst.RESULT_OK);
            reply.put("msg", "表格未上锁");
        } else if (!lockOwner.getUid().equals(currUser.getUid())) {
            reply.put("result", EditorConst.RESULT_FAILED);
            reply.put("msg", "解锁失败，当前表格被" + lockOwner.getName() + "锁定！");
        } else {
            tableLocks.remove(dataTag);
            reply.put("result", EditorConst.RESULT_OK);
            reply.put("msg", "解锁成功");
        }

        return reply;
    }

    /**
     * 获取指定数据当前的锁主人
     * @param tableName
     * @param sn
     * @return
     */
    public User getCurrentTableDataLockOwner(String tableName, String sn) {
        User tableLockOwner = tableLocks.get(tableName);

        if (tableLockOwner != null) {
            // 如果整个表被锁，返回锁表的用户
            return tableLockOwner;
        } else {
            // 否则，返回锁当前表数据的用户
            return tableLocks.get(tableName + ":" + sn);
        }
    }

    /**
     * 查询当前是否有表格的定时器被触发，将本地改动写入Excel中
     * @param isAsync：是否异步执行持久化
     */
    public void dataPersistHandler(boolean isAsync) {
        Iterator<Map.Entry<String, Timer>> iter = tableTimers.entrySet().iterator();
        Map.Entry<String, Timer> entry;
        List<String> candidates = new ArrayList<>();

        while (iter.hasNext()) {
            entry = iter.next();

            if (entry.getValue().isDue()) {
                // 停止写入Excel定时器
                entry.getValue().stopTimer();
                candidates.add(entry.getKey());
            }
        }

        if (candidates.size() > 0) {
            LogEditor.serv.info("定时器被触发，将缓存刷新至Excel中...");

            if (isAsync) {
                JSONObject params = new JSONObject();
                params.put("candidates", candidates);

                // 提交至任务队列中
                GlobalManager.addTask(new Task(var -> {
                    // 将本地缓存的改动写入至Excel表格中
                    List<String> tables = (List<String>) var.get("candidates");
                    persistData(false, tables);
                }, params));
            } else {
                persistData(false, candidates);
            }
        }
    }

    /**
     * 强制将更新写入Excel并提交至SVN
     * @param params：异步调用时的参数
     */
    public void promptDataPersistAndCommit(JSONObject params) {
        Set<String> unPersistedTables = new HashSet<>();
        Set<String> unCommittedTables = new HashSet<>();

        // 判断是否还有改动未写入Excel
        for (Map.Entry<String, Timer> entry : tableTimers.entrySet()) {
            if (entry.getValue().isRunning()) {
                // 停止写入Excel定时器
                entry.getValue().stopTimer();
                unPersistedTables.add(entry.getKey());
            }
        }

        // 判断有哪些表格存在未提交的改动
        for (Map.Entry<String, Boolean> entry : tableModifiedSinceLastCommit.entrySet()) {
            if (entry.getValue()) {
                // 重置未改动标识
                entry.setValue(false);
                unCommittedTables.add(entry.getKey());
            }
        }

        if (unPersistedTables.size() > 0 || unCommittedTables.size() > 0) {
            LogEditor.serv.info("定时器被触发，将缓存刷新至Excel中...");

            params = (params == null ? new JSONObject() : params);
            params.put("unPersistedTables", unPersistedTables);
            params.put("unCommittedTables", unCommittedTables);

            if (!GlobalManager.hasPendingCommitTask) {
                // 设置标志flag
                GlobalManager.hasPendingCommitTask = true;
            } else {
                GameActionJson msgCaller = (GameActionJson)params.get("MsgCaller");
                RequestData requestCtx = (RequestData)params.get("RequestContext");

                JSONObject replyMsg = new JSONObject();
                replyMsg.put("result", EditorConst.RESULT_FAILED);
                replyMsg.put("hint", "当前正在执行另一个COMMIT操作，请稍后重试");

                msgCaller.sendMsg(requestCtx.ctx, replyMsg);
                return;
            }


            // 提交至任务队列中
            GlobalManager.addTask(new Task(var -> {
                // 读取参数信息
                Set<String> tablesToPersist = (Set<String>) var.get("unPersistedTables");
                Set<String> tablesToCommit = (Set<String>) var.get("unCommittedTables");
                GameActionJson msgCaller = (GameActionJson)var.get("MsgCaller");
                RequestData requestCtx = (RequestData)var.get("RequestContext");
                User currUser = (User)var.get("User");

                String commitMsg = "CSJ-0000 【编辑器】提交 | 提交者【" + currUser.getName() + "】";
                JSONArray commitResults = new JSONArray();

                // 将本地缓存的改动写入至Excel表格中
                persistData(false, new ArrayList<>(tablesToPersist));

                LogEditor.serv.info("【SVN COMMIT】开始COMMIT");

                // 获取所有需要提交的Excel
                Map<String, List<String>> excelsToTablesToCommit = new HashMap<>();
                for (String tableToCommit : tablesToCommit) {
                    String excelName = ExcelManager.getInstance().getConfig(tableToCommit).getExcel();
                    excelsToTablesToCommit.computeIfAbsent(excelName, r -> new ArrayList<>()).add(tableToCommit);
                }

                for (String excelToCommit : excelsToTablesToCommit.keySet()) {
                    // 记录所有需要提交的文件
                    List<String> commitFiles = new ArrayList<>();

                    excelPathPrefix.append(excelToCommit);
                    commitFiles.add(excelPathPrefix.toString());
                    excelPathPrefix.delete(excelPathPrefix.lastIndexOf("/") + 1, excelPathPrefix.length());

                    // 获取需要提交的CSV文件信息
                    if (shouldCommitCSV) {
                        for (String tableToCommit : excelsToTablesToCommit.get(excelToCommit)) {
                            ExcelConfig config = ExcelManager.getInstance().getConfig(tableToCommit);
                            csvPathPrefix.append(config.getSheet().substring(config.getSheet().lastIndexOf("|") + 1))
                                    .append(".csv");
                            commitFiles.add(csvPathPrefix.toString());
                            csvPathPrefix.delete(csvPathPrefix.lastIndexOf("/") + 1, csvPathPrefix.length());
                        }
                    }

                    // 提交文件
                    SVNCommitInfo commitInfo = SVNManager.commit(true,  commitMsg,
                            commitFiles.toArray(new String[0]));

                    // 记录提交信息
                    JSONObject currCommitResult = new JSONObject();
                    if (commitInfo.getNewRevision() == -1L) {
                        currCommitResult.put("result", EditorConst.RESULT_FAILED);

                        String errorMessage = "";
                        try {
                            errorMessage = commitInfo.getErrorMessage().getMessage();
                        } catch (NullPointerException e) {
                            LogEditor.serv.error("在获取文件的COMMIT结果时抛出异常");
                        } finally {
                            currCommitResult.put("hint", errorMessage.isBlank() ? "FAILED TO OBTAIN MSG" : errorMessage);
                        }
                    } else {
                        currCommitResult.put("result", EditorConst.RESULT_OK);
                        currCommitResult.put("hint", "提交成功");
                    }

                    commitResults.add(currCommitResult);
                }

                LogEditor.serv.info("【SVN COMMIT】COMMIT完成");

                // 还原标志flag
                GlobalManager.hasPendingCommitTask = false;

                // 发送消息
                JSONObject replyMsg = new JSONObject();
                try {
                    replyMsg.put("result", EditorConst.RESULT_OK);
                    replyMsg.put("hint", commitResults.toString());
                    msgCaller.sendMsg(requestCtx.ctx, replyMsg);
                } catch (IllegalStateException e) {
                    LogEditor.serv.error("服务器返回COMMIT结果失败：【" + e.getMessage() + "】");
                }
            }, params));
        } else {
            GameActionJson msgCaller = (GameActionJson)params.get("MsgCaller");
            RequestData requestCtx = (RequestData)params.get("RequestContext");

            LogEditor.serv.info("没有需要COMMIT的表格");
            JSONObject replyMsg = new JSONObject();
            try {
                replyMsg.put("result", EditorConst.RESULT_OK);
                replyMsg.put("hint", "没有需要COMMIT的表格");
                msgCaller.sendMsg(requestCtx.ctx, replyMsg);
            } catch (IllegalStateException e) {
                LogEditor.serv.error("服务器返回COMMIT结果失败：【" + e.getMessage() + "】");
            }
        }
    }

    /**
     * 将本地缓存的改动写入至Excel表格中
     * @param shouldPersistAll：是否需要写入配置中指定的所有表格
     * @param tablesToPersist: 指定需要写入的表格
     */
    public void persistData(boolean shouldPersistAll, List<String> tablesToPersist) {
        LogEditor.serv.info("=========开始将Redis数据写入Excel中========");

        // 预处理需要写入的表格
        ExcelConfig[] excelConfigs = ExcelManager.getInstance().getConfigs();
        Set<String> targetTables = new HashSet<>(tablesToPersist);

        FileInputStream fis = null;
        FileOutputStream fos = null;

        try {
            for (ExcelConfig config : excelConfigs) {
                String redisTableName = config.getRedis_table();

                // 跳过不需要持久化的表格
                if (!shouldPersistAll && !targetTables.contains(redisTableName)) {
                    continue;
                }

                String excelName = config.getExcel();
                String sheetName = config.getSheet();
                String excelPath = EditorConfig.svn_export + "/" + excelName;
                List<String> columnNames = columnSeqMap.get(redisTableName);

                LogEditor.serv.info("开始写入表格：" + redisTableName);

                // 打开表格
                fis = new FileInputStream(excelPath);
                XSSFWorkbook workbook = new XSSFWorkbook(fis);
                XSSFSheet sheet = workbook.getSheet(sheetName);

                // 初始化格式
                DataFormat dataFormat = workbook.createDataFormat();
                Map<String, String> redisHash = JedisManager.hgetAll(redisTableName);
                String[] defaultNames = ExcelManager.getInstance().getDefaultNames();

                LogEditor.serv.info("总写入行数：" + redisHash.size());

                // 记录表格的所有列ID，每张表格只需录入一次
                List<String> colKeys = null;

                for (int rowIndex = 0; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
                    XSSFRow row = sheet.getRow(rowIndex);

                    String excelRowSN;
                    if (rowIndex < defaultNames.length) {
                        excelRowSN = defaultNames[rowIndex];
                    } else {
                        // 确定当前Excel行在第一列中定义的SN
                        Cell snCell;
                        if (row == null || (snCell = row.getCell(0)) == null) {
                            // 无法读取到SN列中的值
                            if (row != null) {
                                LogEditor.serv.warn("在表格【" + sheetName + "】的行" + row.getRowNum() + "中的SN列为空！");
                            } else {
                                LogEditor.serv.warn("表格【" + sheetName + "】的第" + (rowIndex + 1) + "行为空行！");
                            }
                            excelRowSN = null;
                        } else {
                            excelRowSN = snCell.toString();
                        }

                        // 统一数字格式
                        if (excelRowSN != null && !excelRowSN.isBlank()) {
                            excelRowSN = String.valueOf(String.format("%.0f", Double.parseDouble(excelRowSN)));
                        }
                    }

                    if (excelRowSN == null || excelRowSN.isBlank()) {
                        break;
                    }

                    // 更新excel对应的行内容
                    String redisRowData;
                    if ((redisRowData = redisHash.get(excelRowSN)) != null) {
                        // 若SN在Redis中有对应数据
                        JSONObject rowJO = JSON.parseObject(redisRowData, Feature.OrderedField);
                        colKeys = (colKeys == null ? new ArrayList<>(rowJO.keySet()) : colKeys);

                        for (int colIndex = 0; colIndex < colKeys.size(); colIndex++) {
                            XSSFCell cell ;
                            if ((cell = row.getCell(colIndex)) == null) {
                                cell = row.createCell(colIndex);
                            }

                            // 保留原格式的同时，设置数据格式为字符串，以正常写入大数字
                            CellStyle cellStyle = cell.getCellStyle();
                            cellStyle.setDataFormat(dataFormat.getFormat("@"));
                            cell.setCellStyle(cellStyle);

                            // 设置单元格内容
                            String cellValue = resolveStringForJSON(String.valueOf(rowJO.get(colKeys.get(colIndex))));

                            // 空值的情况下填入空字符串
                            if (cellValue.equals("null") || cellValue.equals("")) {
                                cell.setCellValue("");
                                cell.setCellType(CellType.BLANK);
                            }
                            else {
                                cell.setCellValue(convertToBR(cellValue));
                            }
                        }
                    }
                    else {
                        // 数据在excel中存在，但在redis中没有对应记录，说明记录被删除，需在Excel中删去对应行
                        LogEditor.serv.info("写入Excel时，需要删除第" + rowIndex + "(0-based)行的数据");

                        // 如果要删除的行为表格的最后一行，直接调用XSSFSheet#removeRow方法
                        if (rowIndex == sheet.getLastRowNum()) {
                            row = sheet.getRow(rowIndex);
                            if (row != null) {
                                sheet.removeRow(row);
                            }
                        }
                        else {
                            // 否则，调用XSSFSheet#shiftRows方法
                            int shiftStartIndex = rowIndex + 1;
                            int shiftEndIndex = sheet.getLastRowNum();
                            int shiftOffset = -1;
                            int startRowIndexAfterShift = shiftStartIndex + shiftOffset;
                            int endRowIndexAfterShift = shiftEndIndex + shiftOffset;

                            sheet.shiftRows(shiftStartIndex, shiftEndIndex, shiftOffset);

                            // 需要更新shift后cell的引用关系，否则Excel文件会被损坏
                            // 参见https://bz.apache.org/bugzilla/show_bug.cgi?id=57423#c13
                            for (int shiftedRowIdx = startRowIndexAfterShift; shiftedRowIdx <= endRowIndexAfterShift;
                                 shiftedRowIdx++) {
                                XSSFRow shiftedRow = sheet.getRow(shiftedRowIdx);
                                if (shiftedRow != null) {
                                    for (Cell c : shiftedRow) {
                                        ((XSSFCell)c).updateCellReferencesForShifting("MSG");
                                    }
                                }
                            }

                            // rowIndex指针在删除当前行之后，由于shift操作，指针指向的是下一行的数据，所以需要手动往回移动指针
                            rowIndex -= 1;
                        }
                    }

                    // 删除已经遍历过的key
                    redisHash.remove(excelRowSN);
                }

                // redisHash中仍有剩余内容，则为Redis中新增的表记录
                if (redisHash.size() > 0) {
                    LogEditor.serv.info("开始写入新增的表记录...");

                    // 为了避免空行，将cursor定位到最后一个不为空的行
                    int rowIndex;
                    for (rowIndex = sheet.getLastRowNum(); rowIndex >= 0 ; rowIndex--) {
                        XSSFRow row;
                        XSSFCell cell;
                        if ((row = sheet.getRow(rowIndex)) != null && (cell = row.getCell(0)) != null) {
                            String cellValue = cell.toString();

                            if (cellValue != null && cellValue.length() > 0) {
                                break;
                            }
                        }
                    }

                    rowIndex += 1;

                    // 保持记录按照sn增序排列
                    LinkedList<String> redisHashKeys = new LinkedList<>(redisHash.keySet());
                    Collections.sort(redisHashKeys);

                    while (!redisHashKeys.isEmpty()) {
                        XSSFRow row = sheet.createRow(rowIndex++);
                        String redisHashKey = redisHashKeys.poll();
                        String redisRowData = redisHash.get(redisHashKey);
                        JSONObject json = JSON.parseObject(redisRowData);

                        for (int colIndex = 0; colIndex < columnNames.size(); colIndex++) {
                            // 设置单元格格式，比保证大数字可被正常写入
                            XSSFCell cell = row.createCell(colIndex);
                            CellStyle cellStyle = cell.getCellStyle();

                            if (cellStyle == null) {
                                cellStyle = workbook.createCellStyle();
                            }

                            cellStyle.setDataFormat(dataFormat.getFormat("@"));
                            cellStyle.setAlignment(HorizontalAlignment.CENTER);
                            cell.setCellStyle(cellStyle);

                            // 空值的情况下填入空字符串
                            String cellValue = resolveStringForJSON(String.valueOf(json.get(columnNames.get(colIndex))));
                            if (cellValue.equals("null") || cellValue.equals("")) {
                                cell.setCellValue("");
                                cell.setCellType(CellType.BLANK);
                            }
                            else {
                                cell.setCellValue(convertToBR(cellValue));
                            }
                        }
                    }
                }

                fos = new FileOutputStream(excelPath);
                workbook.write(fos);

                LogEditor.serv.info("写入Excel完成！");

                // 生成与Excel对应的CSV文件
                if (shouldCommitCSV) {
                    toCSV(workbook, excelName, sheetName);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fis != null) {
                    fis.close();
                }
                if (fos != null) {
                    fos.flush();
                    fos.close();
                }
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 生成指定excel表单对应的csv文件
     * @param workbook
     * @param excelName
     * @param sheetName
     */
    public void toCSV(Workbook workbook, String excelName, String sheetName) {
        try {
            Sheet currSheet = workbook.getSheet(sheetName);
            sheetName = sheetName.substring(sheetName.indexOf("|") + 1);

            LogEditor.serv.info("开始生成CSV：" + sheetName);

            String fileName = sheetName + ".csv";
            String csvPath = EditorConfig.svn_export + "/data/csv/" + fileName;
            FileOutputStream fos = new FileOutputStream(csvPath, false);

            // UTF-8 BOM HEADER
            byte[] bytes = new byte[]{(byte) 0xEF, (byte) 0xBB, (byte) 0xBF};
            fos.write(bytes);

            int totalRowCount = currSheet.getPhysicalNumberOfRows();
            int totalColCount = currSheet.getRow(2).getPhysicalNumberOfCells();

            A:
            for (int rowIdx = 0; rowIdx < totalRowCount; rowIdx++) {
                StringJoiner rowJoiner = new StringJoiner(",");
                Row row = currSheet.getRow(rowIdx);
                if (row == null) {
                    continue;
                }
                for (int colIdx = 0; colIdx < totalColCount; colIdx++) {
                    Cell cell = row.getCell(colIdx);
                    if (cell == null) {
                        if (colIdx != 0) {
                            rowJoiner.add("\"\"");
                            continue;
                        } else {
                            if (rowIdx == 0) {
                                throw new IllegalArgumentException(excelName + " " + sheetName + " ☢配置首列不能为null");
                            }
                            continue A;
                        }
                    }
                    String cellValue = EditorUtils.getCellValueStr(cell);
                    if (colIdx == 0 && rowIdx >= 4 && cellValue.isBlank()) {
                        // 跳过空单元格的情况
                        continue A;
                    }

                    cellValue = cellValue.replaceAll("\"", "\"\"");
                    cellValue = "\"" + cellValue + "\"";
                    rowJoiner.add(cellValue);
                }
                fos.write(rowJoiner.toString().getBytes(StandardCharsets.UTF_8));
                fos.write("\n".getBytes(StandardCharsets.UTF_8));
            }

            fos.flush();
            fos.close();
            LogEditor.serv.info("生成CSV完成！");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 数据被修改的回调方法
     * @param tableName：表格名
     * @param dataSn：修改的数据SN
     * @param shouldIncrementVerNum：是否需要修改版本号
     * @param shouldUpdateSnPool: 是否需要更新可用SN池
     * @return 新的版本号（如果需要生成的话）
     */
    public Long onTableDataChanged(String tableName, String dataSn, boolean shouldIncrementVerNum, boolean shouldUpdateSnPool) {
        // 刷新计时器
        tableTimers.get(tableName).reStartTimer();
        LogEditor.serv.info("Excel【" + tableName + "】计时器被刷新");

        // 刷新改动记录表
        tableModifiedSinceLastCommit.put(tableName, true);

        // 若需要更新最大可用SN池
        if (shouldUpdateSnPool) {
            // 更新最大SN表
            String currMaxSnStr;
            long currMaxSn = 0L, currSn = Long.parseLong(dataSn);

            if ((currMaxSnStr = JedisManager.hget(EditorConst.TABLE_SN_MAX, tableName)) != null) {
                currMaxSn = Math.max(currMaxSn, Long.parseLong(currMaxSnStr));
            }

            if (currSn > currMaxSn) {
                JedisManager.hset(EditorConst.TABLE_SN_MAX, tableName, String.valueOf(currSn));
                LogEditor.serv.info("表格" + tableName + "的最大SN被更新为" + currSn);
            }
        }

        // 若需要更新数据版本号
        if (shouldIncrementVerNum) {
            return Long.parseLong(VersionManager.getInstance().incrementTableDataVersion(tableName, dataSn));
        }
        else {
            return Long.parseLong(VersionManager.getInstance().getTableDataVersion(tableName, dataSn));
        }
    }
}

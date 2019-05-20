package com.abc.editorserver.manager;

import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.abc.editorserver.config.EditorConfig;
import com.abc.editorserver.db.JedisManager;
import com.abc.editorserver.module.JSONModule.ExcelConfig;
import com.abc.editorserver.module.JSONModule.ExcelTrigger;
import com.abc.editorserver.utils.Task;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.abc.editorserver.support.LogEditor;
import com.alibaba.fastjson.parser.Feature;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import com.abc.editorserver.utils.Timer;

/**
 * 数据管理器
 */
public class DataManager {

    private static DataManager mgr = new DataManager();
    public static DataManager getInstance(){ return mgr; }
    private static Map<String, List<String>> columnSeqMap = new HashMap<>();
    private Map<String, Map<String, JSONObject>> triggerData = new HashMap<>();
    private Map<String, Timer> tableTimers = new HashMap<>();

    private final long dataPersistInterval = Timer.HALF_MINUTE;

    private DataManager() {}

    private static final String ConfigPath = "ExcelConfig.json";

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

        LogEditor.serv.info("======拆分表格写入Excel中======");
        redisToExcel();
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
        try {
            File file = new File(EditorConfig.CONFIG_PATH + ConfigPath);
            InputStream input = new FileInputStream(file);
            byte[] buff = input.readAllBytes();
            String jsonStr = new String(buff);
            JSONObject jo = JSON.parseObject(jsonStr);
            ExcelManager.setInstance(jo.toJavaObject(ExcelManager.class));
        }
        catch(Exception e) {
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

        XSSFRow row;
        XSSFCell cell;
        XSSFSheet sheet;
        XSSFWorkbook workbook;
        String triggerName, excelName, sheetName;
        List<String> colNames, rowValues;

        ExcelTrigger[] triggers = ExcelManager.getInstance().getTriggers();

        try {
            for (ExcelTrigger trigger : triggers) {
                excelName = trigger.getExcel();
                sheetName = trigger.getSheet();
                triggerName = excelName.substring(0, excelName.lastIndexOf('.')) + "_" + sheetName;
                excelName = EditorConfig.svn_export + "/" + excelName;

                workbook = new XSSFWorkbook(new FileInputStream(excelName));
                sheet = workbook.getSheet(sheetName);

                // 获取表格所有的列名
                colNames = new ArrayList<>();
                row = sheet.getRow(0);

                for (int colIndex = 0; colIndex < row.getLastCellNum(); colIndex++) {
                    cell = row.getCell(colIndex);

                    if (cell == null) {
                        colNames.add(String.valueOf(colIndex));
                    } else {
                        colNames.add(cell.toString());
                    }
                }

                // 缓存表格数据
                for (int rowIndex = 1; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
                    rowValues = new ArrayList<>();
                    row = sheet.getRow(rowIndex);

                    String indexColumn = row.getCell(0).toString();

                    for (int colIndex = 0; colIndex < row.getLastCellNum(); colIndex++) {
                        cell = row.getCell(colIndex);

                        if (cell == null) {
                            rowValues.add("");
                        } else {
                            rowValues.add(convertFromBR(cell.toString()));
                        }
                    }

                    while (rowValues.size() < colNames.size())
                    {
                        rowValues.add("");
                    }

                    triggerData.computeIfAbsent(triggerName, s -> new LinkedHashMap<>())
                            .put(indexColumn, JSONObject.parseObject(stringToJSON(colNames, rowValues)));
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 根据配置信息将对应的excel表写入Redis
     */
    public void excelToRedis(boolean shouldOverride) {
        try {
            for (ExcelConfig conf: ExcelManager.getInstance().getConfigs()) {
                String fileName = EditorConfig.svn_export + "/" + conf.getExcel();
                String sheetName = conf.getSheet();
                XSSFWorkbook workbook = new XSSFWorkbook(new FileInputStream(fileName));
                XSSFSheet sheet = workbook.getSheet(sheetName);

                //表格每一列的英文名称
                List<String> colNames = new ArrayList<>();
                XSSFRow nameRow = sheet.getRow(2);
                for (int i = 0; i < nameRow.getLastCellNum(); i++) {
                    if (nameRow.getCell(i)==null) {
                        colNames.add(Integer.toString(i));
                    } else{
                        colNames.add(nameRow.getCell(i).toString());
                    }
                }

                columnSeqMap.put(conf.getRedis_table(),colNames);

                // 清除数据
                if (shouldOverride) {
                    JedisManager.getInstance().del(conf.getRedis_table());
                }

                for (int i = 0; i <= sheet.getLastRowNum(); i++) {
                    List<String> valueList = new ArrayList<>();
                    XSSFRow row = sheet.getRow(i);
                    for (int j = 0; j < row.getLastCellNum(); j++) {
                        XSSFCell cell = row.getCell(j);
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

                    JedisManager.getInstance().hset(conf.getRedis_table(), key, stringToJSON(colNames, valueList));
                }

            }
        }
        catch (Exception e) {
            LogEditor.config.error("Excel写入Redis失败：", e);
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
            return value.replaceAll("@n@", "\\n");
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
    private String stringToJSON(List<String> keys, List<String> values) throws Exception{
        int length = keys.size();
//        if(values.size() != length){
//            throw new Exception("key与value的数目不相同");
//        }
        StringBuffer buffer = new StringBuffer();
        buffer.append("{\"");
        for(int i = 0; i < length-1; i++){
            buffer.append(keys.get(i));
            buffer.append("\":\"");
            buffer.append(values.get(i));
            buffer.append("\",\"");
        }
        buffer.append(keys.get(length-1));
        buffer.append("\":\"");
        buffer.append(values.get(length-1));
        buffer.append("\"}");

        return buffer.toString();
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

                Map<String, String> mapAll = JedisManager.getInstance().hgetAll(conf.getRedis_table());
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
        Map<String, String> datas = JedisManager.getInstance().hgetAll(config.getRedis_table());

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
        String value = JedisManager.getInstance().hget(tableName,"cnName");
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
     * @param table
     * @param sn
     * @param field
     * @param value
     * @param shouldIncrementVerNum: 是否需要更新版本号
     * @return
     */
    public long updateTableData(String table, String sn, String field, String value, boolean shouldIncrementVerNum) {
        ExcelConfig config = ExcelManager.getInstance().getConfig(table);
        if (config == null) {
            return -1;
        }

        String json = JedisManager.getInstance().hget(config.getRedis_table(), sn);
        JSONObject jo;

        if (json == null) {
            jo = new JSONObject();
        } else {
            jo = JSON.parseObject(json);
        }
        jo.put(field, value);

        JedisManager.getInstance().hset(config.getRedis_table(), sn, jo.toJSONString());

        // 刷新计时器
        tableTimers.get(table).reStartTimer();
        LogEditor.serv.info("写Excel计时器被刷新");

        if (shouldIncrementVerNum) {
            return Long.parseLong(VersionManager.getInstance().incrementTableDataVersion(table, sn));
        }
        else {
            return Long.parseLong(VersionManager.getInstance().getTableDataVersion(table, sn));
        }
    }

    /**
     * 更新数据表数据（不更新版本号）
     * @param table
     * @param sn
     * @param field
     * @param value
     * @return
     */
    public long updateTableData(String table, String sn, String field, String value) {
        return updateTableData(table, sn, field, value, false);
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
        String json = JedisManager.getInstance().hget(config.getRedis_table(), sn);
        return json;
    }

    /**
     * 检查表中是否有对应SN的记录
     * @param tableName
     * @param sn
     * @return
     */
    public boolean SnExistsInTable(String tableName, String sn) {
        return getTableDataBySn(tableName, sn) != null;
    }

    /**
     * 获取指定表格下一个可用的SN
     * @param tableName
     * @return
     */
    public String getNextAvailableSn(String tableName) {
        ExcelConfig config = ExcelManager.getInstance().getConfig(tableName);

        if (config == null) {
            LogEditor.serv.info("提供的表名错误，无法获取到下一个可用的SN");
            return null;
        }

        Map<String ,String> map = JedisManager.getInstance().hgetAll(config.getRedis_table());
        Iterator<String> iter = map.keySet().iterator();
        String nextSN = "";
        long currSN = 0L, maxSN = 0L;

        while (iter.hasNext()) {
            try {
                nextSN = iter.next();
                currSN = Long.parseLong(nextSN);
            } catch (NumberFormatException e) {
                LogEditor.serv.info("在数据库表" + config.getRedis_table() + "中出现非数字类型的SN值：" + nextSN);
            }

            maxSN = Math.max(maxSN, currSN);
        }

        maxSN += 1;

        return String.valueOf(maxSN);
    }

    /**
     * 在数据表中新增一条数据
     * @param tableName
     * @param params
     * @return
     */
    public int addTableData(String tableName, JSONObject params) {
        ExcelConfig config = ExcelManager.getInstance().getConfig(tableName);
        if (config == null) {
            return -1;
        }

        JedisManager.getInstance().hset(config.getRedis_table(), params.getString("sn"), params.toJSONString());

        // 刷新计时器
        tableTimers.get(tableName).reStartTimer();
        LogEditor.serv.info("写Excel计时器被刷新");

        return 1;
    }

    /**
     * 在数据表中删去一条数据
     * @param tableName
     * @param sn
     * @return
     */
    public int deleteTableData(String tableName, String sn) {
        ExcelConfig config = ExcelManager.getInstance().getConfig(tableName);
        if (config == null) {
            return -1;
        }

        JedisManager.getInstance().hdel(config.getRedis_table(), sn);

        // 刷新计时器
        tableTimers.get(tableName).reStartTimer();
        LogEditor.serv.info("写Excel计时器被刷新");

        return 1;
    }

    /**
     * 查询当前是否有表格的定时器被触发，需要的时候向SVN提交commit
     */
    public void dataPersistHandler() {
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

            JSONObject params = new JSONObject();
            params.put("candidates", candidates);

            // 提交至任务队列中
            GlobalManager.addTask(new Task(var -> {
                List<String> tables = (List<String>) var.get("candidates");

                // 将本地缓存的改动写入至Excel表格中
                persistData(false, tables);
            }, params));
        }
    }

    /**
     * 强制将更新写入Excel并提交至SVN
     */
    public void promptDataPersist() {
        Iterator<Map.Entry<String, Timer>> iter = tableTimers.entrySet().iterator();
        Map.Entry<String, Timer> entry;
        List<String> candidates = new ArrayList<>();

        while (iter.hasNext()) {
            entry = iter.next();

            if (entry.getValue().isRunning()) {
                // 停止写入Excel定时器
                entry.getValue().stopTimer();
                candidates.add(entry.getKey());
            }
        }

        if (candidates.size() > 0) {
            LogEditor.serv.info("定时器被触发，将缓存刷新至Excel中...");

            JSONObject params = new JSONObject();
            params.put("candidates", candidates);

            // 提交至任务队列中
            GlobalManager.addTask(new Task(var -> {
                List<String> tables = (List<String>) var.get("candidates");

                // 将本地缓存的改动写入至Excel表格中
                persistData(false, tables);

                // 强制触发下一次commit
                GlobalManager.getCommitTimer().triggerDue();
            }, params));
        }
    }

    /**
     * 将本地缓存的改动写入至Excel表格中
     * @param shouldPersistAll：是否需要写入配置中指定的所有表格
     * @param tablesToPersist: 指定需要写入的表格
     */
    public void persistData(boolean shouldPersistAll, List<String> tablesToPersist) {

        LogEditor.serv.info("=========开始将Redis数据写入Excel中========");

        /// Redis操作相关变量
        String redisTableName, redisRowData, redisHashKey;
        Map<String, String> redisHash;
        LinkedList<String> redisHashKeys;
        int countRedisHash;

        /// Excel操作相关变量
        XSSFWorkbook workbook;
        XSSFSheet sheet;
        XSSFCell cell;
        XSSFRow row;
        String excelName, sheetName, excelPath, cellValue;
        List<String> columnNames;

        /// 解析相关变量
        JSONObject json;
        List<String> colKeys;
        int rowIndex, colIndex;

        /// IO相关变量
        FileInputStream fis = null;
        FileOutputStream fos = null;

        ExcelConfig[] excelConfigs = ExcelManager.getInstance().getConfigs();
        JedisManager jedisManager = JedisManager.getInstance();

        /// Excel数据格式相关变量
        DataFormat dataFormat;
        CellStyle cellStyle;

        /// 预处理需要写入的表格
        Set<String> targetTables = new HashSet<>(tablesToPersist);

        try {
            for (ExcelConfig config : excelConfigs) {
                redisTableName = config.getRedis_table();

                /// 跳过不需要持久化的表格
                if (!shouldPersistAll && !targetTables.contains(redisTableName)) {
                    continue;
                }

                excelName = config.getExcel();
                sheetName = config.getSheet();
                excelPath = EditorConfig.svn_export + "/" + excelName;
                columnNames = columnSeqMap.get(redisTableName);

                LogEditor.serv.info("开始写入表格：" + redisTableName);

                fis = new FileInputStream(excelPath);
                workbook = new XSSFWorkbook(fis);
                sheet = workbook.getSheet(sheetName);
                colKeys = null;

                redisHash = jedisManager.hgetAll(redisTableName);
                String[] defaultNames = ExcelManager.getInstance().getDefaultNames();
                countRedisHash = redisHash.size();

                /// 初始化格式
                dataFormat = workbook.createDataFormat();

                LogEditor.serv.info("总写入行数：" + countRedisHash);

                for (rowIndex = 0; rowIndex < countRedisHash; rowIndex++) {
                    row = sheet.getRow(rowIndex);

                    if (rowIndex < defaultNames.length) {
                        redisHashKey = defaultNames[rowIndex];
                    } else {
                        /// 确定当前Excel行对应的redisHashKey（也即SN）
                        redisHashKey = (row == null ? null : row.getCell(0).toString());

                        /// 统一数字格式
                        if (redisHashKey != null) {
                            redisHashKey = String.valueOf(String.format("%.0f", Double.parseDouble(redisHashKey)));
                        }
                    }

                    if (redisHashKey == null || redisHashKey.length() == 0) {
                        break;
                    }

                    redisRowData = redisHash.get(redisHashKey);

                    if (redisRowData != null) {
                        json = JSON.parseObject(redisRowData, Feature.OrderedField);

                        colKeys = (colKeys == null ? new ArrayList<>(json.keySet()) : colKeys);

                        for (colIndex = 0; colIndex < colKeys.size(); colIndex++) {
                            cell = row.getCell(colIndex);

                            if (cell == null) {
                                cell = row.createCell(colIndex);
                            }

                            /// 保留原格式的同时，设置数据格式为字符串，以正常写入大数字
                            cellStyle = cell.getCellStyle();
                            cellStyle.setDataFormat(dataFormat.getFormat("@"));
                            cell.setCellStyle(cellStyle);

                            // 设置单元格内容
                            cellValue = String.valueOf(json.get(colKeys.get(colIndex)));

                            // 空值的情况下填入空字符串
                            if (cellValue.equals("null") || cellValue.equals("")) {
                                cell.setCellValue("");
                                cell.setCellType(CellType.BLANK);
                            }
                            else {
                                cell.setCellValue(cellValue);
                            }
                        }
                    } else {
                        /// 记录被删除，需在Excel中删去对应行
                        sheet.shiftRows(rowIndex + 1, sheet.getLastRowNum(), -1);
                    }

                    /// 删除已经遍历过的key
                    redisHash.remove(redisHashKey);
                }

                // 处理新增的表记录
                if (redisHash.size() > 0) {
                    LogEditor.serv.info("开始写入新增的表记录...");

                    // 保持记录按照sn增序排列
                    redisHashKeys = new LinkedList<>(redisHash.keySet());
                    Collections.sort(redisHashKeys);

                    while (!redisHashKeys.isEmpty()) {
                        row = sheet.createRow(rowIndex++);
                        redisHashKey = redisHashKeys.poll();
                        redisRowData = redisHash.get(redisHashKey);
                        json = JSON.parseObject(redisRowData);

                        for (colIndex = 0; colIndex < columnNames.size(); colIndex++) {
                            // 设置单元格格式，比保证大数字可被正常写入
                            cell = row.createCell(colIndex);
                            cellStyle = cell.getCellStyle();

                            if (cellStyle == null) {
                                cellStyle = workbook.createCellStyle();
                            }

                            cellStyle.setDataFormat(dataFormat.getFormat("@"));
                            cellStyle.setAlignment(HorizontalAlignment.CENTER);
                            cell.setCellStyle(cellStyle);

                            // 空值的情况下填入空字符串
                            cellValue = String.valueOf(json.get(columnNames.get(colIndex)));
                            if (cellValue.equals("null") || cellValue.equals("")) {
                                cell.setCellValue("");
                                cell.setCellType(CellType.BLANK);
                            }
                            else {
                                cell.setCellValue(cellValue);
                            }
                        }
                    }
                }

                fos = new FileOutputStream(excelPath);
                workbook.write(fos);

                LogEditor.serv.info("写入Excel完成！");
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
}

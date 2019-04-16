package com.abc.editorserver.manager;

import java.io.*;
import java.util.*;

import com.abc.editorserver.config.EditorConfig;
import com.abc.editorserver.db.JedisManager;
import com.abc.editorserver.module.JSONModule.ExcelConfig;
import com.abc.editorserver.module.JSONModule.ExcelTrigger;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.abc.editorserver.support.LogEditor;
import com.alibaba.fastjson.parser.Feature;
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
    private Map<String, Map<String, JSONObject>> triggerData = new HashMap<>();

    private Timer timer = new Timer();

    private DataManager(){ }

    public Timer getTimer() {
        return this.timer;
    }

    private static final String ConfigPath = "ExcelConfig.json";

    public void init(){
        loadExcelConfig();

        LogEditor.serv.info("======开始加载Trigger表======");
        initTriggers();

        excelToRedis();
        redisToExcel();
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
     * 读取ExcelConfig中配置的Trigger表，缓存表格内容
     */
    public void initTriggers() {

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
    private void excelToRedis() {
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
                    }
                    else{
                        colNames.add(nameRow.getCell(i).toString());
                    }
                }

                for (int i = 0; i <= sheet.getLastRowNum(); i++) {
                    List<String> valueList = new ArrayList<>();
                    XSSFRow row = sheet.getRow(i);
                    for (int j = 0; j < row.getLastCellNum(); j++) {
                        XSSFCell cell = row.getCell(j);
                        if (null == cell) {
                            valueList.add("");
                        }
                        else {
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
                    //JedisManager.getInstance().push(conf.getRedis_table()+"_Row",key);
                    JedisManager.getInstance().hset(conf.getRedis_table(), key, stringToJSON(colNames, valueList));
                }

            }
        }
        catch (Exception e) {
            LogEditor.config.error("Excel写入Redis失败：", e);
        }
    }

    private String convertFromBR(String value) {
        if (value.contains("\n") || value.contains("\r\n")) {
            return value.replaceAll("\\n", "@n@");
        }
        if (value.contains("\"")) {
            return value.replaceAll("\"", "@q@");
        }
        if(value.endsWith(".0")){
            return value.substring(0,value.length()-2);
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
            return value.substring(0,value.length()-2);
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
                }
                else {
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
            JSONObject jo = JSON.parseObject(value, Feature.OrderedField); // 生成有序的JSON对象
            List<String> keys = new ArrayList<>(jo.keySet());
            int cellIndex = 0;
            XSSFCell cell;

            for (String key : keys) {
                cell = row.createCell(cellIndex++);
                cell.setCellValue(convertToBR(jo.getString(key)));
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
        JSONArray ret = new JSONArray();

        JSONObject jo = JSONObject.parseObject(value, Feature.OrderedField);    // 获取有序的JSON对象
        List<String> keys = new ArrayList<>(jo.keySet());

        for(String key : keys) {
            ret.add("{\"prop\":\"" + key + "\",\"label\":\"" + convertToBR(jo.getString(key)) + "\"}");
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
     * @return
     */
    public long updateTableData(String table, String sn, String field, String value) {
        ExcelConfig config = ExcelManager.getInstance().getConfig(table);
        if (config == null) {
            return -1;
        }

        String json = JedisManager.getInstance().hget(config.getRedis_table(), sn);
        JSONObject jo;

        if (json == null) {
            jo = new JSONObject();
        }
        else {
            jo = JSON.parseObject(json);
        }
        jo.put(field, value);

        JedisManager.getInstance().hset(config.getRedis_table(), sn, jo.toJSONString());

        // 刷新计时器
        timer.reStartTimer();

        return Long.parseLong(VersionManager.getInstance().incrementTableDataVersion(table, sn));
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
        timer.reStartTimer();

        return 1;
    }
}

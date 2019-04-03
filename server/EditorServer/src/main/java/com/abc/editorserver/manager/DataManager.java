package com.abc.editorserver.manager;

import java.io.*;
import java.util.*;

import com.abc.editorserver.config.EditorConfig;
import com.abc.editorserver.db.JedisManager;
import com.abc.editorserver.module.JSONModule.ExcelConfig;
import com.abc.editorserver.module.JSONModule.ExcelConfigs;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.abc.editorserver.support.LogEditor;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * 数据管理器
 */
public class DataManager {

    private static DataManager mgr = new DataManager();
    public static DataManager gi(){ return mgr; }
    private static Map<String, List<String>> columnSeqMap = new HashMap<String, List<String>>();

    private DataManager(){ }

    private static final String excelConfigPath = "../config/ExcelConfig.json";

    public void init(){
        loadExcelConfig();
        excelToRedis();
        redisToExcel();
    }

    /**
     * 读取ExcelConfig.json配置文件，解析成excelConfigs对象
     * @return
     */
    private void loadExcelConfig(){
        try{
            File file = new File(excelConfigPath);
            InputStream input = new FileInputStream(file);
            byte[] buff = input.readAllBytes();
            String jsonStr = new String(buff);
            JSONObject jo = JSON.parseObject(jsonStr);
            ExcelConfigs.setInstance(jo.toJavaObject(ExcelConfigs.class));
        }
        catch(Exception e){
            LogEditor.config.error("读取Excel配置失败：", e);
        }
    }

    /**
     * 根据配置信息将对应的excel表写入Redis
     */
    private void excelToRedis(){
        try{
            for(ExcelConfig conf:ExcelConfigs.gi().getConfigs()){
                String fileName = EditorConfig.svn_export + "/" + conf.getExcel();
                String sheetName = conf.getSheet();
                XSSFWorkbook workbook = new XSSFWorkbook(new FileInputStream(fileName));
                XSSFSheet sheet = workbook.getSheet(sheetName);

                //表格每一列的英文名称
                List<String> keyList = new ArrayList<>();
                XSSFRow nameRow = sheet.getRow(2);
                for(int i = 0; i < nameRow.getLastCellNum(); i++){
                    if(nameRow.getCell(i)==null){
                        keyList.add(Integer.toString(i));
                    }
                    else{
                        keyList.add(nameRow.getCell(i).toString());
                    }
                }
                columnSeqMap.put(conf.getRedis_table(),keyList);
                for(int i = 0; i <= sheet.getLastRowNum(); i++){
                    List<String> valueList = new ArrayList<>();
                    XSSFRow row = sheet.getRow(i);
                    for(int j = 0; j < row.getLastCellNum(); j++){
                        XSSFCell cell = row.getCell(j);
                        if(null == cell){
                            valueList.add("");
                        }
                        else{
                            valueList.add(convertFromBR(cell.toString()));
                        }
                    }
                    while(valueList.size() < keyList.size()){
                        valueList.add("");
                    }

                    //System.out.println(toJSON(keyList, valueList));
                    String key = valueList.get(0);
                    if(i<ExcelConfigs.gi().getDefaultNames().length){
                        key = ExcelConfigs.gi().getDefaultNames()[i];
                    }
                    //JedisManager.gi().push(conf.getRedis_table()+"_Row",key);
                    JedisManager.gi().hset(conf.getRedis_table(), key, stringToJSON(keyList, valueList));
                }

            }
        }
        catch(Exception e){
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
            for (ExcelConfig conf : ExcelConfigs.gi().getConfigs()) {
                //根据原表名和页签sheet名生成新表名,目前格式为：原表名_页签英文名.xlsx
                String fileName = EditorConfig.svn_export + "/" + conf.getExcel();
                if (fileName.endsWith(".xlsx")) {
                    fileName = fileName.substring(0, fileName.length() - 5);
                }
                else {
                    throw new Exception("要写入的目标文件不是xlsx格式：" + conf.getExcel());
                }
                String sheetName = conf.getSheet();
                String[] sArr = sheetName.split("\\|");
                fileName += "_" + sArr[1] + ".xlsx";
                File file = new File(fileName);
                if(!file.exists()){
                    file.createNewFile();
                }

                Map<String, String> mapAll = JedisManager.gi().hgetAll(conf.getRedis_table());
                FileInputStream fis = new FileInputStream(fileName);
                XSSFWorkbook workbook = new XSSFWorkbook();
                //遍历删除原表的sheet
                int sheetNum = workbook.getNumberOfSheets();
                for(int i=0;i<sheetNum;i++){
                    workbook.removeSheetAt(i);
                }
                XSSFSheet sheet  = workbook.createSheet(sheetName);
                String[] defaultName = ExcelConfigs.gi().getDefaultNames();
                int rowCount = 0;
                for(String name:defaultName){
                    String value = mapAll.get(name);
                    makeRow(value, sheet, rowCount++, conf.getRedis_table());
                    mapAll.remove(name);
                }
                for(String key:mapAll.keySet()){
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

    private void makeRow(String value, XSSFSheet sheet, int rowNum, String tableName){
        try{
            XSSFRow row = sheet.createRow(rowNum);
            JSONObject jo = JSON.parseObject(value);
            int cellNum = 0;
            XSSFCell cell;
            while(jo.size()>0){
                String columnKey = columnSeqMap.get(tableName).get(cellNum);
                cell = row.createCell(cellNum);
                cell.setCellValue(convertToBR(jo.getString(columnKey)));
                cellNum++;
                jo.remove(columnKey);
            }
        }
        catch(Exception e){
            LogEditor.serv.error("行号："+rowNum,e);
        }
    }

    /**
     * 获取数据表数据
     * @param tableName
     * @return
     */
    public JSONArray getTableData(String tableName) {
        ExcelConfig config = ExcelConfigs.gi().getConfig(tableName);
        JSONArray ret = new JSONArray();
        if (config == null) {
            return ret;
        }
        Map<String, String> datas = JedisManager.gi().hgetAll(config.getRedis_table());
        //在返回数据前去掉属性行
        String[] defaultName = ExcelConfigs.gi().getDefaultNames();
        for(String name:defaultName){
            datas.remove(name);
        }
        for (String data : datas.values()) {
            ret.add(data);
        }
        return  ret;
    }

    public JSONArray getTableColumnName(String tableName) {
        String value = JedisManager.gi().hget(tableName,"cnName");
        JSONObject jo = JSONObject.parseObject(value);
        JSONArray ret = new JSONArray();
        List<String> columnName = columnSeqMap.get(tableName);
        for(String name:columnName){
            ret.add("{\"prop\":\"" + name +"\",\"label\":\""+ convertToBR(jo.getString(name))+ "\"}");
        }
        return  ret;
    }

}

package com.abc.editorserver.manager;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.*;

import com.abc.editorserver.config.EditorConfig;
import com.abc.editorserver.db.JedisManager;
import com.abc.editorserver.module.JSONModule.excelConfig;
import com.abc.editorserver.module.JSONModule.excelConfigs;
import com.alibaba.fastjson.JSON;
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
    public static DataManager DataMgr(){ return mgr; }

    private DataManager(){ }

    private static final String excelConfigPath = "../config/ExcelConfig.json";

    public void init(){
        excelConfigs config = loadExcelConfig();
        excelToRedis(config);
        redisToExcel(config);
    }

    /**
     * 读取ExcelConfig.json配置文件，解析成excelConfigs对象
     * @return
     */
    private excelConfigs loadExcelConfig(){
        try{
            File file = new File(excelConfigPath);
            InputStream input = new FileInputStream(file);
            byte[] buff = input.readAllBytes();
            String jsonStr = new String(buff);
            //System.out.println(jsonStr);
            JSONObject jo = JSON.parseObject(jsonStr);
            excelConfigs config = jo.toJavaObject(excelConfigs.class);
            return config;
        }
        catch(Exception e){
            LogEditor.config.error("读取Excel配置失败：", e);
        }
        return null;
    }

    /**
     * 根据配置信息将对应的excel表写入Redis
     * @param config
     */
    private void excelToRedis(excelConfigs config){
        try{
            for(excelConfig conf:config.getConfigs()){
                String fileName = EditorConfig.svn_export + "/" + conf.getExcel();
                String sheetName = conf.getSheet();
                XSSFWorkbook workbook = new XSSFWorkbook(new FileInputStream(fileName));
                XSSFSheet sheet = workbook.getSheet(sheetName);

                //表格每一列的英文名称
                List<String> keyList = new ArrayList<>();
                XSSFRow nameRow = sheet.getRow(2);
                for(int i = 0; i < nameRow.getLastCellNum(); i++){
                    keyList.add(nameRow.getCell(i).toString());
                }

                for(int i = 0; i <= sheet.getLastRowNum(); i++){
                    List<String> valueList = new ArrayList<>();
                    XSSFRow row = sheet.getRow(i);
                    for(int j = 0; j < row.getLastCellNum(); j++){
                        XSSFCell cell = row.getCell(j);
                        if(null == cell){
                            valueList.add("");
                        }
                        else{
                            valueList.add(cell.toString());
                        }
                    }
                    while(valueList.size() < keyList.size()){
                        valueList.add("");
                    }

                    //System.out.println(toJSON(keyList, valueList));
                    String key = "";
                    switch(i){
                        case 0 :
                            key = "cs";
                            break;
                        case 1:
                            key = "type";
                            break;
                        case 2:
                            key = "enName";
                            break;
                        case 3:
                            key = "cnName";
                            break;
                         default:
                             key = valueList.get(0);
                    }
                    JedisManager.gi().hset(conf.getRedis_table(), key, toJSON(keyList, valueList));
                }

            }
        }
        catch(Exception e){
            LogEditor.config.error("Excel写入Redis失败：", e);
        }
    }

    /**
     * 根据excel一行的值返回json串
     * @param keys
     * @param values
     * @return
     * @throws Exception
     */
    private String toJSON(List<String> keys, List<String> values) throws Exception{
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
     * @param config
     */
    private void redisToExcel(excelConfigs config){
        try {
            for (excelConfig conf : config.getConfigs()) {
                //根据原表名和页签sheet名生成新表名,目前格式为：原表名_页签英文名.xlsx
                String fileName = EditorConfig.svn_export + "/" + conf.getExcel();
                if (fileName.endsWith(".xlsx")) {
                    fileName = fileName.substring(0, fileName.length() - 5);
                } else {
                    throw new Exception("要写入的目标文件不是xlsx格式：" + conf.getExcel());
                }
                String sheetName = conf.getSheet();
                String[] sArr = sheetName.split("\\|");
                fileName += "_" + sArr[1] + ".xlsx";

                FileOutputStream fos = new FileOutputStream(fileName);

                Map<String, String> mapAll = JedisManager.gi().hgetAll(conf.getRedis_table());

            }
        }
        catch(Exception e){
            LogEditor.config.error("Redis写入Excel失败：", e);
        }
    }

    /**
     * 获取数据表数据
     * @param table_name
     * @return
     */
    public JSONObject getTableData(String table_name) {
        JedisManager.gi().hgetAll()
    }

}

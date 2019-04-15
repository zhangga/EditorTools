package com.abc.editorserver.manager;

import com.abc.editorserver.config.EditorConfig;
import com.abc.editorserver.db.JedisManager;
import com.abc.editorserver.module.JSONModule.ExcelConfig;
import com.abc.editorserver.support.LogEditor;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * VersionManager
 * Created by Marco
 * Date: 2019/4/15 10:16
 */

public class VersionManager {

    private static VersionManager manager = new VersionManager();
    private Set<String> tableSN = new HashSet<>();
    private Map<String, AtomicLong> tableDataVersions = new ConcurrentHashMap<>();

    private VersionManager() {}

    public static VersionManager getInstance() {
        return manager;
    }

    public static final String redisTableName = "VERSION";

    public void init() {
        LogEditor.serv.info("======开始加载版本号信息======");
        initTableData();
        initTableDataVersion();
    }

    /**
     * 从Excel中加载所有的人物数据
     */
    private void initTableData() {
        try {
            ExcelConfig[] configs = ExcelManager.getInstance().getConfigs();

            for (ExcelConfig config : configs) {
                String excelName = config.getExcel();
                String excelNameWOExtension = excelName.substring(0, excelName.lastIndexOf("."));
                String excelPath = EditorConfig.svn_export + "/" + excelName;
                String sheetName = config.getSheet();

                XSSFWorkbook workbook = new XSSFWorkbook(new FileInputStream(excelPath));
                XSSFSheet sheet = workbook.getSheet(sheetName);

                ExcelManager excelManager = ExcelManager.getInstance();

                for (int rowIndex = 0; rowIndex <= sheet.getLastRowNum(); rowIndex++) {

                    if (rowIndex < excelManager.getDefaultNames().length) {
                        continue;
                    }

                    XSSFCell cell = sheet.getRow(rowIndex).getCell(0);

                    if (cell == null) {
                        continue;
                    }

                    String rowSN = cell.toString();
                    rowSN = rowSN.substring(0, rowSN.length() - 2);
                    tableSN.add(excelNameWOExtension + "|" + sheetName + "|" + rowSN);
                }
            }
        } catch (IOException e) {
            // TODO
            e.printStackTrace();
        }
    }

    /**
     * 从redis中加载版本信息
     */
    private void initTableDataVersion() {
        JedisManager redisManager = JedisManager.getInstance();

        Iterator<String> iter = tableSN.iterator();
        String sn, dataVersion;

        while (iter.hasNext()) {
            sn = iter.next();
            dataVersion = redisManager.hget(redisTableName, sn);

            if (dataVersion != null) {
                tableDataVersions.put(sn, new AtomicLong(Long.valueOf(dataVersion)));
            }
            else {
                redisManager.hset(redisTableName, sn, "0");
                tableDataVersions.put(sn, new AtomicLong(1L));
            }
        }
    }

    /**
     * 获取指定的版本号信息
     * @param excelName
     * @param sheetName
     * @param sn
     * @return
     */
    public String getTableDataVersion(String excelName, String sheetName, String sn) {
        String tableNameWithSn = excelName + "|" + sheetName + "|" + sn;

        if (!tableDataVersions.containsKey(tableNameWithSn)) {
            LogEditor.serv.info("获取版本号信息失败：无法获取表信息" + tableNameWithSn);
            return null;
        }
        else {
            return tableDataVersions.get(tableNameWithSn).toString();
        }
    }

    /**
     * 自增版本号
     * @param excelName
     * @param sheetName
     * @param sn
     * @return
     */
    public long incrementTableDataVersion(String excelName, String sheetName, String sn) {
        String tableNameWithSn = excelName + "|" + sheetName + "|" + sn;

        if (!tableDataVersions.containsKey(tableNameWithSn)) {
            LogEditor.serv.info("自增版本号失败：无法获取表信息" + tableNameWithSn);
            return -1;
        }
        else {
            return tableDataVersions.compute(tableNameWithSn, (k, v) -> {
                v.incrementAndGet();
                return v;
            }).get();
        }
    }

    /**
     * 检查当前数据版本号与提供的版本号是否一致
     * @param excelName
     * @param sheetName
     * @param sn
     * @param verNum
     * @return
     */
    public boolean hasTableDataVersionChanged(String excelName, String sheetName, String sn, String verNum) {
        String currVerNum = getTableDataVersion(excelName, sheetName, sn);

        if (verNum == null || currVerNum == null) {
            return false;
        }
        else {
            return !currVerNum.equals(verNum);
        }
    }
}

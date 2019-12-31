package com.abc.editorserver.utils;

import com.abc.editorserver.support.SysException;
import org.apache.logging.log4j.message.ParameterizedMessage;
import org.apache.poi.ss.usermodel.Cell;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Properties;

/**
 * 工具类
 * Created by U-Demon
 * Date: 2019/3/30
 */
public class EditorUtils {

    /**
     * 读取配置文件
     * @param path 目录名
     * @param name 文件名
     * @return
     */
    public static Properties readProperties(String path, String name) {
        try(FileInputStream in = new FileInputStream(new File(path, name))) {
            InputStreamReader isr = new InputStreamReader(in, "UTF-8");
            Properties p = new Properties();
            p.load(isr);
            isr.close();
            return p;
        } catch (Exception e) {
            throw new SysException(e);
        }
    }

    /**
     * 基于参数创建字符串
     * #0开始
     * @param str
     * @param params
     * @return
     */
    public static String createStr(String str, Object...params) {
        return ParameterizedMessage.format(str, params);
    }

    /**
     * 解析Excel单元格中的内容
     * @param cell
     * @return
     */
    public static String getCellValueStr(Cell cell) {
        String cellValue = "";
        if (cell == null) {
            return cellValue;
        }
        switch (cell.getCellType()) {
            case NUMERIC:
                cellValue = getNumericCellValue(cell);
                break;
            case STRING:
                cellValue = cell.getStringCellValue();
                break;
            case BOOLEAN:
                cellValue = String.valueOf(cell.getBooleanCellValue());
                break;
            case FORMULA:
                try {
                    cellValue = cell.getStringCellValue();
                } catch (IllegalStateException e) {
                    cellValue = getNumericCellValue(cell);
                }
                break;
            case BLANK:
                cellValue = "";
                break;
            case ERROR:
                cellValue = "非法字符";
                break;
            default:
                cellValue = "未知类型";
                break;
        }
        return cellValue;
    }

    /**
     * 将Excel单元格中的内容解析为数字
     * @param cell
     * @return
     */
    private static String getNumericCellValue(Cell cell) {
        // 去掉*.00的情况
        double va = cell.getNumericCellValue();
        int temp = (int) va;
        return va - temp > 0 ? String.valueOf(va) : String.valueOf(temp);
    }
}

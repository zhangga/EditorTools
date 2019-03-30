package com.abc.editorserver.utils;

import com.abc.editorserver.support.SysException;
import org.apache.logging.log4j.message.ParameterizedMessage;

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

}

package com.abc.editorserver.config;

import com.abc.editorserver.utils.EditorUtils;
import org.apache.commons.io.FilenameUtils;

import java.net.URL;
import java.util.Properties;

/**
 * 配置文件
 * Created by U-Demon
 * Date: 2019/3/30
 */
public class EditorConfig {

    public static final boolean DEBUG = true;

    /** 配置文件名称 */
    private static final String CONFIG_NAME = "config.properties";
    /** Configuration */
    private static final String LOG_CONFIGURATION = "log4j2.xml";

    /** 服务器端口 */
    public static final int http_port;
    public static final boolean http_ssl;

    /** Redis配置 */
    public static final String redis_ip;
    public static final int redis_port;
    public static final String redis_pwd;

    /** SVN */
    public static final String svn_name;
    public static final String svn_pwd;
    public static final String svn_root;
    public static final String svn_export;

    /** 服务器配置 */
    public static final String server_ip;
    public static final boolean dev_mode;

    /** 配置文件路径 */
    public static String CONFIG_PATH;

    /**
     * config.properties 文件位置搜索顺序为：resources -> System.getProperty:configPath -> classPath/../../../config
     */
    static {
        URL configuration = Thread.currentThread().getContextClassLoader().getResource(CONFIG_NAME);
        // 默认路径没有则通过property取Config路径
        if (configuration == null) {
            String configPath = System.getProperty("configPath");
            // 系统变量取不到则用默认值
            configPath = configPath != null ? configPath : "../../../config/";
            CONFIG_PATH = EditorConfig.class.getResource("/").getPath() + configPath;
        }
        else {
            CONFIG_PATH = FilenameUtils.getFullPath(configuration.getPath());
        }

        // 以下是log4j2的方式，如果使用其他日志实现得修改
        URL logConfiguration = Thread.currentThread().getContextClassLoader().getResource(LOG_CONFIGURATION);
        // 默认位置没有则取系统变量
        if (logConfiguration == null) {
            String configurationFile = System.getProperty("log4j.configurationFile");
            // 系统变量中没有则取Config目录下的
            if (configurationFile == null) {
                System.setProperty("log4j.configurationFile", CONFIG_PATH + LOG_CONFIGURATION);
            }
        }

        // 获取配置
        Properties prop = EditorUtils.readProperties(CONFIG_PATH, CONFIG_NAME);
        http_port = Integer.valueOf(prop.getProperty("http_port"));
        http_ssl = Boolean.valueOf(prop.getProperty("http_ssl"));
        redis_ip = prop.getProperty("redis_ip");
        redis_port = Integer.valueOf(prop.getProperty("redis_port"));
        redis_pwd = prop.getProperty("redis_pwd");
        svn_name = prop.getProperty("svn_name");
        svn_pwd = prop.getProperty("svn_pwd");
        svn_root = prop.getProperty("svn_root");
        svn_export = prop.getProperty("svn_export");
        server_ip = prop.getProperty("server_ip");
        dev_mode = Boolean.valueOf(prop.getProperty("dev_mode"));
    }

}

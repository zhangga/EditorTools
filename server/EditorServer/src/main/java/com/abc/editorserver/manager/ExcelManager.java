package com.abc.editorserver.manager;

import com.abc.editorserver.module.JSONModule.ExcelConfig;

public class ExcelManager {

    private ExcelManager(){}

    public static ExcelManager getInstance(){
        return instance;
    }

    public static void setInstance(ExcelManager ecs){
        instance = ecs;
    }

    private static ExcelManager instance;
    private String[] defaultNames;
    private ExcelConfig[] configs;

    /**
     * 通过表名获取配置信息
     * @param tableName
     * @return
     */
    public ExcelConfig getConfig(String tableName) {
        for (ExcelConfig config : configs) {
            if (config.getRedis_table().toUpperCase().equals(tableName.toUpperCase())) {
                return config;
            }
        }
        return null;
    }

    public ExcelConfig[] getConfigs() {
        return configs;
    }

    public void setConfigs(ExcelConfig[] configs) {
        this.configs = configs;
    }

    public String[] getDefaultNames() {
        return defaultNames;
    }

    public void setDefaultNames(String[] defaultNames) {
        this.defaultNames = defaultNames;
    }
}

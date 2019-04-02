package com.abc.editorserver.module.JSONModule;

public class ExcelConfigs {
    public static ExcelConfigs gi(){
        return instance;
    }
    public static void setInstance(ExcelConfigs ecs){
        instance = ecs;
    }
    private static ExcelConfigs instance;

    private ExcelConfigs(){ }

    public ExcelConfig[] getConfigs() {
        return configs;
    }

    public void setConfigs(ExcelConfig[] configs) {
        this.configs = configs;
    }

    private ExcelConfig[] configs;

    /**
     * 通过表名获取配置信息
     * @param tableName
     * @return
     */
    public ExcelConfig getConfig(String tableName) {
        for (ExcelConfig config : configs) {
            if (config.getRedis_table().toUpperCase().equals(tableName.toUpperCase())) {
                return  config;
            }
        }
        return null;
    }

    public String[] getDefaultNames() {
        return defaultNames;
    }

    public void setDefaultNames(String[] defaultNames) {
        this.defaultNames = defaultNames;
    }

    private String[] defaultNames;

}

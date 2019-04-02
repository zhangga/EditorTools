package com.abc.editorserver.module.JSONModule;

public class ExcelConfigs {
    public static ExcelConfigs Instance(){
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

    public String[] getDefaultNames() {
        return defaultNames;
    }

    public void setDefaultNames(String[] defaultNames) {
        this.defaultNames = defaultNames;
    }

    private String[] defaultNames;

}

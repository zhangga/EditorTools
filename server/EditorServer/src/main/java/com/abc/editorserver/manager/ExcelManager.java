package com.abc.editorserver.manager;

import com.abc.editorserver.module.JSONModule.ExcelConfig;
import com.abc.editorserver.module.JSONModule.ExcelTrigger;

import java.util.HashMap;
import java.util.Map;

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
    private Map<String, ExcelConfig> configMap;
    private ExcelTrigger[] triggers;
    private String[] EnumQuestType;

    /**
     * 通过表名获取配置信息
     * @param tableName
     * @return
     */
    public ExcelConfig getConfig(String tableName) {
        if (tableName == null) {
            return null;
        }
        return configMap.get(tableName.toUpperCase());
    }

    public ExcelConfig[] getConfigs() {
        return configs;
    }

    public void setConfigs(ExcelConfig[] configs) {
        this.configs = configs;
        Map<String, ExcelConfig> temp = new HashMap<>();
        for (ExcelConfig config : this.configs) {
            temp.put(config.getRedis_table().toUpperCase(), config);
        }
        this.configMap = temp;
    }

    public String[] getDefaultNames() {
        return defaultNames;
    }

    public void setDefaultNames(String[] defaultNames) {
        this.defaultNames = defaultNames;
    }

    public ExcelTrigger[] getTriggers() {
        return triggers;
    }

    public void setTriggers(ExcelTrigger[] triggers) {
        this.triggers = triggers;
    }

    public String getQuestType(int id) {
        for (int i = 0; i < EnumQuestType.length; i += 2) {
            if (EnumQuestType[i].equals(String.valueOf(id))) {
                return EnumQuestType[i + 1];
            }
        }
        return "NULL";
    }

    public int getQuestIndex(int id) {
        for (int i = 0; i < EnumQuestType.length; i += 2) {
            if (EnumQuestType[i].equals(String.valueOf(id))) {
                return i / 2;
            }
        }
        return 0;
    }

    public String[] getEnumQuestType() {
        return EnumQuestType;
    }

    public void setEnumQuestType(String[] enumQuestType) {
        EnumQuestType = enumQuestType;
    }
}

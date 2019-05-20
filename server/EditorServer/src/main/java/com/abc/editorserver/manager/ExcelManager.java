package com.abc.editorserver.manager;

import com.abc.editorserver.db.JedisManager;
import com.abc.editorserver.module.JSONModule.ExcelConfig;
import com.abc.editorserver.module.JSONModule.ExcelTrigger;
import com.abc.editorserver.utils.TableEditAction;
import com.alibaba.fastjson.JSONObject;

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

    // 用于统计表格当前被编辑用户数
    private static final String EditHistoryRedisTableName = "EDIT_HISTORY";
    private Map<String, TableEditAction> pastEdits;

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

    /**
     * 通过表名获取上次表格操作详情
     * @param tableName
     * @return
     */
    public TableEditAction retrieveLastEdit(String tableName) {
        if (tableName == null) {
            return null;
        }

        return pastEdits.get(tableName.toUpperCase());
    }

    /**
     * 更新表格操作详情
     * @param tableName
     * @return
     */
    public void updateLastEdit(String tableName, String userId) {
        if (tableName == null || userId == null) {
            return;
        }

        TableEditAction pastEdit = new TableEditAction(userId);
        pastEdits.put(tableName, pastEdit);
        JedisManager.hset(EditHistoryRedisTableName, tableName, JSONObject.toJSONString(pastEdit));
    }

    public void updateLastEdit(String tableName, String userId, String dateTime) {
        if (tableName == null || userId == null) {
            return;
        }
        else if (dateTime == null) {
            updateLastEdit(tableName, userId);
        }
        else {
            TableEditAction pastEdit = new TableEditAction(userId, dateTime);
            pastEdits.put(tableName, pastEdit);
            JedisManager.setKey(tableName, JSONObject.toJSONString(pastEdit));
        }
    }

    public void setConfigs(ExcelConfig[] configs) {
        this.configs = configs;

        Map<String, ExcelConfig> temp = new HashMap<>();
        Map<String, TableEditAction> editHistoryTmp = new HashMap<>();

        String editHistoryJson;
        TableEditAction editAction;

        for (ExcelConfig config : this.configs) {
            temp.put(config.getRedis_table().toUpperCase(), config);

            // 加载编辑历史
            editHistoryJson = JedisManager.hget(EditHistoryRedisTableName, config.getRedis_table());

            if (editHistoryJson == null) {
                editHistoryTmp.put(config.getRedis_table(), null);
            }
            else {
                editAction = JSONObject.parseObject(editHistoryJson, TableEditAction.class);
                editHistoryTmp.put(config.getRedis_table(), editAction);
            }
        }

        this.configMap = temp;
        this.pastEdits = editHistoryTmp;
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

package com.abc.editorserver.module.excel;

import com.abc.editorserver.manager.DataManager;
import com.abc.editorserver.module.user.User;
import com.abc.editorserver.msg.GameActionJson;
import com.abc.editorserver.net.RequestData;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

/**
 * GetConditionInfoAction
 * Created by Marco
 * Date: 2019/5/5 18:20
 */
public class GetConditionInfoAction extends GameActionJson {
    @Override
    public void doAction(User user, RequestData request) {
        DataManager dataManager = DataManager.getInstance();
        JSONArray conditions = dataManager.getTableData("CONDITION");
        JSONArray conditionTypes = dataManager.getAllTriggerTableData("Trigger.xlsx", "EConditionType");

        JSONArray formattedConditions = new JSONArray();

        for (int i = 0; i < conditions.size(); i++) {
            formattedConditions.add(JSON.parseObject(conditions.getString(i)));
        }

        JSONObject replyMsg = new JSONObject();
        replyMsg.put("condition", formattedConditions);
        replyMsg.put("conditionType", conditionTypes.toArray());
        sendMsg(request.ctx, replyMsg);
    }
}

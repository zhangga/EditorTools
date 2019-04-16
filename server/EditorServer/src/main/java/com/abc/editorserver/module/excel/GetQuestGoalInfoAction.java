package com.abc.editorserver.module.excel;

import com.abc.editorserver.manager.DataManager;
import com.abc.editorserver.module.user.User;
import com.abc.editorserver.msg.GameActionJson;
import com.abc.editorserver.net.RequestData;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

/**
 * Created by U-Demon
 * Date: 2019/4/16
 */
public class GetQuestGoalInfoAction extends GameActionJson {

    @Override
    public void doAction(User user, RequestData request) {
        JSONArray data = DataManager.getInstance().getTableData("QUESTGOAL");
        JSONArray enumGoal = DataManager.getInstance().getAllTriggerTableData("Trigger.xlsx", "EnumQuestGoalType");
        JSONObject msg = new JSONObject();
        msg.put("data", data);
        msg.put("enumGoal", enumGoal);
        sendMsg(request.ctx, msg);
    }

}

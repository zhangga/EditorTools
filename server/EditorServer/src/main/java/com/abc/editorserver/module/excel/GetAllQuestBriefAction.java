package com.abc.editorserver.module.excel;

import com.abc.editorserver.manager.DataManager;
import com.abc.editorserver.manager.ExcelManager;
import com.abc.editorserver.module.user.User;
import com.abc.editorserver.msg.GameActionJson;
import com.abc.editorserver.net.RequestData;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

/**
 * 获取所有的任务简要
 * Created by U-Demon
 * Date: 2019/4/4
 */
public class GetAllQuestBriefAction extends GameActionJson {

    @Override
    public void doAction(User user, RequestData request) {
        JSONArray quests = DataManager.getInstance().getTableData("QUEST");
        // 不同类型的任务信息
        // [
        //  {text: "主线任务", children: [{id: 1, text: xx}, {id: 1, text: xx}]},
        //  {{text: "XX任务",  children: []}
        //  ]
        JSONArray datas = new JSONArray();
        for (int i = 0; i < ExcelManager.getInstance().getEnumQuestType().length; i+=2) {
            JSONObject data = new JSONObject();
            data.put("text", ExcelManager.getInstance().getEnumQuestType()[i+1]);
            data.put("children", new JSONArray());
            datas.add(data);
        }
        for (int i = 0; i < quests.size(); ++i) {
            JSONObject quest = JSONObject.parseObject(quests.getString(i));
            // 简要信息
            JSONObject brief = new JSONObject();
            int sn = quest.getInteger("sn");
            brief.put("id", quest.getInteger("sn"));
            brief.put("text", sn + ":\t" + quest.getString("questName"));
            // 任务类型
            int type = quest.getIntValue("questType");
            int index = ExcelManager.getInstance().getQuestIndex(type);
            JSONObject data = datas.getJSONObject(index);
            data.getJSONArray("children").add(brief);
        }

        // 更新编辑历史
        ExcelManager.getInstance().updateLastEdit("QUEST", user.getName());

        JSONObject msg = new JSONObject();
        msg.put("data", datas);
        sendMsg(request.ctx, msg);
    }

}

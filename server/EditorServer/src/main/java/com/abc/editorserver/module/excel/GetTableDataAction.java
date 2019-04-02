package com.abc.editorserver.module.excel;

import com.abc.editorserver.manager.DataManager;
import com.abc.editorserver.module.user.User;
import com.abc.editorserver.msg.GameActionJson;
import com.abc.editorserver.net.RequestData;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

/**
 * 获取表数据
 * Created by U-Demon
 * Date: 2019/4/2
 */
public class GetTableDataAction extends GameActionJson {

    @Override
    public void doAction(User user, RequestData request) {
        String table_name = request.msg.getString("table_name");
        JSONArray data = DataManager.gi().getTableData(table_name);
        JSONObject msg = new JSONObject();
        msg.put("data", data);
        sendMsg(request.ctx, msg);
    }

}

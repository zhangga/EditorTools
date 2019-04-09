package com.abc.editorserver.module.excel;

import com.abc.editorserver.manager.DataManager;
import com.abc.editorserver.module.user.User;
import com.abc.editorserver.msg.GameActionJson;
import com.abc.editorserver.net.RequestData;
import com.alibaba.fastjson.JSONObject;

/**
 * Created by U-Demon
 * Date: 2019/4/9
 */
public class GetTableDataBySnAction extends GameActionJson {

    @Override
    public void doAction(User user, RequestData request) {
        String table = request.msg.getString("table");
        String sn = request.msg.getString("sn");
        String json = DataManager.getInstance().getTableDataBySn(table, sn);
        JSONObject msg = new JSONObject();
        msg.put("ret", 1);
        msg.put("data", json);
        sendMsg(request.ctx, msg);
    }

}

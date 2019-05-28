package com.abc.editorserver.module.excel;

import com.abc.editorserver.config.EditorConst;
import com.abc.editorserver.manager.DataManager;
import com.abc.editorserver.module.user.User;
import com.abc.editorserver.msg.GameActionJson;
import com.abc.editorserver.net.RequestData;
import com.abc.editorserver.support.LogEditor;
import com.alibaba.fastjson.JSONObject;

/**
 * DeleteTableDataBySnAction
 * Created by Marco
 * Date: 2019/5/16 15:16
 */
public class DeleteTableDataBySnAction extends GameActionJson {
    @Override
    public void doAction(User user, RequestData request) {
        String table = request.msg.getString("table");
        String sn = request.msg.getString("sn");

        LogEditor.serv.info("接收到删除请求：【table】" + table + ",【sn】" + sn);
        DataManager.getInstance().deleteTableData(table, sn, user);

        JSONObject msg = new JSONObject();
        msg.put("result", EditorConst.RESULT_OK);
        sendMsg(request.ctx, msg);
    }
}

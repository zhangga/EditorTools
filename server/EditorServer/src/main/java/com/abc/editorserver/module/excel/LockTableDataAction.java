package com.abc.editorserver.module.excel;

import com.abc.editorserver.manager.DataManager;
import com.abc.editorserver.module.user.User;
import com.abc.editorserver.msg.GameActionJson;
import com.abc.editorserver.net.RequestData;
import com.alibaba.fastjson.JSONObject;

/**
 * LockTableDataAction
 * Created by Marco
 * Date: 2019/5/20 20:01
 */
public class LockTableDataAction extends GameActionJson {
    @Override
    public void doAction(User user, RequestData request) {
        String tableName = request.msg.getString("table");
        String sn = request.msg.getString("sn");

        JSONObject reply = DataManager.getInstance().tryLockingTableData(tableName, sn, user);
        sendMsg(request.ctx, reply);
    }
}

package com.abc.editorserver.module.excel;

import com.abc.editorserver.config.EditorConst;
import com.abc.editorserver.manager.DataManager;
import com.abc.editorserver.module.user.User;
import com.abc.editorserver.msg.GameActionJson;
import com.abc.editorserver.net.RequestData;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.List;

/**
 * GetTableDataAtColumnsAction
 * Created by Marco
 * Date: 2019/5/20 20:41
 */
public class GetTableDataAtColumnsAction extends GameActionJson {
    @Override
    public void doAction(User user, RequestData request) {
        String tableName = request.msg.getString("table");
        String cols = request.msg.getString("cols");

        List<String> colNames = JSONObject.parseArray(cols, String.class);
        JSONArray tableData = DataManager.getInstance().getTableDataAtColumns(tableName, colNames);

        JSONObject replyMsg = new JSONObject();
        replyMsg.put("result", EditorConst.RESULT_OK);
        replyMsg.put("data", tableData);
        sendMsg(request.ctx, replyMsg);
    }
}

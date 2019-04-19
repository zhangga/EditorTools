package com.abc.editorserver.module.excel;

import com.abc.editorserver.config.EditorConst;
import com.abc.editorserver.manager.ExcelManager;
import com.abc.editorserver.module.user.User;
import com.abc.editorserver.msg.GameActionJson;
import com.abc.editorserver.net.RequestData;
import com.abc.editorserver.support.LogEditor;
import com.abc.editorserver.utils.TableEditAction;
import com.alibaba.fastjson.JSONObject;

/**
 * GetTableStatusAction
 * Created by Marco
 * Date: 2019/4/18 20:25
 */
public class GetTableStatusAction extends GameActionJson {
    @Override
    public void doAction(User user, RequestData request) {
        String tableNames = request.msg.getString("table_names");

        String[] redisTableNames = tableNames.split(",");


        ExcelManager manager = ExcelManager.getInstance();

        JSONObject editHistories = new JSONObject();
        TableEditAction editAction;

        for (String tableName : redisTableNames) {
            editAction = manager.retrieveLastEdit(tableName);

            if (editAction == null) {
                editHistories.put(tableName, "");
            }
            else {
                editHistories.put(tableName, editAction.toString());
            }
        }

        LogEditor.serv.info("回复消息：【最近编辑信息】" + editHistories.toJSONString());

        JSONObject replyMsg = new JSONObject();
        replyMsg.put("result", EditorConst.RESULT_OK);
        replyMsg.put("data", editHistories.toJSONString());

        sendMsg(request.ctx, replyMsg);
    }
}

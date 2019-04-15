package com.abc.editorserver.module.excel;

import com.abc.editorserver.config.EditorConst;
import com.abc.editorserver.manager.DataManager;
import com.abc.editorserver.manager.VersionManager;
import com.abc.editorserver.module.user.User;
import com.abc.editorserver.msg.GameActionJson;
import com.abc.editorserver.net.RequestData;
import com.abc.editorserver.support.LogEditor;
import com.alibaba.fastjson.JSONObject;

/**
 * Created by U-Demon
 * Date: 2019/4/9
 */
public class UpdateTableDataAction extends GameActionJson {

    @Override
    public void doAction(User user, RequestData request) {
        String table = request.msg.getString("table");
        String sn = request.msg.getString("sn");
        String versionNum = request.msg.getString("verNum");

        JSONObject replyMsg = new JSONObject();

        if (versionNum != null) {
            VersionManager versionManager = VersionManager.getInstance();
            if (versionManager.hasTableDataVersionChanged(table, sn, versionNum)) {
                replyMsg.put("result", EditorConst.RESULT_FAILED);
                replyMsg.put("hint", "当前表格数据已被更改，请刷新重试");
            }
        }

        String field = request.msg.getString("field");
        String value = request.msg.getString("value");

        int ret = DataManager.getInstance().updateTableData(table, sn, field, value);

        LogEditor.serv.info("更新【" + table + "】表中SN为【" + sn + "】的记录的【" + field + "】列为：" + value);

        replyMsg.put("result", EditorConst.RESULT_OK);
        replyMsg.put("hint", "更新成功");
        replyMsg.put("data", ret);
        sendMsg(request.ctx, replyMsg);
    }

}

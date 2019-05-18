package com.abc.editorserver.module.excel;

import com.abc.editorserver.config.EditorConst;
import com.abc.editorserver.manager.VersionManager;
import com.abc.editorserver.module.user.User;
import com.abc.editorserver.msg.GameActionJson;
import com.abc.editorserver.net.RequestData;
import com.abc.editorserver.support.LogEditor;
import com.alibaba.fastjson.JSONObject;

/**
 * GetVersionNumberBySnAction
 *
 * 获取某条数据的最新版本号
 * @request_param table: 指定查找的表格名
 * @request_param sn: 指定查找记录的SN
 *
 * Created by Marco
 * Date: 2019/5/17 15:06
 */
public class GetVersionNumberBySnAction extends GameActionJson {
    @Override
    public void doAction(User user, RequestData request) {
        String table = request.msg.getString("table");
        String sn = request.msg.getString("sn");

        JSONObject replyMsg = new JSONObject();

        // 附加版本号信息
        String verNum = VersionManager.getInstance().getTableDataVersion(table, sn);
        LogEditor.serv.info("获取到表格" + table + "中记录" + sn + "的最新版本号为" + verNum);

        replyMsg.put("result", EditorConst.RESULT_OK);
        replyMsg.put("verNum", verNum);
        sendMsg(request.ctx, replyMsg);
    }
}

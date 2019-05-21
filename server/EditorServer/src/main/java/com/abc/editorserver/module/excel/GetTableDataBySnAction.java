package com.abc.editorserver.module.excel;

import com.abc.editorserver.config.EditorConst;
import com.abc.editorserver.manager.DataManager;
import com.abc.editorserver.manager.VersionManager;
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

        JSONObject replyMsg = new JSONObject();

        // 获取表数据
        String tableDataInfo = DataManager.getInstance().getTableDataBySn(table, sn);
        JSONObject jo = JSONObject.parseObject(tableDataInfo);

        // 获取版本号信息
        String verNum = VersionManager.getInstance().getTableDataVersion(table, sn);
        if (jo != null) {
            // 附加版本号信息
            jo.put("verNum", verNum);

            replyMsg.put("result", EditorConst.RESULT_OK);
            replyMsg.put("data", jo.toString());
        }
        else {
            replyMsg.put("result", EditorConst.RESULT_FAILED);
            replyMsg.put("hint", "获取失败，请检查配置文件");
        }

        sendMsg(request.ctx, replyMsg);
    }

}

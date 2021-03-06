package com.abc.editorserver.module.excel;

import com.abc.editorserver.config.EditorConst;
import com.abc.editorserver.manager.DataManager;
import com.abc.editorserver.manager.VersionManager;
import com.abc.editorserver.module.user.User;
import com.abc.editorserver.msg.GameActionJson;
import com.abc.editorserver.net.RequestData;
import com.abc.editorserver.support.LogEditor;
import com.alibaba.fastjson.JSONObject;

import java.util.Set;

/**
 * UpdateMultipleDataInSameRowAction
 * Created by Marco
 * Date: 2019/5/14 16:48
 */
public class UpdateMultipleDataInSameRowAction extends GameActionJson {
    @Override
    public void doAction(User user, RequestData request) {
        String table = request.msg.getString("table");
        String sn = request.msg.getString("sn");
        String versionNum = request.msg.getString("verNum");

        JSONObject replyMsg = new JSONObject();

        VersionManager versionManager = VersionManager.getInstance();
        if (versionManager.hasTableDataVersionChanged(table, sn, versionNum)) {
            LogEditor.serv.info("收到的versionNum: " + versionNum);
            replyMsg.put("result", EditorConst.RESULT_FAILED);
            replyMsg.put("hint", "当前表格数据已被更改，请刷新重试");
            sendMsg(request.ctx, replyMsg);
            return;
        }

        String values = request.msg.getString("values");
        JSONObject valuesJO = JSONObject.parseObject(values);
        Set<String> valueKeys = valuesJO.keySet();

        for (String valueKey : valueKeys) {
            // 更新数据时不自动增加版本号
            DataManager.getInstance().updateTableData(table, sn, valueKey, valuesJO.getString(valueKey), user);
        }

        // 手动更新版本号
        String verNum = VersionManager.getInstance().incrementTableDataVersion(table, sn);

        LogEditor.serv.info("更新【" + table + "】表中SN为【" + sn + "】的记录为：" + valuesJO.toJSONString());
        LogEditor.serv.info("返回版本号：" + verNum);

        // 将新的版本号信息写入redis中
        versionManager.versionCacheToRedis();

        replyMsg.put("result", EditorConst.RESULT_OK);
        replyMsg.put("hint", "更新成功");
        replyMsg.put("data", verNum);
        sendMsg(request.ctx, replyMsg);
    }
}

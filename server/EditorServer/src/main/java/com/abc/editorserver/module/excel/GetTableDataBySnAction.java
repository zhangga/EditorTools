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

        String json = DataManager.getInstance().getTableDataBySn(table, sn);

        String verNum = VersionManager.getInstance().getTableDataVersion(table, sn);
        String ret = null;
        if (json != null) {
            JSONObject jo = JSONObject.parseObject(json);
            jo.put("verNum", verNum);
            ret = jo.toJSONString();
        }

        JSONObject msg = new JSONObject();
        msg.put("result", EditorConst.RESULT_OK);
        msg.put("data", ret);
        sendMsg(request.ctx, msg);
    }

}

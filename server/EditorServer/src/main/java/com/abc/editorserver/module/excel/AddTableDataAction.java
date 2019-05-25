package com.abc.editorserver.module.excel;

import com.abc.editorserver.config.EditorConst;
import com.abc.editorserver.manager.DataManager;
import com.abc.editorserver.module.user.User;
import com.abc.editorserver.msg.GameActionJson;
import com.abc.editorserver.net.RequestData;
import com.abc.editorserver.support.LogEditor;
import com.alibaba.fastjson.JSONObject;

/**
 * AddTableDataAction
 * Created by Marco
 * Date: 2019/4/12 18:01
 */

public class AddTableDataAction extends GameActionJson {

    @Override
    public void doAction(User user, RequestData request) {
        String table = request.msg.getString("table");
        String keyValues = request.msg.getString("kv");

        LogEditor.serv.info("接受到的消息：【Table】" + table + " 【keyValues】" + keyValues);

        JSONObject replyMsg = new JSONObject();

        // 检查参数合法性
        if (table == null ||keyValues == null) {
            replyMsg.put("result", EditorConst.RESULT_FAILED);
            replyMsg.put("hint", "解析请求失败！");
        }
        else {
            JSONObject params = JSONObject.parseObject(keyValues);
            String sn = params.getString("sn");

            // 检查参数是否包含sn
            if (sn == null) {
                sn = DataManager.getInstance().getAndIncrNextAvailableSn(table);
                params.put("sn", sn);
                LogEditor.serv.info("服务器提供了当前表格下一个可用的SN：" + sn);
            }

            // 添加新数据
            int addResult = DataManager.getInstance().addTableData(table, params, user);

            switch (addResult) {
                case -2:
                    LogEditor.serv.error("添加失败，当前表格/表格数据被锁定，请稍后重试");
                    replyMsg.put("result", EditorConst.RESULT_FAILED);
                    replyMsg.put("hint", "请求失败，当前表格/表格数据被锁定，请稍后重试");
                case -1:
                    LogEditor.serv.error("添加失败，请检查配置文件");
                    replyMsg.put("result", EditorConst.RESULT_FAILED);
                    replyMsg.put("hint", "添加失败，请检查配置文件");
                    break;
                case 0:
                    LogEditor.serv.error("添加失败，数据库中已有当前SN对应的记录，请提供新的未被使用过的SN");
                    replyMsg.put("result", EditorConst.RESULT_FAILED);
                    replyMsg.put("hint", "添加失败，数据库中已有当前SN对应的记录，请提供新的未被使用过的SN");
                    sendMsg(request.ctx, replyMsg);
                    break;
                case 1:
                    LogEditor.serv.info("在【Table】" + table + "中新增记录：" + " 【keyValues】" + keyValues);
                    replyMsg.put("result", EditorConst.RESULT_OK);
                    replyMsg.put("hint", "添加成功");

                    JSONObject jo = new JSONObject();
                    jo.put("sn", sn);
                    jo.put("questType", params.getString("questType"));
                    replyMsg.put("data", jo.toJSONString());
                    break;
            }
        }

        sendMsg(request.ctx, replyMsg);
    }
}

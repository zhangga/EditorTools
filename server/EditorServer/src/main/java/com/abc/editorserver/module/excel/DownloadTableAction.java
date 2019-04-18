package com.abc.editorserver.module.excel;

import com.abc.editorserver.config.EditorConfig;
import com.abc.editorserver.config.EditorConst;
import com.abc.editorserver.module.user.User;
import com.abc.editorserver.msg.GameActionJson;
import com.abc.editorserver.net.RequestData;
import com.abc.editorserver.support.LogEditor;
import com.alibaba.fastjson.JSONObject;

/**
 * DownloadTableAction
 * Created by Marco
 * Date: 2019/4/18 10:59
 */

public class DownloadTableAction extends GameActionJson {
    @Override
    public void doAction(User user, RequestData request) {
        String excelName = request.msg.getString("excel_name");
        String sheetName = request.msg.getString("sheet_name");

        // 处理excelName和sheetName
        String extension = excelName.split("\\.")[1];
        excelName = excelName.split("\\.")[0];
        sheetName = sheetName.split("\\|")[1];

        // 暂时下载excelName对应的Excel
        String tableName = excelName;

        String redirectAddr = null;
        String redirectPrefix = "http://" + EditorConfig.server_ip + "/download?name=";

        JSONObject replyMsg = new JSONObject();

        if (tableName != null) {
            redirectAddr = redirectPrefix + tableName + "." + extension;

            LogEditor.serv.info("发送下载链接：" + redirectAddr);

            replyMsg.put("result", EditorConst.RESULT_OK);
            replyMsg.put("addr", redirectAddr);
            replyMsg.put("file_name", tableName + "." + extension);
        }
        else {
            replyMsg.put("result", EditorConst.RESULT_FAILED);
            replyMsg.put("hint", "请求下载失败，请稍后重试！");
        }

        sendMsg(request.ctx, replyMsg);
    }
}

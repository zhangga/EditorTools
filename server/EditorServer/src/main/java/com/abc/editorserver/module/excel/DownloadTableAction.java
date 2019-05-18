package com.abc.editorserver.module.excel;

import com.abc.editorserver.config.EditorConfig;
import com.abc.editorserver.config.EditorConst;
import com.abc.editorserver.manager.DataManager;
import com.abc.editorserver.manager.ExcelManager;
import com.abc.editorserver.module.JSONModule.ExcelConfig;
import com.abc.editorserver.module.user.User;
import com.abc.editorserver.msg.GameActionJson;
import com.abc.editorserver.net.RequestData;
import com.abc.editorserver.support.LogEditor;
import com.alibaba.fastjson.JSONObject;

import java.util.Arrays;

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
        String excelNameWithoutExtension = excelName.split("\\.")[0];
        String formattedSheetName = sheetName.split("\\|")[1];

        // 找到当前excel_name与sheet_name对应的redis_table_name
        ExcelConfig[] configs = ExcelManager.getInstance().getConfigs();
        String redisTableName = null;
        for (ExcelConfig config : configs) {
            if (config.getExcel().equals(excelName) && config.getSheet().equals(sheetName)) {
                redisTableName = config.getRedis_table();
                break;
            }
        }

        // 本地持久化excelName对应的表格，保证下载的表格内容为最新
        if (redisTableName != null) {
            LogEditor.serv.info("下载表格" + redisTableName + "前将改动写入Excel中");

            // 将改动写入对应Excel表格中
            DataManager.getInstance().persistData(false, Arrays.asList(redisTableName));

            // 更新完成后停止计时器
            DataManager.getInstance().stopTimerOfTable(redisTableName);
        }

        // 暂时只下载excelName对应的Excel（表格未拆分，因此sheetName暂时无用，待表格拆分后可下载excelName + sheetName对应的表格）
        String tableName = excelNameWithoutExtension;

        String redirectAddr = null;
        String redirectPrefix = "http://" + EditorConfig.server_ip + "/download?name=";

        JSONObject replyMsg = new JSONObject();

        if (tableName != null) {
            redirectAddr = redirectPrefix + excelName;

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

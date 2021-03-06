package com.abc.editorserver.module.excel;

import com.abc.editorserver.config.EditorConst;
import com.abc.editorserver.manager.DataManager;
import com.abc.editorserver.module.user.User;
import com.abc.editorserver.msg.GameActionJson;
import com.abc.editorserver.net.RequestData;
import com.alibaba.fastjson.JSONObject;

/**
 * DeleteMultipleTableDataBySnAction
 *
 * 向服务器请求批量删除多行数据，需要提供的参数包括：
 *  * @request_param table: 指定更新数据的表格名
 *  * @request_param sn_batch: 对应需要操作的多行数据的sn，用“|"分隔
 *
 * Created by Marco
 * Date: 2019/5/16 21:19
 */
public class DeleteMultipleTableDataBySnAction extends GameActionJson {

    @Override
    public void doAction(User user, RequestData request) {
        String table = request.msg.getString("table");
        String snBatch = request.msg.getString("sn_batch");

        String[] snBatchArray = snBatch.split("\\|");

        for (String sn : snBatchArray) {
            DataManager.getInstance().deleteTableData(table, sn, user);
        }

        JSONObject msg = new JSONObject();
        msg.put("result", EditorConst.RESULT_OK);
        sendMsg(request.ctx, msg);
    }
}

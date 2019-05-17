package com.abc.editorserver.module.excel;

import com.abc.editorserver.config.EditorConst;
import com.abc.editorserver.manager.DataManager;
import com.abc.editorserver.module.user.User;
import com.abc.editorserver.msg.GameActionJson;
import com.abc.editorserver.net.RequestData;
import com.abc.editorserver.support.LogEditor;
import com.alibaba.fastjson.JSONObject;

/**
 * UpdateMultipleDataInSameColumnAction
 *
 * 向服务器请求更新同一列下的多行数据，需要提供的参数包括：
 * @request_param table: 指定更新数据的表格名
 * @request_param sn_batch: 对应需要操作的多行数据的sn，用“|"分隔
 * @request_param col_name: 对应需要操作的数据列名
 * @request_param value_batch: 对应每行数据对应列在更新后的数值，用“|”分隔
 *
 * TODO: 处理表名/列名错误的情况
 *
 * Created by Marco
 * Date: 2019/5/16 21:23
 */
public class UpdateMultipleDataInSameColumnAction extends GameActionJson {
    @Override
    public void doAction(User user, RequestData request) {
        String table = request.msg.getString("table");
        String snBatch = request.msg.getString("sn_batch");
        String colName = request.msg.getString("col_name");
        String valueBatch = request.msg.getString("value_batch");

        // 向服务器提供的sn_batch及value_batch数据用“|”分隔
        String[] snBatchArray = snBatch.split("\\|");
        String[] valueBatchArray = valueBatch.split("\\|");

        JSONObject replyMsg = new JSONObject();

        // 判断sn_batch及value_batch对应的数据数量是否一致
        if (snBatchArray.length != valueBatchArray.length) {
            replyMsg.put("result", EditorConst.RESULT_FAILED);
            replyMsg.put("hint", "更新失败，请检查sn_batch与value_batch对应的数据数量是否一致");
            sendMsg(request.ctx, replyMsg);
        } else {
            // 更新数据时不自增版本号
            for (int i = 0; i < snBatchArray.length; i++) {
                DataManager.getInstance().updateTableData(table, snBatchArray[i], colName, valueBatchArray[i]);
                LogEditor.serv.info("更新【" + table + "】表中SN为【" + snBatchArray[i] + "】的记录为：" + valueBatchArray[i]);
            }

            replyMsg.put("result", EditorConst.RESULT_OK);
            replyMsg.put("hint", "更新成功");
        }

        sendMsg(request.ctx, replyMsg);
    }
}

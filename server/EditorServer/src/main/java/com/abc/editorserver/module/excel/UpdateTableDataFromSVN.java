package com.abc.editorserver.module.excel;

import com.abc.editorserver.config.EditorConfig;
import com.abc.editorserver.config.EditorConst;
import com.abc.editorserver.manager.SVNManager;
import com.abc.editorserver.module.user.User;
import com.abc.editorserver.msg.GameActionJson;
import com.abc.editorserver.net.RequestData;
import com.abc.editorserver.support.LogEditor;
import com.alibaba.fastjson.JSONObject;
import org.tmatesoft.svn.core.SVNDepth;
import org.tmatesoft.svn.core.wc.SVNRevision;

/**
 * UpdateTableDataFromSVN
 * Created by Marco
 * Date: 2019/4/8 11:06
 */

public class UpdateTableDataFromSVN extends GameActionJson {

    @Override
    public void doAction(User user, RequestData request) {
        if (SVNManager.isWorkingCopy(EditorConfig.svn_export)) {
            LogEditor.serv.info("开始更新服务器表格数据");
            SVNManager.update(EditorConfig.svn_export, SVNRevision.HEAD, SVNDepth.INFINITY);
        }

        JSONObject msg = new JSONObject();
        msg.put("result", EditorConst.RESULT_OK);
        sendMsg(request.ctx, msg);
    }
}

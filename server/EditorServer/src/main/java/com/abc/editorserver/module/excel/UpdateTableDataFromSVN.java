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
import org.tmatesoft.svn.core.SVNErrorCode;
import org.tmatesoft.svn.core.wc.SVNRevision;

/**
 * UpdateTableDataFromSVN
 * Created by Marco
 * Date: 2019/4/8 11:06
 */

public class UpdateTableDataFromSVN extends GameActionJson {

    private User currUpdatingUser;

    @Override
    public void doAction(User user, RequestData request) {
        JSONObject msg = new JSONObject();

        /* 当前正在执行上一个用户的更新请求 */
        if (currUpdatingUser != null) {
            String errorInfo = "另一个用户：" + user.getName() + "正在执行更新请求";
            LogEditor.serv.info(errorInfo);

            msg.put("result", EditorConst.RESULT_FAILED);
            msg.put("desc", errorInfo);
            sendMsg(request.ctx, msg);
            return;
        }

        if (SVNManager.isWorkingCopy(EditorConfig.svn_export)) {
            currUpdatingUser = user;
            LogEditor.serv.info("开始更新服务器表格数据");
            long updateResult = SVNManager.update(EditorConfig.svn_export, SVNRevision.HEAD, SVNDepth.INFINITY);

            if (updateResult < 0) {
                String errorInfo = "更新出错！【" + SVNErrorCode.getErrorCode((int)(-updateResult)).getDescription() + "】";
                LogEditor.serv.info(errorInfo);

                msg.put("result", EditorConst.RESULT_FAILED);
                msg.put("desc", errorInfo);
            }
            else {
                msg.put("result", EditorConst.RESULT_OK);
            }

            currUpdatingUser = null;
            sendMsg(request.ctx, msg);
        }
        else {
            String errorInfo = "本地SVN目录配置出错！";
            LogEditor.serv.info(errorInfo);

            msg.put("result", EditorConst.RESULT_FAILED);
            msg.put("desc", errorInfo);
            sendMsg(request.ctx, msg);
        }
    }
}

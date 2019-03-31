package com.abc.editorserver.module.hi;

import com.abc.editorserver.config.EditorConst;
import com.abc.editorserver.manager.SVNManager;
import com.abc.editorserver.manager.UserManager;
import com.abc.editorserver.module.user.User;
import com.abc.editorserver.msg.GameActionJson;
import com.abc.editorserver.net.RequestData;
import com.alibaba.fastjson.JSONObject;

/**
 * Created by U-Demon
 * Date: 2019/3/30
 */
public class HiAction extends GameActionJson {

    @Override
    public void doAction(User user, RequestData request) {
        String name = request.msg.getString("name");
        String pwd = request.msg.getString("pwd");
        boolean ret = SVNManager.auth(name, pwd);
        JSONObject msg = new JSONObject();
        msg.put(EditorConst.RET, ret);
        // SVN验证成功
        if (ret) {
            String uid = UserManager.loginUser(name, pwd);
            msg.put(EditorConst.UID, uid);
        }
        sendMsg(request.ctx, msg);
    }

}

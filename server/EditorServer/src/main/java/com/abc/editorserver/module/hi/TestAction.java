package com.abc.editorserver.module.hi;

import com.abc.editorserver.config.EditorConst;
import com.abc.editorserver.manager.SVNManager;
import com.abc.editorserver.manager.UserManager;
import com.abc.editorserver.module.user.User;
import com.abc.editorserver.msg.GameActionJson;
import com.abc.editorserver.net.RequestData;
import com.alibaba.fastjson.JSONObject;

import java.util.Map;

/**
 * Created by U-Demon
 * Date: 2019/3/30
 */
public class TestAction extends GameActionJson {

    @Override
    public void doAction(User user, RequestData request) {
        String token = request.msg.getString(EditorConst.TOKEN);
        JSONObject msg = new JSONObject();
        // TOKEN登录。简易版直接拿UID当token
        if (token != null) {
            user = UserManager.getUser(token);
            if (user != null) {
                msg.put(EditorConst.RET, true);
                String[] Test = new String[10];
                for(int i=0;i<Test.length;i++){
                    Test[i] = "Test"+i;
                }
                msg.put(EditorConst.TOKEN, user.getUid());
                msg.put("test",Test);
                sendMsg(request.ctx, msg);
                return;
            }
            else {
                msg.put(EditorConst.RET, false);
                sendMsg(request.ctx, msg);
                return;
            }
        }
    }

}

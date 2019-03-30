package com.abc.editorserver.module.hi;

import com.abc.editorserver.module.user.User;
import com.abc.editorserver.msg.GameActionJson;
import com.alibaba.fastjson.JSONObject;

/**
 * Created by U-Demon
 * Date: 2019/3/30
 */
public class HiAction extends GameActionJson {

    @Override
    public void doAction(User user, JSONObject request) {
        System.out.println(request.getString("hi"));
        JSONObject msg = new JSONObject();
        msg.put("msg", "hello world!");
        sendMsg(user, msg);
    }

}

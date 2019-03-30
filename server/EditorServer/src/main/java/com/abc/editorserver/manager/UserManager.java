package com.abc.editorserver.manager;

import com.abc.editorserver.module.user.User;
import io.netty.channel.ChannelHandlerContext;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by U-Demon
 * Date: 2019/3/30
 */
public class UserManager {

    /**
     * 存放当前服务器登陆的所有用户
     */
    private static Map<Long, User> userIdMap = new HashMap<>();

    public static User getUser(long uid) {
        if (uid == 0) {
            return null;
        }
        return userIdMap.get(uid);
    }

}

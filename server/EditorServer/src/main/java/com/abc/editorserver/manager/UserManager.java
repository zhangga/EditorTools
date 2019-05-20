package com.abc.editorserver.manager;

import com.abc.editorserver.config.EditorConst;
import com.abc.editorserver.db.JedisManager;
import com.abc.editorserver.module.user.User;
import com.alibaba.fastjson.JSONObject;

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
    private static Map<String, User> userIdMap = new HashMap<>();
    private static Map<String, User> userNameMap = new HashMap<>();

    public static User getUser(String uid) {
        if (uid == null || uid.equals("")) {
            return null;
        }
        return userIdMap.get(uid);
    }

    /**
     * 用户登录
     * @param name
     * @param pwd
     * @return
     */
    public static String loginUser(String name, String pwd) {
        User user = userNameMap.get(name);
        if (user == null) {
            String json = JedisManager.getKey(name);
            // 创建用户
            if (json == null) {
                user = new User();
                user.setUid(EditorConst.USER_ID_PREFIX + getUserId());
                user.setName(name);
                user.setPwd(pwd);
                JedisManager.setKey(user.getName(), JSONObject.toJSONString(user));
                userNameMap.put(user.getName(), user);
                userIdMap.put(user.getUid(), user);
            }
            else {
                user = JSONObject.parseObject(json, User.class);
                userNameMap.put(user.getName(), user);
                userIdMap.put(user.getUid(), user);
            }
        }
        return user.getUid();
    }

    /**
     * 获取当前在线用户量
     * @return
     */
    public static long getActiveUserCount() {
        return userIdMap.size();
    }

    public static long getUserId() {
        long uid = JedisManager.incr(EditorConst.USER_ID_MAX);
        return uid;
    }

}

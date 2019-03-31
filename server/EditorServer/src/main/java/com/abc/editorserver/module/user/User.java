package com.abc.editorserver.module.user;

import com.alibaba.fastjson.annotation.JSONField;
import io.netty.channel.ChannelHandlerContext;

/**
 * 用户类
 * Created by U-Demon
 * Date: 2019/3/30
 */
public class User {

    // 用户ID
    private String uid;

    // 用户名
    private String name;

    // 密码
    private String pwd;

    @JSONField(serialize=false)
    private ChannelHandlerContext ctx;

    public User() {
    }

    public User(ChannelHandlerContext ctx) {
        this.ctx = ctx;
    }

    public void setCtx(ChannelHandlerContext ctx) {
        this.ctx = ctx;
    }

    public ChannelHandlerContext getCtx() {
        return this.ctx;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

}

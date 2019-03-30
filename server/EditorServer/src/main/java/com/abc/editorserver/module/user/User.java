package com.abc.editorserver.module.user;

import io.netty.channel.ChannelHandlerContext;

/**
 * 用户类
 * Created by U-Demon
 * Date: 2019/3/30
 */
public class User {

    private ChannelHandlerContext ctx;

    public User(ChannelHandlerContext ctx) {
        this.ctx = ctx;
    }

    public void setCtx(ChannelHandlerContext ctx) {
        this.ctx = ctx;
    }

    public ChannelHandlerContext getCtx() {
        return this.ctx;
    }

}

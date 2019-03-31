package com.abc.editorserver.net;

import com.alibaba.fastjson.JSONObject;
import io.netty.channel.ChannelHandlerContext;

/**
 * Created by U-Demon
 * Date: 2018/12/29
 */
public class RequestData {

    public ChannelHandlerContext ctx;

    public String uid;

    public JSONObject msg;

    public RequestData(ChannelHandlerContext ctx, String uid, JSONObject msg) {
        this.ctx = ctx;
        this.uid = uid;
        this.msg = msg;
    }

}

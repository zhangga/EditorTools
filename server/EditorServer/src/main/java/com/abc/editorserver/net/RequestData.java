package com.abc.editorserver.net;

import com.alibaba.fastjson.JSONObject;
import io.netty.channel.ChannelHandlerContext;

/**
 * Created by U-Demon
 * Date: 2018/12/29
 */
public class RequestData {

    public ChannelHandlerContext ctx;

    public long uid;

    public JSONObject param;

    public RequestData(ChannelHandlerContext ctx, long uid, JSONObject param) {
        this.ctx = ctx;
        this.uid = uid;
        this.param = param;
    }

    public JSONObject getParam() {
        return param;
    }

}

package com.abc.editorserver.msg;

import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

import java.io.UnsupportedEncodingException;

import com.abc.editorserver.module.user.User;
import com.abc.editorserver.support.LogEditor;
import com.alibaba.fastjson.JSONObject;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;

/**
 * JSON数据的处理
 *
 * @author U-Demon
 * @date 2018年5月26日 下午1:21:31
 */
public abstract class GameActionJson {

	/**
	 * 指令编号
	 */
	protected int cmd;
	
	public void action(User user, JSONObject request) {
		try {
			LogEditor.msg.info("接收消息：{}", request);
			doAction(user, request);
		} catch (Exception e) {
			LogEditor.serv.error("处理消息时发生异常：msgId = {}, exception = {}", cmd, e);
		}
	}
	
	/**
	 * 具体子类实现逻辑
	 * @param user
	 * @param request
	 */
	public abstract void doAction(User user, JSONObject request);
	
	/**
	 * 发送消息
	 * @param user
	 * @param msg
	 */
	public void sendMsg(User user, JSONObject msg) {
		sendMsg(cmd, user, msg);
	}
	
	/**
	 * 发送消息
	 * @param cmd
	 * @param user
	 * @param msg
	 */
	public static void sendMsg(int cmd, User user, JSONObject msg) {
		msg.put("cmd", cmd);
		sendMsg0(user.getCtx().channel(), msg.toJSONString());
	}
	
	/***
     * 发送消息
     * @param channel
     * @param msg
     */
    public static void sendMsg0(Channel channel, String msg) {
    	LogEditor.msg.info("回复消息：" + msg);
        ByteBuf buf = null;
        try {
            buf = Unpooled.wrappedBuffer(msg.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            LogEditor.msg.error("发送消息: " + msg + ", 转码异常: ", e);
            return;
        }

        // 构建response
        FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, OK, buf);

        // 写入HTTP头
        response.headers().set(HttpHeaderNames.CONTENT_TYPE, "application/json; charset=UTF-8");
        // keepAlive需要设置'Content-Length' HTTP头
        response.headers().set(HttpHeaderNames.CONTENT_LENGTH, response.content().readableBytes());
        response.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);

        //允许跨域访问
        response.headers().set(HttpHeaderNames.ACCESS_CONTROL_ALLOW_ORIGIN, "*");
        response.headers().set(HttpHeaderNames.ACCESS_CONTROL_ALLOW_HEADERS, "Origin, X-Requested-With, Content-Type, Accept");
        response.headers().set(HttpHeaderNames.ACCESS_CONTROL_ALLOW_METHODS, "GET, POST, PUT, DELETE");

        // 返回请求结果
        if (channel != null && channel.isActive() && channel.isWritable()) {
            channel.write(response);
            channel.flush();
        }
        else {
        	LogEditor.msg.error("HTTP写入返回值失败, msg: " + msg);
        }
    }

	public int getCmd() {
		return cmd;
	}

	public void setCmd(int cmd) {
		this.cmd = cmd;
	}
	
}

package com.abc.editorserver.net;

import com.abc.editorserver.config.EditorConst;
import com.abc.editorserver.support.LogEditor;
import com.alibaba.fastjson.JSONObject;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.multipart.*;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshaker;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshakerFactory;
import io.netty.util.CharsetUtil;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

/**
 * HTTP协议处理Handler
 *
 * @Author U-Demon
 * @Date 2018/12/16 22:06
 */
public class HttpServerHandler extends SimpleChannelInboundHandler<Object> {

    private WebSocketServerHandshaker handshaker;

    /** 数据工厂类 */
    public static final HttpDataFactory factory = new DefaultHttpDataFactory(DefaultHttpDataFactory.MINSIZE);

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        // 传统的HTTP接入
        if (msg instanceof FullHttpRequest) {
            handleHttpRequest(ctx, (FullHttpRequest) msg);
        }
        // WebSocket接入
        else if (msg instanceof WebSocketFrame) {
            handleWebSocketFrame(ctx, (WebSocketFrame) msg);
        }
    }

    /**
     * 处理HTTP请求
     * @param ctx
     * @param req
     * @throws Exception
     */
    private void handleHttpRequest(ChannelHandlerContext ctx, FullHttpRequest req) throws Exception {
        // 如果HTTP解码失败，返回HHTP异常
        if (!req.decoderResult().isSuccess()) {
            sendHttpResponse(ctx, req, new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.BAD_REQUEST));
            return;
        }

        String upgrade = req.headers().get("Upgrade");
        // HTTP请求
        if (upgrade == null) {
            initHttpRequest(ctx, req);
            return;
        }

        // HTTP WebSocket
        if ("websocket".equals(upgrade)) {
            // 构造握手响应返回，本机测试
            WebSocketServerHandshakerFactory wsFactory = new WebSocketServerHandshakerFactory(
                    "ws://"+req.headers().get(HttpHeaderNames.HOST)+req.uri(), null, false);
            handshaker = wsFactory.newHandshaker(req);
            if (handshaker == null) {
                WebSocketServerHandshakerFactory.sendUnsupportedVersionResponse(ctx.channel());
            } else {
                handshaker.handshake(ctx.channel(), req);
            }
            return;
        }
    }

    /**
     * 处理WebSocket请求
     * @param ctx
     * @param frame
     */
    private void handleWebSocketFrame(ChannelHandlerContext ctx, WebSocketFrame frame) {

    }

    /**
     * 解析HTTP请求
     * @param ctx
     * @param req
     */
    private void initHttpRequest(ChannelHandlerContext ctx, FullHttpRequest req) {
        JSONObject param = req.method() == HttpMethod.GET ? initHttpGet(req) : initHttpPost(req);
        if (param == null) {
            LogEditor.msg.error("解析http请求错误。url: {}", req.uri());
            return;
        }
        String uid = param.getString(EditorConst.TOKEN);
        if (uid == null) {
//            // 随机一个id，用于分配线程
//            uid = -new Random().nextLong();
            // 保证注册用户在同一个线程里
            uid = "";
        }
        // 通过uid分配线程
//        GameLogicExecutor.exec.submit(uid, new Runnable() {
//            @Override
//            public void run() {
//                GameAction.execute(ctx.channel(), param);
//            }
//        });
        // 线程处理消息
        // 编辑器用一个线程来处理消息，安全。
        RequestData requestData = new RequestData(ctx, uid, param);
        MsgManager.submit(requestData);
    }

    /**
     * 解析get
     * @param request
     */
    private JSONObject initHttpGet(FullHttpRequest request) {
        JSONObject ret = new JSONObject();
        QueryStringDecoder query = new QueryStringDecoder(request.uri());
        for (var p : query.parameters().entrySet()) {
            // 如果有多个值，那么只有第一个值生效
            ret.put(p.getKey(), p.getValue().get(0));
        }
        return ret;
    }

    /**
     * 解析post
     * @param request
     */
    private JSONObject initHttpPost(FullHttpRequest request) {
        JSONObject ret = null;
        String contentType = request.headers().get("Content-Type");
        if (contentType == null || contentType.contains("json")) {
            ByteBuf content = request.content();
            byte[] reqContent = new byte[content.readableBytes()];
            content.readBytes(reqContent);
            String strContent = "{}";
            try {
                strContent = URLDecoder.decode(new String(reqContent, "UTF-8"), "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            ret = JSONObject.parseObject(strContent);
        }
        else if (contentType.contains("x-www-form-urlencoded")) {
            ret = new JSONObject();
            HttpPostRequestDecoder decoder = new HttpPostRequestDecoder(factory, request);
            var postData = decoder.getBodyHttpDatas();

            for (InterfaceHttpData data : postData) {
                if (data.getHttpDataType() == InterfaceHttpData.HttpDataType.Attribute) {
                    Attribute attribute = (Attribute) data;
                    try {
                        ret.put(attribute.getName(), attribute.getValue());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return ret;
    }

    /**
     * 发送HTTP回应
     * @param ctx
     * @param req
     * @param res
     */
    private static void sendHttpResponse(ChannelHandlerContext ctx, FullHttpRequest req, FullHttpResponse res) {
        // Generate an error page if response getStatus code is not OK (200).
        if (res.status().code() != 200) {
            ByteBuf buf = Unpooled.copiedBuffer(res.status().toString(), CharsetUtil.UTF_8);
            res.content().writeBytes(buf);
            buf.release();
            HttpUtil.setContentLength(res, res.content().readableBytes());
        }

        // Send the response and close the connection if necessary.
        ChannelFuture f = ctx.channel().writeAndFlush(res);
        if (!HttpUtil.isKeepAlive(req) || res.status().code() != 200) {
            f.addListener(ChannelFutureListener.CLOSE);
        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        super.channelReadComplete(ctx);
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
        String msg = cause.getMessage();
        if (msg.equals("远程主机强迫关闭了一个现有的连接。") || msg.startsWith("io.netty.handler.ssl.NotSslRecordException")) {
            return;
        }
        cause.printStackTrace();
    }

}

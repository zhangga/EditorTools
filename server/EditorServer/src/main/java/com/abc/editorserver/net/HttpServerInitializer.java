package com.abc.editorserver.net;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.stream.ChunkedWriteHandler;

/**
 * HTTP协议
 *
 * @Author U-Demon
 * @Date 2018/12/16 22:02
 */
public class HttpServerInitializer extends ChannelInitializer<SocketChannel> {

    private final SslContext sslCtx;

    public HttpServerInitializer(SslContext sslCtx) {
        this.sslCtx = sslCtx;
    }

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        if (sslCtx != null) {
            pipeline.addLast(sslCtx.newHandler(ch.alloc()));
        }

        // HttpServerCodec：将请求和应答消息解码为HTTP消息
        pipeline.addLast(new HttpServerCodec());
        // HttpObjectAggregator：将HTTP消息的多个部分合成一条完整的HTTP消息
        pipeline.addLast(new HttpObjectAggregator(65536));
        // ChunkedWriteHandler：向客户端发送HTML5文件
        pipeline.addLast(new ChunkedWriteHandler());
        // 在管道中添加我们自己的接收数据实现方法
        pipeline.addLast(new HttpServerHandler());

//        // 也可以这样写
//        pipeline.addLast(new WebSocketServerProtocolHandler("/websocket"));
//
//        // 不同的消息分不同handler解析
//        pipeline.addLast(new TextFrameHandler());
//        pipeline.addLast(new BinaryFrameHandler());
//        pipeline.addLast(new ContinuationFrameHandler());
    }

}

package com.abc.editorserver.net;

import com.abc.editorserver.config.EditorConfig;
import com.abc.editorserver.support.LogEditor;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.WriteBufferWaterMark;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.SelfSignedCertificate;

import javax.net.ssl.SSLException;
import java.security.cert.CertificateException;

/**
 * HTTP服务
 * Created by U-Demon
 * Date: 2019/3/30
 */
public class HttpServer extends Thread {

    @Override
    public void run() {
        // Config SSL
        SslContext sslCtx = null;
        if (EditorConfig.http_ssl) {
            try {
                SelfSignedCertificate ssc = new SelfSignedCertificate();
                sslCtx = SslContextBuilder.forServer(ssc.certificate(), ssc.privateKey()).build();
            } catch (CertificateException e) {
                e.printStackTrace();
            } catch (SSLException e) {
                e.printStackTrace();
            }
        }
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.option(ChannelOption.SO_BACKLOG, 10240);
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)		//使用内存池
                    .childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)	//使用内存池
                    .childOption(ChannelOption.TCP_NODELAY, true)
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    .childOption(ChannelOption.WRITE_BUFFER_WATER_MARK, new WriteBufferWaterMark(256 * 1024, 512 * 1024))	//控制输出流量
                    .childHandler(new HttpServerInitializer(sslCtx));

            // 启动
            Channel ch = b.bind(EditorConfig.http_port).sync().channel();

            LogEditor.serv.error("服务器启动完成！--------------》FINISH《----------------");

            ch.closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

}

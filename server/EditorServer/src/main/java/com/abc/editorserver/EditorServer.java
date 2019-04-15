package com.abc.editorserver;

import com.abc.editorserver.config.EditorConfig;
import com.abc.editorserver.db.JedisManager;
import com.abc.editorserver.manager.DataManager;
import com.abc.editorserver.manager.GlobalManager;
import com.abc.editorserver.manager.SVNManager;
import com.abc.editorserver.manager.VersionManager;
import com.abc.editorserver.net.HttpServer;
import com.abc.editorserver.support.LogEditor;

/**
 * 主入口
 * Created by U-Demon
 * Date: 2019/3/30
 */
public class EditorServer {

    public static void main( String[] args ) {
        System.setProperty("logFileName", "editor_server");

        EditorServer es = new EditorServer();
        es.start();
    }

    private void start() {
        SVNManager.init();
        // 初始化数据
        DataManager.getInstance().init();
        // 初始化版本号信息
        VersionManager.getInstance().init();
        // 启动一个守护线程
        GlobalManager.init();
        // DB
        JedisManager.getInstance().test();
        // 监听网络端口
        initNet();
        // 启动日志信息
        LogEditor.serv.info("====================");
        LogEditor.serv.info("editor server started.");
        LogEditor.serv.info("Listen:" + EditorConfig.http_port);
        LogEditor.serv.info("====================");
    }

    /**
     * 初始化网络
     */
    private void initNet() {
        LogEditor.serv.info("======启动网络监听======");
        // 启动HTTP服务
        new HttpServer().start();
    }

}

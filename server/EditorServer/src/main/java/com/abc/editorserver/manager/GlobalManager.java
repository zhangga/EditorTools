package com.abc.editorserver.manager;

import com.abc.editorserver.support.LogEditor;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 全局的管理器
 * Created by U-Demon
 * Date: 2019/3/30
 */
public class GlobalManager {

    private static ScheduledThreadPoolExecutor executor = (ScheduledThreadPoolExecutor) Executors.newScheduledThreadPool(1);

    private static long lastTime = 0;

    public static void init() {
        executor.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                lastTime = System.currentTimeMillis();
                try {
                    pulse();
                }
                catch (Exception e) {
                    e.printStackTrace();
                    LogEditor.serv.error("全局定时器执行异常{}", e);
                }
            }
        }, 1, 1, TimeUnit.SECONDS);
    }

    private static void pulse() {

    }

}

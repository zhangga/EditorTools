package com.abc.editorserver.manager;

import com.abc.editorserver.config.EditorConfig;
import com.abc.editorserver.support.LogEditor;
import com.abc.editorserver.utils.Function;
import com.abc.editorserver.utils.Task;
import com.abc.editorserver.utils.Timer;
import com.alibaba.fastjson.JSONObject;
import org.tmatesoft.svn.core.SVNDepth;
import org.tmatesoft.svn.core.wc.SVNRevision;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 全局的管理器
 * Created by U-Demon
 * Date: 2019/3/30
 */
public class GlobalManager {

    private static ScheduledThreadPoolExecutor pulseExecutor = (ScheduledThreadPoolExecutor) Executors.
            newScheduledThreadPool(1);

    private static ThreadPoolExecutor taskExecutor = (ThreadPoolExecutor)Executors.
            newFixedThreadPool(10);

    private static long lastTime = 0;

    private static ConcurrentLinkedQueue<Task> taskQueue = new ConcurrentLinkedQueue<>();

    private static Timer updateTimer = new Timer();
    private static Timer commitTimer = new Timer();

    private static DataManager dataManager = DataManager.getInstance();

    public static final boolean isDevMode = true;

    public static void init() {
        updateTimer.startTimer(Timer.ONE_MINUTE);
        commitTimer.startTimer(Timer.FIFTEEN_MINUTES);

        pulseExecutor.scheduleAtFixedRate(() -> {
            lastTime = System.currentTimeMillis();
            try {
                pulse();
            }
            catch (Exception e) {
                e.printStackTrace();
                LogEditor.serv.error("全局定时器执行异常{}", e);
            }
        }, 1, 1, TimeUnit.SECONDS);
    }

    /**
     * 添加执行任务
     * @param task
     */
    public static void addTask(Task task) {
        taskQueue.add(task);
    }

    /**
     * 获取下一个待执行的任务
     * @return
     */
    private static Task getNextTask() {
        return taskQueue.poll();
    }

    /**
     * 心跳
     */
    private static void pulse() {
        // 处理任务队列
        while (!taskQueue.isEmpty()) {
            taskExecutor.execute(getNextTask());
        }

        // 定时从SVN更新
        if (updateTimer.isDue()) {
            taskQueue.add(new Task(var -> {
                SVNManager.update(EditorConfig.svn_export, SVNRevision.HEAD, SVNDepth.INFINITY);
                LogEditor.serv.info("定时执行了SVN更新");
            }, null));
        }

        // 定时将改动写入至Excel中
        dataManager.dataPersistHandler();

        // 定时向SVN提交改动
        if (!isDevMode && commitTimer.isDue()) {
            taskQueue.add(new Task(var -> {
                if (!GlobalManager.isDevMode) {
                    SVNManager.commit(true, "【任务编辑器】自动更新", EditorConfig.svn_export);
                }
            }, null));
        }
    }

}

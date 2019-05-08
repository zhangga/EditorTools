package com.abc.editorserver.manager;

import com.abc.editorserver.EditorServer;
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
    private static ConcurrentLinkedQueue<Task> taskQueue = new ConcurrentLinkedQueue<>();

    private static Timer updateTimer;
    private static Timer commitTimer;

    private static long lastTime = 0;
    private static final long commitInterval = Timer.FIFTEEN_MINUTES;
    private static final long updateInterval = Timer.ONE_MINUTE;

    private static DataManager dataManager = DataManager.getInstance();

    public static volatile boolean isDoingUpdate = false;

    private GlobalManager() {}

    /**
     * 初始化方法
     */
    public static void init() {
        // 初始化并启动SVN更新计时器
        updateTimer = new Timer(updateInterval);
        updateTimer.startTimer();

        // 初始化并启动SVN提交计时器
        commitTimer = new Timer(commitInterval);
        commitTimer.startTimer();

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
            // 重置SVN更新计时器
            updateTimer.reStartTimer();

            taskQueue.add(new Task(var -> {
                if (!isDoingUpdate) {
                    // 更新更新状态flag
                    isDoingUpdate = true;

                    SVNManager.update(EditorConfig.svn_export, SVNRevision.HEAD, SVNDepth.INFINITY);
                    LogEditor.serv.info("定时执行了SVN更新");
                }
            }, null));
        }

        // 定时将改动写入至Excel中
        dataManager.dataPersistHandler();

        // 定时向SVN提交改动
        if (!EditorServer.isDevMode && commitTimer.isDue()) {
            // 重置SVN提交计时器
            commitTimer.reStartTimer();

            taskQueue.add(new Task(var -> {
                // 如果当前正在执行更新任务，延迟提交至下一个心跳
                if (isDoingUpdate) {
                    commitTimer.triggerDue();
                } else {
                    LogEditor.serv.info("【SVN COMMIT】COMMIT前执行UPDATE");
                    isDoingUpdate = true;
                    SVNManager.update(EditorConfig.svn_export, SVNRevision.HEAD, SVNDepth.INFINITY);

                    LogEditor.serv.info("【SVN COMMIT】开始COMMIT");
//                    SVNManager.commit(true, "【任务编辑器】自动更新", EditorConfig.svn_export);
                    LogEditor.serv.info("【SVN COMMIT】COMMIT完成");
                }
            }, null));
        }
    }

    public static Timer getCommitTimer() {
        if (commitTimer == null) {
            commitTimer = new Timer(commitInterval);
        }
        return commitTimer;
    }

    public static Timer getUpdateTimer() {
        if (updateTimer == null) {
            updateTimer = new Timer(updateInterval);
        }
        return updateTimer;
    }

}

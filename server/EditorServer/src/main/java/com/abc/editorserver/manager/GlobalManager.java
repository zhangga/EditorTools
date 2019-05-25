package com.abc.editorserver.manager;

import com.abc.editorserver.config.EditorConfig;
import com.abc.editorserver.support.LogEditor;
import com.abc.editorserver.utils.Task;
import com.abc.editorserver.utils.Timer;
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
    /** 心跳线程 */
    private static ScheduledThreadPoolExecutor pulseExecutor = (ScheduledThreadPoolExecutor) Executors.
            newScheduledThreadPool(1);

    /** 任务执行线程 */
    private static ThreadPoolExecutor taskExecutor = (ThreadPoolExecutor)Executors.
            newFixedThreadPool(10);

    /** 任务队列 */
    private static ConcurrentLinkedQueue<Task> taskQueue = new ConcurrentLinkedQueue<>();

    /** 保证队列中最多只有一个待执行COMMIT任务 */
    public static volatile boolean hasPendingCommitTask = false;

    /** 保证同时只有一个线程在执行UPDATE任务 */
    public static volatile boolean isDoingUpdate = false;

    /** 更新间隔常量 */
    private static final long updateInterval = Timer.HALF_MINUTE;

    /** 更新计时器 */
    private static Timer updateTimer;

    private static DataManager dataManager = DataManager.getInstance();
    private GlobalManager() {}

    /**
     * 初始化方法
     */
    public static void init() {
        // 初始化并启动SVN更新计时器
        updateTimer = new Timer(updateInterval);
        updateTimer.startTimer();

        pulseExecutor.scheduleAtFixedRate(() -> {
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
        if (false && updateTimer.isDue()) {
            // 重置SVN更新计时器
            updateTimer.reStartTimer();

            taskQueue.add(new Task(var -> {
                if (!isDoingUpdate) {
                    // 更新更新状态flag
                    isDoingUpdate = true;

                    // 更新前将未写入的改动持久化至Excel
                    dataManager.dataPersistHandler(false);

                    // 执行SVN更新
                    SVNManager.update(EditorConfig.svn_export, SVNRevision.HEAD, SVNDepth.INFINITY);

                    // 将更新后的Excel数据刷新至数据库中
                    DataManager.getInstance().reloadDataAfterUpdate();

                    LogEditor.serv.info("定时执行了SVN更新");
                }
            }, null));
        }

        // 定时将改动写入至Excel中
        dataManager.dataPersistHandler(true);
    }
}

package com.abc.editorserver.utils;

import com.abc.editorserver.support.LogEditor;

/**
 * Timer
 * Created by Marco
 * Date: 2019/4/15 20:00
 */
public class Timer {

    private long interval;
    private long nextDue = 0L;
    private long currTime = 0L;
    private boolean isRunning = false;
    private boolean hasStarted = false;

    public static final long HALF_MINUTE = 30 * 1000;
    public static final long ONE_MINUTE = 60 * 1000;
    public static final long FIVE_MINUTES = 5 * 60 * 1000;
    public static final long FIFTEEN_MINUTES = 15 * 60 * 1000;

    public Timer(long interval) {
        this.interval = interval;
    }

    /**
     * 配置并启动定时器
     * @param interval
     */
    public void startTimer(long interval) {
        isRunning = true;
        hasStarted = true;
        currTime = System.currentTimeMillis();
        this.interval = interval;
        nextDue = currTime + interval;
    }

    /**
     * 启动定时器
     */
    public void startTimer() {
        currTime = System.currentTimeMillis();

        if (interval == 0L) {
            LogEditor.serv.info("定时器时间间隔为0，无法启动！");
        }
        else {
            hasStarted = true;
            isRunning = true;
            nextDue = currTime + this.interval;
        }
    }

    /**
     * 重启定时器
     */
    public void reStartTimer() {
        currTime = System.currentTimeMillis();

        if (interval == 0L) {
            LogEditor.serv.info("定时器时间间隔为0，无法重新启动！");
        }
        else {
            hasStarted = true;
            isRunning = true;
            nextDue = currTime + interval;
        }
    }

    /**
     * 检测定时器是否被触发
     * @return
     */
    public boolean isDue() {
        currTime = System.currentTimeMillis();

        if (!isRunning || !hasStarted) {
            return false;
        }
        else {
            return currTime >= nextDue;
        }
    }

    /**
     * 检测定时器运行状态
     * @return
     */
    public boolean isRunning() {
        return isRunning;
    }

    /**
     * 停止定时器
     */
    public void stopTimer() {
        isRunning = false;
    }

    /**
     * 强制触发定时器超时
     */
    public void triggerDue() {
        isRunning = true;
        hasStarted = true;
        currTime = System.currentTimeMillis();
        nextDue = currTime;
    }

    /**
     * Getter & Setter
     */
    public void setInterval(long interval) {
        this.interval = interval;
    }

    public long getInterval() {
        return this.interval;
    }
}

package com.abc.editorserver.utils;

/**
 * Timer
 * Created by Marco
 * Date: 2019/4/15 20:00
 */
public class Timer {
    private long interval = 0L;
    private long nextDue = 0L;
    private long currTime = 0L;
    private boolean isRunning = false;

    public Timer() {}

    public static final long ONE_MINUTE = 60 * 1000;
    public static final long FIVE_MINUTES = 5 * 60 * 1000;
    public static final long FIFTEEN_MINUTES = 15 * 60 * 1000;

    /**
     * 配置并启动定时器
     * @param interval
     */
    public void startTimer(long interval) {
        isRunning = true;
        currTime = System.currentTimeMillis();
        this.interval = interval;
        nextDue = currTime + interval;
    }

    /**
     * 重启定时器
     */
    public void reStartTimer() {
        if (interval == 0L) {
            return;
        }
        else {
            isRunning = true;
            currTime = System.currentTimeMillis();
            nextDue = currTime + interval;
        }
    }

    /**
     * 检测定时器是否被触发
     * @return
     */
    public boolean isDue() {
        if (!isRunning) {
            return false;
        }
        else {
            currTime = System.currentTimeMillis();

            if (nextDue <= currTime) {
                while (nextDue <= currTime) {
                    nextDue += interval;
                }
                return true;
            }
            else {
                return false;
            }
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
    public void stop() {
        isRunning = false;
    }
}

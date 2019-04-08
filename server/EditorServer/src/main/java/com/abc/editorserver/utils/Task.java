package com.abc.editorserver.utils;

import com.alibaba.fastjson.JSONObject;

/**
 * 线程池执行任务类
 * Created by Marco
 * Date: 2019/4/8 20:18
 */

public class Task implements Runnable {

    public Function<JSONObject> taskFunc;
    public JSONObject taskParams;

    public Task(Function<JSONObject> func, JSONObject params) {
        taskFunc = func;
        taskParams = params;
    }

    @Override
    public void run() {
        taskFunc.apply(taskParams);
    }
}

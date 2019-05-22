package com.abc.editorserver.module.excel;

import com.abc.editorserver.manager.DataManager;
import com.abc.editorserver.manager.GlobalManager;
import com.abc.editorserver.module.user.User;
import com.abc.editorserver.msg.GameActionJson;
import com.abc.editorserver.net.RequestData;
import com.abc.editorserver.support.LogEditor;
import com.abc.editorserver.utils.Task;
import com.alibaba.fastjson.JSONObject;

/**
 * SubmitToSVNAction
 * Created by Marco
 * Date: 2019/4/23 21:40
 */

public class SubmitToSVNAction extends GameActionJson {
    @Override
    public void doAction(User user, RequestData request) {
        LogEditor.serv.info("开始写入！");
        LogEditor.serv.info("强制将数据更新写入Excel及SVN...");
        DataManager dataManager = DataManager.getInstance();

        // 构建任务参数
        JSONObject params = new JSONObject();
        params.put("RequestContext", request);
        params.put("MsgCaller", this);
        params.put("User", user);

        // 将持久化延迟到下一帧执行，避免并发操作问题
        GlobalManager.addTask(new Task(dataManager::promptDataPersist, params));
    }
}

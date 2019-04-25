package com.abc.editorserver.module.excel;

import com.abc.editorserver.manager.DataManager;
import com.abc.editorserver.module.user.User;
import com.abc.editorserver.msg.GameActionJson;
import com.abc.editorserver.net.RequestData;
import com.abc.editorserver.support.LogEditor;

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
        dataManager.promptDataPersist();
    }
}

package com.abc.editorserver.net;

import com.abc.editorserver.config.EditorConst;
import com.abc.editorserver.manager.UserManager;
import com.abc.editorserver.module.user.User;
import com.abc.editorserver.msg.GameActionJson;
import com.abc.editorserver.msg.GameActionsJson;
import com.abc.editorserver.support.LogEditor;

import java.util.concurrent.*;

/**
 * 消息管理器
 * Created by U-Demon
 * Date: 2019/3/30
 */
public class MsgManager {

    private static final SerialKeyExecutor<Long> protocolExec;

    static{
        protocolExec = new SerialKeyExecutor<>(
                new ThreadPoolExecutor(4, 10, 15, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>()));
    }

    /**
     * 提交一个客户端请求，执行
     * @param req
     */
    public static void submit(final RequestData req) {
        protocolExec.submit(req.uid , new Runnable() {

            @Override
            public void run() {
                try {
                    // 合法性检测
                    if (!doCheck(req)) {
                        LogEditor.msg.error("消息未通过合法性检测!!!");
                        return;
                    }

                    // 消息分发
                    doDispatch(req);
                } catch (Exception e) {
                    LogEditor.serv.error("处理消息发生异常：", e);
                }
            }
        });
    }

    /**
     * 消息分发
     * @param req
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
    private static void doDispatch(RequestData req) throws InstantiationException, IllegalAccessException {
        // 客户端上送的消息
        int cmd = req.param.getInteger(EditorConst.CMD);

        GameActionsJson type = GameActionsJson.getType(cmd);
        if (type == null) {
            LogEditor.msg.error("接收到不存在的消息号：msgId = " + cmd);
            return;
        }

        // 处理消息的Action
        GameActionJson action = type.getAction();
        if (action == null) {
            return;
        }

        // 解析消息对象
//		IMsgReq request = type.getReqClazz().newInstance();
//		request.readFrom(req);

        // User对象
        User user = UserManager.getUser(req.uid);
        if (user == null) {
            user = new User(req.ctx);
        }
        else {
            user.setCtx(req.ctx);
        }

        // 执行
        action.action(user, req.getParam());
    }

    /**
     * 合法性检测
     * @param req
     * @return
     */
    private static boolean doCheck(RequestData req) {
        return true;
    }

}

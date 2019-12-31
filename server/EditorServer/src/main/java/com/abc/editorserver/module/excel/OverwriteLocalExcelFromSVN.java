package com.abc.editorserver.module.excel;

import com.abc.editorserver.config.EditorConfig;
import com.abc.editorserver.manager.DataManager;
import com.abc.editorserver.manager.ExcelManager;
import com.abc.editorserver.manager.SVNManager;
import com.abc.editorserver.module.JSONModule.ExcelConfig;
import com.abc.editorserver.module.user.User;
import com.abc.editorserver.msg.GameActionJson;
import com.abc.editorserver.net.RequestData;
import org.tmatesoft.svn.core.SVNDepth;
import org.tmatesoft.svn.core.wc.SVNRevision;

import java.io.File;

/**
 * OverwriteLocalExcelFromSVN
 * Created by Marco
 * Date: 2019/5/24 11:50
 */
public class OverwriteLocalExcelFromSVN extends GameActionJson {
    @Override
    public void doAction(User user, RequestData request) {
        String tableName = request.msg.getString("table");

        // 执行覆写操作前锁数据库
        User defaultUser = new User();
        defaultUser.setUid("DEFAULT_USER");
        DataManager.getInstance().tryLockingTable(tableName, defaultUser);

        StringBuilder excelPathPrefix = new StringBuilder(EditorConfig.svn_export).append('/');

        // 删除本地Excel文件
        ExcelConfig config = ExcelManager.getInstance().getConfig(tableName);
        File excelFile = new File(excelPathPrefix.append(config.getExcel()).toString());
        excelFile.delete();

        // 执行SVN更新，恢复Excel文件
        SVNManager.update(EditorConfig.svn_export, SVNRevision.HEAD, SVNDepth.INFINITY);

        // 将Excel内容写入Redis中
        DataManager.getInstance().reloadDataAfterUpdate();

        // 解锁数据库
        DataManager.getInstance().tryUnlockingTable(tableName, defaultUser);
    }
}

package com.abc.editorserver.manager;

import com.abc.editorserver.config.EditorConfig;
import com.abc.editorserver.module.user.User;
import com.abc.editorserver.support.LogEditor;
import org.tmatesoft.svn.core.*;
import org.tmatesoft.svn.core.auth.ISVNAuthenticationManager;
import org.tmatesoft.svn.core.internal.io.svn.SVNRepositoryFactoryImpl;
import org.tmatesoft.svn.core.internal.wc.DefaultSVNOptions;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.io.SVNRepositoryFactory;
import org.tmatesoft.svn.core.wc.SVNClientManager;
import org.tmatesoft.svn.core.wc.SVNRevision;
import org.tmatesoft.svn.core.wc.SVNUpdateClient;
import org.tmatesoft.svn.core.wc.SVNWCUtil;

import java.io.File;
import java.util.Date;

/**
 * SVN工具
 * Created by U-Demon
 * Date: 2019/3/31
 */
public class SVNManager {

    private SVNManager() {}

    private static SVNClientManager client;
    private static SVNURL svnurl;
    private static User currUpdatingUser;

    /**
     * 初始化SVN
     * @return
     */
    public static void init() {
        // 初始化版本库
        SVNRepositoryFactoryImpl.setup();
        // 创建库连接
        SVNRepository repository = null;
        try {
            svnurl = SVNURL.parseURIEncoded(EditorConfig.svn_root);
            repository = SVNRepositoryFactory.create(svnurl);
        } catch (SVNException e) {
            LogEditor.config.error("初始化SVN连接失败");
            client = null;
            return;
        }
        // 身份验证
        ISVNAuthenticationManager authMgr = SVNWCUtil.createDefaultAuthenticationManager(
                EditorConfig.svn_name, EditorConfig.svn_pwd.toCharArray());
        repository.setAuthenticationManager(authMgr);

        DefaultSVNOptions options = SVNWCUtil.createDefaultOptions(true);
        client = SVNClientManager.newInstance(options, authMgr);

        // 检出svn或者更新
        if (isWorkingCopy(EditorConfig.svn_export)) {
            update(EditorConfig.svn_export, SVNRevision.HEAD, SVNDepth.INFINITY);
        }
        else {
            checkout(EditorConfig.svn_export, SVNRevision.HEAD, SVNDepth.INFINITY);
        }
    }

    private static SVNClientManager getClient() {
        if (client == null) {
            init();
        }
        return client;
    }

    /**
     * 获取当前正在进行更新操作的用户
     * @return
     */
    public static User getCurrUpdatingUser() {
        return currUpdatingUser;
    }

    /**
     * 设置当前正在进行更新操作的用户
     * @param user
     */
    public static void setCurrUpdatingUser(User user) {
        currUpdatingUser = user;
    }

    /**
     * 验证用户名和密码
     * @param username
     * @param password
     * @return
     */
    public static boolean auth(String username, String password) {
        SVNURL url = null;
        try {
            url = SVNURL.parseURIEncoded(EditorConfig.svn_root);
        } catch (SVNException e) {
            LogEditor.config.error("创建svnurl出错：{}", e);
            return false;
        }
        return isURLExist(url, username, password);
    }

    /**
     * 确定一个URL在SVN上是否存在，可以用来验证SVN用户名和密码
     * @param url
     * @param username
     * @param password
     * @return
     */
    public static boolean isURLExist(SVNURL url, String username, String password) {
        try {
            SVNRepository svnRepository = SVNRepositoryFactory.create(url);
            ISVNAuthenticationManager authMgr = SVNWCUtil.createDefaultAuthenticationManager(username, password.toCharArray());
            svnRepository.setAuthenticationManager(authMgr);
            SVNNodeKind nodeKind = svnRepository.checkPath("", -1); //遍历SVN,获取结点。
            return nodeKind == SVNNodeKind.NONE ? false : true;
        } catch (SVNException e) {
            return false;
        }
    }

    /**
     * 确定path是否是一个工作空间
     * @param path
     * @return
     */
    public static boolean isWorkingCopy(String path) {
        File file = new File(path);
        if (!file.exists()) {
            return false;
        }
        try {
            if (null == SVNWCUtil.getWorkingCopyRoot(file, false)) {
                return false;
            }
        } catch (SVNException e) {
            LogEditor.config.error("{}", e);
        }
        return true;
    }

    /**
     * 检出
     * @param revision
     * @param destPath
     * @param depth
     * @return
     */
    public static long checkout(String destPath, SVNRevision revision, SVNDepth depth) {
        SVNUpdateClient updateClient = getClient().getUpdateClient();
        updateClient.setIgnoreExternals(false);
        try {
            File dest = new File(destPath);
            LogEditor.serv.info("正在从SVN检出项目......");
            return updateClient.doCheckout(svnurl, dest, revision, revision, depth, false);
        } catch(SVNException e) {
            LogEditor.config.error("检出SNV项目出错。{}", e);
        }
        return 0;
    }

    /**
     * 更新
     * @param wcPath
     * @param updateToRevision
     * @param depth
     * @return
     */
    public static long update(String wcPath, SVNRevision updateToRevision, SVNDepth depth) {
        SVNUpdateClient updateClient = getClient().getUpdateClient();
        updateClient.setIgnoreExternals(false);
        try {
            File wc = new File(wcPath);
            LogEditor.serv.info("正在从SVN更新项目......");
            return updateClient.doUpdate(wc, updateToRevision, depth, false, false);
        } catch (SVNException e) {
            LogEditor.config.error("更新SNV项目出错。{}", e);
            return -e.getErrorMessage().getErrorCode().getCode();
        } finally {
            GlobalManager.isDoingUpdate = false;
        }
    }

    /**
     * 提交
     * @param keepLocks
     * @param commitMessage
     * @param wcPath
     * @return
     */
    public static SVNCommitInfo commit(boolean keepLocks, String commitMessage, String... wcPath) {
        if (wcPath.length == 0) {
            return new SVNCommitInfo(-1, "", new Date());
        }
        try {
            File[] files = new File[wcPath.length];
            for (int i = 0; i < wcPath.length; ++i) {
                files[i] = new File(wcPath[i]);
            }
//            LogEditor.serv.info("正在向SVN提交......");
            return getClient().getCommitClient().doCommit(files, keepLocks,
                    commitMessage, null, null, false, false, SVNDepth.INFINITY);
        } catch (SVNException e) {
            LogEditor.config.error("提交SNV项目出错。{}", e);
            return new SVNCommitInfo(-1, "", new Date(), e.getErrorMessage());
        }
    }

    /**
     * 新增项目
     * @param wcPath
     */
    public static void addEntry(String... wcPath) {
        if (wcPath.length == 0) {
            return;
        }
        try {
            File[] files = new File[wcPath.length];
            for (int i = 0; i < wcPath.length; ++i) {
                files[i] = new File(wcPath[i]);
            }
            LogEditor.serv.info("正在向SVN新增项目......");
            getClient().getWCClient().doAdd(files, true, false, false,
                    SVNDepth.INFINITY, false, false, true);
        } catch (SVNException e) {
            LogEditor.config.error("新增SNV项目出错。{}", e);
        }
    }

    /**
     * 删除项目
     * @param wcPath
     */
    public static void delEntry(String wcPath) {
        try {
            File file = new File(wcPath);
            LogEditor.serv.info("正在向SVN删除项目......");
            getClient().getWCClient().doDelete(file, true, true);
        } catch (SVNException e) {
            LogEditor.config.error("删除SNV项目出错。{}", e);
        }
    }

    /**
     * lock
     * @param stealLock
     * @param lockMessage
     * @param wcPath
     */
    public static void lock(boolean stealLock, String lockMessage, String... wcPath) {
        if (wcPath.length == 0) {
            return;
        }
        try {
            File[] files = new File[wcPath.length];
            for (int i = 0; i < wcPath.length; ++i) {
                files[i] = new File(wcPath[i]);
            }
            LogEditor.serv.info("正在向SVN LOCK项目......");
            getClient().getWCClient().doLock(files, stealLock, lockMessage);
        } catch (SVNException e) {
            LogEditor.config.error("LOCK SNV项目出错。{}", e);
        }
    }
    /**
     * unlock
     * @param wcPath
     */
    public static void unlock(String... wcPath) {
        if (wcPath.length == 0) {
            return;
        }
        try {
            File[] files = new File[wcPath.length];
            for (int i = 0; i < wcPath.length; ++i) {
                files[i] = new File(wcPath[i]);
            }
            LogEditor.serv.info("正在向SVN UNLOCK项目......");
            getClient().getWCClient().doUnlock(files, true);
        } catch (SVNException e) {
            LogEditor.config.error("UNLOCK SNV项目出错。{}", e);
        }
    }

}

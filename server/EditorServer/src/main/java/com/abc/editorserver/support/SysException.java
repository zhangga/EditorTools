package com.abc.editorserver.support;

import com.abc.editorserver.utils.EditorUtils;

/**
 * 当程序出现系统级错误，不希望用户看到出错信息的时候抛出此异常
 * Created by U-Demon
 * Date: 2019/3/30
 */
public class SysException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public SysException(String str) {
        super(str);
    }

    public SysException(Throwable e) {
        super(e);
    }

    public SysException(Throwable e, String str) {
        super(str, e);
    }

    public SysException(String str, Object... params) {
        super(EditorUtils.createStr(str, params));
    }

    public SysException(Throwable e, String str, Object... params) {
        super(EditorUtils.createStr(str, params), e);
    }

}

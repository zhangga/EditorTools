package com.abc.editorserver.utils;

/**
 * 方法引用接口
 * Created by Marco
 * Date: 2019/4/8 20:18
 */

@FunctionalInterface
public interface Function<T> {
    void apply(T var);
}

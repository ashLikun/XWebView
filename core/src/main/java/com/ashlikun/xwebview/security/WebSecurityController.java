package com.ashlikun.xwebview.security;

/**
 * @author　　: 李坤
 * 创建时间: 2018/9/21 17:05
 * 邮箱　　：496546144@qq.com
 * <p>
 * 功能介绍：安全检测
 */

public interface WebSecurityController<T> {

    void check(T t);

}

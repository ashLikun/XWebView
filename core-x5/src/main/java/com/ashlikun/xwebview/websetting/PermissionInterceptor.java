package com.ashlikun.xwebview.websetting;

/**
 * @author　　: 李坤
 * 创建时间: 2018/9/21 17:07
 * 邮箱　　：496546144@qq.com
 * <p>
 * 功能介绍：权限拦截
 */

public interface PermissionInterceptor {

    boolean intercept(String url, String[] permissions, String action);

}

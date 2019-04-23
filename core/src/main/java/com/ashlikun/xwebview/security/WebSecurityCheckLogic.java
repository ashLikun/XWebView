package com.ashlikun.xwebview.security;

import androidx.collection.ArrayMap;
import android.webkit.WebView;

import com.ashlikun.xwebview.XWeb;


/**
 * @author　　: 李坤
 * 创建时间: 2018/9/21 17:03
 * 邮箱　　：496546144@qq.com
 * <p>
 * 功能介绍：安全相关设置
 */

public interface WebSecurityCheckLogic {
    /**
     * 安全设置
     *
     * @param view
     */
    void dealHoneyComb(WebView view);

    void dealJsInterface(ArrayMap<String, Object> objects, XWeb.SecurityType securityType);
}

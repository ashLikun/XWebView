package com.ashlikun.xwebview.websetting;

import com.tencent.smtt.sdk.WebView;

/**
 * @author　　: 李坤
 * 创建时间: 2018/9/21 13:23
 * 邮箱　　：496546144@qq.com
 * <p>
 * 功能介绍：WebSettings 接口规范
 */

public interface IWebSettings<T extends com.tencent.smtt.sdk.WebSettings> {

    IWebSettings toSetting(WebView webView);

    T getWebSettings();

}

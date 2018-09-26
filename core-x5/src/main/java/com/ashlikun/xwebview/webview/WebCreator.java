package com.ashlikun.xwebview.webview;

import android.widget.FrameLayout;

import com.ashlikun.xwebview.indicator.IWebIndicator;
import com.tencent.smtt.sdk.WebView;

/**
 * @author　　: 李坤
 * 创建时间: 2018/9/21 15:51
 * 邮箱　　：496546144@qq.com
 * <p>
 * 功能介绍：webview创建
 */

public interface WebCreator extends IWebIndicator {
    WebCreator create();

    WebView getWebView();

    FrameLayout getWebParentLayout();
}

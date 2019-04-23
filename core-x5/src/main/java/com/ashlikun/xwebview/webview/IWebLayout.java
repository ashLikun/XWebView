package com.ashlikun.xwebview.webview;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.ViewGroup;
import com.tencent.smtt.sdk.WebView;

/**
 * @author　　: 李坤
 * 创建时间: 2018/9/21 18:03
 * 邮箱　　：496546144@qq.com
 * 
 * 功能介绍：
 */

public interface IWebLayout<T extends WebView,V extends ViewGroup> {

    /**
     *
     * @return WebView 的父控件
     */
    @NonNull V getLayout();

    /**
     *
     * @return 返回 WebView  或 WebView 的子View ，返回null Web 内部会创建适当 WebView
     */
    @Nullable T getWebView();
}

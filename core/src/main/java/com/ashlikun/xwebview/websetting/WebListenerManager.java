package com.ashlikun.xwebview.websetting;

import android.webkit.DownloadListener;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * @author　　: 李坤
 * 创建时间: 2018/9/21 13:24
 * 邮箱　　：496546144@qq.com
 * <p>
 * 功能介绍：webView控件的设置监听管理器
 */

public interface WebListenerManager {
    /**
     * 设置WebChromeClient
     *
     * @param webView
     * @param webChromeClient
     * @return
     */
    WebListenerManager setWebChromeClient(WebView webView, WebChromeClient webChromeClient);

    /**
     * 设置WebViewClient
     *
     * @param webView
     * @param webViewClient
     * @return
     */
    WebListenerManager setWebViewClient(WebView webView, WebViewClient webViewClient);

    /**
     * 设置下载监听
     *
     * @param webView
     * @param downloadListener
     * @return
     */
    WebListenerManager setDownloader(WebView webView, DownloadListener downloadListener);
}

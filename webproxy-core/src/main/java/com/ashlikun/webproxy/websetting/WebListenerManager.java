/*
 * Copyright (C)  Justson(https://github.com/Justson/AgentWeb)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ashlikun.webproxy.websetting;

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

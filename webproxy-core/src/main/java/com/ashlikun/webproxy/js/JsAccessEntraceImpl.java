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

package com.ashlikun.webproxy.js;

import android.os.Handler;
import android.os.Looper;
import android.webkit.ValueCallback;
import android.webkit.WebView;

/**
 * @author　　: 李坤
 * 创建时间: 2018/9/21 13:18
 * 邮箱　　：496546144@qq.com
 * <p>
 * 功能介绍：java回调js的
 */

public class JsAccessEntraceImpl extends BaseJsAccessEntrace {

    private WebView mWebView;
    private Handler mHandler = new Handler(Looper.getMainLooper());


    public static JsAccessEntraceImpl getInstance(WebView webView) {
        return new JsAccessEntraceImpl(webView);
    }

    private JsAccessEntraceImpl(WebView webView) {
        super(webView);
        this.mWebView = webView;
    }


    private void safeCallJs(final String s, final ValueCallback valueCallback) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                callJs(s, valueCallback);
            }
        });
    }

    @Override
    public void callJs(String params, final ValueCallback<String> callback) {
        if (Thread.currentThread() != Looper.getMainLooper().getThread()) {
            safeCallJs(params, callback);
            return;
        }
        super.callJs(params, callback);
    }


}

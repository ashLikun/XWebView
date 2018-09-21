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

package com.ashlikun.webproxy.lifecycle;

import android.os.Build;
import android.webkit.WebView;

import com.ashlikun.webproxy.WebProxyUtils;

/**
 * @author　　: 李坤
 * 创建时间: 2018/9/21 13:31
 * 邮箱　　：496546144@qq.com
 * <p>
 * 功能介绍：生命周期具体实现
 */

public class DefaultWebLifeCycleImpl implements WebLifeCycle {
    private WebView mWebView;

    public DefaultWebLifeCycleImpl(WebView webView) {
        this.mWebView = webView;
    }

    @Override
    public void onResume() {
        if (this.mWebView != null) {

            if (Build.VERSION.SDK_INT >= 11) {
                this.mWebView.onResume();
            }
            this.mWebView.resumeTimers();
        }


    }

    @Override
    public void onPause() {

        if (this.mWebView != null) {

            if (Build.VERSION.SDK_INT >= 11) {
                this.mWebView.onPause();
            }
            this.mWebView.pauseTimers();
        }
    }

    @Override
    public void onDestroy() {

        if (this.mWebView != null) {
            this.mWebView.resumeTimers();
        }
        WebProxyUtils.clearWebView(this.mWebView);

    }
}

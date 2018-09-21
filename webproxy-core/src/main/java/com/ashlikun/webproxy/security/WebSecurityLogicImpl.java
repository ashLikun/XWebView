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

package com.ashlikun.webproxy.security;

import android.annotation.TargetApi;
import android.os.Build;
import android.support.v4.util.ArrayMap;
import android.webkit.WebView;

import com.ashlikun.webproxy.WebProxy;
import com.ashlikun.webproxy.WebProxyConfig;


/**
 * @author　　: 李坤
 * 创建时间: 2018/9/21 17:03
 * 邮箱　　：496546144@qq.com
 * <p>
 * 功能介绍：安全相关设置
 */

public class WebSecurityLogicImpl implements WebSecurityCheckLogic {
    public static WebSecurityLogicImpl getInstance() {
        return new WebSecurityLogicImpl();
    }

    public WebSecurityLogicImpl() {
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    public void dealHoneyComb(WebView view) {
        if (Build.VERSION_CODES.HONEYCOMB > Build.VERSION.SDK_INT || Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR1) {
            return;
        }
        view.removeJavascriptInterface("searchBoxJavaBridge_");
        view.removeJavascriptInterface("accessibility");
        view.removeJavascriptInterface("accessibilityTraversal");
    }

    @Override
    public void dealJsInterface(ArrayMap<String, Object> objects, WebProxy.SecurityType securityType) {

        if (securityType == WebProxy.SecurityType.STRICT_CHECK
                && WebProxyConfig.WEBVIEW_TYPE != WebProxyConfig.WEBVIEW_AGENTWEB_SAFE_TYPE
                && Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
            objects.clear();
            objects = null;
            System.gc();
        }

    }
}

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

import android.os.Build;
import android.support.v4.util.ArrayMap;
import android.webkit.WebView;

import com.ashlikun.webproxy.WebProxy;


/**
 * @author　　: 李坤
 * 创建时间: 2018/9/21 17:05
 * 邮箱　　：496546144@qq.com
 * <p>
 * 功能介绍：安全相关设置
 */

public class WebSecurityControllerImpl implements WebSecurityController<WebSecurityCheckLogic> {

    private WebView mWebView;
    private ArrayMap<String, Object> mMap;
    private WebProxy.SecurityType mSecurityType;

    public WebSecurityControllerImpl(WebView view, ArrayMap<String, Object> map, WebProxy.SecurityType securityType) {
        this.mWebView = view;
        this.mMap = map;
        this.mSecurityType = securityType;
    }

    @Override
    public void check(WebSecurityCheckLogic webSecurityCheckLogic) {

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB) {
            webSecurityCheckLogic.dealHoneyComb(mWebView);
        }

        if (mMap != null && mSecurityType == WebProxy.SecurityType.STRICT_CHECK && !mMap.isEmpty()) {
            webSecurityCheckLogic.dealJsInterface(mMap, mSecurityType);
        }

    }
}

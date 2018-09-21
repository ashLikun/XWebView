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

import android.webkit.WebView;

import com.ashlikun.webproxy.WebProxy;

import java.util.Map;
import java.util.Set;

/**
 * @author　　: 李坤
 * 创建时间: 2018/9/21 17:13
 * 邮箱　　：496546144@qq.com
 * <p>
 * 功能介绍：添加js方法
 */

public class JsInterfaceHolderImpl extends JsBaseInterfaceHolder {

    private WebView mWebView;
    private WebProxy.SecurityType mSecurityType;

    public static JsInterfaceHolderImpl getJsInterfaceHolder(WebView webView, WebProxy.SecurityType securityType) {
        return new JsInterfaceHolderImpl(webView, securityType);
    }


    JsInterfaceHolderImpl(WebView webView, WebProxy.SecurityType securityType) {
        super(securityType);
        this.mWebView = webView;
        this.mSecurityType = securityType;
    }

    @Override
    public JsInterfaceHolder addJavaObjects(Map<String, Object> maps) {
        if (!checkSecurity()) {
            return this;
        }
        Set<Map.Entry<String, Object>> sets = maps.entrySet();
        for (Map.Entry<String, Object> mEntry : sets) {

            Object v = mEntry.getValue();
            boolean t = checkObject(v);
            if (!t) {
                throw new JsInterfaceObjectException("This object has not offer method javascript to call ,please check addJavascriptInterface annotation was be added");
            } else {
                addJavaObjectDirect(mEntry.getKey(), v);
            }
        }

        return this;
    }

    @Override
    public JsInterfaceHolder addJavaObject(String k, Object v) {

        if (!checkSecurity()) {
            return this;
        }
        boolean t = checkObject(v);
        if (!t) {
            throw new JsInterfaceObjectException("this object has not offer method javascript to call , please check addJavascriptInterface annotation was be added");
        } else {
            addJavaObjectDirect(k, v);
        }
        return this;
    }

    private JsInterfaceHolder addJavaObjectDirect(String k, Object v) {
        this.mWebView.addJavascriptInterface(v, k);
        return this;
    }


}

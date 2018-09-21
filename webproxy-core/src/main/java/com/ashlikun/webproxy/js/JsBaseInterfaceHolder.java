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

import android.os.Build;
import android.webkit.JavascriptInterface;

import com.ashlikun.webproxy.WebProxyConfig;
import com.ashlikun.webproxy.WebProxy;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

/**
 * @author　　: 李坤
 * 创建时间: 2018/9/21 17:11
 * 邮箱　　：496546144@qq.com
 *
 * 功能介绍：添加js方法
 */

public abstract class JsBaseInterfaceHolder implements JsInterfaceHolder {

    private WebProxy.SecurityType mSecurityType;

    protected JsBaseInterfaceHolder(WebProxy.SecurityType securityType) {
        this.mSecurityType = securityType;
    }

    @Override
    public boolean checkObject(Object v) {

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1){
            return true;
        }
        if (WebProxyConfig.WEBVIEW_TYPE == WebProxyConfig.WEBVIEW_AGENTWEB_SAFE_TYPE){
            return true;
        }
        boolean tag = false;
        Class clazz = v.getClass();

        Method[] mMethods = clazz.getMethods();

        for (Method mMethod : mMethods) {

            Annotation[] mAnnotations = mMethod.getAnnotations();

            for (Annotation mAnnotation : mAnnotations) {

                if (mAnnotation instanceof JavascriptInterface) {
                    tag = true;
                    break;
                }

            }
            if (tag){
                break;
            }
        }

        return tag;
    }

    protected boolean checkSecurity() {
        return mSecurityType != WebProxy.SecurityType.STRICT_CHECK
                ? true : WebProxyConfig.WEBVIEW_TYPE == WebProxyConfig.WEBVIEW_AGENTWEB_SAFE_TYPE
                ? true : Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR1;
    }


}

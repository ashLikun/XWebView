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

import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.webkit.JavascriptInterface;

import com.ashlikun.webproxy.WebProxy;
import com.ashlikun.webproxy.WebProxyUtils;

import java.lang.ref.WeakReference;

/**
 * @author　　: 李坤
 * 创建时间: 2018/9/21 15:45
 * 邮箱　　：496546144@qq.com
 * <p>
 * 功能介绍：js接口兼容
 */

public class WebJsInterfaceCompat {

    private WeakReference<WebProxy> mReference = null;
    private WeakReference<Activity> mActivityWeakReference = null;

    public WebJsInterfaceCompat(WebProxy agentWeb, Activity activity) {
        mReference = new WeakReference<WebProxy>(agentWeb);
        mActivityWeakReference = new WeakReference<Activity>(activity);
    }


    @JavascriptInterface
    public void uploadFile() {
        uploadFile("*/*");
    }

    @JavascriptInterface
    public void uploadFile(String acceptType) {
        if (mActivityWeakReference.get() != null && mReference.get() != null) {

            WebProxyUtils.showFileChooserCompat(mActivityWeakReference.get(),
                    mReference.get().getWebCreator().getWebView(),
                    null,
                    null,
                    mReference.get().getPermissionInterceptor(),
                    null,
                    acceptType,
                    new Handler.Callback() {
                        @Override
                        public boolean handleMessage(Message msg) {
                            if (mReference.get() != null) {
                                mReference.get().getJsAccessEntrace()
                                        .quickCallJs("uploadFileResult",
                                                msg.obj instanceof String ? (String) msg.obj : null);
                            }
                            return true;
                        }
                    }
            );


        }
    }

}

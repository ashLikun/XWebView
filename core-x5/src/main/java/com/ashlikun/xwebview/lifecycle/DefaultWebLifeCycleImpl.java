package com.ashlikun.xwebview.lifecycle;

import android.os.Build;
import com.tencent.smtt.sdk.WebView;

import com.ashlikun.xwebview.XWebUtils;

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
        XWebUtils.clearWebView(this.mWebView);

    }
}

package com.ashlikun.xwebview.js;

import android.os.Handler;
import android.os.Looper;
import com.tencent.smtt.sdk.ValueCallback;
import com.tencent.smtt.sdk.WebView;

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

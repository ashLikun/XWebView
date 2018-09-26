
package com.ashlikun.xwebview.event;

import android.view.KeyEvent;
import android.webkit.WebView;

/**
 * @author　　: 李坤
 * 创建时间: 2018/9/21 15:54
 * 邮箱　　：496546144@qq.com
 * <p>
 * 功能介绍：处理返回键
 */

public class EventHandlerImpl implements IEventHandler {
    private WebView mWebView;
    private EventInterceptor mEventInterceptor;

    public static final EventHandlerImpl getInstantce(WebView view, EventInterceptor eventInterceptor) {
        return new EventHandlerImpl(view, eventInterceptor);
    }

    public EventHandlerImpl(WebView webView, EventInterceptor eventInterceptor) {
        this.mWebView = webView;
        this.mEventInterceptor = eventInterceptor;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return back();
        }
        return false;
    }

    @Override
    public boolean back() {
        if (this.mEventInterceptor != null && this.mEventInterceptor.event()) {
            return true;
        }
        if (mWebView != null && mWebView.canGoBack()) {
            mWebView.goBack();
            return true;
        }
        return false;
    }

}

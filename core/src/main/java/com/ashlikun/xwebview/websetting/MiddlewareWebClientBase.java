package com.ashlikun.xwebview.websetting;

import android.webkit.WebViewClient;

/**
 * @author　　: 李坤
 * 创建时间: 2018/9/21 13:59
 * 邮箱　　：496546144@qq.com
 * <p>
 * 功能介绍：WebViewClient 中间层
 */

public class MiddlewareWebClientBase extends WebViewClientDelegate {
    private MiddlewareWebClientBase mMiddleWrareWebClientBase;

    MiddlewareWebClientBase(MiddlewareWebClientBase client) {
        super(client);
        this.mMiddleWrareWebClientBase = client;
    }

    protected MiddlewareWebClientBase(WebViewClient client) {
        super(client);
    }

    protected MiddlewareWebClientBase() {
        super(null);
    }

    public final MiddlewareWebClientBase next() {
        return this.mMiddleWrareWebClientBase;
    }


    @Override
    public final void setDelegate(WebViewClient delegate) {
        super.setDelegate(delegate);

    }

    public final MiddlewareWebClientBase enq(MiddlewareWebClientBase middleWrareWebClientBase) {
        setDelegate(middleWrareWebClientBase);
        this.mMiddleWrareWebClientBase = middleWrareWebClientBase;
        return middleWrareWebClientBase;
    }


}

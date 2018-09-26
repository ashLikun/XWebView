package com.ashlikun.xwebview.websetting;

import android.webkit.WebChromeClient;

/**
 * @author　　: 李坤
 * 创建时间: 2018/9/21 17:54
 * 邮箱　　：496546144@qq.com
 *
 * 功能介绍：WebChromeClient 中间层
 */

public class MiddlewareWebChromeBase extends WebChromeClientDelegate {

    private MiddlewareWebChromeBase mMiddlewareWebChromeBase;

    protected MiddlewareWebChromeBase(WebChromeClient webChromeClient) {
        super(webChromeClient);
    }

    protected MiddlewareWebChromeBase() {
        super(null);
    }

    @Override
    public final void setDelegate(WebChromeClient delegate) {
        super.setDelegate(delegate);
    }

    public final MiddlewareWebChromeBase enq(MiddlewareWebChromeBase middlewareWebChromeBase) {
        setDelegate(middlewareWebChromeBase);
        this.mMiddlewareWebChromeBase = middlewareWebChromeBase;
        return this.mMiddlewareWebChromeBase;
    }


    public final MiddlewareWebChromeBase next() {
        return this.mMiddlewareWebChromeBase;
    }

}

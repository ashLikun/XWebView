package com.ashlikun.xwebview.ui;

import android.app.Activity;
import android.os.Handler;
import com.tencent.smtt.export.external.interfaces.JsPromptResult;
import com.tencent.smtt.export.external.interfaces.JsResult;
import com.tencent.smtt.sdk.WebView;


/**
 * @author　　: 李坤
 * 创建时间: 2018/9/21 14:21
 * 邮箱　　：496546144@qq.com
 * <p>
 * 功能介绍：默认的web与UI交互
 */
public class WebUIControllerImplBase extends AbsWebUIController {


    /**
     * 创建一个默认的
     *
     * @return
     */
    public static AbsWebUIController build() {
        return new WebUIControllerImplBase();
    }

    @Override
    public void onJsAlert(WebView view, String url, String message) {
        getDelegate().onJsAlert(view, url, message);
    }

    @Override
    public void onOpenPagePrompt(WebView view, String url, Handler.Callback callback) {
        getDelegate().onOpenPagePrompt(view, url, callback);
    }

    @Override
    public void onJsConfirm(WebView view, String url, String message, JsResult jsResult) {
        getDelegate().onJsConfirm(view, url, message, jsResult);
    }

    @Override
    public void onSelectItemsPrompt(WebView view, String url, String[] ways, Handler.Callback callback) {
        getDelegate().onSelectItemsPrompt(view, url, ways, callback);
    }

    @Override
    public void onForceDownloadAlert(String url, Handler.Callback callback) {
        getDelegate().onForceDownloadAlert(url, callback);
    }

    @Override
    public void onJsPrompt(WebView view, String url, String message, String defaultValue, JsPromptResult jsPromptResult) {
        getDelegate().onJsPrompt(view, url, message, defaultValue, jsPromptResult);
    }

    @Override
    public void onMainFrameError(WebView view, int errorCode, String description, String failingUrl) {
        getDelegate().onMainFrameError(view, errorCode, description, failingUrl);
    }

    @Override
    public void onShowMainFrame() {
        getDelegate().onShowMainFrame();
    }

    @Override
    public void onLoading(String msg) {
        getDelegate().onLoading(msg);
    }

    @Override
    public void onCancelLoading() {
        getDelegate().onCancelLoading();
    }


    @Override
    public void onShowMessage(String message, String from) {
        getDelegate().onShowMessage(message, from);
    }

    @Override
    public void onPermissionsDeny(String[] permissions, String permissionType, String action) {
        getDelegate().onPermissionsDeny(permissions, permissionType, action);
    }

    @Override
    protected void bindSupportWebParent(WebParentLayout webParentLayout, Activity activity) {
        getDelegate().bindSupportWebParent(webParentLayout, activity);
    }


}

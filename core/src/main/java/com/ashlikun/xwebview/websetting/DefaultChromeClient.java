package com.ashlikun.xwebview.websetting;

import android.app.Activity;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import android.view.View;
import android.webkit.ConsoleMessage;
import android.webkit.GeolocationPermissions;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebStorage;
import android.webkit.WebView;

import com.ashlikun.xwebview.XWebUtils;
import com.ashlikun.xwebview.indicator.IndicatorController;
import com.ashlikun.xwebview.media.IVideo;
import com.ashlikun.xwebview.security.WebPermissions;
import com.ashlikun.xwebview.ui.AbsWebUIController;
import com.ashlikun.xwebview.ui.Action;
import com.ashlikun.xwebview.ui.ActionActivity;

import java.lang.ref.WeakReference;
import java.util.List;


/**
 * @author　　: 李坤
 * 创建时间: 2018/9/21 13:57
 * 邮箱　　：496546144@qq.com
 * <p>
 * 功能介绍：ChromeClient
 */

public class DefaultChromeClient extends MiddlewareWebChromeBase {

    /**
     * Activity 的弱引用
     */
    private WeakReference<Activity> mActivityWeakReference = null;
    /**
     * Android WebChromeClient path ，用于反射，用户是否重写来该方法
     */
    public static final String ANDROID_WEBCHROMECLIENT_PATH = "android.webkit.WebChromeClient";
    /**
     * WebChromeClient
     */
    private WebChromeClient mWebChromeClient;
    /**
     * 是否被包装过
     */
    private boolean mIsWrapper = false;
    /**
     * Video 处理类
     */
    private IVideo mIVideo;
    /**
     * PermissionInterceptor 权限拦截器
     */
    private PermissionInterceptor mPermissionInterceptor;
    /**
     * 当前 WebView
     */
    private WebView mWebView;
    /**
     * Web端触发的定位 mOrigin
     */
    private String mOrigin = null;
    /**
     * Web 端触发的定位 Callback 回调成功，或者失败
     */
    private GeolocationPermissions.Callback mCallback = null;
    /**
     * 标志位
     */
    public static final int FROM_CODE_INTENTION = 0x18;
    /**
     * 标识当前是获取定位权限
     */
    public static final int FROM_CODE_INTENTION_LOCATION = FROM_CODE_INTENTION << 2;
    /**
     * AbsWebUIController
     */
    private WeakReference<AbsWebUIController> mWebUIController = null;
    /**
     * IndicatorController 进度条控制器
     */
    private IndicatorController mIndicatorController;
    /**
     * 文件选择器
     */
    private Object mFileChooser;

    public DefaultChromeClient(Activity activity,
                               IndicatorController indicatorController,
                               WebChromeClient chromeClient,
                               @Nullable IVideo iVideo,
                               PermissionInterceptor permissionInterceptor, WebView webView) {
        super(chromeClient);
        this.mIndicatorController = indicatorController;
        mIsWrapper = chromeClient != null ? true : false;
        this.mWebChromeClient = chromeClient;
        mActivityWeakReference = new WeakReference<Activity>(activity);
        this.mIVideo = iVideo;
        this.mPermissionInterceptor = permissionInterceptor;
        this.mWebView = webView;
        mWebUIController = new WeakReference<AbsWebUIController>(XWebUtils.getWebUIControllerByWebView(webView));
    }


    @Override
    public void onProgressChanged(WebView view, int newProgress) {
        super.onProgressChanged(view, newProgress);

        if (mIndicatorController != null) {
            mIndicatorController.progress(view, newProgress);
        }

    }

    @Override
    public void onReceivedTitle(WebView view, String title) {
        if (mIsWrapper) {
            super.onReceivedTitle(view, title);
        }
    }

    @Override
    public boolean onJsAlert(WebView view, String url, String message, JsResult result) {


        if (XWebUtils.isOverriedMethod(mWebChromeClient, "onJsAlert", "public boolean " + ANDROID_WEBCHROMECLIENT_PATH + ".onJsAlert", WebView.class, String.class, String.class, JsResult.class)) {
            return super.onJsAlert(view, url, message, result);
        }

        if (mWebUIController.get() != null) {
            mWebUIController.get().onJsAlert(view, url, message);
        }

        result.confirm();

        return true;
    }


    @Override
    public void onReceivedIcon(WebView view, Bitmap icon) {
        super.onReceivedIcon(view, icon);
    }

    @Override
    public void onGeolocationPermissionsHidePrompt() {
        super.onGeolocationPermissionsHidePrompt();
    }

    //location
    @Override
    public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback callback) {
        if (XWebUtils.isOverriedMethod(mWebChromeClient, "onGeolocationPermissionsShowPrompt", "public void " + ANDROID_WEBCHROMECLIENT_PATH + ".onGeolocationPermissionsShowPrompt", String.class, GeolocationPermissions.Callback.class)) {
            super.onGeolocationPermissionsShowPrompt(origin, callback);
            return;
        }
        onGeolocationPermissionsShowPromptInternal(origin, callback);
    }


    private void onGeolocationPermissionsShowPromptInternal(String origin, GeolocationPermissions.Callback callback) {

        if (mPermissionInterceptor != null) {
            if (mPermissionInterceptor.intercept(this.mWebView.getUrl(), WebPermissions.LOCATION, "location")) {
                callback.invoke(origin, false, false);
                return;
            }
        }

        Activity mActivity = mActivityWeakReference.get();
        if (mActivity == null) {
            callback.invoke(origin, false, false);
            return;
        }

        List<String> deniedPermissions = null;
        if ((deniedPermissions = XWebUtils.getDeniedPermissions(mActivity, WebPermissions.LOCATION)).isEmpty()) {
            callback.invoke(origin, true, false);
        } else {

            Action mAction = Action.createPermissionsAction(deniedPermissions.toArray(new String[]{}));
            mAction.setFromIntention(FROM_CODE_INTENTION_LOCATION);
            ActionActivity.setPermissionListener(mPermissionListener);
            this.mCallback = callback;
            this.mOrigin = origin;
            ActionActivity.start(mActivity, mAction);
        }


    }

    private ActionActivity.PermissionListener mPermissionListener = new ActionActivity.PermissionListener() {
        @Override
        public void onRequestPermissionsResult(@NonNull String[] permissions, @NonNull int[] grantResults, Bundle extras) {
            if (extras.getInt(ActionActivity.KEY_FROM_INTENTION) == FROM_CODE_INTENTION_LOCATION) {
                boolean hasPermission = XWebUtils.hasPermission(mActivityWeakReference.get(), permissions);
                if (mCallback != null) {
                    if (hasPermission) {
                        mCallback.invoke(mOrigin, true, false);
                    } else {
                        mCallback.invoke(mOrigin, false, false);
                    }

                    mCallback = null;
                    mOrigin = null;
                }

                if (!hasPermission && null != mWebUIController.get()) {
                    mWebUIController
                            .get()
                            .onPermissionsDeny(
                                    WebPermissions.LOCATION,
                                    WebPermissions.ACTION_LOCATION,
                                    "Location");
                }
            }

        }
    };

    @Override
    public boolean onJsPrompt(WebView view, String url, String message, String defaultValue, JsPromptResult result) {


        try {
            if (XWebUtils.isOverriedMethod(mWebChromeClient, "onJsPrompt", "public boolean " + ANDROID_WEBCHROMECLIENT_PATH + ".onJsPrompt", WebView.class, String.class, String.class, String.class, JsPromptResult.class)) {
                return super.onJsPrompt(view, url, message, defaultValue, result);
            }
            if (this.mWebUIController.get() != null) {
                this.mWebUIController.get().onJsPrompt(mWebView, url, message, defaultValue, result);
            }
        } catch (Exception e) {
        }
        return true;
    }

    @Override
    public boolean onJsConfirm(WebView view, String url, String message, JsResult result) {

        if (XWebUtils.isOverriedMethod(mWebChromeClient, "onJsConfirm", "public boolean " + ANDROID_WEBCHROMECLIENT_PATH + ".onJsConfirm", WebView.class, String.class, String.class, JsResult.class)) {
            return super.onJsConfirm(view, url, message, result);
        }

        if (mWebUIController.get() != null) {
            mWebUIController.get().onJsConfirm(view, url, message, result);
        }
        return true;
    }


    @Override
    public void onExceededDatabaseQuota(String url, String databaseIdentifier, long quota, long estimatedDatabaseSize, long totalQuota, WebStorage.QuotaUpdater quotaUpdater) {


        if (XWebUtils.isOverriedMethod(mWebChromeClient, "onExceededDatabaseQuota", ANDROID_WEBCHROMECLIENT_PATH + ".onExceededDatabaseQuota", String.class, String.class, long.class, long.class, long.class, WebStorage.QuotaUpdater.class)) {

            super.onExceededDatabaseQuota(url, databaseIdentifier, quota, estimatedDatabaseSize, totalQuota, quotaUpdater);
            return;
        }
        quotaUpdater.updateQuota(totalQuota * 2);
    }

    @Override
    public void onReachedMaxAppCacheSize(long requiredStorage, long quota, WebStorage.QuotaUpdater quotaUpdater) {


        if (XWebUtils.isOverriedMethod(mWebChromeClient, "onReachedMaxAppCacheSize", ANDROID_WEBCHROMECLIENT_PATH + ".onReachedMaxAppCacheSize", long.class, long.class, WebStorage.QuotaUpdater.class)) {

            super.onReachedMaxAppCacheSize(requiredStorage, quota, quotaUpdater);
            return;
        }
        quotaUpdater.updateQuota(requiredStorage * 2);
    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
        if (XWebUtils.isOverriedMethod(mWebChromeClient, "onShowFileChooser", ANDROID_WEBCHROMECLIENT_PATH + ".onShowFileChooser", WebView.class, ValueCallback.class, FileChooserParams.class)) {

            return super.onShowFileChooser(webView, filePathCallback, fileChooserParams);
        }

        return openFileChooserAboveL(webView, filePathCallback, fileChooserParams);
    }

    private boolean openFileChooserAboveL(WebView webView, ValueCallback<Uri[]> valueCallbacks, FileChooserParams fileChooserParams) {
        Activity mActivity = this.mActivityWeakReference.get();
        if (mActivity == null || mActivity.isFinishing()) {
            return false;
        }

        return XWebUtils.showFileChooserCompat(mActivity,
                mWebView,
                valueCallbacks,
                fileChooserParams,
                this.mPermissionInterceptor,
                null,
                null,
                null
        );

    }

    /**
     * Android  >= 4.1
     *
     * @param uploadFile ValueCallback ,  File URI callback
     * @param acceptType
     * @param capture
     */
    @Override
    public void openFileChooser(ValueCallback<Uri> uploadFile, String acceptType, String capture) {
        if (XWebUtils.isOverriedMethod(mWebChromeClient, "openFileChooser", ANDROID_WEBCHROMECLIENT_PATH + ".openFileChooser", ValueCallback.class, String.class, String.class)) {
            super.openFileChooser(uploadFile, acceptType, capture);
            return;
        }
        createAndOpenCommonFileChooser(uploadFile, acceptType);
    }




    private void createAndOpenCommonFileChooser(ValueCallback valueCallback, String mimeType) {
        Activity mActivity = this.mActivityWeakReference.get();
        if (mActivity == null || mActivity.isFinishing()) {
            valueCallback.onReceiveValue(new Object());
            return;
        }


        XWebUtils.showFileChooserCompat(mActivity,
                mWebView,
                null,
                null,
                this.mPermissionInterceptor,
                valueCallback,
                mimeType,
                null
        );

    }


    @Override
    public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
        super.onConsoleMessage(consoleMessage);
        return true;
    }


    @Override
    public void onShowCustomView(View view, CustomViewCallback callback) {
        if (XWebUtils.isOverriedMethod(mWebChromeClient, "onShowCustomView", ANDROID_WEBCHROMECLIENT_PATH + ".onShowCustomView", View.class, CustomViewCallback.class)) {
            super.onShowCustomView(view, callback);
            return;
        }

        if (mIVideo != null) {
            mIVideo.onShowCustomView(view, callback);
        }

    }

    @Override
    public void onHideCustomView() {
        if (XWebUtils.isOverriedMethod(mWebChromeClient, "onHideCustomView", ANDROID_WEBCHROMECLIENT_PATH + ".onHideCustomView")) {
            super.onHideCustomView();
            return;
        }

        if (mIVideo != null) {
            mIVideo.onHideCustomView();
        }
    }
}

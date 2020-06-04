package com.ashlikun.xwebview.websetting;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.HttpAuthHandler;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.RequiresApi;

import com.ashlikun.xwebview.XWebConfig;
import com.ashlikun.xwebview.XWebUtils;
import com.ashlikun.xwebview.ui.AbsWebUIController;

import java.lang.ref.WeakReference;
import java.lang.reflect.Method;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author　　: 李坤
 * 创建时间: 2018/9/21 14:41
 * 邮箱　　：496546144@qq.com
 * <p>
 * 功能介绍：WebClient
 */

public class DefaultWebClient extends MiddlewareWebClientBase {

    /**
     * Activity's WeakReference
     */
    private WeakReference<Context> mWeakReference = null;
    /**
     * 缩放
     */
    private static final int CONSTANTS_ABNORMAL_BIG = 7;
    /**
     * WebViewClient
     */
    private WebViewClient mWebViewClient;
    /**
     * 是否内部帮您处理一些url
     */
    private boolean webClientHelper = true;
    /**
     * Android  WebViewClient ' path 用于反射，判断用户是否重写了WebViewClient的某一个方法
     */
    private static final String ANDROID_WEBVIEWCLIENT_PATH = "android.webkit.WebViewClient";
    /**
     * intent ' s scheme
     */
    public static final String INTENT_SCHEME = "intent://";
    /**
     * http scheme
     */
    public static final String HTTP_SCHEME = "http://";
    /**
     * https scheme
     */
    public static final String HTTPS_SCHEME = "https://";

    /**
     * 直接打开其他页面
     */
    public static final int DERECT_OPEN_OTHER_PAGE = 1001;
    /**
     * 弹窗咨询用户是否前往其他页面
     */
    public static final int ASK_USER_OPEN_OTHER_PAGE = DERECT_OPEN_OTHER_PAGE >> 2;
    /**
     * 不允许打开其他页面
     */
    public static final int DISALLOW_OPEN_OTHER_APP = DERECT_OPEN_OTHER_PAGE >> 4;
    /**
     * 默认为咨询用户
     */
    private int mUrlHandleWays = ASK_USER_OPEN_OTHER_PAGE;
    /**
     * 是否拦截找不到相应页面的Url，默认拦截
     */
    private boolean mIsInterceptUnkownUrl = true;
    /**
     * AbsWebUIController
     */
    private WeakReference<AbsWebUIController> mWebUIController = null;
    /**
     * WebView
     */
    private WebView mWebView;
    /**
     * 弹窗回调
     */
    private Handler.Callback mCallback = null;
    /**
     * MainFrameErrorMethod
     */
    private Method onMainFrameErrorMethod = null;
    /**
     * SMS scheme
     */
    public static final String SCHEME_SMS = "sms:";
    /**
     * 缓存当前出现错误的页面
     */
    private Set<String> mErrorUrlsSet = new HashSet<>();
    /**
     * 缓存等待加载完成的页面 onPageStart()执行之后 ，onPageFinished()执行之前
     */
    private Set<String> mWaittingFinishSet = new HashSet<>();


    DefaultWebClient(Builder builder) {
        super(builder.mClient);
        this.mWebView = builder.mWebView;
        this.mWebViewClient = builder.mClient;
        mWeakReference = new WeakReference<Context>(builder.mContext);
        this.webClientHelper = builder.mWebClientHelper;
        mWebUIController = new WeakReference<AbsWebUIController>(XWebUtils.getWebUIControllerByWebView(builder.mWebView));
        mIsInterceptUnkownUrl = builder.mIsInterceptUnkownScheme;

        if (builder.mUrlHandleWays <= 0) {
            mUrlHandleWays = ASK_USER_OPEN_OTHER_PAGE;
        } else {
            mUrlHandleWays = builder.mUrlHandleWays;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
        int tag = -1;

        if (XWebUtils.isOverriedMethod(mWebViewClient, "shouldOverrideUrlLoading", ANDROID_WEBVIEWCLIENT_PATH + ".shouldOverrideUrlLoading", WebView.class, WebResourceRequest.class) && (((tag = 1) > 0) && super.shouldOverrideUrlLoading(view, request))) {
            return true;
        }

        String url = request.getUrl().toString();
        //正常的url
        if (url.startsWith(HTTP_SCHEME) || url.startsWith(HTTPS_SCHEME)) {
            return false;
        }
        if (!webClientHelper) {
            return false;
        }
        if (handleCommonLink(url)) {
            return true;
        }
        // intent
        if (url.startsWith(INTENT_SCHEME)) {
            handleIntentUrl(url);
            return true;
        }

        if (queryActiviesNumber(url) > 0 && deepLink(url)) {
            return true;
        }
        if (mIsInterceptUnkownUrl) {
            return true;
        }
        if (tag > 0) {
            return false;
        }
        return super.shouldOverrideUrlLoading(view, request);
    }

    @Override
    public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
        return super.shouldInterceptRequest(view, url);
    }

    @Override
    public void onReceivedHttpAuthRequest(WebView view, HttpAuthHandler handler, String host, String realm) {
        super.onReceivedHttpAuthRequest(view, handler, host, realm);
    }

    private boolean deepLink(String url) {

        switch (mUrlHandleWays) {
            // 直接打开其他App
            case DERECT_OPEN_OTHER_PAGE:
                lookup(url);
                return true;
            // 咨询用户是否打开其他App
            case ASK_USER_OPEN_OTHER_PAGE:
                if (mWebUIController.get() != null) {
                    mWebUIController.get()
                            .onOpenPagePrompt(this.mWebView,
                                    mWebView.getUrl(),
                                    getCallback(url));
                }
                return true;
            // 默认不打开
            default:
                return false;
        }
    }

    @Override
    public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
        return super.shouldInterceptRequest(view, request);
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        int tag = -1;
        if (XWebUtils.isOverriedMethod(mWebViewClient, "shouldOverrideUrlLoading", ANDROID_WEBVIEWCLIENT_PATH + ".shouldOverrideUrlLoading", WebView.class, String.class) && (((tag = 1) > 0) && super.shouldOverrideUrlLoading(view, url))) {
            return true;
        }
        if (url.startsWith(HTTP_SCHEME) || url.startsWith(HTTPS_SCHEME)) {
            return false;
        }

        if (!webClientHelper) {
            return false;
        }
        //电话 ， 邮箱 ， 短信
        if (handleCommonLink(url)) {
            return true;
        }
        //Intent scheme
        if (url.startsWith(INTENT_SCHEME)) {
            handleIntentUrl(url);
            return true;
        }
        //打开url 相对应的页面
        if (queryActiviesNumber(url) > 0 && deepLink(url)) {
            return true;
        }
        // 手机里面没有页面能匹配到该链接 ，拦截下来。
        if (mIsInterceptUnkownUrl) {
            return true;
        }
        if (tag > 0) {
            return false;
        }

        return super.shouldOverrideUrlLoading(view, url);
    }


    private int queryActiviesNumber(String url) {

        try {
            if (mWeakReference.get() == null) {
                return 0;
            }
            Intent intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME);
            PackageManager mPackageManager = mWeakReference.get().getPackageManager();
            List<ResolveInfo> mResolveInfos = mPackageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
            return mResolveInfos == null ? 0 : mResolveInfos.size();
        } catch (URISyntaxException ignore) {
            return 0;
        }
    }

    private void handleIntentUrl(String intentUrl) {
        try {

            Intent intent = null;
            if (TextUtils.isEmpty(intentUrl) || !intentUrl.startsWith(INTENT_SCHEME)) {
                return;
            }

            if (lookup(intentUrl)) {
                return;
            }
        } catch (Throwable e) {
        }
    }

    private boolean lookup(String url) {
        try {
            Intent intent;
            Context mContext = null;
            if ((mContext = mWeakReference.get()) == null) {
                return true;
            }
            PackageManager packageManager = mContext.getPackageManager();
            intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME);
            ResolveInfo info = packageManager.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY);
            // 跳到该应用
            if (info != null) {
                mContext.startActivity(intent);
                return true;
            }
        } catch (Throwable ignore) {
        }

        return false;
    }


    private boolean handleCommonLink(String url) {
        if (url.startsWith(WebView.SCHEME_TEL)
                || url.startsWith(SCHEME_SMS)
                || url.startsWith(WebView.SCHEME_MAILTO)
                || url.startsWith(WebView.SCHEME_GEO)) {
            try {
                Context mContext = null;
                if ((mContext = mWeakReference.get()) == null) {
                    return false;
                }
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));
                mContext.startActivity(intent);
            } catch (ActivityNotFoundException ignored) {
                if (XWebConfig.DEBUG) {
                    ignored.printStackTrace();
                }
            }
            return true;
        }
        return false;
    }

    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {

        if (!mWaittingFinishSet.contains(url)) {
            mWaittingFinishSet.add(url);
        }
        super.onPageStarted(view, url, favicon);

    }


    /**
     * MainFrame Error
     *
     * @param view
     * @param errorCode
     * @param description
     * @param failingUrl
     */
    @Override
    public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {

        if (XWebUtils.isOverriedMethod(mWebViewClient, "onReceivedError", ANDROID_WEBVIEWCLIENT_PATH + ".onReceivedError", WebView.class, int.class, String.class, String.class)) {
            super.onReceivedError(view, errorCode, description, failingUrl);
//            return;
        }
        onMainFrameError(view, errorCode, description, failingUrl);
    }


    @Override
    public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (XWebUtils.isOverriedMethod(mWebViewClient, "onReceivedError",
                    ANDROID_WEBVIEWCLIENT_PATH + ".onReceivedError", WebView.class, WebResourceRequest.class, WebResourceError.class)) {
                super.onReceivedError(view, request, error);
//            return;
            }
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (request.isForMainFrame()) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    onMainFrameError(view,
                            error.getErrorCode(), error.getDescription().toString(),
                            request.getUrl().toString());
                }
            }
        }
    }

    private void onMainFrameError(WebView view, int errorCode, String description, String failingUrl) {
        mErrorUrlsSet.add(failingUrl);
        // 下面逻辑判断开发者是否重写了 onMainFrameError 方法 ， 优先交给开发者处理
        if (this.mWebViewClient != null && webClientHelper) {
            Method mMethod = this.onMainFrameErrorMethod;
            if (mMethod != null || (this.onMainFrameErrorMethod = mMethod = XWebUtils.isExistMethod(mWebViewClient, "onMainFrameError", AbsWebUIController.class, WebView.class, int.class, String.class, String.class)) != null) {
                try {
                    mMethod.invoke(this.mWebViewClient, mWebUIController.get(), view, errorCode, description, failingUrl);
                } catch (Throwable ignore) {
                }
                return;
            }
        }
        if (mWebUIController.get() != null) {
            mWebUIController.get().onMainFrameError(view, errorCode, description, failingUrl);
        }
    }


    @Override
    public void onPageFinished(WebView view, String url) {

        if (!mErrorUrlsSet.contains(url) && mWaittingFinishSet.contains(url)) {
            if (mWebUIController.get() != null) {
                mWebUIController.get().onShowMainFrame();
            }
        } else {
            view.setVisibility(View.VISIBLE);
        }
        if (mWaittingFinishSet.contains(url)) {
            mWaittingFinishSet.remove(url);
        }
        if (!mErrorUrlsSet.isEmpty()) {
            mErrorUrlsSet.clear();
        }
        super.onPageFinished(view, url);

    }


    @Override
    public boolean shouldOverrideKeyEvent(WebView view, KeyEvent event) {
        return super.shouldOverrideKeyEvent(view, event);
    }


    public void startActivity(String url) {
        try {
            if (mWeakReference.get() == null) {
                return;
            }
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(url));
            mWeakReference.get().startActivity(intent);

        } catch (Exception e) {
        }
    }


    @Override
    public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {
        super.onReceivedHttpError(view, request, errorResponse);
    }

    @Override
    public void onScaleChanged(WebView view, float oldScale, float newScale) {


        if (XWebUtils.isOverriedMethod(mWebViewClient, "onScaleChanged", ANDROID_WEBVIEWCLIENT_PATH + ".onScaleChanged", WebView.class, float.class, float.class)) {
            super.onScaleChanged(view, oldScale, newScale);
            return;
        }
        if (newScale - oldScale > CONSTANTS_ABNORMAL_BIG) {
            view.setInitialScale((int) (oldScale / newScale * 100));
        }

    }


    private Handler.Callback getCallback(final String url) {
        if (this.mCallback != null) {
            return this.mCallback;
        }
        return this.mCallback = new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                switch (msg.what) {
                    case 1:
                        lookup(url);
                        break;
                    default:
                        return true;
                }
                return true;
            }
        };
    }


    public static Builder createBuilder() {
        return new Builder();
    }

    public static class Builder {

        private Context mContext;
        private WebViewClient mClient;
        private boolean mWebClientHelper;
        private PermissionInterceptor mPermissionInterceptor;
        private WebView mWebView;
        private boolean mIsInterceptUnkownScheme;
        private int mUrlHandleWays;

        public Builder setContext(Context context) {
            this.mContext = context;
            return this;
        }

        public Builder setClient(WebViewClient client) {
            this.mClient = client;
            return this;
        }

        public Builder setWebClientHelper(boolean webClientHelper) {
            this.mWebClientHelper = webClientHelper;
            return this;
        }

        public Builder setPermissionInterceptor(PermissionInterceptor permissionInterceptor) {
            this.mPermissionInterceptor = permissionInterceptor;
            return this;
        }

        public Builder setWebView(WebView webView) {
            this.mWebView = webView;
            return this;
        }

        public Builder setInterceptUnkownUrl(boolean interceptUnkownScheme) {
            this.mIsInterceptUnkownScheme = interceptUnkownScheme;
            return this;
        }

        public Builder setUrlHandleWays(int urlHandleWays) {
            this.mUrlHandleWays = urlHandleWays;
            return this;
        }

        public DefaultWebClient build() {
            return new DefaultWebClient(this);
        }
    }

    public static enum OpenOtherPageWays {
        DERECT(DefaultWebClient.DERECT_OPEN_OTHER_PAGE), ASK(DefaultWebClient.ASK_USER_OPEN_OTHER_PAGE), DISALLOW(DefaultWebClient.DISALLOW_OPEN_OTHER_APP);
        public int code;

        OpenOtherPageWays(int code) {
            this.code = code;
        }
    }
}

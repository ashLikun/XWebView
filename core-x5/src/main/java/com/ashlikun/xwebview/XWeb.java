package com.ashlikun.xwebview;

import android.app.Activity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.ColorInt;
import androidx.annotation.IdRes;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.collection.ArrayMap;
import androidx.fragment.app.Fragment;

import com.ashlikun.xwebview.event.EventHandlerImpl;
import com.ashlikun.xwebview.event.EventInterceptor;
import com.ashlikun.xwebview.event.IEventHandler;
import com.ashlikun.xwebview.indicator.BaseIndicatorView;
import com.ashlikun.xwebview.indicator.IndicatorController;
import com.ashlikun.xwebview.indicator.IndicatorHandler;
import com.ashlikun.xwebview.js.JsAccessEntrace;
import com.ashlikun.xwebview.js.JsAccessEntraceImpl;
import com.ashlikun.xwebview.js.JsInterfaceHolder;
import com.ashlikun.xwebview.js.JsInterfaceHolderImpl;
import com.ashlikun.xwebview.js.WebJsInterfaceCompat;
import com.ashlikun.xwebview.lifecycle.DefaultWebLifeCycleImpl;
import com.ashlikun.xwebview.lifecycle.WebLifeCycle;
import com.ashlikun.xwebview.media.IVideo;
import com.ashlikun.xwebview.media.VideoImpl;
import com.ashlikun.xwebview.security.WebSecurityCheckLogic;
import com.ashlikun.xwebview.security.WebSecurityController;
import com.ashlikun.xwebview.security.WebSecurityControllerImpl;
import com.ashlikun.xwebview.security.WebSecurityLogicImpl;
import com.ashlikun.xwebview.ui.AbsWebUIController;
import com.ashlikun.xwebview.ui.WebParentLayout;
import com.ashlikun.xwebview.ui.WebUIControllerImplBase;
import com.ashlikun.xwebview.websetting.AbsXWebSettings;
import com.ashlikun.xwebview.websetting.DefaultChromeClient;
import com.ashlikun.xwebview.websetting.DefaultWebClient;
import com.ashlikun.xwebview.websetting.IWebSettings;
import com.ashlikun.xwebview.websetting.MiddlewareWebChromeBase;
import com.ashlikun.xwebview.websetting.MiddlewareWebClientBase;
import com.ashlikun.xwebview.websetting.PermissionInterceptor;
import com.ashlikun.xwebview.websetting.WebListenerManager;
import com.ashlikun.xwebview.webview.DefaultWebCreator;
import com.ashlikun.xwebview.webview.IUrlLoader;
import com.ashlikun.xwebview.webview.IWebLayout;
import com.ashlikun.xwebview.webview.UrlLoaderImpl;
import com.ashlikun.xwebview.webview.WebCreator;
import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;

import java.lang.ref.WeakReference;
import java.util.Map;

/**
 * @author　　: 李坤
 * 创建时间: 2018/9/21 11:41
 * 邮箱　　：496546144@qq.com
 * <p>
 * 功能介绍：是一个轻量级而且功能强大的 Web 库
 */

public final class XWeb {
    /**
     * Activity
     */
    private Activity mActivity;
    /**
     * 承载 WebParentLayout 的 ViewGroup
     */
    private ViewGroup mViewGroup;
    /**
     * 负责创建布局 WebView ，WebParentLayout  Indicator等。
     */
    private WebCreator mWebCreator;
    /**
     * 管理 WebSettings
     */
    private IWebSettings mWebSettings;
    /**
     * Web
     */
    private XWeb mWeb = null;
    /**
     * IndicatorController 控制Indicator
     */
    private IndicatorController mIndicatorController;
    /**
     * WebChromeClient
     */
    private WebChromeClient mWebChromeClient;
    /**
     * WebViewClient
     */
    private WebViewClient mWebViewClient;
    /**
     * 是否启动进度条
     */
    private boolean mEnableIndicator;
    /**
     * IEventHandler 处理WebView相关返回事件
     */
    private IEventHandler mIEventHandler;
    /**
     * WebView 注入对象
     */
    private ArrayMap<String, Object> mJavaObjects = new ArrayMap<>();
    /**
     * 用于表示当前在 Fragment 使用还是 Activity 上使用
     */
    private int TAG_TARGET = 0;
    /**
     * WebListenerManager
     */
    private WebListenerManager mWebListenerManager;
    /**
     * 安全把控
     */
    private WebSecurityController<WebSecurityCheckLogic> mWebSecurityController = null;
    /**
     * WebSecurityCheckLogic
     */
    private WebSecurityCheckLogic mWebSecurityCheckLogic = null;
    /**
     * WebChromeClient
     */
    private WebChromeClient mTargetChromeClient;
    /**
     * 安全类型
     */
    private SecurityType mSecurityType = SecurityType.DEFAULT_CHECK;
    /**
     * Activity 标识
     */
    private static final int ACTIVITY_TAG = 0;
    /**
     * Fragment 标识
     */
    private static final int FRAGMENT_TAG = 1;
    /**
     * Web 注入对象
     */
    private WebJsInterfaceCompat mWebJsInterfaceCompat = null;
    /**
     * JsAccessEntrace 提供快速的JS调用
     */
    private JsAccessEntrace mJsAccessEntrace = null;
    /**
     * URL Loader ， 封装了 mWebView.loadUrl(url) reload() stopLoading（） postUrl()等方法
     */
    private IUrlLoader mIUrlLoader = null;
    /**
     * WebView 生命周期 ， 适当的释放CPU
     */
    private WebLifeCycle mWebLifeCycle;
    /**
     * Video 视屏播放类
     */
    private IVideo mIVideo = null;
    /**
     * WebViewClient 辅助控制开关
     */
    private boolean mWebClientHelper = true;
    /**
     * PermissionInterceptor 权限拦截
     */
    private PermissionInterceptor mPermissionInterceptor;
    /**
     * 是否拦截未知的Url， 用于 DefaultWebClient
     */
    private boolean mIsInterceptUnkownUrl = false;
    /**
     * 该变量控制了是否咨询用户页面跳转，或者直接拦截
     */
    private int mUrlHandleWays = -1;
    /**
     * MiddlewareWebClientBase WebViewClient 中间件
     */
    private MiddlewareWebClientBase mMiddleWrareWebClientBaseHeader;
    /**
     * MiddlewareWebChromeBase WebChromeClient 中间件
     */
    private MiddlewareWebChromeBase mMiddlewareWebChromeBaseHeader;
    /**
     * 事件拦截
     */
    private EventInterceptor mEventInterceptor;


    private JsInterfaceHolder mJsInterfaceHolder = null;


    private XWeb(XBuilder builder) {
        TAG_TARGET = builder.mTag;
        this.mActivity = builder.mActivity;
        this.mViewGroup = builder.mViewGroup;
        this.mIEventHandler = builder.mIEventHandler;
        this.mEnableIndicator = builder.mEnableIndicator;
        mWebCreator = builder.mWebCreator == null ? configWebCreator(builder.mBaseIndicatorView, builder.mIndex, builder.mLayoutParams, builder.mIndicatorColor, builder.mHeight, builder.mWebView, builder.mWebLayout) : builder.mWebCreator;
        mIndicatorController = builder.mIndicatorController;
        this.mWebChromeClient = builder.mWebChromeClient;
        this.mWebViewClient = builder.mWebViewClient;
        mWeb = this;
        this.mWebSettings = builder.mWebSettings;

        if (builder.mJavaObject != null && !builder.mJavaObject.isEmpty()) {
            this.mJavaObjects.putAll((Map<? extends String, ?>) builder.mJavaObject);
        }
        this.mPermissionInterceptor = builder.mPermissionInterceptor == null ? null : new PermissionInterceptorWrapper(builder.mPermissionInterceptor);
        this.mSecurityType = builder.mSecurityType;
        this.mIUrlLoader = new UrlLoaderImpl(mWebCreator.create().getWebView());
        if (this.mWebCreator.getWebParentLayout() instanceof WebParentLayout) {
            WebParentLayout mWebParentLayout = (WebParentLayout) this.mWebCreator.getWebParentLayout();
            mWebParentLayout.bindController(builder.mWebUIController == null ? WebUIControllerImplBase.build() : builder.mWebUIController);
            mWebParentLayout.setErrorLayoutRes(builder.mErrorLayout, builder.mReloadId);
            mWebParentLayout.setErrorView(builder.mErrorView);
        }
        this.mWebLifeCycle = new DefaultWebLifeCycleImpl(mWebCreator.getWebView());
        mWebSecurityController = new WebSecurityControllerImpl(mWebCreator.getWebView(), this.mWeb.mJavaObjects, this.mSecurityType);
        this.mWebClientHelper = builder.mWebClientHelper;
        this.mIsInterceptUnkownUrl = builder.mIsInterceptUnkownUrl;
        if (builder.mOpenOtherPage != null) {
            this.mUrlHandleWays = builder.mOpenOtherPage.code;
        }
        this.mMiddleWrareWebClientBaseHeader = builder.mMiddlewareWebClientBaseHeader;
        this.mMiddlewareWebChromeBaseHeader = builder.mChromeMiddleWareHeader;
        init();
    }


    /**
     * @return PermissionInterceptor 权限控制者
     */
    public PermissionInterceptor getPermissionInterceptor() {
        return this.mPermissionInterceptor;
    }


    public WebLifeCycle getWebLifeCycle() {
        return this.mWebLifeCycle;
    }

    /**
     * 调用js
     * @return
     */
    public JsAccessEntrace getJsAccessEntrace() {

        JsAccessEntrace mJsAccessEntrace = this.mJsAccessEntrace;
        if (mJsAccessEntrace == null) {
            this.mJsAccessEntrace = mJsAccessEntrace = JsAccessEntraceImpl.getInstance(mWebCreator.getWebView());
        }
        return mJsAccessEntrace;
    }


    public XWeb clearWebCache() {

        if (this.getWebCreator().getWebView() != null) {
            XWebUtils.clearWebViewAllCache(mActivity, this.getWebCreator().getWebView());
        } else {
            XWebUtils.clearWebViewAllCache(mActivity);
        }
        return this;
    }


    /**
     * 这个是对于webview在xml里面已经写入了
     *
     * @param activity
     * @return
     */
    public static IndicatorBuilder withXml(@NonNull Activity activity) {
        if (activity == null) {
            throw new NullPointerException("activity can not be null .");
        }
        return new IndicatorBuilder(new XBuilder(activity));
    }

    public static XBuilder with(@NonNull Activity activity) {
        if (activity == null) {
            throw new NullPointerException("activity can not be null .");
        }
        return new XBuilder(activity);
    }

    public static XBuilder with(@NonNull Fragment fragment) {


        Activity mActivity = null;
        if ((mActivity = fragment.getActivity()) == null) {
            throw new NullPointerException("activity can not be null .");
        }
        return new XBuilder(mActivity, fragment);
    }

    /**
     * 处理物理返回键
     *
     * @param keyCode
     * @param keyEvent
     * @return
     */
    public boolean handleKeyEvent(int keyCode, KeyEvent keyEvent) {

        if (mIEventHandler == null) {
            mIEventHandler = EventHandlerImpl.getInstantce(mWebCreator.getWebView(), getInterceptor());
        }
        return mIEventHandler.onKeyDown(keyCode, keyEvent);
    }

    /**
     * 非物理返回
     *
     * @return
     */
    public boolean back() {

        if (mIEventHandler == null) {
            mIEventHandler = EventHandlerImpl.getInstantce(mWebCreator.getWebView(), getInterceptor());
        }
        return mIEventHandler.back();
    }


    public WebCreator getWebCreator() {
        return this.mWebCreator;
    }

    public IEventHandler getIEventHandler() {
        return this.mIEventHandler == null ? (this.mIEventHandler = EventHandlerImpl.getInstantce(mWebCreator.getWebView(), getInterceptor())) : this.mIEventHandler;
    }


    public IWebSettings getWebSettings() {
        return this.mWebSettings;
    }

    public IndicatorController getIndicatorController() {
        return this.mIndicatorController;
    }

    public JsInterfaceHolder getJsInterfaceHolder() {
        return this.mJsInterfaceHolder;
    }

    public IUrlLoader getUrlLoader() {
        return this.mIUrlLoader;
    }

    public void destroy() {
        this.mWebLifeCycle.onDestroy();
    }

    public static class PreXWeb {
        private XWeb mWeb;
        private boolean isReady = false;

        PreXWeb(XWeb web) {
            this.mWeb = web;
        }


        public PreXWeb ready() {
            if (!isReady) {
                mWeb.ready();
                isReady = true;
            }
            return this;
        }

        public XWeb go(@Nullable String url) {
            if (!isReady) {
                ready();
            }
            return mWeb.go(url);
        }


    }


    private void doSafeCheck() {

        WebSecurityCheckLogic mWebSecurityCheckLogic = this.mWebSecurityCheckLogic;
        if (mWebSecurityCheckLogic == null) {
            this.mWebSecurityCheckLogic = mWebSecurityCheckLogic = WebSecurityLogicImpl.getInstance();
        }
        mWebSecurityController.check(mWebSecurityCheckLogic);

    }

    /**
     * 添加兼容的js接口
     */
    private void doCompat() {
        mJavaObjects.put("xweb", mWebJsInterfaceCompat = new WebJsInterfaceCompat(this, mActivity));
    }

    private WebCreator configWebCreator(BaseIndicatorView progressView, int index, ViewGroup.LayoutParams lp, int indicatorColor, int height_dp, WebView webView, IWebLayout webLayout) {

        if (progressView != null && mEnableIndicator) {
            return new DefaultWebCreator(mActivity, mViewGroup, lp, index, progressView, webView, webLayout);
        } else {
            return mEnableIndicator ?
                    new DefaultWebCreator(mActivity, mViewGroup, lp, index, indicatorColor, height_dp, webView, webLayout)
                    : new DefaultWebCreator(mActivity, mViewGroup, lp, index, webView, webLayout);
        }
    }

    private XWeb go(String url) {
        this.getUrlLoader().loadUrl(url);
        IndicatorController mIndicatorController = null;
        if (!TextUtils.isEmpty(url) && (mIndicatorController = getIndicatorController()) != null && mIndicatorController.offerIndicator() != null) {
            getIndicatorController().offerIndicator().show();
        }
        return this;
    }

    private EventInterceptor getInterceptor() {

        if (this.mEventInterceptor != null) {
            return this.mEventInterceptor;
        }

        if (mIVideo instanceof VideoImpl) {
            return this.mEventInterceptor = (EventInterceptor) this.mIVideo;
        }

        return null;

    }

    private void init() {
        doCompat();
        doSafeCheck();
    }

    private IVideo getIVideo() {
        return mIVideo == null ? new VideoImpl(mActivity, mWebCreator.getWebView()) : mIVideo;
    }

    private WebViewClient getWebViewClient() {

        DefaultWebClient mDefaultWebClient = DefaultWebClient
                .createBuilder()
                .setActivity(this.mActivity)
                .setClient(this.mWebViewClient)
                .setWebClientHelper(this.mWebClientHelper)
                .setPermissionInterceptor(this.mPermissionInterceptor)
                .setWebView(this.mWebCreator.getWebView())
                .setInterceptUnkownUrl(this.mIsInterceptUnkownUrl)
                .setUrlHandleWays(this.mUrlHandleWays)
                .build();
        MiddlewareWebClientBase header = this.mMiddleWrareWebClientBaseHeader;
        if (header != null) {
            MiddlewareWebClientBase tail = header;
            int count = 1;
            MiddlewareWebClientBase tmp = header;
            while (tmp.next() != null) {
                tail = tmp = tmp.next();
                count++;
            }
            tail.setDelegate(mDefaultWebClient);
            return header;
        } else {
            return mDefaultWebClient;
        }

    }


    private XWeb ready() {

        XWebConfig.initCookiesManager(mActivity.getApplicationContext());
        IWebSettings mWebSettings = this.mWebSettings;
        if (mWebSettings == null) {
            this.mWebSettings = mWebSettings = AbsXWebSettings.getInstance();
        }

        if (mWebSettings instanceof AbsXWebSettings) {
            ((AbsXWebSettings) mWebSettings).bindWeb(this);
        }
        if (mWebListenerManager == null && mWebSettings instanceof AbsXWebSettings) {
            mWebListenerManager = (WebListenerManager) mWebSettings;
        }
        mWebSettings.toSetting(mWebCreator.getWebView());
        if (mJsInterfaceHolder == null) {
            mJsInterfaceHolder = JsInterfaceHolderImpl.getJsInterfaceHolder(mWebCreator.getWebView(), this.mSecurityType);
        }
        if (mJavaObjects != null && !mJavaObjects.isEmpty()) {
            mJsInterfaceHolder.addJavaObjects(mJavaObjects);
        }

        if (mWebListenerManager != null) {
            mWebListenerManager.setDownloader(mWebCreator.getWebView(), null);
            mWebListenerManager.setWebChromeClient(mWebCreator.getWebView(), getChromeClient());
            mWebListenerManager.setWebViewClient(mWebCreator.getWebView(), getWebViewClient());
        }

        return this;
    }

    private WebChromeClient getChromeClient() {
        IndicatorController mIndicatorController =
                (this.mIndicatorController == null) ?
                        IndicatorHandler.getInstance().inJectIndicator(mWebCreator.offer())
                        : this.mIndicatorController;

        DefaultChromeClient mDefaultChromeClient =
                new DefaultChromeClient(this.mActivity,
                        this.mIndicatorController = mIndicatorController,
                        this.mWebChromeClient, this.mIVideo = getIVideo(),
                        this.mPermissionInterceptor, mWebCreator.getWebView());

        MiddlewareWebChromeBase header = this.mMiddlewareWebChromeBaseHeader;
        if (header != null) {
            MiddlewareWebChromeBase tail = header;
            int count = 1;
            MiddlewareWebChromeBase tmp = header;
            for (; tmp.next() != null; ) {
                tail = tmp = tmp.next();
                count++;
            }
            tail.setDelegate(mDefaultChromeClient);
            return this.mTargetChromeClient = header;
        } else {
            return this.mTargetChromeClient = mDefaultChromeClient;
        }
    }


    public enum SecurityType {
        DEFAULT_CHECK, STRICT_CHECK;
    }


    public static final class XBuilder {
        private Activity mActivity;
        private Fragment mFragment;
        private ViewGroup mViewGroup;
        private boolean mIsNeedDefaultProgress;
        private int mIndex = -1;
        private BaseIndicatorView mBaseIndicatorView;
        private IndicatorController mIndicatorController = null;
        /*默认进度条是显示的*/
        private boolean mEnableIndicator = true;
        private ViewGroup.LayoutParams mLayoutParams = null;
        private WebViewClient mWebViewClient;
        private WebChromeClient mWebChromeClient;
        private int mIndicatorColor = -1;
        private IWebSettings mWebSettings;
        private WebCreator mWebCreator;
        private IEventHandler mIEventHandler;
        private int mHeight = -1;
        private ArrayMap<String, Object> mJavaObject;
        private SecurityType mSecurityType = SecurityType.DEFAULT_CHECK;
        private WebView mWebView;
        private boolean mWebClientHelper = true;
        private IWebLayout mWebLayout = null;
        private PermissionInterceptor mPermissionInterceptor = null;
        private AbsWebUIController mWebUIController;
        private DefaultWebClient.OpenOtherPageWays mOpenOtherPage = null;
        private boolean mIsInterceptUnkownUrl = false;
        private MiddlewareWebClientBase mMiddlewareWebClientBaseHeader;
        private MiddlewareWebClientBase mMiddlewareWebClientBaseTail;
        private MiddlewareWebChromeBase mChromeMiddleWareHeader = null;
        private MiddlewareWebChromeBase mChromeMiddleWareTail = null;
        private View mErrorView;
        private int mErrorLayout;
        private int mReloadId;
        private int mTag = -1;


        public XBuilder(@NonNull Activity activity, @NonNull Fragment fragment) {
            mActivity = activity;
            mFragment = fragment;
            mTag = XWeb.FRAGMENT_TAG;
        }

        public XBuilder(@NonNull Activity activity) {
            mActivity = activity;
            mTag = XWeb.ACTIVITY_TAG;
        }

        /**
         * 传入WebView的父控件。
         */
        public IndicatorBuilder setWebParent(@NonNull ViewGroup v, @NonNull ViewGroup.LayoutParams lp) {
            this.mViewGroup = v;
            this.mLayoutParams = lp;
            return new IndicatorBuilder(this);
        }

        /**
         * 传入WebView的父控件。
         */
        public IndicatorBuilder setWebParent(@NonNull ViewGroup v, int index, @NonNull ViewGroup.LayoutParams lp) {
            this.mViewGroup = v;
            this.mLayoutParams = lp;
            this.mIndex = index;
            return new IndicatorBuilder(this);
        }

        private PreXWeb buildWeb() {
            if (mTag == XWeb.FRAGMENT_TAG && this.mViewGroup == null) {
                throw new NullPointerException("ViewGroup is null,Please check your parameters .");
            }
            return new PreXWeb(new XWeb(this));
        }

        private void addJavaObject(String key, Object o) {
            if (mJavaObject == null) {
                mJavaObject = new ArrayMap<>();
            }
            mJavaObject.put(key, o);
        }
    }

    /**
     * 设置进度条颜色与高度，-1为默认值，高度为2，单位为dp。
     */
    public static class IndicatorBuilder {
        private XBuilder mBuilder = null;

        public IndicatorBuilder(XBuilder builder) {
            this.mBuilder = builder;
        }

        public CommonBuilder useDefaultIndicator(int color) {
            this.mBuilder.mEnableIndicator = true;
            this.mBuilder.mIndicatorColor = color;
            return new CommonBuilder(mBuilder);
        }

        public CommonBuilder useDefaultIndicator() {
            this.mBuilder.mEnableIndicator = true;
            return new CommonBuilder(mBuilder);
        }

        public CommonBuilder closeIndicator() {
            this.mBuilder.mEnableIndicator = false;
            this.mBuilder.mIndicatorColor = -1;
            this.mBuilder.mHeight = -1;
            return new CommonBuilder(mBuilder);
        }

        public CommonBuilder setCustomIndicator(@NonNull BaseIndicatorView v) {
            if (v != null) {
                this.mBuilder.mEnableIndicator = true;
                this.mBuilder.mBaseIndicatorView = v;
                this.mBuilder.mIsNeedDefaultProgress = false;
            } else {
                this.mBuilder.mEnableIndicator = true;
                this.mBuilder.mIsNeedDefaultProgress = true;
            }

            return new CommonBuilder(mBuilder);
        }

        public CommonBuilder useDefaultIndicator(@ColorInt int color, int height_dp) {
            this.mBuilder.mIndicatorColor = color;
            this.mBuilder.mHeight = height_dp;
            return new CommonBuilder(this.mBuilder);
        }

    }


    public static class CommonBuilder {
        private XBuilder mBuilder;

        public CommonBuilder(XBuilder builder) {
            this.mBuilder = builder;
        }

        public CommonBuilder setEventHanadler(@Nullable IEventHandler iEventHandler) {
            mBuilder.mIEventHandler = iEventHandler;
            return this;
        }

        public CommonBuilder closeWebViewClientHelper() {
            mBuilder.mWebClientHelper = false;
            return this;
        }


        public CommonBuilder setWebChromeClient(@Nullable WebChromeClient webChromeClient) {
            this.mBuilder.mWebChromeClient = webChromeClient;
            return this;

        }

        public CommonBuilder setWebViewClient(@Nullable WebViewClient webChromeClient) {
            this.mBuilder.mWebViewClient = webChromeClient;
            return this;
        }

        public CommonBuilder useMiddlewareWebClient(@NonNull MiddlewareWebClientBase middleWrareWebClientBase) {
            if (middleWrareWebClientBase == null) {
                return this;
            }
            if (this.mBuilder.mMiddlewareWebClientBaseHeader == null) {
                this.mBuilder.mMiddlewareWebClientBaseHeader = this.mBuilder.mMiddlewareWebClientBaseTail = middleWrareWebClientBase;
            } else {
                this.mBuilder.mMiddlewareWebClientBaseTail.enq(middleWrareWebClientBase);
                this.mBuilder.mMiddlewareWebClientBaseTail = middleWrareWebClientBase;
            }
            return this;
        }

        public CommonBuilder useMiddlewareWebChrome(@NonNull MiddlewareWebChromeBase middlewareWebChromeBase) {
            if (middlewareWebChromeBase == null) {
                return this;
            }
            if (this.mBuilder.mChromeMiddleWareHeader == null) {
                this.mBuilder.mChromeMiddleWareHeader = this.mBuilder.mChromeMiddleWareTail = middlewareWebChromeBase;
            } else {
                this.mBuilder.mChromeMiddleWareTail.enq(middlewareWebChromeBase);
                this.mBuilder.mChromeMiddleWareTail = middlewareWebChromeBase;
            }
            return this;
        }

        public CommonBuilder setMainFrameErrorView(@NonNull View view) {
            this.mBuilder.mErrorView = view;
            return this;
        }

        /**
         * @param errorLayout 错误显示的布局
         * @param clickViewId 点击刷新控件ID -1表示点击整个布局都刷新
         * @return
         */
        public CommonBuilder setMainFrameErrorView(@LayoutRes int errorLayout, @IdRes int clickViewId) {
            this.mBuilder.mErrorLayout = errorLayout;
            this.mBuilder.mReloadId = clickViewId;
            return this;
        }

        public CommonBuilder setWebWebSettings(@Nullable IWebSettings webSettings) {
            this.mBuilder.mWebSettings = webSettings;
            return this;
        }

        public PreXWeb createWeb() {
            return this.mBuilder.buildWeb();
        }


        public CommonBuilder addJavascriptInterface(@NonNull String name, @NonNull Object o) {
            this.mBuilder.addJavaObject(name, o);
            return this;
        }

        public CommonBuilder setSecurityType(@NonNull SecurityType type) {
            this.mBuilder.mSecurityType = type;
            return this;
        }

        /**
         * 设置webview
         *
         * @param webView
         * @return
         */
        public CommonBuilder setWebView(@Nullable WebView webView) {
            this.mBuilder.mWebView = webView;
            return this;
        }

        /**
         * 设置web布局
         *
         * @param iWebLayout
         * @return
         */
        public CommonBuilder setWebLayout(@Nullable IWebLayout iWebLayout) {
            this.mBuilder.mWebLayout = iWebLayout;
            return this;
        }

        /**
         * 设置权限拦截
         *
         * @param permissionInterceptor
         * @return
         */
        public CommonBuilder setPermissionInterceptor(@Nullable PermissionInterceptor permissionInterceptor) {
            this.mBuilder.mPermissionInterceptor = permissionInterceptor;
            return this;
        }

        /**
         * 自定义UI
         *
         * @param webUIController
         * @return
         */
        public CommonBuilder setWebUIController(@Nullable WebUIControllerImplBase webUIController) {
            this.mBuilder.mWebUIController = webUIController;
            return this;
        }

        public CommonBuilder setOpenOtherPageWays(@Nullable DefaultWebClient.OpenOtherPageWays openOtherPageWays) {
            this.mBuilder.mOpenOtherPage = openOtherPageWays;
            return this;
        }

        public CommonBuilder interceptUnkownUrl() {
            this.mBuilder.mIsInterceptUnkownUrl = true;
            return this;
        }

    }

    private static final class PermissionInterceptorWrapper implements PermissionInterceptor {

        private WeakReference<PermissionInterceptor> mWeakReference;

        private PermissionInterceptorWrapper(PermissionInterceptor permissionInterceptor) {
            this.mWeakReference = new WeakReference<PermissionInterceptor>(permissionInterceptor);
        }

        @Override
        public boolean intercept(String url, String[] permissions, String a) {
            if (this.mWeakReference.get() == null) {
                return false;
            }
            return mWeakReference.get().intercept(url, permissions, a);
        }
    }


}

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

package com.ashlikun.webproxy;

import android.app.Activity;
import android.support.annotation.ColorInt;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.util.ArrayMap;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.ashlikun.webproxy.event.EventHandlerImpl;
import com.ashlikun.webproxy.event.EventInterceptor;
import com.ashlikun.webproxy.event.IEventHandler;
import com.ashlikun.webproxy.indicator.BaseIndicatorView;
import com.ashlikun.webproxy.indicator.IndicatorController;
import com.ashlikun.webproxy.indicator.IndicatorHandler;
import com.ashlikun.webproxy.js.JsAccessEntrace;
import com.ashlikun.webproxy.js.JsAccessEntraceImpl;
import com.ashlikun.webproxy.js.JsInterfaceHolder;
import com.ashlikun.webproxy.js.JsInterfaceHolderImpl;
import com.ashlikun.webproxy.js.WebJsInterfaceCompat;
import com.ashlikun.webproxy.lifecycle.DefaultWebLifeCycleImpl;
import com.ashlikun.webproxy.lifecycle.WebLifeCycle;
import com.ashlikun.webproxy.media.IVideo;
import com.ashlikun.webproxy.media.VideoImpl;
import com.ashlikun.webproxy.security.WebSecurityCheckLogic;
import com.ashlikun.webproxy.security.WebSecurityController;
import com.ashlikun.webproxy.security.WebSecurityControllerImpl;
import com.ashlikun.webproxy.security.WebSecurityLogicImpl;
import com.ashlikun.webproxy.ui.AbsWebUIController;
import com.ashlikun.webproxy.ui.WebParentLayout;
import com.ashlikun.webproxy.ui.WebUIControllerImplBase;
import com.ashlikun.webproxy.websetting.AbsWebProxySettings;
import com.ashlikun.webproxy.websetting.DefaultChromeClient;
import com.ashlikun.webproxy.websetting.DefaultWebClient;
import com.ashlikun.webproxy.websetting.IAgentWebSettings;
import com.ashlikun.webproxy.websetting.MiddlewareWebChromeBase;
import com.ashlikun.webproxy.websetting.MiddlewareWebClientBase;
import com.ashlikun.webproxy.websetting.PermissionInterceptor;
import com.ashlikun.webproxy.websetting.WebListenerManager;
import com.ashlikun.webproxy.webview.DefaultWebCreator;
import com.ashlikun.webproxy.webview.IUrlLoader;
import com.ashlikun.webproxy.webview.IWebLayout;
import com.ashlikun.webproxy.webview.UrlLoaderImpl;
import com.ashlikun.webproxy.webview.WebCreator;

import java.lang.ref.WeakReference;
import java.util.Map;

/**
 * @author　　: 李坤
 * 创建时间: 2018/9/21 11:41
 * 邮箱　　：496546144@qq.com
 * <p>
 * 功能介绍：是一个轻量级而且功能强大的 Web 库
 */

public final class WebProxy {
    /**
     * AgentWeb TAG
     */
    private static final String TAG = WebProxy.class.getSimpleName();
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
    private IAgentWebSettings mAgentWebSettings;
    /**
     * AgentWeb
     */
    private WebProxy mAgentWeb = null;
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
     * AgentWeb 注入对象
     */
    private WebJsInterfaceCompat mAgentWebJsInterfaceCompat = null;
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


    private WebProxy(ProxyBuilder agentBuilder) {
        TAG_TARGET = agentBuilder.mTag;
        this.mActivity = agentBuilder.mActivity;
        this.mViewGroup = agentBuilder.mViewGroup;
        this.mIEventHandler = agentBuilder.mIEventHandler;
        this.mEnableIndicator = agentBuilder.mEnableIndicator;
        mWebCreator = agentBuilder.mWebCreator == null ? configWebCreator(agentBuilder.mBaseIndicatorView, agentBuilder.mIndex, agentBuilder.mLayoutParams, agentBuilder.mIndicatorColor, agentBuilder.mHeight, agentBuilder.mWebView, agentBuilder.mWebLayout) : agentBuilder.mWebCreator;
        mIndicatorController = agentBuilder.mIndicatorController;
        this.mWebChromeClient = agentBuilder.mWebChromeClient;
        this.mWebViewClient = agentBuilder.mWebViewClient;
        mAgentWeb = this;
        this.mAgentWebSettings = agentBuilder.mAgentWebSettings;

        if (agentBuilder.mJavaObject != null && !agentBuilder.mJavaObject.isEmpty()) {
            this.mJavaObjects.putAll((Map<? extends String, ?>) agentBuilder.mJavaObject);
        }
        this.mPermissionInterceptor = agentBuilder.mPermissionInterceptor == null ? null : new PermissionInterceptorWrapper(agentBuilder.mPermissionInterceptor);
        this.mSecurityType = agentBuilder.mSecurityType;
        this.mIUrlLoader = new UrlLoaderImpl(mWebCreator.create().getWebView());
        if (this.mWebCreator.getWebParentLayout() instanceof WebParentLayout) {
            WebParentLayout mWebParentLayout = (WebParentLayout) this.mWebCreator.getWebParentLayout();
            mWebParentLayout.bindController(agentBuilder.mWebUIController == null ? WebUIControllerImplBase.build() : agentBuilder.mWebUIController);
            mWebParentLayout.setErrorLayoutRes(agentBuilder.mErrorLayout, agentBuilder.mReloadId);
            mWebParentLayout.setErrorView(agentBuilder.mErrorView);
        }
        this.mWebLifeCycle = new DefaultWebLifeCycleImpl(mWebCreator.getWebView());
        mWebSecurityController = new WebSecurityControllerImpl(mWebCreator.getWebView(), this.mAgentWeb.mJavaObjects, this.mSecurityType);
        this.mWebClientHelper = agentBuilder.mWebClientHelper;
        this.mIsInterceptUnkownUrl = agentBuilder.mIsInterceptUnkownUrl;
        if (agentBuilder.mOpenOtherPage != null) {
            this.mUrlHandleWays = agentBuilder.mOpenOtherPage.code;
        }
        this.mMiddleWrareWebClientBaseHeader = agentBuilder.mMiddlewareWebClientBaseHeader;
        this.mMiddlewareWebChromeBaseHeader = agentBuilder.mChromeMiddleWareHeader;
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


    public JsAccessEntrace getJsAccessEntrace() {

        JsAccessEntrace mJsAccessEntrace = this.mJsAccessEntrace;
        if (mJsAccessEntrace == null) {
            this.mJsAccessEntrace = mJsAccessEntrace = JsAccessEntraceImpl.getInstance(mWebCreator.getWebView());
        }
        return mJsAccessEntrace;
    }


    public WebProxy clearWebCache() {

        if (this.getWebCreator().getWebView() != null) {
            WebProxyUtils.clearWebViewAllCache(mActivity, this.getWebCreator().getWebView());
        } else {
            WebProxyUtils.clearWebViewAllCache(mActivity);
        }
        return this;
    }


    public static ProxyBuilder with(@NonNull Activity activity) {
        if (activity == null) {
            throw new NullPointerException("activity can not be null .");
        }
        return new ProxyBuilder(activity);
    }

    public static ProxyBuilder with(@NonNull Fragment fragment) {


        Activity mActivity = null;
        if ((mActivity = fragment.getActivity()) == null) {
            throw new NullPointerException("activity can not be null .");
        }
        return new ProxyBuilder(mActivity, fragment);
    }

    public boolean handleKeyEvent(int keyCode, KeyEvent keyEvent) {

        if (mIEventHandler == null) {
            mIEventHandler = EventHandlerImpl.getInstantce(mWebCreator.getWebView(), getInterceptor());
        }
        return mIEventHandler.onKeyDown(keyCode, keyEvent);
    }

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


    public IAgentWebSettings getAgentWebSettings() {
        return this.mAgentWebSettings;
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

    public static class PreAgentWeb {
        private WebProxy mAgentWeb;
        private boolean isReady = false;

        PreAgentWeb(WebProxy agentWeb) {
            this.mAgentWeb = agentWeb;
        }


        public PreAgentWeb ready() {
            if (!isReady) {
                mAgentWeb.ready();
                isReady = true;
            }
            return this;
        }

        public WebProxy go(@Nullable String url) {
            if (!isReady) {
                ready();
            }
            return mAgentWeb.go(url);
        }


    }


    private void doSafeCheck() {

        WebSecurityCheckLogic mWebSecurityCheckLogic = this.mWebSecurityCheckLogic;
        if (mWebSecurityCheckLogic == null) {
            this.mWebSecurityCheckLogic = mWebSecurityCheckLogic = WebSecurityLogicImpl.getInstance();
        }
        mWebSecurityController.check(mWebSecurityCheckLogic);

    }

    private void doCompat() {
        mJavaObjects.put("agentWeb", mAgentWebJsInterfaceCompat = new WebJsInterfaceCompat(this, mActivity));
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

    private WebProxy go(String url) {
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


    private WebProxy ready() {

        WebProxyConfig.initCookiesManager(mActivity.getApplicationContext());
        IAgentWebSettings mAgentWebSettings = this.mAgentWebSettings;
        if (mAgentWebSettings == null) {
            this.mAgentWebSettings = mAgentWebSettings = AbsWebProxySettings.getInstance();
        }

        if (mAgentWebSettings instanceof AbsWebProxySettings) {
            ((AbsWebProxySettings) mAgentWebSettings).bindAgentWeb(this);
        }
        if (mWebListenerManager == null && mAgentWebSettings instanceof AbsWebProxySettings) {
            mWebListenerManager = (WebListenerManager) mAgentWebSettings;
        }
        mAgentWebSettings.toSetting(mWebCreator.getWebView());
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


    public static final class ProxyBuilder {
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
        private IAgentWebSettings mAgentWebSettings;
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


        public ProxyBuilder(@NonNull Activity activity, @NonNull Fragment fragment) {
            mActivity = activity;
            mFragment = fragment;
            mTag = WebProxy.FRAGMENT_TAG;
        }

        public ProxyBuilder(@NonNull Activity activity) {
            mActivity = activity;
            mTag = WebProxy.ACTIVITY_TAG;
        }


        public IndicatorBuilder setAgentWebParent(@NonNull ViewGroup v, @NonNull ViewGroup.LayoutParams lp) {
            this.mViewGroup = v;
            this.mLayoutParams = lp;
            return new IndicatorBuilder(this);
        }

        public IndicatorBuilder setAgentWebParent(@NonNull ViewGroup v, int index, @NonNull ViewGroup.LayoutParams lp) {
            this.mViewGroup = v;
            this.mLayoutParams = lp;
            this.mIndex = index;
            return new IndicatorBuilder(this);
        }


        private PreAgentWeb buildAgentWeb() {
            if (mTag == WebProxy.FRAGMENT_TAG && this.mViewGroup == null) {
                throw new NullPointerException("ViewGroup is null,Please check your parameters .");
            }
            return new PreAgentWeb(new WebProxy(this));
        }

        private void addJavaObject(String key, Object o) {
            if (mJavaObject == null) {
                mJavaObject = new ArrayMap<>();
            }
            mJavaObject.put(key, o);
        }
    }

    public static class IndicatorBuilder {
        private ProxyBuilder mAgentBuilder = null;

        public IndicatorBuilder(ProxyBuilder agentBuilder) {
            this.mAgentBuilder = agentBuilder;
        }

        public CommonBuilder useDefaultIndicator(int color) {
            this.mAgentBuilder.mEnableIndicator = true;
            this.mAgentBuilder.mIndicatorColor = color;
            return new CommonBuilder(mAgentBuilder);
        }

        public CommonBuilder useDefaultIndicator() {
            this.mAgentBuilder.mEnableIndicator = true;
            return new CommonBuilder(mAgentBuilder);
        }

        public CommonBuilder closeIndicator() {
            this.mAgentBuilder.mEnableIndicator = false;
            this.mAgentBuilder.mIndicatorColor = -1;
            this.mAgentBuilder.mHeight = -1;
            return new CommonBuilder(mAgentBuilder);
        }

        public CommonBuilder setCustomIndicator(@NonNull BaseIndicatorView v) {
            if (v != null) {
                this.mAgentBuilder.mEnableIndicator = true;
                this.mAgentBuilder.mBaseIndicatorView = v;
                this.mAgentBuilder.mIsNeedDefaultProgress = false;
            } else {
                this.mAgentBuilder.mEnableIndicator = true;
                this.mAgentBuilder.mIsNeedDefaultProgress = true;
            }

            return new CommonBuilder(mAgentBuilder);
        }

        public CommonBuilder useDefaultIndicator(@ColorInt int color, int height_dp) {
            this.mAgentBuilder.mIndicatorColor = color;
            this.mAgentBuilder.mHeight = height_dp;
            return new CommonBuilder(this.mAgentBuilder);
        }

    }


    public static class CommonBuilder {
        private ProxyBuilder mAgentBuilder;

        public CommonBuilder(ProxyBuilder agentBuilder) {
            this.mAgentBuilder = agentBuilder;
        }

        public CommonBuilder setEventHanadler(@Nullable IEventHandler iEventHandler) {
            mAgentBuilder.mIEventHandler = iEventHandler;
            return this;
        }

        public CommonBuilder closeWebViewClientHelper() {
            mAgentBuilder.mWebClientHelper = false;
            return this;
        }


        public CommonBuilder setWebChromeClient(@Nullable WebChromeClient webChromeClient) {
            this.mAgentBuilder.mWebChromeClient = webChromeClient;
            return this;

        }

        public CommonBuilder setWebViewClient(@Nullable WebViewClient webChromeClient) {
            this.mAgentBuilder.mWebViewClient = webChromeClient;
            return this;
        }

        public CommonBuilder useMiddlewareWebClient(@NonNull MiddlewareWebClientBase middleWrareWebClientBase) {
            if (middleWrareWebClientBase == null) {
                return this;
            }
            if (this.mAgentBuilder.mMiddlewareWebClientBaseHeader == null) {
                this.mAgentBuilder.mMiddlewareWebClientBaseHeader = this.mAgentBuilder.mMiddlewareWebClientBaseTail = middleWrareWebClientBase;
            } else {
                this.mAgentBuilder.mMiddlewareWebClientBaseTail.enq(middleWrareWebClientBase);
                this.mAgentBuilder.mMiddlewareWebClientBaseTail = middleWrareWebClientBase;
            }
            return this;
        }

        public CommonBuilder useMiddlewareWebChrome(@NonNull MiddlewareWebChromeBase middlewareWebChromeBase) {
            if (middlewareWebChromeBase == null) {
                return this;
            }
            if (this.mAgentBuilder.mChromeMiddleWareHeader == null) {
                this.mAgentBuilder.mChromeMiddleWareHeader = this.mAgentBuilder.mChromeMiddleWareTail = middlewareWebChromeBase;
            } else {
                this.mAgentBuilder.mChromeMiddleWareTail.enq(middlewareWebChromeBase);
                this.mAgentBuilder.mChromeMiddleWareTail = middlewareWebChromeBase;
            }
            return this;
        }

        public CommonBuilder setMainFrameErrorView(@NonNull View view) {
            this.mAgentBuilder.mErrorView = view;
            return this;
        }

        public CommonBuilder setMainFrameErrorView(@LayoutRes int errorLayout, @IdRes int clickViewId) {
            this.mAgentBuilder.mErrorLayout = errorLayout;
            this.mAgentBuilder.mReloadId = clickViewId;
            return this;
        }

        public CommonBuilder setAgentWebWebSettings(@Nullable IAgentWebSettings agentWebSettings) {
            this.mAgentBuilder.mAgentWebSettings = agentWebSettings;
            return this;
        }

        public PreAgentWeb createAgentWeb() {
            return this.mAgentBuilder.buildAgentWeb();
        }


        public CommonBuilder addJavascriptInterface(@NonNull String name, @NonNull Object o) {
            this.mAgentBuilder.addJavaObject(name, o);
            return this;
        }

        public CommonBuilder setSecurityType(@NonNull SecurityType type) {
            this.mAgentBuilder.mSecurityType = type;
            return this;
        }

        public CommonBuilder setWebView(@Nullable WebView webView) {
            this.mAgentBuilder.mWebView = webView;
            return this;
        }

        public CommonBuilder setWebLayout(@Nullable IWebLayout iWebLayout) {
            this.mAgentBuilder.mWebLayout = iWebLayout;
            return this;
        }

        public CommonBuilder setPermissionInterceptor(@Nullable PermissionInterceptor permissionInterceptor) {
            this.mAgentBuilder.mPermissionInterceptor = permissionInterceptor;
            return this;
        }

        public CommonBuilder setWebUIController(@Nullable WebUIControllerImplBase webUIController) {
            this.mAgentBuilder.mWebUIController = webUIController;
            return this;
        }

        public CommonBuilder setOpenOtherPageWays(@Nullable DefaultWebClient.OpenOtherPageWays openOtherPageWays) {
            this.mAgentBuilder.mOpenOtherPage = openOtherPageWays;
            return this;
        }

        public CommonBuilder interceptUnkownUrl() {
            this.mAgentBuilder.mIsInterceptUnkownUrl = true;
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
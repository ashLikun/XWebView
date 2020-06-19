package com.ashlikun.xwebview.webview;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.webkit.WebView;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.ashlikun.xwebview.R;
import com.ashlikun.xwebview.XWebConfig;
import com.ashlikun.xwebview.XWebUtils;
import com.ashlikun.xwebview.indicator.BaseIndicatorSpec;
import com.ashlikun.xwebview.indicator.BaseIndicatorView;
import com.ashlikun.xwebview.indicator.WebIndicator;
import com.ashlikun.xwebview.ui.WebParentLayout;

/**
 * @author　　: 李坤
 * 创建时间: 2018/9/21 15:53
 * 邮箱　　：496546144@qq.com
 * <p>
 * 功能介绍：webview创建
 */

public class DefaultWebCreator implements WebCreator {

    private Context mContext;
    private ViewGroup mViewGroup;
    private boolean mIsNeedDefaultProgress;
    private int mIndex;
    private BaseIndicatorView mProgressView;
    private int mColor = -1;
    /**
     * 单位dp
     */
    private int mHeight;
    private boolean mIsCreated = false;
    private IWebLayout mIWebLayout;
    private BaseIndicatorSpec mBaseIndicatorSpec;
    private WebView mWebView = null;
    private FrameLayout mFrameLayout = null;
    private View mTargetProgress;

    /**
     * 使用默认的进度条
     *
     * @param viewGroup
     * @param index
     * @param color
     * @param mHeight
     * @param webView
     * @param webLayout
     */
    public DefaultWebCreator(@NonNull Context context,
                             @Nullable ViewGroup viewGroup,
                             int index,
                             int color,
                             int mHeight,
                             WebView webView,
                             IWebLayout webLayout) {
        this.mContext = context;
        this.mViewGroup = viewGroup;
        this.mIsNeedDefaultProgress = true;
        this.mIndex = index;
        this.mColor = color;
        this.mHeight = mHeight;
        this.mWebView = webView;
        this.mIWebLayout = webLayout;
    }

    /**
     * 关闭进度条
     *
     * @param context
     * @param viewGroup
     * @param index
     * @param webView
     * @param webLayout
     */
    public DefaultWebCreator(@NonNull Context context, @Nullable ViewGroup viewGroup, int index, @Nullable WebView webView, IWebLayout webLayout) {
        this.mContext = context;
        this.mViewGroup = viewGroup;
        this.mIsNeedDefaultProgress = false;
        this.mIndex = index;
        this.mWebView = webView;
        this.mIWebLayout = webLayout;
    }

    /**
     * 自定义Indicator
     *
     * @param context
     * @param viewGroup
     * @param index
     * @param progressView
     * @param webView
     * @param webLayout
     */
    public DefaultWebCreator(@NonNull Context context, @Nullable ViewGroup viewGroup, int index, BaseIndicatorView progressView, WebView webView, IWebLayout webLayout) {
        this.mContext = context;
        this.mViewGroup = viewGroup;
        this.mIsNeedDefaultProgress = false;
        this.mIndex = index;
        this.mProgressView = progressView;
        this.mWebView = webView;
        this.mIWebLayout = webLayout;
    }


    public void setWebView(WebView webView) {
        mWebView = webView;
    }

    public FrameLayout getFrameLayout() {
        return mFrameLayout;
    }


    public View getTargetProgress() {
        return mTargetProgress;
    }

    public void setTargetProgress(View targetProgress) {
        this.mTargetProgress = targetProgress;
    }

    @Override
    public DefaultWebCreator create() {


        if (mIsCreated) {
            return this;
        }
        mIsCreated = true;
        ViewGroup mViewGroup = this.mViewGroup;
        if (mViewGroup == null) {
            mViewGroup = (ViewGroup) mWebView.getParent();
        }
        ViewGroup.LayoutParams mLayoutParams = mWebView.getLayoutParams();
        if (mLayoutParams == null) {
            mLayoutParams = new ViewGroup.LayoutParams(-1, -1);
        }
        if (mIndex == -1) {
            mViewGroup.addView(this.mFrameLayout = (FrameLayout) createLayout(), mLayoutParams);
        } else {
            mViewGroup.addView(this.mFrameLayout = (FrameLayout) createLayout(), mIndex, mLayoutParams);
        }
        return this;
    }

    @Override
    public WebView getWebView() {
        return mWebView;
    }

    @Override
    public FrameLayout getWebParentLayout() {
        return mFrameLayout;
    }

    private ViewGroup createLayout() {
        WebParentLayout mFrameLayout = new WebParentLayout(mContext);
        mFrameLayout.setId(R.id.web_parent_layout_id);
        mFrameLayout.setBackgroundColor(Color.WHITE);
        View target = mIWebLayout == null ? (this.mWebView = (WebView) createWebView()) : webLayout();
        FrameLayout.LayoutParams mLayoutParams;
        if (mWebView.getLayoutParams() != null) {
            mLayoutParams = new FrameLayout.LayoutParams(mWebView.getLayoutParams());
        } else {
            mLayoutParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
        }

        if (target.getParent() != null) {
            ((ViewGroup) target.getParent()).removeView(target);
        }
        mFrameLayout.addView(target, mLayoutParams);
        mFrameLayout.bindWebView(this.mWebView);
        if (this.mWebView instanceof XWebView) {
            XWebConfig.WEBVIEW_TYPE = XWebConfig.WEBVIEW_XWEB_SAFE_TYPE;
        }
        ViewStub mViewStub = new ViewStub(mContext);
        mViewStub.setId(R.id.mainframe_error_viewsub_id);
        mFrameLayout.addView(mViewStub, new FrameLayout.LayoutParams(-1, -1));
        if (mIsNeedDefaultProgress) {
            FrameLayout.LayoutParams lp = null;
            WebIndicator mWebIndicator = new WebIndicator(mContext);
            if (mHeight > 0) {
                lp = new FrameLayout.LayoutParams(-2, XWebUtils.dp2px(mContext, mHeight));
            } else {
                lp = mWebIndicator.offerLayoutParams();
            }
            if (mColor != -1) {
                mWebIndicator.setColor(mColor);
            }
            lp.gravity = Gravity.TOP;
            mFrameLayout.addView((View) (this.mBaseIndicatorSpec = mWebIndicator), lp);
            mWebIndicator.setVisibility(View.GONE);
        } else if (!mIsNeedDefaultProgress && mProgressView != null) {
            mFrameLayout.addView((View) (this.mBaseIndicatorSpec = (BaseIndicatorSpec) mProgressView), mProgressView.offerLayoutParams());
            mProgressView.setVisibility(View.GONE);
        }
        return mFrameLayout;

    }


    private View webLayout() {
        WebView mWebView = null;
        if ((mWebView = mIWebLayout.getWebView()) == null) {
            mWebView = createWebView();
            mIWebLayout.getLayout().addView(mWebView, -1, -1);
        } else {
            XWebConfig.WEBVIEW_TYPE = XWebConfig.WEBVIEW_CUSTOM_TYPE;
        }
        this.mWebView = mWebView;
        return mIWebLayout.getLayout();

    }

    private WebView createWebView() {

        WebView mWebView = null;
        if (this.mWebView != null) {
            mWebView = this.mWebView;
            XWebConfig.WEBVIEW_TYPE = XWebConfig.WEBVIEW_CUSTOM_TYPE;
        } else if (XWebConfig.IS_KITKAT_OR_BELOW_KITKAT) {
            mWebView = new XWebView(mContext);
            XWebConfig.WEBVIEW_TYPE = XWebConfig.WEBVIEW_XWEB_SAFE_TYPE;
        } else {
            mWebView = new WebView(mContext);
            XWebConfig.WEBVIEW_TYPE = XWebConfig.WEBVIEW_DEFAULT_TYPE;
        }

        return mWebView;
    }

    @Override
    public BaseIndicatorSpec offer() {
        return mBaseIndicatorSpec;
    }
}

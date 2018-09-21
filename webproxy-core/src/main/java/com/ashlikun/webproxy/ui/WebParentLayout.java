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

package com.ashlikun.webproxy.ui;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.webkit.WebView;
import android.widget.FrameLayout;

import com.ashlikun.webproxy.R;

/**
 * @author　　: 李坤
 * 创建时间: 2018/9/21 14:24
 * 邮箱　　：496546144@qq.com
 * <p>
 * 功能介绍：webview父控件,封装显示错误页面
 */

public class WebParentLayout extends FrameLayout {
    private AbsWebUIController mAgentWebUIController = null;
    private static final String TAG = WebParentLayout.class.getSimpleName();
    @LayoutRes
    private int mErrorLayoutRes;
    @IdRes
    private int mClickId = -1;
    private View mErrorView;
    private WebView mWebView;
    private FrameLayout mErrorLayout = null;

    public WebParentLayout(@NonNull Context context) {
        this(context, null);
    }

    WebParentLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, -1);
    }

    WebParentLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if (!(context instanceof Activity)) {
            throw new IllegalArgumentException("WebParentLayout context must be activity or activity sub class .");
        }
        this.mErrorLayoutRes = R.layout.webproxy_error_page;
    }

    public void bindController(AbsWebUIController agentWebUIController) {
        this.mAgentWebUIController = agentWebUIController;
        this.mAgentWebUIController.bindWebParent(this, (Activity) getContext());
    }

    public void showPageMainFrameError() {

        View container = this.mErrorLayout;
        if (container != null) {
            container.setVisibility(View.VISIBLE);
        } else {
            createErrorLayout();
            container = this.mErrorLayout;
        }
        View clickView = null;
        if (mClickId != -1 && (clickView = container.findViewById(mClickId)) != null) {
            clickView.setClickable(true);
        } else {
            container.setClickable(true);
        }
    }

    private void createErrorLayout() {

        final FrameLayout mFrameLayout = new FrameLayout(getContext());
        mFrameLayout.setBackgroundColor(Color.WHITE);
        mFrameLayout.setId(R.id.mainframe_error_container_id);
        if (this.mErrorView == null) {
            LayoutInflater mLayoutInflater = LayoutInflater.from(getContext());
            mLayoutInflater.inflate(mErrorLayoutRes, mFrameLayout, true);
        } else {
            mFrameLayout.addView(mErrorView);
        }

        ViewStub mViewStub = (ViewStub) this.findViewById(R.id.mainframe_error_viewsub_id);
        final int index = this.indexOfChild(mViewStub);
        this.removeViewInLayout(mViewStub);
        final ViewGroup.LayoutParams layoutParams = getLayoutParams();
        if (layoutParams != null) {
            this.addView(this.mErrorLayout = mFrameLayout, index, layoutParams);
        } else {
            this.addView(this.mErrorLayout = mFrameLayout, index);
        }

        mFrameLayout.setVisibility(View.VISIBLE);
        if (mClickId != -1) {
            final View clickView = mFrameLayout.findViewById(mClickId);
            if (clickView != null) {
                clickView.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (getWebView() != null) {
                            clickView.setClickable(false);
                            getWebView().reload();
                        }
                    }
                });
                return;
            } else {
            }

        }

        mFrameLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getWebView() != null) {
                    mFrameLayout.setClickable(false);
                    getWebView().reload();
                }

            }
        });
    }

    public void hideErrorLayout() {
        View mView = null;
        if ((mView = this.findViewById(R.id.mainframe_error_container_id)) != null) {
            mView.setVisibility(View.GONE);
        }
    }

    public void setErrorView(@NonNull View errorView) {
        this.mErrorView = errorView;
    }

    public void setErrorLayoutRes(@LayoutRes int resLayout, @IdRes int id) {
        this.mClickId = id;
        if (this.mClickId <= 0) {
            this.mClickId = -1;
        }
        this.mErrorLayoutRes = resLayout;
        if (this.mErrorLayoutRes <= 0) {
            this.mErrorLayoutRes = R.layout.webproxy_error_page;
        }
    }

    public AbsWebUIController getWebUIController() {
        return this.mAgentWebUIController;
    }


    public void bindWebView(WebView view) {
        if (this.mWebView == null) {
            this.mWebView = view;
        }
    }

    WebView getWebView() {
        return this.mWebView;
    }


}

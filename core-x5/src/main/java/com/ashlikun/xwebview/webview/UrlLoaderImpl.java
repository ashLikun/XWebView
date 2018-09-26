package com.ashlikun.xwebview.webview;

import android.os.Handler;
import android.os.Looper;
import com.tencent.smtt.sdk.WebView;

import com.ashlikun.xwebview.XWebUtils;

import java.util.Map;

/**
 * @author　　: 李坤
 * 创建时间: 2018/9/21 16:45
 * 邮箱　　：496546144@qq.com
 * <p>
 * 功能介绍：封装加载url
 */

public class UrlLoaderImpl implements IUrlLoader {


    private Handler mHandler = null;
    private WebView mWebView;

    public UrlLoaderImpl(WebView webView) {
        this.mWebView = webView;
        if (this.mWebView == null) {
            new NullPointerException("webview cannot be null .");
        }
        mHandler = new Handler(Looper.getMainLooper());
    }

    private void safeLoadUrl(final String url) {

        mHandler.post(new Runnable() {
            @Override
            public void run() {
                loadUrl(url);
            }
        });
    }

    private void safeReload() {

        mHandler.post(new Runnable() {
            @Override
            public void run() {
                reload();
            }
        });
    }

    @Override
    public void loadUrl(String url) {
        this.loadUrl(url, null);
    }

    @Override
    public void loadUrl(final String url, final Map<String, String> headers) {

        if (!XWebUtils.isUIThread()) {
            XWebUtils.runInUiThread(new Runnable() {
                @Override
                public void run() {
                    loadUrl(url, headers);
                }
            });
        }
        if (headers == null || headers.isEmpty()) {
            this.mWebView.loadUrl(url);
        } else {
            this.mWebView.loadUrl(url, headers);
        }
    }

    @Override
    public void reload() {
        if (!XWebUtils.isUIThread()) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    reload();
                }
            });
            return;
        }
        this.mWebView.reload();


    }

    @Override
    public void loadData(final String data, final String mimeType, final String encoding) {

        if (!XWebUtils.isUIThread()) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    loadData(data, mimeType, encoding);
                }
            });
            return;
        }
        this.mWebView.loadData(data, mimeType, encoding);

    }

    @Override
    public void stopLoading() {

        if (!XWebUtils.isUIThread()) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    stopLoading();
                }
            });
            return;
        }
        this.mWebView.stopLoading();

    }

    @Override
    public void loadDataWithBaseURL(final String baseUrl, final String data, final String mimeType, final String encoding, final String historyUrl) {

        if (!XWebUtils.isUIThread()) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    loadDataWithBaseURL(baseUrl, data, mimeType, encoding, historyUrl);
                }
            });
            return;
        }
        this.mWebView.loadDataWithBaseURL(baseUrl, data, mimeType, encoding, historyUrl);

    }

    @Override
    public void postUrl(final String url, final byte[] postData) {

        if (!XWebUtils.isUIThread()) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    postUrl(url, postData);
                }
            });
            return;
        }

        this.mWebView.postUrl(url, postData);
    }
}

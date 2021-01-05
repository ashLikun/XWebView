package com.ashlikun.xwebview.simple;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.DownloadListener;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.ashlikun.xwebview.XWeb;
import com.ashlikun.xwebview.websetting.AbsXWebSettings;
import com.ashlikun.xwebview.websetting.IWebSettings;
import com.ashlikun.xwebview.websetting.WebListenerManager;
import com.ashlikun.xwebview.webview.XWebView;


public class CommonActivity extends AppCompatActivity {

    XWeb xWeb;
    TextView toolbarTitle;
    public String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        url = getIntent().getStringExtra("url");
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_common);
        toolbarTitle = findViewById(R.id.toolbar_title);
        ViewGroup webView = findViewById(R.id.webView);

//        webView.setNestedScrollingEnabled(true);
        xWeb = XWeb.with(webView)
                .useDefaultIndicator()
                .setWebWebSettings(getWebSettings())
                .setWebChromeClient(mWebChromeClient)
                .setWebViewClient(mWebViewClient)
                .createWeb()
                .ready()
                .ok();
//        xWeb.getUrlLoader().loadData(UrlConfig.AAA, "text/html; charset=UTF-8", null);
        xWeb.getUrlLoader().loadUrl(url);
        findViewById(R.id.iv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                xWeb.back();
            }
        });
        findViewById(R.id.iv_finish).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        findViewById(R.id.iv_more).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(CommonActivity.this, new WebView(CommonActivity.this).getSettings().getUserAgentString(), Toast.LENGTH_SHORT).show();
            }
        });

        //      注册监听方法当js中调用callHandler方法时会调用此方法（handlerName必须和js中相同）
//        webView.registerHandler("JsToAppHandler", new BridgeHandler() {
//            @Override
//            public void handler(String data, CallBackFunction function) {
//                Log.e("TAG", "js返回：" + data);
//                //显示js传递给Android的消息
//                Toast.makeText(CommonActivity.this, "js返回:" + data, Toast.LENGTH_LONG).show();
//            }
//        });
//        bt.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
////              调用js中的方法（必须和js中的handlerName想同）
//                webview.callHandler("functionInJs", "Android调用js66", new CallBackFunction() {
//                    @Override
//                    public void onCallBack(String data) {
//                        Log.e("TAG", "onCallBack:" + data);
//                        Toast.makeText(MainActivity.this, data, Toast.LENGTH_LONG).show();
//                    }
//                });
//            }
//        });
    }

    @Override
    public void applyOverrideConfiguration(Configuration overrideConfiguration) {
        super.applyOverrideConfiguration(overrideConfiguration);
    }

    protected WebViewClient mWebViewClient = new WebViewClient() {
        @Override
        public void onReceivedError(WebView webView, int i, String s, String s1) {
            super.onReceivedError(webView, i, s, s1);
            Log.e("CommonActivity", "onReceivedError   " + i + "  " + s + "   " + s1);
        }

        @Override
        public void onReceivedError(WebView webView, WebResourceRequest webResourceRequest, WebResourceError webResourceError) {
            super.onReceivedError(webView, webResourceRequest, webResourceError);
            Log.e("CommonActivity", "onReceivedError   " + webResourceError.getErrorCode() + "   " + webResourceError.getDescription());
        }

        @Override
        public void onReceivedHttpError(WebView webView, WebResourceRequest webResourceRequest, WebResourceResponse webResourceResponse) {
            super.onReceivedHttpError(webView, webResourceRequest, webResourceResponse);
            Log.e("CommonActivity", "onReceivedHttpError   ");
        }

        @Override
        public void onPageFinished(WebView webView, String s) {
            super.onPageFinished(webView, s);
            Log.e("CommonActivity", "onPageFinished   " + s);
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            Log.e("shouldOverrideUrl", url);
            return false;
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            Log.e("shouldOverrideUrl", url);
            return false;
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            Log.e("onPageStarted", url);
            super.onPageStarted(view, url, favicon);
        }
    };
    protected WebChromeClient mWebChromeClient = new WebChromeClient() {

        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            Log.e("CommonActivity", "onProgressChanged   " + newProgress);
        }

        @Override
        public void onReceivedTitle(WebView view, String title) {
            super.onReceivedTitle(view, title);
            if (toolbarTitle != null && !TextUtils.isEmpty(title)) {
                if (title.length() > 10) {
                    title = title.substring(0, 10).concat("...");
                }
            }
            toolbarTitle.setText(title);
        }
    };

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (xWeb.handleKeyEvent(keyCode, event)) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public void onClick(View view) {

    }

    @Override
    protected void onResume() {
        super.onResume();
        xWeb.getWebLifeCycle().onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        xWeb.getWebLifeCycle().onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        xWeb.getWebLifeCycle().onDestroy();
    }

    public static void start(Context context, String url) {
        Intent intent = new Intent(context, CommonActivity.class);
        intent.putExtra("url", url);
        context.startActivity(intent);
    }

    public IWebSettings getWebSettings() {
        return new AbsXWebSettings() {
            private XWeb xWeb;

            @Override
            protected void bindWebSupport(XWeb xWeb) {
                this.xWeb = xWeb;
            }

            @Override
            public WebListenerManager setDownloader(WebView webView, DownloadListener downloadListener) {
                return super.setDownloader(webView, downloadListener);
            }
        };
    }
}

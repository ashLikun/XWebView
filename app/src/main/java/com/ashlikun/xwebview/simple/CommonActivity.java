package com.ashlikun.xwebview.simple;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ashlikun.xwebview.XWeb;
import com.ashlikun.xwebview.websetting.AbsXWebSettings;
import com.ashlikun.xwebview.websetting.IWebSettings;
import com.ashlikun.xwebview.websetting.WebListenerManager;
import com.ashlikun.xwebview.webview.XWebView;
import com.tencent.smtt.sdk.DownloadListener;
import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.WebView;

public class CommonActivity extends AppCompatActivity {

    XWeb xWeb;
    LinearLayout rootView;
    TextView toolbarTitle;
    public String url;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        url = getIntent().getStringExtra("url");
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_common);
        rootView = findViewById(R.id.rootView);
        toolbarTitle = findViewById(R.id.toolbar_title);
        XWebView webView = findViewById(R.id.webView);
        xWeb = XWeb.withXml(this)
                .useDefaultIndicator()
                .setWebWebSettings(getWebSettings())
                .setWebView(webView)
                .setWebChromeClient(mWebChromeClient)
                .createWeb()
                .ready()
                .go(url);

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
                Log.e("", "啊啊啊啊啊");
            }
        });
    }

    protected WebChromeClient mWebChromeClient = new WebChromeClient() {
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
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

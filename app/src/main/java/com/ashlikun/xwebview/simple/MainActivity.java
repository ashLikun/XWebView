package com.ashlikun.xwebview.simple;

import android.os.Bundle;
import android.view.View;
import android.view.Window;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
    }


    public void onJindong(View view) {
        CommonActivity.start(this, UrlConfig.JD);
    }

    public void onHuitan(View view) {
        CommonActivity.start(this, UrlConfig.PULL_HUITAN);
    }

    public void onRenrenying(View view) {
//        CommonActivity.start(this, UrlConfig.RENREN_YIN);
        CommonActivity.start(this, UrlConfig.RENREN_YIN);
    }

    public void onDownload(View view) {
        CommonActivity.start(this, UrlConfig.DOWNLOAD);
    }

    public void onOgow11(View view) {
        //js方式上传文件要用 js对象内部已经有了xweb
        CommonActivity.start(this, UrlConfig.UPLOADFILE);
    }

    public void input(View view) {
        CommonActivity.start(this, UrlConfig.INPUT_UPLOAD_FILE);
    }
}

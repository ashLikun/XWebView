package com.ashlikun.xwebview.simple;

import android.app.Application;
import android.util.Log;

import com.tencent.smtt.sdk.QbSdk;

/**
 * 作者　　: 李坤
 * 创建时间: 2018/9/26　13:42
 * 邮箱　　：496546144@qq.com
 * <p>
 * 功能介绍：
 */
public class MyApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        QbSdk.initX5Environment(this, new QbSdk.PreInitCallback() {
            @Override
            public void onCoreInitFinished() {
                Log.e("QbSdk", "内核加载成功");
            }

            @Override
            public void onViewInitFinished(boolean b) {
                Log.e("QbSdk", "加载内核是否成功:" + b);
            }
        });

    }
}

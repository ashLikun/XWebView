package com.ashlikun.xwebview.js;

import android.os.Build;
import android.support.annotation.RequiresApi;
import com.tencent.smtt.sdk.ValueCallback;


/**
 * @author　　: 李坤
 * 创建时间: 2018/9/21 15:50
 * 邮箱　　：496546144@qq.com
 * <p>
 * 功能介绍：快速调用js
 */

public interface QuickCallJs {
    @RequiresApi(Build.VERSION_CODES.KITKAT)
    void quickCallJs(String method, ValueCallback<String> callback, String... params);

    void quickCallJs(String method, String... params);

    void quickCallJs(String method);
}

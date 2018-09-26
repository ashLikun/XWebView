package com.ashlikun.xwebview.js;

import com.tencent.smtt.sdk.ValueCallback;

/**
 * @author　　: 李坤
 * 创建时间: 2018/9/21 13:18
 * 邮箱　　：496546144@qq.com
 * <p>
 * 功能介绍：java回调js的接口
 */

public interface JsAccessEntrace extends QuickCallJs {
    /**
     * java回调js
     *
     * @param js       js方法
     * @param callback
     */
    void callJs(String js, ValueCallback<String> callback);

    void callJs(String js);
}

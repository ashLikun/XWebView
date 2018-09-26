package com.ashlikun.xwebview.indicator;

import android.webkit.WebView;

/**
 * @author　　: 李坤
 * 创建时间: 2018/9/21 15:49
 * 邮箱　　：496546144@qq.com
 * <p>
 * 功能介绍：指示器控制器
 */
public interface IndicatorController {

    void progress(WebView v, int newProgress);

    BaseIndicatorSpec offerIndicator();

    void showIndicator();

    void setProgress(int newProgress);

    void finish();
}

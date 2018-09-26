package com.ashlikun.xwebview.indicator;

import android.webkit.WebView;


/**
 * @author　　: 李坤
 * 创建时间: 2018/9/21 15:50
 * 邮箱　　：496546144@qq.com
 * <p>
 * 功能介绍：指示器具体实现类
 */

public class IndicatorHandler implements IndicatorController {
    private BaseIndicatorSpec mBaseIndicatorSpec;

    @Override
    public void progress(WebView v, int newProgress) {

        if (newProgress == 0) {
            reset();
        } else if (newProgress > 0 && newProgress <= 10) {
            showIndicator();
        } else if (newProgress > 10 && newProgress < 95) {
            setProgress(newProgress);
        } else {
            setProgress(newProgress);
            finish();
        }

    }

    @Override
    public BaseIndicatorSpec offerIndicator() {
        return this.mBaseIndicatorSpec;
    }

    public void reset() {

        if (mBaseIndicatorSpec != null) {
            mBaseIndicatorSpec.reset();
        }
    }

    @Override
    public void finish() {
        if (mBaseIndicatorSpec != null) {
            mBaseIndicatorSpec.hide();
        }
    }

    @Override
    public void setProgress(int n) {
        if (mBaseIndicatorSpec != null) {
            mBaseIndicatorSpec.setProgress(n);
        }
    }

    @Override
    public void showIndicator() {

        if (mBaseIndicatorSpec != null) {
            mBaseIndicatorSpec.show();
        }
    }

    public static IndicatorHandler getInstance() {
        return new IndicatorHandler();
    }


    public IndicatorHandler inJectIndicator(BaseIndicatorSpec baseIndicatorSpec) {
        this.mBaseIndicatorSpec = baseIndicatorSpec;
        return this;
    }
}

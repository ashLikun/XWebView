package com.ashlikun.xwebview.indicator;

import android.content.Context;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.FrameLayout;


/**
 * @author　　: 李坤
 * 创建时间: 2018/9/21 15:47
 * 邮箱　　：496546144@qq.com
 * <p>
 * 功能介绍：顶部进度条指示器
 */
public abstract class BaseIndicatorView extends FrameLayout implements BaseIndicatorSpec, LayoutParamsOffer {
    public BaseIndicatorView(Context context) {
        super(context);
    }

    public BaseIndicatorView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public BaseIndicatorView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void reset() {

    }

    @Override
    public void setProgress(int newProgress) {

    }

    @Override
    public void show() {

    }

    @Override
    public void hide() {

    }

}

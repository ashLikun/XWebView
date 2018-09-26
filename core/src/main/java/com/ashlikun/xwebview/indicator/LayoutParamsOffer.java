package com.ashlikun.xwebview.indicator;

import android.widget.FrameLayout;

/**
 * @author　　: 李坤
 * 创建时间: 2018/9/21 15:48
 * 邮箱　　：496546144@qq.com
 * <p>
 * 功能介绍：指示器LayoutParams 偏移量
 */

public interface LayoutParamsOffer<T extends FrameLayout.LayoutParams> {
    T offerLayoutParams();
}

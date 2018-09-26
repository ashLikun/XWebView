package com.ashlikun.xwebview.indicator;



/**
 * @author　　: 李坤
 * 创建时间: 2018/9/21 17:09
 * 邮箱　　：496546144@qq.com
 *
 * 功能介绍：顶部指示器
 */

public interface BaseIndicatorSpec {
    /**
     * indicator
     */
    void show();

    void hide();

    void reset();

    void setProgress(int newProgress);

}

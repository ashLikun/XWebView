package com.ashlikun.xwebview.lifecycle;

/**
 * @author　　: 李坤
 * 创建时间: 2018/9/21 13:30
 * 邮箱　　：496546144@qq.com
 * <p>
 * 功能介绍：生命周期接口
 */

public interface WebLifeCycle {
    void onResume();

    void onPause();

    void onDestroy();
}

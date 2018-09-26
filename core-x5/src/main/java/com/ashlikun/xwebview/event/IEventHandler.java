package com.ashlikun.xwebview.event;

import android.view.KeyEvent;

/**
 * @author　　: 李坤
 * 创建时间: 2018/9/21 15:54
 * 邮箱　　：496546144@qq.com
 * <p>
 * 功能介绍：对事件的处理
 */

public interface IEventHandler {

    /**
     * 按键处理
     *
     * @param keyCode
     * @param event
     * @return
     */
    boolean onKeyDown(int keyCode, KeyEvent event);


    /**
     * 非按键返回
     *
     * @return
     */
    boolean back();
}

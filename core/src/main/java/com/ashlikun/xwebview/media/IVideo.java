package com.ashlikun.xwebview.media;

import android.view.View;
import android.webkit.WebChromeClient;


/**
 * @author　　: 李坤
 * 创建时间: 2018/9/21 15:56
 * 邮箱　　：496546144@qq.com
 * <p>
 * 功能介绍：视频接口
 */

public interface IVideo {


    void onShowCustomView(View view, WebChromeClient.CustomViewCallback callback);


    void onHideCustomView();


    boolean isVideoState();

}

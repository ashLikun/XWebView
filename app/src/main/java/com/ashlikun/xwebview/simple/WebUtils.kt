package com.ashlikun.xwebview.simple

import android.os.Build
import android.view.View
import android.webkit.WebSettings
import android.webkit.WebView
import com.ashlikun.xwebview.XWebConfig
import com.ashlikun.xwebview.XWebUtils
import com.ashlikun.xwebview.websetting.AbsXWebSettings

/**
 * 作者　　: 李坤
 * 创建时间: 2020/5/15　18:31
 * 邮箱　　：496546144@qq.com
 *
 * 功能介绍：
 */
object WebUtils {

    fun settings(webView: WebView) {
        val mWebSettings = webView.settings
        mWebSettings.javaScriptEnabled = true
        mWebSettings.setSupportZoom(true)
        mWebSettings.builtInZoomControls = false
        mWebSettings.savePassword = false
        if (XWebUtils.checkNetwork(webView.context)) {
            //根据cache-control获取数据。
            mWebSettings.cacheMode = WebSettings.LOAD_DEFAULT
        } else {
            //没网，则从本地获取，即离线加载
            mWebSettings.cacheMode = WebSettings.LOAD_CACHE_ELSE_NETWORK
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            //适配5.0不允许http和https混合使用情况
            mWebSettings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
        }
        mWebSettings.textZoom = 100
        mWebSettings.databaseEnabled = true
        mWebSettings.setAppCacheEnabled(true)
        mWebSettings.loadsImagesAutomatically = true
        mWebSettings.setSupportMultipleWindows(false)
        // 是否阻塞加载网络图片  协议http or https
        // 是否阻塞加载网络图片  协议http or https
        mWebSettings.blockNetworkImage = false
        // 允许加载本地文件html  file协议
        // 允许加载本地文件html  file协议
        mWebSettings.allowFileAccess = true
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            // 通过 file url 加载的 Javascript 读取其他的本地文件 .建议关闭
            mWebSettings.allowFileAccessFromFileURLs = false
            // 允许通过 file url 加载的 Javascript 可以访问其他的源，包括其他的文件和 http，https 等其他的源
            mWebSettings.allowUniversalAccessFromFileURLs = false
        }
        mWebSettings.javaScriptCanOpenWindowsAutomatically = true
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            mWebSettings.layoutAlgorithm = WebSettings.LayoutAlgorithm.SINGLE_COLUMN
        } else {
            mWebSettings.layoutAlgorithm = WebSettings.LayoutAlgorithm.NORMAL
        }
        mWebSettings.loadWithOverviewMode = false
        mWebSettings.useWideViewPort = false
        mWebSettings.domStorageEnabled = true
        mWebSettings.setNeedInitialFocus(true)
        mWebSettings.defaultTextEncodingName = "utf-8" //设置编码格式

        mWebSettings.defaultFontSize = 16
        mWebSettings.minimumFontSize = 12 //设置 WebView 支持的最小字体大小，默认为 8

        mWebSettings.setGeolocationEnabled(true)
        //
        val dir = XWebConfig.getCachePath(webView.context)


        //设置数据库路径  api19 已经废弃,这里只针对 webkit 起作用
        mWebSettings.setGeolocationDatabasePath(dir)
        mWebSettings.databasePath = dir
        mWebSettings.setAppCachePath(dir)

        //缓存文件最大值
        mWebSettings.setAppCacheMaxSize(Long.MAX_VALUE)
        mWebSettings.userAgentString = (mWebSettings
                .userAgentString
                + AbsXWebSettings.USER_WEB + AbsXWebSettings.USER_UC)
    }
}
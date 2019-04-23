package com.ashlikun.xwebview.security;

import android.annotation.TargetApi;
import android.os.Build;
import androidx.collection.ArrayMap;
import com.tencent.smtt.sdk.WebView;

import com.ashlikun.xwebview.XWeb;
import com.ashlikun.xwebview.XWebConfig;


/**
 * @author　　: 李坤
 * 创建时间: 2018/9/21 17:03
 * 邮箱　　：496546144@qq.com
 * <p>
 * 功能介绍：安全相关设置
 */

public class WebSecurityLogicImpl implements WebSecurityCheckLogic {
    public static WebSecurityLogicImpl getInstance() {
        return new WebSecurityLogicImpl();
    }

    public WebSecurityLogicImpl() {
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    public void dealHoneyComb(WebView view) {
        if (Build.VERSION_CODES.HONEYCOMB > Build.VERSION.SDK_INT || Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR1) {
            return;
        }
        view.removeJavascriptInterface("searchBoxJavaBridge_");
        view.removeJavascriptInterface("accessibility");
        view.removeJavascriptInterface("accessibilityTraversal");
    }

    @Override
    public void dealJsInterface(ArrayMap<String, Object> objects, XWeb.SecurityType securityType) {

        if (securityType == XWeb.SecurityType.STRICT_CHECK
                && XWebConfig.WEBVIEW_TYPE != XWebConfig.WEBVIEW_XWEB_SAFE_TYPE
                && Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
            objects.clear();
            objects = null;
            System.gc();
        }

    }
}

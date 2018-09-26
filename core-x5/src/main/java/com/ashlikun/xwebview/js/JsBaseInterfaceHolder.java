package com.ashlikun.xwebview.js;

import android.os.Build;
import android.webkit.JavascriptInterface;

import com.ashlikun.xwebview.XWeb;
import com.ashlikun.xwebview.XWebConfig;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

/**
 * @author　　: 李坤
 * 创建时间: 2018/9/21 17:11
 * 邮箱　　：496546144@qq.com
 *
 * 功能介绍：添加js方法
 */

public abstract class JsBaseInterfaceHolder implements JsInterfaceHolder {

    private XWeb.SecurityType mSecurityType;

    protected JsBaseInterfaceHolder(XWeb.SecurityType securityType) {
        this.mSecurityType = securityType;
    }

    @Override
    public boolean checkObject(Object v) {

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1){
            return true;
        }
        if (XWebConfig.WEBVIEW_TYPE == XWebConfig.WEBVIEW_XWEB_SAFE_TYPE){
            return true;
        }
        boolean tag = false;
        Class clazz = v.getClass();

        Method[] mMethods = clazz.getMethods();

        for (Method mMethod : mMethods) {

            Annotation[] mAnnotations = mMethod.getAnnotations();

            for (Annotation mAnnotation : mAnnotations) {

                if (mAnnotation instanceof JavascriptInterface) {
                    tag = true;
                    break;
                }

            }
            if (tag){
                break;
            }
        }

        return tag;
    }

    protected boolean checkSecurity() {
        return mSecurityType != XWeb.SecurityType.STRICT_CHECK
                ? true : XWebConfig.WEBVIEW_TYPE == XWebConfig.WEBVIEW_XWEB_SAFE_TYPE
                ? true : Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR1;
    }


}

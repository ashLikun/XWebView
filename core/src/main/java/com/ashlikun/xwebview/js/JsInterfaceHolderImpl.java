package com.ashlikun.xwebview.js;
import android.webkit.WebView;

import com.ashlikun.xwebview.XWeb;

import java.util.Map;
import java.util.Set;

/**
 * @author　　: 李坤
 * 创建时间: 2018/9/21 17:13
 * 邮箱　　：496546144@qq.com
 * <p>
 * 功能介绍：添加js方法
 */

public class JsInterfaceHolderImpl extends JsBaseInterfaceHolder {

    private WebView mWebView;
    private XWeb.SecurityType mSecurityType;

    public static JsInterfaceHolderImpl getJsInterfaceHolder(WebView webView, XWeb.SecurityType securityType) {
        return new JsInterfaceHolderImpl(webView, securityType);
    }


    JsInterfaceHolderImpl(WebView webView, XWeb.SecurityType securityType) {
        super(securityType);
        this.mWebView = webView;
        this.mSecurityType = securityType;
    }

    @Override
    public JsInterfaceHolder addJavaObjects(Map<String, Object> maps) {
        if (!checkSecurity()) {
            return this;
        }
        Set<Map.Entry<String, Object>> sets = maps.entrySet();
        for (Map.Entry<String, Object> mEntry : sets) {

            Object v = mEntry.getValue();
            boolean t = checkObject(v);
            if (!t) {
                throw new JsInterfaceObjectException("This object has not offer method javascript to call ,please check addJavascriptInterface annotation was be added");
            } else {
                addJavaObjectDirect(mEntry.getKey(), v);
            }
        }

        return this;
    }

    @Override
    public JsInterfaceHolder addJavaObject(String k, Object v) {

        if (!checkSecurity()) {
            return this;
        }
        boolean t = checkObject(v);
        if (!t) {
            throw new JsInterfaceObjectException("this object has not offer method javascript to call , please check addJavascriptInterface annotation was be added");
        } else {
            addJavaObjectDirect(k, v);
        }
        return this;
    }

    private JsInterfaceHolder addJavaObjectDirect(String k, Object v) {
        this.mWebView.addJavascriptInterface(v, k);
        return this;
    }


}

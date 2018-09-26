package com.ashlikun.xwebview.js;

import java.util.Map;

/**
 * @author　　: 李坤
 * 创建时间: 2018/9/21 17:12
 * 邮箱　　：496546144@qq.com
 * <p>
 * 功能介绍：添加js方法接口
 */

public interface JsInterfaceHolder {

    JsInterfaceHolder addJavaObjects(Map<String, Object> maps);

    JsInterfaceHolder addJavaObject(String k, Object v);

    boolean checkObject(Object v);

}

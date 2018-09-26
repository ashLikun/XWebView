package com.ashlikun.xwebview.webview;

import java.util.Map;

/**
 * @author　　: 李坤
 * 创建时间: 2018/9/21 16:47
 * 邮箱　　：496546144@qq.com
 * <p>
 * 功能介绍：封装加载url
 */

public interface IUrlLoader {


    void loadUrl(String url);

    void loadUrl(String url, Map<String, String> headers);

    void reload();

    void loadData(String data, String mimeType, String encoding);

    void stopLoading();

    void loadDataWithBaseURL(String baseUrl, String data,
                             String mimeType, String encoding, String historyUrl);

    void postUrl(String url, byte[] params);
}

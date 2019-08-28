[![Release](https://jitpack.io/v/ashLikun/XWebView.svg)](https://jitpack.io/#ashLikun/XWebView)

# **XWebView**
XWebView 是一个基于的 Android WebView ，极度容易使用以及功能强大的库，提供了 Android WebView 一系列的问题解决方案 ，并且轻量和极度灵活。

## 使用方法

* build.gradle文件中添加:
```gradle
allprojects {
    repositories {
        maven { url "https://jitpack.io" }
    }
}
```
* 并且:

```gradle
dependencies {
     implementation 'com.github.ashLikun.XWebView:core:{latest version}'//原生WebView
     implementation 'com.github.ashLikun.XWebView:core-x5:{latest version}'//使用腾讯X5,需要自行下载sdk(jar包)
}
```
* #### 基本用法


```java
 xWeb = XWeb.withXml(this)
            .useDefaultIndicator()
            .setWebWebSettings(getWebSettings())
            .setWebView(webView)
            .setWebChromeClient(mWebChromeClient)
            .createWeb()
            .ready()
            .go(url);

```

* #### 调用 Javascript 方法拼接太麻烦 ？ 请看 。
```javascript
function callByAndroid(){
      console.log("callByAndroid")
  }
xWeb.getJsAccessEntrace().quickCallJs("callByAndroid");
```


* #### Javascript 调 Java ?
```java
xWeb.getJsInterfaceHolder().addJavaObject("android",new AndroidInterface(xWeb,this));
window.android.callAndroid();
```

* #### 事件处理
```java
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (xWeb.handleKeyEvent(keyCode, event)) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
```

* #### 跟随 Activity Or Fragment 生命周期 ， 释放 CPU 更省电 。
```java
    @Override
    protected void onPause() {
        xWeb.getWebLifeCycle().onPause(); 
        super.onPause();

    }

    @Override
    protected void onResume() {
        xWeb.getWebLifeCycle().onResume();
        super.onResume();
    }
    @Override
    public void onDestroyView() {
        xWeb.getWebLifeCycle().onDestroy();
        super.onDestroyView();
    }    
```

* #### JsBridge?

 XBridgeWebView
 其他就参考JsBridge

* #### 全屏视频播放
```
<!--如果你的应用需要用到视频 ， 那么请你在使用 AgentWeb 的 Activity 对应的清单文件里加入如下配置-->
android:hardwareAccelerated="true"
android:configChanges="orientation|screenSize"
```

* #### 定位
```
<!--AgentWeb 是默认允许定位的 ，如果你需要该功能 ， 请在你的 AndroidManifest 文件里面加入如下权限 。-->
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
```

* #### 返回上一页
```java
if (!xWeb.back()){
      finish();
}
```

* #### 获取 WebView
```java
	xWeb.getWebCreator().getWebView();
```

* #### 查看 Cookies
```java
String cookies=XWebConfig.getCookiesByUrl(targetUrl);
```

* #### 同步 Cookie
```java
XWebConfig.syncCookie("http://www.jd.com","ID=XXXX");
```

* #### MiddlewareWebChromeBase 支持多个 WebChromeClient
```java
    useMiddlewareWebClient(middleWrareWebClientBase)
```
* #### MiddlewareWebClientBase 支持多个 WebViewClient
```java
    useMiddlewareWebChrome(middlewareWebChromeBase)
```

* ####  清空缓存 
```java
XWebConfig.clearDiskCache(this.getContext());
```

* #### 权限拦截
```java
protected PermissionInterceptor mPermissionInterceptor = new PermissionInterceptor() {

        @Override
        public boolean intercept(String url, String[] permissions, String action) {
            Log.i(TAG, "url:" + url + "  permission:" + permissions + " action:" + action);
            return false;
        }
    };
```

### 混肴
####

##### X5内核混淆

    -keep public enum com.tencent.smtt.sdk.WebSettings$** {
       *;
    }
    -keep public enum com.tencent.smtt.sdk.QbSdk$** {
       *;
    }
    -keep public class com.tencent.smtt.sdk.WebSettings {
       public *;
    }


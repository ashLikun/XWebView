package com.ashlikun.xwebview;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.text.TextUtils;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.ValueCallback;
import android.webkit.WebView;

import androidx.annotation.Nullable;

import java.io.File;

/**
 * @author　　: 李坤
 * 创建时间: 2018/9/21 16:47
 * 邮箱　　：496546144@qq.com
 * <p>
 * 功能介绍：全局配置
 */

public class XWebConfig {
    public static final String FILE_CACHE_PATH = File.separator + "xweb-cache";
    /**
     * 缓存路径，可以更改
     */
    public static String XWEB_CHCHE_PATH;

    /**
     * DEBUG 模式 ， 如果需要查看日志请设置为 true
     */
    public static boolean DEBUG = false;
    /**
     * 当前操作系统是否低于 KITKAT
     */
    public static final boolean IS_KITKAT_OR_BELOW_KITKAT = Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT;
    /**
     * 默认 WebView  类型 。
     */
    public static final int WEBVIEW_DEFAULT_TYPE = 1;
    /**
     * 使用 XWebView
     */
    public static final int WEBVIEW_XWEB_SAFE_TYPE = 2;
    /**
     * 自定义 WebView
     */
    public static final int WEBVIEW_CUSTOM_TYPE = 3;
    public static int WEBVIEW_TYPE = WEBVIEW_DEFAULT_TYPE;
    private static volatile boolean IS_INITIALIZED = false;
    /**
     * XWeb 的版本
     */
    public static final String WEB_VERSION = " xweb/4.0.2 ";
    public static final String XWEB_NAME = "XWeb";
    /**
     * 通过JS获取的文件大小， 这里限制最大为5MB ，太大会抛出 OutOfMemoryError
     */
    public static int MAX_FILE_LENGTH = 1024 * 1024 * 5;


    /**
     * 获取Cookie
     *
     * @param url
     * @return
     */
    public static String getCookiesByUrl(String url) {
        return CookieManager.getInstance() == null ? null : CookieManager.getInstance().getCookie(url);
    }

    public static void debug() {
        DEBUG = true;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WebView.setWebContentsDebuggingEnabled(true);
        }
    }

    /**
     * 删除所有已经过期的 Cookies
     */
    public static void removeExpiredCookies() {
        CookieManager mCookieManager = null;
        if ((mCookieManager = CookieManager.getInstance()) != null) {
            //同步清除
            mCookieManager.removeExpiredCookie();
            toSyncCookies();
        }
    }

    /**
     * 删除所有 Cookies
     */
    public static void removeAllCookies() {
        removeAllCookies(null);
    }

    /**
     * 解决兼容 Android 4.4 java.lang.NoSuchMethodError: android.webkit.CookieManager.removeSessionCookies
     */
    public static void removeSessionCookies() {
        removeSessionCookies(null);
    }

    /**
     * 同步cookie
     *
     * @param url
     * @param cookies
     */
    public static void syncCookie(String url, String cookies) {

        CookieManager mCookieManager = CookieManager.getInstance();
        if (mCookieManager != null) {
            mCookieManager.setCookie(url, cookies);
            toSyncCookies();
        }
    }

    public static void removeSessionCookies(ValueCallback<Boolean> callback) {

        if (callback == null) {
            callback = getDefaultIgnoreCallback();
        }
        if (CookieManager.getInstance() == null) {
            callback.onReceiveValue(new Boolean(false));
            return;
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            CookieManager.getInstance().removeSessionCookie();
            toSyncCookies();
            callback.onReceiveValue(new Boolean(true));
            return;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            CookieManager.getInstance().removeSessionCookies(callback);
        }
        toSyncCookies();

    }

    /**
     * @param context
     * @return WebView 的缓存路径
     */
    public static String getCachePath(Context context) {
        if (!TextUtils.isEmpty(XWebConfig.XWEB_CHCHE_PATH)) {
            return XWebConfig.XWEB_CHCHE_PATH;
        }
        String dir = context.getCacheDir().getAbsolutePath();
        File mFile = new File(dir, XWebConfig.FILE_CACHE_PATH);
        try {
            if (!mFile.exists()) {
                mFile.mkdirs();
            }
        } catch (Throwable throwable) {
        }
        return XWebConfig.XWEB_CHCHE_PATH = mFile.getAbsolutePath();
    }

    /**
     * @param context
     * @return XWeb 缓存路径
     */
    public static String getExternalCachePath(Context context) {
        return XWebUtils.getXWebFilePath(context);
    }


    /**
     * Android  4.4  NoSuchMethodError: android.webkit.CookieManager.removeAllCookies
     *
     * @param callback
     */
    public static void removeAllCookies(@Nullable ValueCallback<Boolean> callback) {

        if (callback == null) {
            callback = getDefaultIgnoreCallback();
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            CookieManager.getInstance().removeAllCookie();
            toSyncCookies();
            callback.onReceiveValue(!CookieManager.getInstance().hasCookies());
            return;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            CookieManager.getInstance().removeAllCookies(callback);
        }
        toSyncCookies();
    }

    /**
     * 清空缓存
     *
     * @param context
     */
    public static synchronized void clearDiskCache(Context context) {

        try {

            XWebUtils.clearCacheFolder(new File(getCachePath(context)), 0);
            String path = getExternalCachePath(context);
            if (!TextUtils.isEmpty(path)) {
                File mFile = new File(path);
                XWebUtils.clearCacheFolder(mFile, 0);
            }
        } catch (Throwable throwable) {
        }

    }


    static synchronized void initCookiesManager(Context context) {
        if (!IS_INITIALIZED) {
            createCookiesSyncInstance(context);
            IS_INITIALIZED = true;
        }
    }

    private static void createCookiesSyncInstance(Context context) {


        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            CookieSyncManager.createInstance(context);
        }
    }

    private static void toSyncCookies() {

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            CookieSyncManager.getInstance().sync();
            return;
        }
        AsyncTask.THREAD_POOL_EXECUTOR.execute(new Runnable() {
            @Override
            public void run() {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    CookieManager.getInstance().flush();
                }

            }
        });
    }


    static String getDatabasesCachePath(Context context) {
        return context.getApplicationContext().getDir("database", Context.MODE_PRIVATE).getPath();
    }

    private static ValueCallback<Boolean> getDefaultIgnoreCallback() {
        return new ValueCallback<Boolean>() {
            @Override
            public void onReceiveValue(Boolean ignore) {
            }
        };
    }

}

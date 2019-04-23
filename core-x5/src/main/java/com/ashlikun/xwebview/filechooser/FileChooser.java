package com.ashlikun.xwebview.filechooser;

import android.app.Activity;
import android.content.ClipData;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import androidx.annotation.NonNull;
import android.text.TextUtils;
import android.util.Base64;
import com.tencent.smtt.sdk.ValueCallback;
import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.WebView;

import com.ashlikun.xwebview.R;
import com.ashlikun.xwebview.XWebConfig;
import com.ashlikun.xwebview.XWebUtils;
import com.ashlikun.xwebview.security.WebPermissions;
import com.ashlikun.xwebview.ui.AbsWebUIController;
import com.ashlikun.xwebview.ui.Action;
import com.ashlikun.xwebview.ui.ActionActivity;
import com.ashlikun.xwebview.websetting.PermissionInterceptor;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author　　: 李坤
 * 创建时间: 2018/9/25 14:41
 * 邮箱　　：496546144@qq.com
 * <p>
 * 功能介绍：文件浏览
 */

public class FileChooser {
    /**
     * Activity
     */
    private Activity mActivity;
    /**
     * ValueCallback
     */
    private ValueCallback<Uri> mUriValueCallback;
    /**
     * ValueCallback<Uri[]> After LOLLIPOP
     */
    private ValueCallback<Uri[]> mUriValueCallbacks;
    /**
     * Activity Request Code
     */
    public static final int REQUEST_CODE = 0x254;
    /**
     * 当前系统是否高于 Android 5.0 ；
     */
    private boolean mIsAboveLollipop = false;
    /**
     * WebChromeClient.FileChooserParams 封装了 Intent ，mAcceptType  等参数
     */
    private WebChromeClient.FileChooserParams mFileChooserParams;
    /**
     * 如果是通过 JavaScript 打开文件选择器 ，那么 mJsChannelCallback 不能为空
     */
    private JsChannelCallback mJsChannelCallback;
    /**
     * 是否为Js Channel
     */
    private boolean mJsChannel = false;

    /**
     * 当前 WebView
     */
    private WebView mWebView;
    /**
     * 是否为 Camera State
     */
    private boolean mCameraState = false;
    /**
     * 权限拦截
     */
    private PermissionInterceptor mPermissionInterceptor;
    /**
     * FROM_INTENTION_CODE 用于表示当前Action
     */
    private int FROM_INTENTION_CODE = 21;
    /**
     * 当前 AbsWebUIController
     */
    private WeakReference<AbsWebUIController> mWebUIController = null;
    /**
     * 选择文件类型
     */
    private String mAcceptType = "*/*";
    /**
     * 修复某些特定手机拍照后，立刻获取照片为空的情况
     */
    public static int MAX_WAIT_PHOTO_MS = 8 * 1000;

    private Handler.Callback mJsChannelHandler$Callback;

    public FileChooser(Builder builder) {

        this.mActivity = builder.mActivity;
        this.mUriValueCallback = builder.mUriValueCallback;
        this.mUriValueCallbacks = builder.mUriValueCallbacks;
        this.mIsAboveLollipop = builder.mIsAboveLollipop;
        this.mJsChannel = builder.mJsChannel;
        this.mFileChooserParams = builder.mFileChooserParams;
        if (this.mJsChannel) {
            this.mJsChannelCallback = JsChannelCallback.create(builder.mJsChannelCallback);
        }
        this.mWebView = builder.mWebView;
        this.mPermissionInterceptor = builder.mPermissionInterceptor;
        this.mAcceptType = builder.mAcceptType;
        this.mWebUIController = new WeakReference<AbsWebUIController>(XWebUtils.getWebUIControllerByWebView(this.mWebView));
        this.mJsChannelHandler$Callback = builder.mJsChannelCallback;

    }


    public void openFileChooser() {
        if (!XWebUtils.isUIThread()) {
            XWebUtils.runInUiThread(new Runnable() {
                @Override
                public void run() {
                    openFileChooser();
                }
            });
            return;
        }

        openFileChooserInternal();
    }

    private void fileChooser() {

        List<String> permission = null;
        if (XWebUtils.getDeniedPermissions(mActivity, WebPermissions.STORAGE).isEmpty()) {
            touchOffFileChooserAction();
        } else {
            Action mAction = Action.createPermissionsAction(WebPermissions.STORAGE);
            mAction.setFromIntention(FROM_INTENTION_CODE >> 2);
            ActionActivity.setPermissionListener(mPermissionListener);
            ActionActivity.start(mActivity, mAction);
        }


    }

    private void touchOffFileChooserAction() {
        Action mAction = new Action();
        mAction.setAction(Action.ACTION_FILE);
        ActionActivity.setChooserListener(getChooserListener());
        mActivity.startActivity(new Intent(mActivity, ActionActivity.class).putExtra(ActionActivity.KEY_ACTION, mAction)
                .putExtra(ActionActivity.KEY_FILE_CHOOSER_INTENT, getFileChooserIntent()));
    }

    private Intent getFileChooserIntent() {
        Intent mIntent = null;
        if (mIsAboveLollipop && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && mFileChooserParams != null && (mIntent = mFileChooserParams.createIntent()) != null) {
            return mIntent;
        }

        Intent i = new Intent();
        i.setAction(Intent.ACTION_GET_CONTENT);
        i.addCategory(Intent.CATEGORY_OPENABLE);
        if (TextUtils.isEmpty(this.mAcceptType)) {
            i.setType("*/*");
        } else {
            i.setType(this.mAcceptType);
        }
        i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        return mIntent = Intent.createChooser(i, "");
    }

    private ActionActivity.ChooserListener getChooserListener() {
        return new ActionActivity.ChooserListener() {
            @Override
            public void onChoiceResult(int requestCode, int resultCode, Intent data) {
                onIntentResult(requestCode, resultCode, data);
            }
        };
    }


    private void openFileChooserInternal() {


        // 是否直接打开文件选择器
        if (this.mIsAboveLollipop && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && this.mFileChooserParams != null && this.mFileChooserParams.getAcceptTypes() != null) {
            boolean needCamera = false;
            String[] types = this.mFileChooserParams.getAcceptTypes();
            for (String typeTmp : types) {
                if (TextUtils.isEmpty(typeTmp)) {
                    continue;
                }
                if (typeTmp.contains("*/") || typeTmp.contains("image/")) {
                    needCamera = true;
                    break;
                }
            }
            if (!needCamera) {
                touchOffFileChooserAction();
                return;
            }
        }
        if (!TextUtils.isEmpty(this.mAcceptType) && !this.mAcceptType.contains("*/") && !this.mAcceptType.contains("image/")) {
            touchOffFileChooserAction();
            return;
        }

        if (this.mWebUIController.get() != null) {
            this.mWebUIController
                    .get()
                    .onSelectItemsPrompt(this.mWebView, mWebView.getUrl(),
                            new String[]{mActivity.getString(R.string.xweb_camera),
                                    mActivity.getString(R.string.xweb_file_chooser)}, getCallBack());
        }

    }


    private Handler.Callback getCallBack() {
        return new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                switch (msg.what) {
                    case 0:
                        mCameraState = true;
                        onCameraAction();

                        break;
                    case 1:
                        mCameraState = false;
                        fileChooser();
                        break;
                    default:
                        cancel();
                        break;
                }
                return true;
            }
        };
    }


    private void onCameraAction() {

        if (mActivity == null) {
            return;
        }

        if (mPermissionInterceptor != null) {
            if (mPermissionInterceptor.intercept(FileChooser.this.mWebView.getUrl(), WebPermissions.CAMERA, "camera")) {
                cancel();
                return;
            }

        }

        Action mAction = new Action();
        List<String> deniedPermissions = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !(deniedPermissions = checkNeedPermission()).isEmpty()) {
            mAction.setAction(Action.ACTION_PERMISSION);
            mAction.setPermissions(deniedPermissions.toArray(new String[]{}));
            mAction.setFromIntention(FROM_INTENTION_CODE >> 3);
            ActionActivity.setPermissionListener(this.mPermissionListener);
            ActionActivity.start(mActivity, mAction);
        } else {
            openCameraAction();
        }

    }

    private List<String> checkNeedPermission() {

        List<String> deniedPermissions = new ArrayList<>();

        if (!XWebUtils.hasPermission(mActivity, WebPermissions.CAMERA)) {
            deniedPermissions.add(WebPermissions.CAMERA[0]);
        }
        if (!XWebUtils.hasPermission(mActivity, WebPermissions.STORAGE)) {
            deniedPermissions.addAll(Arrays.asList(WebPermissions.STORAGE));
        }
        return deniedPermissions;
    }

    private void openCameraAction() {
        Action mAction = new Action();
        mAction.setAction(Action.ACTION_CAMERA);
        ActionActivity.setChooserListener(this.getChooserListener());
        ActionActivity.start(mActivity, mAction);
    }

    private ActionActivity.PermissionListener mPermissionListener = new ActionActivity.PermissionListener() {

        @Override
        public void onRequestPermissionsResult(@NonNull String[] permissions, @NonNull int[] grantResults, Bundle extras) {

            boolean tag = true;
            tag = XWebUtils.hasPermission(mActivity, Arrays.asList(permissions)) ? true : false;
            permissionResult(tag, extras.getInt(ActionActivity.KEY_FROM_INTENTION));

        }
    };

    private void permissionResult(boolean grant, int from_intention) {
        if (from_intention == FROM_INTENTION_CODE >> 2) {
            if (grant) {
                touchOffFileChooserAction();
            } else {
                cancel();

                if (null != mWebUIController.get()) {
                    mWebUIController
                            .get()
                            .onPermissionsDeny(
                                    WebPermissions.STORAGE,
                                    WebPermissions.ACTION_STORAGE,
                                    "Open file chooser");
                }
            }
        } else if (from_intention == FROM_INTENTION_CODE >> 3) {
            if (grant) {
                openCameraAction();
            } else {
                cancel();
                if (null != mWebUIController.get()) {
                    mWebUIController
                            .get()
                            .onPermissionsDeny(
                                    WebPermissions.CAMERA,
                                    WebPermissions.ACTION_CAMERA,
                                    "Take photo");
                }
            }
        }


    }

    public void onIntentResult(int requestCode, int resultCode, Intent data) {

        if (REQUEST_CODE != requestCode) {
            return;
        }

        //用户已经取消
        if (resultCode == Activity.RESULT_CANCELED || data == null) {
            cancel();
            return;
        }

        if (resultCode != Activity.RESULT_OK) {
            cancel();
            return;
        }

        //通过Js获取文件
        if (mJsChannel) {
            convertFileAndCallback(mCameraState ? new Uri[]{data.getParcelableExtra(ActionActivity.KEY_URI)} : processData(data));
            return;
        }

        //5.0以上系统通过input标签获取文件
        if (mIsAboveLollipop) {
            aboveLollipopCheckFilesAndCallback(mCameraState ? new Uri[]{data.getParcelableExtra(ActionActivity.KEY_URI)} : processData(data), mCameraState);
            return;
        }


        //4.4以下系统通过input标签获取文件
        if (mUriValueCallback == null) {
            cancel();
            return;
        }

        if (mCameraState) {
            mUriValueCallback.onReceiveValue((Uri) data.getParcelableExtra(ActionActivity.KEY_URI));
        } else {
            belowLollipopUriCallback(data);
        }

        /*if (mIsAboveLollipop)
            aboveLollipopCheckFilesAndCallback(mCameraState ? new Uri[]{data.getParcelableExtra(KEY_URI)} : processData(data));
        else if (mJsChannel)
            convertFileAndCallback(mCameraState ? new Uri[]{data.getParcelableExtra(KEY_URI)} : processData(data));
        else {
            if (mCameraState && mUriValueCallback != null)
                mUriValueCallback.onReceiveValue((Uri) data.getParcelableExtra(KEY_URI));
            else
                belowLollipopUriCallback(data);
        }*/


    }

    private void cancel() {
        if (mJsChannel) {
            mJsChannelCallback.call(null);
            return;
        }
        if (mUriValueCallback != null) {
            mUriValueCallback.onReceiveValue(null);
        }
        if (mUriValueCallbacks != null) {
            mUriValueCallbacks.onReceiveValue(null);
        }
        return;
    }


    private void belowLollipopUriCallback(Intent data) {


        if (data == null) {
            if (mUriValueCallback != null) {
                mUriValueCallback.onReceiveValue(Uri.EMPTY);
            }
            return;
        }
        Uri mUri = data.getData();
        if (mUriValueCallback != null) {
            mUriValueCallback.onReceiveValue(mUri);
        }

    }

    private Uri[] processData(Intent data) {

        Uri[] datas = null;
        if (data == null) {
            return datas;
        }
        String target = data.getDataString();
        if (!TextUtils.isEmpty(target)) {
            return datas = new Uri[]{Uri.parse(target)};
        }
        ClipData mClipData = data.getClipData();
        if (mClipData != null && mClipData.getItemCount() > 0) {
            datas = new Uri[mClipData.getItemCount()];
            for (int i = 0; i < mClipData.getItemCount(); i++) {

                ClipData.Item mItem = mClipData.getItemAt(i);
                datas[i] = mItem.getUri();

            }
        }
        return datas;


    }

    private void convertFileAndCallback(final Uri[] uris) {

        String[] paths = null;
        if (uris == null || uris.length == 0 || (paths = XWebUtils.uriToPath(mActivity, uris)) == null || paths.length == 0) {
            mJsChannelCallback.call(null);
            return;
        }

        int sum = 0;
        for (String path : paths) {
            if (TextUtils.isEmpty(path)) {
                continue;
            }
            File mFile = new File(path);
            if (!mFile.exists()) {
                continue;
            }
            sum += mFile.length();
        }

        if (sum > XWebConfig.MAX_FILE_LENGTH) {
            if (mWebUIController.get() != null) {
                mWebUIController.get().onShowMessage(mActivity.getString(R.string.xweb_max_file_length_limit, (XWebConfig.MAX_FILE_LENGTH / 1024 / 1024) + ""), "convertFileAndCallback");
            }
            mJsChannelCallback.call(null);
            return;
        }

        new CovertFileThread(this.mJsChannelCallback, paths).start();

    }

    /**
     * 经过多次的测试，在小米 MIUI ， 华为 ，多部分为 Android 6.0 左右系统相机获取到的文件
     * length为0 ，导致前端 ，获取到的文件， 作预览的时候不正常 ，等待5S左右文件又正常了 ， 所以这里做了阻塞等待处理，
     *
     * @param datas
     * @param isCamera
     */
    private void aboveLollipopCheckFilesAndCallback(final Uri[] datas, boolean isCamera) {
        if (mUriValueCallbacks == null) {
            return;
        }
        if (!isCamera) {
            mUriValueCallbacks.onReceiveValue(datas == null ? new Uri[]{} : datas);
            return;
        }

        if (mWebUIController.get() == null) {
            mUriValueCallbacks.onReceiveValue(null);
            return;
        }
        String[] paths = XWebUtils.uriToPath(mActivity, datas);
        if (paths == null || paths.length == 0) {
            mUriValueCallbacks.onReceiveValue(null);
            return;
        }
        final String path = paths[0];
        mWebUIController.get().onLoading(mActivity.getString(R.string.xweb_loading));
        AsyncTask.THREAD_POOL_EXECUTOR.execute(new WaitPhotoRunnable(path, new AboveLCallback(mUriValueCallbacks, datas, mWebUIController)));

    }

    private static final class AboveLCallback implements Handler.Callback {
        private ValueCallback<Uri[]> mValueCallback;
        private Uri[] mUris;
        private WeakReference<AbsWebUIController> controller;

        private AboveLCallback(ValueCallback<Uri[]> valueCallbacks, Uri[] uris, WeakReference<AbsWebUIController> controller) {
            this.mValueCallback = valueCallbacks;
            this.mUris = uris;
            this.controller = controller;
        }

        @Override
        public boolean handleMessage(final Message msg) {

            XWebUtils.runInUiThread(new Runnable() {
                @Override
                public void run() {
                    AboveLCallback.this.safeHandleMessage(msg);
                }
            });
            return false;
        }

        private void safeHandleMessage(Message msg) {
            if (mValueCallback != null) {
                mValueCallback.onReceiveValue(mUris);
            }
            if (controller != null && controller.get() != null) {
                controller.get().onCancelLoading();
            }
        }
    }

    private static final class WaitPhotoRunnable implements Runnable {
        private String path;
        private Handler.Callback mCallback;

        private WaitPhotoRunnable(String path, Handler.Callback callback) {
            this.path = path;
            this.mCallback = callback;
        }

        @Override
        public void run() {


            if (TextUtils.isEmpty(path) || !new File(path).exists()) {
                if (mCallback != null) {
                    mCallback.handleMessage(Message.obtain(null, -1));
                }
                return;
            }
            int ms = 0;

            while (ms <= MAX_WAIT_PHOTO_MS) {

                ms += 300;
                SystemClock.sleep(300);
                File mFile = new File(path);
                if (mFile.length() > 0) {

                    if (mCallback != null) {
                        mCallback.handleMessage(Message.obtain(null, 1));
                        mCallback = null;
                    }
                    break;
                }

            }

            if (ms > MAX_WAIT_PHOTO_MS) {
                if (mCallback != null) {
                    mCallback.handleMessage(Message.obtain(null, -1));
                }
            }
            mCallback = null;
            path = null;

        }
    }

    // 必须执行在子线程, 会阻塞直到文件转换完成;
    public static Queue<FileParcel> convertFile(String[] paths) throws Exception {

        if (paths == null || paths.length == 0) {
            return null;
        }
        int tmp = Runtime.getRuntime().availableProcessors() + 1;
        int result = paths.length > tmp ? tmp : paths.length;
        Executor mExecutor = Executors.newFixedThreadPool(result);
        final Queue<FileParcel> mQueue = new LinkedBlockingQueue<>();
        CountDownLatch mCountDownLatch = new CountDownLatch(paths.length);

        int i = 1;
        for (String path : paths) {
            if (TextUtils.isEmpty(path)) {
                mCountDownLatch.countDown();
                continue;
            }
            mExecutor.execute(new EncodeFileRunnable(path, mQueue, mCountDownLatch, i++));
        }
        mCountDownLatch.await();

        if (!((ThreadPoolExecutor) mExecutor).isShutdown()) {
            ((ThreadPoolExecutor) mExecutor).shutdownNow();
        }
        return mQueue;
    }


    static class EncodeFileRunnable implements Runnable {

        private String filePath;
        private Queue<FileParcel> mQueue;
        private CountDownLatch mCountDownLatch;
        private int id;

        public EncodeFileRunnable(String filePath, Queue<FileParcel> queue, CountDownLatch countDownLatch, int id) {
            this.filePath = filePath;
            this.mQueue = queue;
            this.mCountDownLatch = countDownLatch;
            this.id = id;
        }


        @Override
        public void run() {
            InputStream is = null;
            ByteArrayOutputStream os = null;
            try {
                File mFile = new File(filePath);
                if (mFile.exists()) {

                    is = new FileInputStream(mFile);
                    if (is == null) {
                        return;
                    }
                    os = new ByteArrayOutputStream();
                    byte[] b = new byte[1024];
                    int len;
                    while ((len = is.read(b, 0, 1024)) != -1) {
                        os.write(b, 0, len);
                    }
                    mQueue.offer(new FileParcel(id, mFile.getAbsolutePath(), Base64.encodeToString(os.toByteArray(), Base64.DEFAULT)));
                }

            } catch (Throwable e) {
                e.printStackTrace();
            } finally {
                XWebUtils.closeIO(is);
                XWebUtils.closeIO(os);
                mCountDownLatch.countDown();
            }


        }
    }

    static String convertFileParcelObjectsToJson(Collection<FileParcel> collection) {

        if (collection == null || collection.size() == 0) {
            return null;
        }
        Iterator<FileParcel> mFileParcels = collection.iterator();
        JSONArray mJSONArray = new JSONArray();
        try {
            while (mFileParcels.hasNext()) {
                JSONObject jo = new JSONObject();
                FileParcel mFileParcel = mFileParcels.next();
                jo.put("contentPath", mFileParcel.getContentPath());
                jo.put("fileBase64", mFileParcel.getFileBase64());
                jo.put("mId", mFileParcel.getId());
                mJSONArray.put(jo);
            }
        } catch (Throwable throwable) {
        }
        return mJSONArray + "";
    }

    static class CovertFileThread extends Thread {

        private WeakReference<JsChannelCallback> mJsChannelCallback;
        private String[] paths;

        private CovertFileThread(JsChannelCallback JsChannelCallback, String[] paths) {
            super("xweb-thread");
            this.mJsChannelCallback = new WeakReference<JsChannelCallback>(JsChannelCallback);
            this.paths = paths;
        }

        @Override
        public void run() {


            try {
                Queue<FileParcel> mQueue = convertFile(paths);
                String result = convertFileParcelObjectsToJson(mQueue);
                if (mJsChannelCallback != null && mJsChannelCallback.get() != null) {
                    mJsChannelCallback.get().call(result);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    static class JsChannelCallback {
        WeakReference<Handler.Callback> callback = null;

        JsChannelCallback(Handler.Callback callback) {
            this.callback = new WeakReference<Handler.Callback>(callback);
        }

        public static JsChannelCallback create(Handler.Callback callback) {
            return new JsChannelCallback(callback);
        }

        void call(String value) {
            if (this.callback != null && this.callback.get() != null) {
                this.callback.get().handleMessage(Message.obtain(null, "JsChannelCallback".hashCode(), value));
            }
        }
    }

    public static Builder newBuilder(Activity activity, WebView webView) {
        return new Builder().setActivity(activity).setWebView(webView);
    }

    public static final class Builder {

        private Activity mActivity;
        private ValueCallback<Uri> mUriValueCallback;
        private ValueCallback<Uri[]> mUriValueCallbacks;
        private boolean mIsAboveLollipop = false;
        private WebChromeClient.FileChooserParams mFileChooserParams;
        private boolean mJsChannel = false;
        private WebView mWebView;
        private PermissionInterceptor mPermissionInterceptor;
        private String mAcceptType = "*/*";
        private Handler.Callback mJsChannelCallback;

        public Builder setAcceptType(String acceptType) {
            if (TextUtils.isEmpty(acceptType)) {
                return this;
            }
            this.mAcceptType = acceptType;
            return this;
        }

        public Builder setPermissionInterceptor(PermissionInterceptor permissionInterceptor) {
            mPermissionInterceptor = permissionInterceptor;
            return this;
        }

        public Builder setActivity(Activity activity) {
            mActivity = activity;
            return this;
        }

        public Builder setUriValueCallback(ValueCallback<Uri> uriValueCallback) {
            if (uriValueCallback != null) {
                mUriValueCallback = uriValueCallback;
                mIsAboveLollipop = false;
                mJsChannel = false;
                mUriValueCallbacks = null;
            }
            return this;
        }

        public Builder setUriValueCallbacks(ValueCallback<Uri[]> uriValueCallbacks) {
            if (uriValueCallbacks != null) {
                mUriValueCallbacks = uriValueCallbacks;
                mIsAboveLollipop = true;
                mUriValueCallback = null;
                mJsChannel = false;
            }
            return this;
        }


        public Builder setFileChooserParams(WebChromeClient.FileChooserParams fileChooserParams) {
            mFileChooserParams = fileChooserParams;
            return this;
        }

        public Builder setJsChannelCallback(Handler.Callback jsChannelCallback) {
            if (jsChannelCallback != null) {
                this.mJsChannelCallback = jsChannelCallback;
                mJsChannel = true;
                mUriValueCallback = null;
                mUriValueCallbacks = null;
            }
            return this;
        }


        public Builder setWebView(WebView webView) {
            mWebView = webView;
            return this;
        }


        public FileChooser build() {
            return new FileChooser(this);
        }
    }


}

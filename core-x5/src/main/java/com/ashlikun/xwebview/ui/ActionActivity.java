package com.ashlikun.xwebview.ui;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import com.ashlikun.xwebview.XWebUtils;

import java.io.File;
import java.util.List;

import static android.provider.MediaStore.EXTRA_OUTPUT;


/**
 * @author　　: 李坤
 * 创建时间: 2018/9/21 15:43
 * 邮箱　　：496546144@qq.com
 * <p>
 * 功能介绍：显示系统ui的Activity
 * 1：权限
 * 2：文件选择
 * 3：相机
 */

public final class ActionActivity extends Activity {

    public static final String KEY_ACTION = "KEY_ACTION";
    public static final String KEY_URI = "KEY_URI";
    public static final String KEY_FROM_INTENTION = "KEY_FROM_INTENTION";
    public static final String KEY_FILE_CHOOSER_INTENT = "KEY_FILE_CHOOSER_INTENT";
    private static RationaleListener mRationaleListener;
    private static PermissionListener mPermissionListener;
    private static ChooserListener mChooserListener;
    private Action mAction;
    public static final int REQUEST_CODE = 0x254;


    public static void start(Activity activity, Action action) {
        Intent mIntent = new Intent(activity, ActionActivity.class);
        mIntent.putExtra(KEY_ACTION, action);
        activity.startActivity(mIntent);
    }

    public static void setChooserListener(ChooserListener chooserListener) {
        mChooserListener = chooserListener;
    }

    public static void setPermissionListener(PermissionListener permissionListener) {
        mPermissionListener = permissionListener;
    }

    private void cancelAction() {
        mChooserListener = null;
        mPermissionListener = null;
        mRationaleListener = null;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        mAction = intent.getParcelableExtra(KEY_ACTION);
        if (mAction == null) {
            cancelAction();
            this.finish();
            return;
        }
        if (mAction.getAction() == Action.ACTION_PERMISSION) {
            permission(mAction);
        } else if (mAction.getAction() == Action.ACTION_CAMERA) {
            realOpenCamera();
        } else {
            fetchFile(mAction);
        }

    }

    private void fetchFile(Action action) {

        if (mChooserListener == null) {
            finish();
        }

        realOpenFileChooser();
    }

    private void realOpenFileChooser() {

        try {
            if (mChooserListener == null) {
                finish();
                return;
            }
            Intent mIntent = getIntent().getParcelableExtra(KEY_FILE_CHOOSER_INTENT);
            if (mIntent == null) {
                cancelAction();
                return;
            }
            this.startActivityForResult(mIntent, REQUEST_CODE);
        } catch (Throwable throwable) {
            chooserActionCallback(-1, null);
        }

    }

    private void chooserActionCallback(int resultCode, Intent data) {
        if (mChooserListener != null) {
            mChooserListener.onChoiceResult(REQUEST_CODE, resultCode, data);
            mChooserListener = null;
        }
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {


        if (requestCode == REQUEST_CODE) {
            chooserActionCallback(resultCode, mUri != null ? new Intent().putExtra(KEY_URI, mUri) : data);
        }
    }

    private void permission(Action action) {


        List<String> permissions = action.getPermissions();
        if (XWebUtils.isEmptyCollection(permissions)) {
            mPermissionListener = null;
            mRationaleListener = null;
            finish();
            return;
        }

        if (mRationaleListener != null) {
            boolean rationale = false;
            for (String permission : permissions) {
                rationale = ActivityCompat.shouldShowRequestPermissionRationale(this, permission);
                if (rationale) {
                    break;
                }
            }
            mRationaleListener.onRationaleResult(rationale, new Bundle());
            mRationaleListener = null;
            finish();
            return;
        }

        if (mPermissionListener != null) {
            ActivityCompat.requestPermissions(this, permissions.toArray(new String[]{}), 1);
        }

    }

    private Uri mUri;

    private void realOpenCamera() {

        try {
            if (mChooserListener == null) {
                finish();
            }
            File mFile = XWebUtils.createImageFile(this);
            if (mFile == null) {
                mChooserListener.onChoiceResult(REQUEST_CODE, Activity.RESULT_CANCELED, null);
                mChooserListener = null;
                finish();
            }
            Intent intent = XWebUtils.getIntentCaptureCompat(this, mFile);
            // 指定开启系统相机的Action
            mUri = intent.getParcelableExtra(EXTRA_OUTPUT);
            this.startActivityForResult(intent, REQUEST_CODE);
        } catch (Throwable ignore) {
            if (mChooserListener != null) {
                mChooserListener.onChoiceResult(REQUEST_CODE, Activity.RESULT_CANCELED, null);
            }
            mChooserListener = null;
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (mPermissionListener != null) {
            Bundle mBundle = new Bundle();
            mBundle.putInt(KEY_FROM_INTENTION, mAction.getFromIntention());
            mPermissionListener.onRequestPermissionsResult(permissions, grantResults, mBundle);
        }
        mPermissionListener = null;
        finish();
    }

    public interface RationaleListener {
        void onRationaleResult(boolean showRationale, Bundle extras);
    }

    public interface PermissionListener {
        void onRequestPermissionsResult(@NonNull String[] permissions, @NonNull int[] grantResults, Bundle extras);
    }

    public interface ChooserListener {
        void onChoiceResult(int requestCode, int resultCode, Intent data);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}

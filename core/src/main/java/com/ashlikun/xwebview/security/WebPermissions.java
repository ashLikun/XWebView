package com.ashlikun.xwebview.security;

import android.Manifest;


/**
 * @author　　: 李坤
 * 创建时间: 2018/9/21 15:46
 * 邮箱　　：496546144@qq.com
 * <p>
 * 功能介绍：权限
 */

public class WebPermissions {


    public static final String[] CAMERA;
    public static final String[] LOCATION;
    public static final String[] STORAGE;

    public static final String ACTION_CAMERA = "Camera";
    public static final String ACTION_LOCATION = "Location";
    public static final String ACTION_STORAGE = "Storage";

    static {


        CAMERA = new String[]{
                Manifest.permission.CAMERA};


        LOCATION = new String[]{
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION};


        STORAGE = new String[]{
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE};
    }


}

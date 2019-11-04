package com.hyphenate.easeui.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;

import java.util.ArrayList;

/**
 * 授权工具类
 * <p>
 * <br> Author: 叶青
 * <br> Version: 1.0.0
 * <br> Date: 2017/1/15
 */
public class EasePermissionUtils {

    /** 请求授权 多个权限 返回码。 */
    private static final int REQUEST_CODE_MULTIPLE_PERMISSION = 10000;
    /** 请求授权 多个权限 返回码。 */
    public static final String REQUEST_MULTIPLE_PERMISSION = "android.permission.REQUEST_ALL";
    /** 单例 */
    private static EasePermissionUtils instance;
    /** 权限开启成功失败回调 */
    private PermissionGrant mPermissionGrant;

    // 需要单独申请的权限，共分为9组
    /** 权限数组 */
    public static final String[] REQUEST_PERMISSIONS = {
            Manifest.permission.RECORD_AUDIO,               // android.permission-group.MICROPHONE
            Manifest.permission.READ_CONTACTS,              // android.permission-group.CONTACTS
            Manifest.permission.CALL_PHONE,                 // android.permission-group.PHONE
            Manifest.permission.CAMERA,                     // android.permission-group.CAMERA
            Manifest.permission.ACCESS_FINE_LOCATION,       // android.permission-group.LOCATION //通过GPS芯片接收卫星
            Manifest.permission.WRITE_EXTERNAL_STORAGE,     // android.permission-group.STORAGE
            Manifest.permission.BODY_SENSORS,               // android.permission-group.SENSORS
            Manifest.permission.READ_CALENDAR,              // android.permission-group.CALENDAR
            Manifest.permission.READ_SMS                    // android.permission-group.SMS
    };

    public static EasePermissionUtils getInstance(PermissionGrant permissionGrant) {
        // if (instance == null) {
        synchronized (EasePermissionUtils.class) {
            if (instance == null) {
                instance = new EasePermissionUtils();
            }
        }
        // }

        if (permissionGrant != null) {
            instance.mPermissionGrant = permissionGrant;
        }
        return instance;
    }

    public static EasePermissionUtils getInstance() {
        return getInstance(null);
    }

    /**
     * 单个申请授权，没有 REQUEST_MULTIPLE_PERMISSION 回调
     *
     * @param activity
     *         baseUI
     * @param permission
     *         请求授权的权限
     */
    public void requestPermission(Activity activity, String permission) {
        if (activity == null || TextUtils.isEmpty(permission)) {
            return;
        }

        boolean isGranted = checkSelfPermission(activity, permission);
        if (isGranted) {
            instance.mPermissionGrant.onPermissionGranted(permission, true);
        } else {
            ActivityCompat.requestPermissions(activity, new String[]{permission}, 0);
        }
    }

    /**
     * 一次申请多个权限，带有 REQUEST_MULTIPLE_PERMISSION 回调
     *
     * @param activity
     *         baseUI
     * @param permissions
     *         请求授权的权限数组
     */
    public void requestPermission(Activity activity, String[] permissions) {
        if (activity == null || permissions == null) {
            return;
        }

        if (permissions.length > 0) {
            // 已授权列表
            ArrayList<String> granted = new ArrayList<>();
            // 未授权列表
            ArrayList<String> notGranted = new ArrayList<>();
            for (String permission : permissions) {
                boolean isGranted = checkSelfPermission(activity, permission);
                if (isGranted) {
                    granted.add(permission);
                } else {
                    notGranted.add(permission);
                }
            }

            if (notGranted.size() == 0) {
                instance.mPermissionGrant.onPermissionGranted(REQUEST_MULTIPLE_PERMISSION, true);
            }

            for (int i = 0; i < granted.size(); i++) {
                instance.mPermissionGrant.onPermissionGranted(granted.get(i), true);
            }

            if (notGranted.size() > 0) {
                ActivityCompat.requestPermissions(activity, notGranted.toArray(new String[notGranted.size()]), REQUEST_CODE_MULTIPLE_PERMISSION);
            }
        }
    }

    /**
     * 授权结果处理
     *
     * @param permissions
     *         权限数组
     * @param grantResults
     *         授权结果处理
     *         0 =PackageManager.PERMISSION_GRANTED 权限开启成功
     *         -1=PackageManager.PERMISSION_DENIED  权限开启失败
     */
    public void requestPermissionsResult(String[] permissions, int[] grantResults, int requestCode) {
        if (permissions == null || grantResults == null || permissions.length <= 0 || grantResults.length <= 0) {
            return;
        }

        if (requestCode == REQUEST_CODE_MULTIPLE_PERMISSION) {// 一次申请多个授权的 结果处理
            // 是否授权多个权限成功
            boolean isGrantedAll = true;
            for (int i = 0; i < permissions.length; i++) {
                mPermissionGrant.onPermissionGranted(permissions[i], grantResults[i] == PackageManager.PERMISSION_GRANTED);

                if (isGrantedAll && grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                    isGrantedAll = false;
                }
            }
            mPermissionGrant.onPermissionGranted(REQUEST_MULTIPLE_PERMISSION, isGrantedAll);

        } else {// 一次申请单个授权的 结果处理
            mPermissionGrant.onPermissionGranted(permissions[0], grantResults[0] == PackageManager.PERMISSION_GRANTED);
        }
    }

    /**
     * 检查是否有XXX权限
     *
     * @param context
     *         Context
     * @param permission
     *         权限m名称
     *
     * @return true有该权限 (6.0以下代表在manifest文件注册了该权限   6.0以上则代表用户授权同意了该权限)
     */
    public boolean checkSelfPermission(Context context, String permission) {
        // 如果是6.0以下的手机，ActivityCompat.checkSelfPermission()会始终等于PERMISSION_GRANTED，除非没有在manifest文件注册才会等于PERMISSION_DENIED
        // 但是，如果用户关闭了你申请的权限，ActivityCompat.checkSelfPermission(),会导致程序崩溃(java.lang.RuntimeException: Unknown exception code: 1 msg null)
        // 以上崩溃问题未重现

        try {
            int checkSelfPermission = ActivityCompat.checkSelfPermission(context, permission);
            // int checkSelfPermission1 = ContextCompat.checkSelfPermission(context, permission);
            // LogUtil.i(checkSelfPermission + "  .......  " + permission);
            return checkSelfPermission == PackageManager.PERMISSION_GRANTED;
        } catch (Exception e) {
            e.printStackTrace();
            return true;
        }
    }

    /**
     * 权限开启成功失败回调
     */
    public interface PermissionGrant {

        /**
         * 权限开启
         *
         * @param permissionName
         *         请求授权的权限名称
         * @param isSuccess
         *         true 授权成功
         */
        void onPermissionGranted(Object permissionName, boolean isSuccess);
    }


    // 6.0权限的基本知识，以下是需要单独申请的权限，共分为9组，每组只要有一个权限申请成功了，就默认整组权限都可以使用了。
    // PermissionGroup	Permissions
    //    用户日历数据
    //    CALENDAR      READ_CALENDAR
    //                  WRITE_CALENDAR
    //    相机
    //    CAMERA        CAMERA
    //    通讯录
    //    CONTACTS      READ_CONTACTS
    //                  WRITE_CONTACTS
    //                  GET_ACCOUNTS
    //    定位
    //    LOCATION      ACCESS_FINE_LOCATION    //通过GPS芯片接收卫星
    //                  ACCESS_COARSE_LOCATION  //通过WIFI芯片接收卫星
    //    麦克风
    //    MICROPHONE    RECORD_AUDIO
    //    通话记录
    //    PHONE         READ_PHONE_STATE
    //                  CALL_PHONE
    //                  READ_CALL_LOG
    //                  WRITE_CALL_LOG
    //                  ADD_VOICEMAIL
    //                  USE_SIP
    //                  PROCESS_OUTGOING_CALLS
    //   传感器
    //   SENSORS        BODY_SENSORS
    //   短信
    //   SMS            SEND_SMS
    //                  RECEIVE_SMS
    //                  READ_SMS
    //                  RECEIVE_WAP_PUSH
    //                  RECEIVE_MMS
    //   存储
    //   STORAGE        READ_EXTERNAL_STORAGE
    //                  WRITE_EXTERNAL_STORAGE

}
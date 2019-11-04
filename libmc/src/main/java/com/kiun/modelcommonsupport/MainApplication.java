package com.kiun.modelcommonsupport;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;

import com.amos.modulebase.ModuleBaseApplication;
import com.amos.modulebase.bean.ResponseBean;
import com.amos.modulebase.mvp.model.HttpOkBiz;
import com.amos.modulebase.utils.IntentUtil;
import com.amos.modulebase.utils.JsonUtil;
import com.amos.modulebase.utils.LogUtil;
import com.amos.modulebase.utils.dialog.CustomDialog;
import com.amos.modulebase.utils.dialog.DialogUtil;
import com.amos.modulebase.utils.http.HttpRequestCallBack;
import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMOptions;
import com.hyphenate.easeui.EaseUI;
import com.igexin.sdk.PushManager;
import com.kiun.modelcommonsupport.controllers.BaseRequestAcitity;
import com.kiun.modelcommonsupport.controllers.authority.LoginActivity;
import com.kiun.modelcommonsupport.data.MCUserInfo;
import com.kiun.modelcommonsupport.data.drive.MCDataField;
import com.kiun.modelcommonsupport.hx.CallReceiver;
import com.kiun.modelcommonsupport.network.MCBaseRequest;
import com.kiun.modelcommonsupport.push.DemoIntentService;
import com.kiun.modelcommonsupport.push.DemoPushService;
import com.kiun.modelcommonsupport.utils.AppUtil;
import com.kiun.modelcommonsupport.utils.HttpRequestBiz;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by kiun_2007 on 2018/8/11.
 */

public class MainApplication extends Application {

    public static final String USER_INFO = "USER_INFO";
    public static final String MASTER_INFO = "MASTER_Profession";

    public static String LOGOUT_ACTION = "com.xzxx.logout";
    public static MCUserInfo localUserInfo; //内存暂时保存.

    private static MainApplication application;
    public static int H, W;

    CallReceiver callReceiver;

    public static MainApplication getInstance() {
        return application;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        ModuleBaseApplication.getInstance().initSDK(this);
        application = this;
        getScreen(this);
        AppUtil.setCurContext(this.getApplicationContext());
        EMClient.getInstance().init(this, new EMOptions());
        EaseUI.getInstance().init(this, new EMOptions());

        MCUserInfo userInfo = getUserInfo(false);

        if (userInfo != null) {
            MCBaseRequest.configTokenId(userInfo.tokenId);
            loginHx(userInfo);
        }

        IntentFilter callFilter = new IntentFilter(EMClient.getInstance().callManager().getIncomingCallBroadcastAction());
        if (callReceiver == null) {
            callReceiver = new CallReceiver();
        }
        getApplicationContext().registerReceiver(callReceiver, callFilter);
        if (handler == null) {
            handler = new DemoHandler();
        }

        PushManager.getInstance().initialize(this.getApplicationContext(), DemoPushService.class);
        // 注册 intentService 后 PushDemoReceiver 无效, sdk 会使用 DemoIntentService 传递数据,
        // AndroidManifest 对应保留一个即可(如果注册 DemoIntentService, 可以去掉 PushDemoReceiver, 如果注册了
        // IntentService, 必须在 AndroidManifest 中声明)
        PushManager.getInstance().registerPushIntentService(this.getApplicationContext(), DemoIntentService.class);
    }

    private void loginHx(MCUserInfo userInfo) {
        if (userInfo != null) {

            if (!TextUtils.isEmpty(userInfo.hxAccount) && !TextUtils.isEmpty(userInfo.hxPassword)) {
                PushManager.getInstance().bindAlias(application, userInfo.hxAccount);
                String cid = PushManager.getInstance().getClientid(this);
                LogUtil.i(cid);
                //                upLoadCid(cid);
                EMClient.getInstance().login(userInfo.hxAccount, userInfo.hxPassword, new EMCallBack() {
                    @Override
                    public void onSuccess() {
                        Log.i("HXLOG", "环信登录成功======================================");
                    }

                    @Override
                    public void onError(int i, String s) {
                    }

                    @Override
                    public void onProgress(int i, String s) {
                    }
                });

//                EMClient.getInstance().addConnectionListener(new EMConnectionListener() {
//                    @Override
//                    public void onConnected() {
//
//                    }
//
//                    @Override
//                    public void onDisconnected(int error) {
//                        //用户删除
//                        //用户登录设备
//                        //ServService限制
//                        //用户密码
//                        //用户登录其他设备
//                        if (error == EMError.USER_REMOVED || error == EMError.USER_LOGIN_ANOTHER_DEVICE || error == EMError.SERVER_SERVICE_RESTRICTED
//                                || error == EMError.USER_KICKED_BY_CHANGE_PASSWORD || error == EMError.USER_KICKED_BY_OTHER_DEVICE) {
//                        } else {
//                            loginHx(MainApplication.getInstance().getUserInfo(false));
//                        }
//                    }
//                });
            }
        }
    }

    public void getScreen(Context context) {
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        H = dm.heightPixels;
        W = dm.widthPixels;
    }

    /**
     * 保存一个数据.
     *
     * @param key
     *         数据键.
     * @param value
     *         数据值.
     */
    public void save(String key, String value) {
        SharedPreferences sp = this.getSharedPreferences("AppSave", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(key, value);
        editor.commit();
    }

    /**
     * 获取保存的数据.
     *
     * @param key
     *         保存时使用的KEY.
     *
     * @return 数据值.
     */
    public String getValue(String key) {
        SharedPreferences sp = this.getSharedPreferences("AppSave", Activity.MODE_PRIVATE);
        return sp.getString(key, null);
    }

    /**
     * 获取用户信息数据.
     *
     * @return 用户信息数据.
     */
    public MCUserInfo getUserInfo(boolean isLogin) {

        if (localUserInfo != null) {
            return localUserInfo;
        }

        String userInfoStr = getValue(USER_INFO);
        if (userInfoStr == null) {
            return null;
        }
        return toUserInfo(userInfoStr);
    }

    private MCUserInfo toUserInfo(String json) {
        MCUserInfo userInfo = new MCUserInfo();
        JSONTokener jsonParser = new JSONTokener(json);
        try {
            JSONObject object = (JSONObject) jsonParser.nextValue();
            MCDataField.fillObject(userInfo, object);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
        return userInfo;
    }

    public void saveUserInfo(MCUserInfo userInfo) {
        if (userInfo.aduitType != null && MCBaseRequest.isMaster() && !userInfo.aduitType.equals("1")) {
            localUserInfo = userInfo;
            return; //未审核通过的账户无需保存文件.
        }

        Map map = MCDataField.objToDict(userInfo, null);
        JSONObject jsonObject = new JSONObject(map);
        save(USER_INFO, jsonObject.toString());
    }

    /**
     * 退出登录.
     */
    public void logOut() {
        SharedPreferences sp = this.getSharedPreferences("AppSave", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.remove(USER_INFO);
        editor.commit();

        MCBaseRequest.configTokenId(null);
        Intent intent = new Intent();
        intent.setAction(LOGOUT_ACTION);
        sendBroadcast(intent);

        EMClient.getInstance().logout(true);
    }

    /**
     * 登入操作.
     *
     * @param object
     *         登入之后服务器返回的数据.
     */
    public void login(Object object) {
        MCUserInfo userInfo = toUserInfo(object.toString());
        saveUserInfo(userInfo);
        MCBaseRequest.configTokenId(userInfo.tokenId);
        saveRequesBaseData();
        loginHx(userInfo);
    }

    public Drawable getBitmapByName(String name) {
        int resId = this.getResId(name, "drawable");
        if (resId == 0) {
            Log.e("MainApplication", "资源文件不存在, name = " + name);
            return null;
        }
        return this.getResources().getDrawable(resId);
    }

    public Drawable getMipmapByName(String name) {
        int resId = this.getResId(name, "mipmap");
        if (resId == 0) {
            Log.e("MainApplication", "资源文件不存在, name = " + name);
            return null;
        }
        return this.getResources().getDrawable(resId);
    }

    public String getStringByName(String name) {
        int resId = this.getResId(name, "string");
        if (resId == 0) {
            Log.e("MainApplication", "资源文件不存在, name = " + name);
            return null;
        }
        return this.getResources().getString(resId);
    }

    public int getResId(String name, String type) {
        return this.getApplicationContext().getResources().getIdentifier(name, type, this.getPackageName());
    }

    public void saveRequest(MCBaseRequest request, String value) {
        File requestFiles = new File(getApplicationContext().getCacheDir(), "requestFiles");
        if (!requestFiles.exists()) {
            requestFiles.mkdir();
        }

        File requestFile = new File(requestFiles, request.cacheFileName());
        try {
            OutputStream outPutStream = new FileOutputStream(requestFile);
            outPutStream.write(value.getBytes("UTF-8"));
            outPutStream.flush();
            outPutStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String readRequest(MCBaseRequest request) {

        File requestFiles = new File(getApplicationContext().getCacheDir(), "requestFiles");
        if (!requestFiles.exists()) {
            requestFiles.mkdir();
        }
        File requestFile = new File(requestFiles, request.cacheFileName());

        if (!requestFile.exists()) {
            return null;
        }

        String encoding = "UTF-8";
        Long filelength = requestFile.length();
        byte[] filecontent = new byte[filelength.intValue()];
        try {
            FileInputStream in = new FileInputStream(requestFile);
            in.read(filecontent);
            in.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            return new String(filecontent, encoding);
        } catch (UnsupportedEncodingException e) {
            System.err.println("The OS does not support " + encoding);
            e.printStackTrace();
            return null;
        }
    }


    /**
     * 应用未启动, 个推 service已经被唤醒,保存在该时间段内离线消息(此时 GetuiSdkDemoActivity.tLogView == null)
     */
    public static StringBuilder payloadData = new StringBuilder();
    private static DemoHandler handler;
    public static BaseRequestAcitity acitity;

    public static void sendMessage(Message msg) {
        handler.sendMessage(msg);
    }

    public static class DemoHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    try {
                        SharedPreferences sp = acitity.getSharedPreferences("AppSave", Activity.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sp.edit();
                        editor.remove(USER_INFO);
                        editor.commit();

                        MCBaseRequest.configTokenId(null);
                        EMClient.getInstance().logout(true);
                        //                        Toast.makeText(acitity, "被抢登", Toast.LENGTH_SHORT).show();
                        //                ToastUtil.showToast(context,"被抢登");
                        DialogUtil.showMessageDg(acitity, "登录通知", "您的账号于" + com.amos.modulebase.utils.DateUtil.getDate("MM-dd HH:mm") + "在其他手机登录，如非本人操作，则密码可能被泄露。", "", "重新登录", new CustomDialog.OnDialogClickListener() {
                            @Override
                            public void onClick(CustomDialog customDialog, int i, Object o) {
                                customDialog.dismiss();
                                android.os.Process.killProcess(android.os.Process.myPid());    //获取PID
                                System.exit(0);
                            }
                        }, new CustomDialog.OnDialogClickListener() {
                            @Override
                            public void onClick(CustomDialog customDialog, int i, Object o) {
                                customDialog.dismiss();
                                IntentUtil.gotoActivity(acitity, LoginActivity.class);
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
            }
        }
    }

    public void saveRequesBaseData() {
        HttpRequestBiz httpRequestBiz = new HttpRequestBiz();
        HashMap<String, String> params = httpRequestBiz.getBaseParams();
        params.put("requestUrl", httpRequestBiz.requestUrl);

        String content = JsonUtil.map2JsonObject(params).toString();
        SharedPreferences sp = this.getSharedPreferences("AppSave", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("saveRequesBaseData", content);
        editor.commit();
    }

    private void upLoadCid(String cid) {
        HashMap<String, String> map = new HashMap<>();
        HttpRequestBiz httpRequestBiz = new HttpRequestBiz();
        map.put("clientId", cid);
        HashMap<String, String> params = httpRequestBiz.getBaseParams("user/userInfo/submitClientId", map);

        HttpOkBiz.getInstance().sendPost(httpRequestBiz.requestUrl, params, new HttpRequestCallBack() {

            @Override
            public void onSuccess(ResponseBean result) {
            }

            @Override
            public void onFail(ResponseBean result) {
            }
        });
    }
}
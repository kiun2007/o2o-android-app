package com.xzxx.decorate.o2o;

import android.content.Intent;

import com.kiun.modelcommonsupport.MainApplication;
import com.kiun.modelcommonsupport.controllers.authority.LoginActivity;
import com.kiun.modelcommonsupport.data.MCUserInfo;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.xzxx.decorate.o2o.pay.WechatPay;

/**
 * Created by kiun_2007 on 2018/8/13.
 */

public class ConsumerApplication extends MainApplication {

    @Override
    public void onCreate() {
        LOGOUT_ACTION = "com.xzxx.logout.consumer";
        super.onCreate();
        WechatPay.setIwxapi(WXAPIFactory.createWXAPI(this.getApplicationContext(), null));
    }

    @Override
    public MCUserInfo getUserInfo(boolean isLogin) {
        MCUserInfo userInfo = super.getUserInfo(isLogin);
        if (userInfo == null && isLogin) {
            Intent intent = new Intent(this.getApplicationContext(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
        return userInfo;
    }
}

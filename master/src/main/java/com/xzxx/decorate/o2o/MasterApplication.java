package com.xzxx.decorate.o2o;

import android.content.Intent;

import com.kiun.modelcommonsupport.MainApplication;
import com.kiun.modelcommonsupport.controllers.authority.LoginActivity;
import com.kiun.modelcommonsupport.data.MCUserInfo;
import com.kiun.modelcommonsupport.network.MCBaseRequest;
import com.xzxx.decorate.o2o.master.MainActivity;

/**
 * Created by kiun_2007 on 2018/8/13.
 */

public class MasterApplication extends MainApplication {

    @Override
    public void onCreate() {
        LOGOUT_ACTION = "com.xzxx.logout.master";
        super.onCreate();
        MCBaseRequest.configType(1);
    }

    @Override
    public MCUserInfo getUserInfo(boolean isLogin) {
        MCUserInfo userInfo = super.getUserInfo(isLogin);
        if(userInfo == null && isLogin){
            Intent intent = new Intent(this.getApplicationContext(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
        return userInfo;
    }

    @Override
    public void login(Object object) {
        super.login(object);
        Intent intent = new Intent(this.getApplicationContext(), MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}

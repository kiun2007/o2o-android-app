package com.kiun.modelcommonsupport.controllers.authority;

import android.content.Intent;
import android.text.TextUtils;
import android.widget.TextView;

import com.kiun.modelcommonsupport.MainApplication;
import com.kiun.modelcommonsupport.R;
import com.kiun.modelcommonsupport.network.MCBaseRequest;
import com.kiun.modelcommonsupport.network.requests.CodeRequest;
import com.kiun.modelcommonsupport.network.requests.ForgetRequest;

/**
 * Created by zf on 2018/7/1.
 * 忘记密码页面
 */
public class ForgetPasswordActivity extends PhoneCodeActivity {

    @Override
    public int getLayoutId() {
        type = "3";
        if (MainApplication.getInstance().getUserInfo(false) != null){
            return R.layout.layout_user_forget_password_settings;
        }
        return R.layout.layout_user_forget_password;
    }
    TextView txt_phone;
    @Override
    public void initView() {
        super.initView();
        txt_phone=findViewById(R.id.txt_phone);
        if (MainApplication.getInstance().getUserInfo(false) != null){
            fillToView(-1, MainApplication.getInstance().getUserInfo(false));
            String phone = MainApplication.getInstance().getValue("sp_key_login_phone");
            if (!TextUtils.isEmpty(phone)) {
                txt_phone.setText(phone.replace(phone.substring(3, 7), "****"));
            }
        }

    }


    @Override
    protected int getCodeEdit() {
        return R.id.codeSendEdit;
    }

    @Override
    protected boolean fillPhoneValue(CodeRequest request) {
        if (MainApplication.getInstance().getUserInfo(false) != null){
            request.phone = MainApplication.getInstance().getUserInfo(false).phone;
        }
        return true;
    }

    @Override
    protected Intent getNextIntent() {
        return new Intent(this, SettingLoginPasswodActivity.class);
    }

    @Override
    protected MCBaseRequest getCurRequest() {
        return null;
    }

    @Override
    protected MCBaseRequest getNextRequest() {
        return new ForgetRequest();
    }

    @Override
    public void onDataChanged(Object data, MCBaseRequest request) {
        super.onDataChanged(data, request);
    }
}

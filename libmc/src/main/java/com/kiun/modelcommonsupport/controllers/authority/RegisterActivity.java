package com.kiun.modelcommonsupport.controllers.authority;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.kiun.modelcommonsupport.R;
import com.kiun.modelcommonsupport.network.MCBaseRequest;
import com.kiun.modelcommonsupport.network.requests.RegisterRequest;
import com.kiun.modelcommonsupport.ui.views.UserEditText;

import java.util.regex.Pattern;

/**
 * Created by kiun_2007 on 2018/8/20.
 */
public class RegisterActivity extends PhoneCodeActivity {

    @Override
    protected int getCodeEdit() {
        return R.id.codeSendEdit;
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
        return new RegisterRequest();
    }

    @Override
    public int getLayoutId() {
        return R.layout.layout_user_register;
    }

    @Override
    public void initView() {
        super.initView();

        View txt_register = findViewById(R.id.txt_register);
        txt_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putString("url", "file:///android_asset/user.htm");
                bundle.putString("title", "哇屋用户协议");
                Intent intent = new Intent(RegisterActivity.this, WebBaseActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        if (getPackageName().contains("master")) {
            getNavigatorBar().setTitle("注册");
        } else {
            getNavigatorBar().setTitle("用户注册");
        }
    }

    //    phoneEdit
    @Override
    public void onSubmitClick(Button button) {
        UserEditText phoneEdit = findViewById(R.id.phoneEdit);
        String phone = phoneEdit.getText();
        if (TextUtils.isEmpty(phone)) {
            Toast.makeText(this, "请输入手机号", Toast.LENGTH_SHORT).show();
            return;
        } else if (!Pattern.matches("^1[0-9]{10}$", phone)) {
            Toast.makeText(this, "请输入正确的手机号", Toast.LENGTH_SHORT).show();
            return;
        }
        super.onSubmitClick(button);
    }
}

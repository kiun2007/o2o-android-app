package com.xzxx.decorate.o2o.master;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;

import com.kiun.modelcommonsupport.adapter.ItemListener;
import com.kiun.modelcommonsupport.network.MCBaseRequest;
import com.kiun.modelcommonsupport.utils.MCDialogManager;
import com.xzxx.decorate.o2o.BaseActivity;

/**
 * Created by zf on 2018/6/29.
 * 设置登陆密码页面
 */
public class SettingLoginPasswodActivity extends BaseActivity{

    MCBaseRequest request = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public int getLayoutId() {
        return R.layout.layout_setting_login_password;
    }

    @Override
    public void initView() {
        request = getIntent().getParcelableExtra("request");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    @Override
    public void onDataChanged(Object data, MCBaseRequest request) {
        MCDialogManager.showMessage(this, "您已注册成功!", "欢迎您来到小装小修", "立即去登录", R.drawable.icon_laugh).setListener(new ItemListener() {
            @Override
            public void onItemClick(View listView, Object itemData, int tag) {
                SettingLoginPasswodActivity.this.finish();
            }
        });
    }

    @Override
    public void onSubmitClick(Button button) {
        if(fillRequest(request, null)){
            commitRequest(request);
        }
    }
}

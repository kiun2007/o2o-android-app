package com.xzxx.decorate.o2o.master;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import com.kiun.modelcommonsupport.network.MCBaseRequest;
import com.xzxx.decorate.o2o.BaseActivity;

/**
 * Created by zf on 2018/7/21.
 * 验证手机号页面
 */
public class VerifyPhoneNumberActivity extends BaseActivity {

    @Override
    public int getLayoutId() {
        return R.layout.layout_verify_phone;
    }

    @Override
    public void initView() {
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onDataChanged(Object data, MCBaseRequest request) {
    }
}

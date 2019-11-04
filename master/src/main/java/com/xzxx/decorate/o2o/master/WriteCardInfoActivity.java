package com.xzxx.decorate.o2o.master;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import com.phillipcalvin.iconbutton.IconButton;

/**
 * Created by zf on 2018/7/21.
 * 填写银行卡及身份信息
 */
public class WriteCardInfoActivity extends AppCompatActivity implements View.OnClickListener {

    private IconButton btn_write_card_next;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_write_card_info);
        btn_write_card_next = findViewById(R.id.btn_write_card_next);
        initEvent();
    }

    private void initEvent() {
        btn_write_card_next.setOnClickListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_write_card_next:
                Intent verifyPhoneIntent = new Intent(this, VerifyPhoneNumberActivity.class);
                startActivity(verifyPhoneIntent);
                break;
        }
    }
}

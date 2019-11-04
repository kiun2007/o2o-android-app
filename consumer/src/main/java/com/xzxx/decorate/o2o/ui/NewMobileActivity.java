package com.xzxx.decorate.o2o.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.kiun.modelcommonsupport.network.MCBaseRequest;
import com.phillipcalvin.iconbutton.IconButton;
import com.xzxx.decorate.o2o.consumer.R;

/**
 * Created by zf on 2018/7/1.
 * 新号码验证页面
 */
public class NewMobileActivity extends BaseActivity implements View.OnClickListener {

    private IconButton iconButton;
    private ImageView backImage;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        backImage = findViewById(R.id.id_new_mobile_return);
        iconButton = findViewById(R.id.id_new_mobile_ok);
        backImage.setOnClickListener(this);
        iconButton.setOnClickListener(this);
    }

    @Override
    public int getLayoutId() {
        return R.layout.layout_new_mobile;
    }

    @Override
    public void initView() {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.id_new_mobile_return) {
            finish();
        } else if (v.getId() == R.id.id_new_mobile_ok) {
            final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
            alertDialog.show();
            alertDialog.setContentView(R.layout.dialog_modify_mobile_success);
            Window window = alertDialog.getWindow();
            window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
            window.setBackgroundDrawableResource(android.R.color.white);
            window.setGravity(Gravity.BOTTOM);
            window.setWindowAnimations(R.style.AlertDialog_AppCompat);

            TextView id_register_just_login_ok = alertDialog.findViewById(R.id.id_register_just_login_ok);
            id_register_just_login_ok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alertDialog.dismiss();
                }
            });
        }
    }

    @Override
    public void onDataChanged(Object data, MCBaseRequest request) {

    }
}

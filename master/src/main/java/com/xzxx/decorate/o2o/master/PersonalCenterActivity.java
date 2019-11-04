package com.xzxx.decorate.o2o.master;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.kiun.modelcommonsupport.MainApplication;
import com.kiun.modelcommonsupport.data.MCUserInfo;
import com.kiun.modelcommonsupport.data.drive.MCDataField;
import com.kiun.modelcommonsupport.network.MCBaseRequest;
import com.xzxx.decorate.o2o.BaseActivity;
import com.xzxx.decorate.o2o.requests.MasterHomeInfoRequest;
import com.xzxx.decorate.o2o.requests.MasterInfoRequest;

import org.json.JSONObject;

/**
 * Created by zf on 2018/7/15.
 * 个人中心页面
 */
public class PersonalCenterActivity extends BaseActivity implements View.OnClickListener{


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        MCUserInfo userInfo = MainApplication.getInstance().getUserInfo(false);
        if(userInfo != null){
            if(!userInfo.masterType.endsWith("0")){
                findViewById(R.id.walletCellView).setVisibility(View.GONE);
            }
        }
    }

    @Override
    public int getLayoutId() {
        return R.layout.layout_personal;
    }

    @Override
    public void initView() {
        findViewById(R.id.id_custer_photo).setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(MainApplication.getInstance().getUserInfo(false) == null){
            finish();
            return;
        }
        commitRequest(new MasterHomeInfoRequest());
    }

    @Override
    public void onDataChanged(Object data, MCBaseRequest request) {
        if(request instanceof MasterHomeInfoRequest){
            fillToView(-1, data);
        }
    }

    public void onClick(View v){
        Intent intent = new Intent(this, PersonalInfoActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}

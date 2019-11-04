package com.xzxx.decorate.o2o.master;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.kiun.modelcommonsupport.MainApplication;
import com.kiun.modelcommonsupport.data.MCUserInfo;
import com.kiun.modelcommonsupport.network.MCBaseRequest;
import com.phillipcalvin.iconbutton.IconButton;
import com.xzxx.decorate.o2o.BaseActivity;
import com.xzxx.decorate.o2o.requests.AuditInfoRequest;

import org.json.JSONObject;

/**
 * Created by zf on 2018/7/25.
 * 师傅注册（资料审核中，资料审核未通过）
 */
public class RegisterSecondActivity extends BaseActivity {

    ImageView auditStatusImageView;
    IconButton btnRegister2Rewrite;
    TextView waitAuditTV;

    @Override
    public int getLayoutId() {
        return R.layout.layout_register_first2;
    }

    @Override
    public void initView() {
        getNavigatorBar().setLeftButtonVisibility(View.GONE);
        onRefresh();
        auditStatusImageView = findViewById(R.id.auditStatusImageView);
        btnRegister2Rewrite = findViewById(R.id.btn_register2_rewrite);
        waitAuditTV = findViewById(R.id.waitAuditTV);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onDataChanged(Object data, MCBaseRequest request) {
        completeRefresh();
        if (request instanceof AuditInfoRequest) {
            fillToView(-1, data);

            if(((JSONObject) data).optString("auditStatus").equals("1")){
                MCUserInfo userInfo = MainApplication.getInstance().getUserInfo(false);
                if (userInfo != null) {
                    userInfo.aduitType = "1";
                    MainApplication.getInstance().saveUserInfo(userInfo);
                }
                finish();

                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                return;
            }

            auditStatusImageView.setVisibility(View.VISIBLE);
            if(((JSONObject) data).optString("auditStatus").equals("0")){
                btnRegister2Rewrite.setVisibility(View.GONE);
                auditStatusImageView.setImageDrawable(getResources().getDrawable(R.drawable.bg_seal_blue));
                findViewById(R.id.auditAdvice).setVisibility(View.GONE);
            }else{
                waitAuditTV.setVisibility(View.GONE);
                auditStatusImageView.setImageDrawable(getResources().getDrawable(R.drawable.bg_seal_red));
                btnRegister2Rewrite.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void onRefresh() {
        commitRequest(new AuditInfoRequest());
    }

    @Override
    public void onSubmitClick(Button button) {
        Intent intent = new Intent(this, RegisterFirstActivity.class);
        intent.putExtra("Audit", true);
        startActivity(intent);
        finish();
    }
}

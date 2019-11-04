package com.xzxx.decorate.o2o.master.bank;

import android.content.Intent;
import android.view.View;
import android.widget.RelativeLayout;

import com.kiun.modelcommonsupport.MainApplication;
import com.kiun.modelcommonsupport.data.MCUserInfo;
import com.kiun.modelcommonsupport.network.MCBaseRequest;
import com.xzxx.decorate.o2o.BaseActivity;
import com.xzxx.decorate.o2o.master.R;
import com.xzxx.decorate.o2o.requests.BankCardListRequest;

/**
 * Created by zf on 2018/7/21.
 * 我的银行卡页面
 */
public class PersonalBandCardActivity extends BaseActivity implements View.OnClickListener {

    private RelativeLayout rl_add_band_card;

    @Override
    public int getLayoutId() {
        return R.layout.layout_personal_bandcard;
    }

    @Override
    public void initView() {
        rl_add_band_card = findViewById(R.id.rl_add_band_card);
        rl_add_band_card.setOnClickListener(this);

        MCUserInfo userInfo = MainApplication.getInstance().getUserInfo(false);
        if (userInfo != null && userInfo.masterType.equals("1")) {
            rl_add_band_card.setVisibility(View.GONE);
        } else {
            rl_add_band_card.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_add_band_card:
                Intent addBandCardIntent = new Intent(this, AddBandCardActivity.class);
                startActivity(addBandCardIntent);
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        commitRequest(new BankCardListRequest());
    }

    @Override
    public void onDataChanged(Object data, MCBaseRequest request) {

        if (request instanceof BankCardListRequest) {
            fillToView(R.id.listView, data, true);
        }
    }
}

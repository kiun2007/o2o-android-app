package com.xzxx.decorate.o2o.master;

import android.content.Intent;
import android.widget.Button;
import com.kiun.modelcommonsupport.network.MCBaseRequest;
import com.kiun.modelcommonsupport.ui.views.ATextView;
import com.xzxx.decorate.o2o.BaseActivity;
import com.xzxx.decorate.o2o.requests.AcntBalanRequest;

/**
 * Created by zf on 2018/7/18.
 * 我的钱包页面
 */
public class PersonalWalletActivity extends BaseActivity {

    ATextView balanceLabel;

    @Override
    public int getLayoutId() {
        return R.layout.layout_my_wallet;
    }

    @Override
    public void initView() {
        balanceLabel = findViewById(R.id.balanceLabel);
        commitRequest(new AcntBalanRequest());
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onDataChanged(Object data, MCBaseRequest request) {
        if (request instanceof AcntBalanRequest) {
            balanceLabel.setText(data.toString());
        }
    }

    @Override
    public void onSubmitClick(Button button) {
        Intent balanceIntent = new Intent(this, BalanceActivity.class);
        startActivity(balanceIntent);
    }
}

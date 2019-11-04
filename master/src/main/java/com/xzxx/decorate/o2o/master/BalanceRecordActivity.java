package com.xzxx.decorate.o2o.master;

import android.content.Intent;
import android.view.View;
import com.kiun.modelcommonsupport.adapter.ItemListener;
import com.kiun.modelcommonsupport.network.MCBaseRequest;
import com.kiun.modelcommonsupport.ui.views.AListView;
import com.xzxx.decorate.o2o.BaseActivity;
import com.xzxx.decorate.o2o.requests.WithdrawListRequest;

/**
 * Created by zf on 2018/7/18.
 * 提现记录
 */
public class BalanceRecordActivity extends BaseActivity implements ItemListener {

    private AListView listView;
    @Override
    public int getLayoutId() {
        return R.layout.layout_balance_record;
    }

    @Override
    public void initView() {
        listView = findViewById(R.id.lv_balance_record);
        listView.setListRequest(new WithdrawListRequest());
        listView.setItemListener(this);
    }

    @Override
    public void onDataChanged(Object data, MCBaseRequest request) {
    }

    @Override
    public void onItemClick(View listView, Object itemData, int tag) {
        Intent intent = new Intent(this, BalanceRecordDetailActivity.class);
        intent.putExtra("extra", itemData.toString());
        startActivity(intent);
    }
}

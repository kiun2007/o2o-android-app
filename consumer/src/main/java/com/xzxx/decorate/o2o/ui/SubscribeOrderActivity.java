package com.xzxx.decorate.o2o.ui;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import com.kiun.modelcommonsupport.controllers.BaseRequestAcitity;
import com.kiun.modelcommonsupport.network.MCBaseRequest;
import com.xzxx.decorate.o2o.consumer.R;
import com.xzxx.decorate.o2o.view.mypicker.TimePickerDialog;

/**
 * Created by zf on 2018/8/13.
 * 预约下单页面
 */
public class SubscribeOrderActivity extends BaseRequestAcitity implements View.OnClickListener {

    private RelativeLayout rl_subscribe_time;
    private Dialog dateDialog, timeDialog;

    @Override
    public int getLayoutId() {
        return R.layout.layout_subscribe_order;
    }

    @Override
    public void initView() {
        rl_subscribe_time = findViewById(R.id.rl_subscribe_time);
        initEvent();
    }

    private void initEvent() {
        rl_subscribe_time.setOnClickListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onDataChanged(Object data, MCBaseRequest request) {

    }

    private void showTimePick() {
        if (timeDialog == null) {
            TimePickerDialog.Builder builder = new TimePickerDialog.Builder(this);
            timeDialog = builder.setOnTimeSelectedListener(new TimePickerDialog.OnTimeSelectedListener() {
                @Override
                public void onTimeSelected(int[] times) {
                    Log.e("SubscribeOrderActivity",times[0] + ":" + times[1]);
                }
            }).create();
        }
        timeDialog.show();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_subscribe_time:
                showTimePick();
                break;
        }
    }
}

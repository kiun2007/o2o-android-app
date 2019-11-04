package com.xzxx.decorate.o2o.master;

import android.view.View;
import android.widget.TextView;

import com.kiun.modelcommonsupport.data.drive.MCDataField;
import com.kiun.modelcommonsupport.network.MCBaseRequest;
import com.xzxx.decorate.o2o.BaseActivity;
import com.xzxx.decorate.o2o.bean.RecordModel;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by zf on 2018/7/19.
 * 提现记录详情
 */
public class BalanceRecordDetailActivity extends BaseActivity {

    View view_fail;
    TextView txt_status;
    @Override
    public int getLayoutId() {
        return R.layout.layout_balance_record_detail;
    }

    @Override
    public void initView() {
        view_fail = findViewById(R.id.view_fail);
        txt_status = findViewById(R.id.txt_status);
        try {
            JSONObject jsonObject = new JSONObject(getIntent().getStringExtra("extra"));
            RecordModel recordModel = new RecordModel();
            MCDataField.fillBean(recordModel, jsonObject);
            fillToView(-1, recordModel);

            //            0处理中 1通过  2失败
            if(recordModel.getStatus().endsWith("2")){
                txt_status.setTextColor(getResources().getColor(R.color.red));
                view_fail.setVisibility(View.VISIBLE);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDataChanged(Object data, MCBaseRequest request) {
    }
}

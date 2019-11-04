package com.kiun.modelcommonsupport.controllers;

import android.content.Intent;
import android.view.View;

import com.kiun.modelcommonsupport.R;
import com.kiun.modelcommonsupport.adapter.ItemListener;
import com.kiun.modelcommonsupport.data.BeanBase;
import com.kiun.modelcommonsupport.network.MCBaseRequest;
import com.kiun.modelcommonsupport.network.requests.MyVoucherRequest;
import com.kiun.modelcommonsupport.ui.views.AListView;

import org.json.JSONObject;

import java.util.Map;

/**
 * Created by kiun_2007 on 2018/8/25.
 */

public class VoucherActivity extends BaseRequestAcitity implements ItemListener<BeanBase>{

    AListView listView = null;

    //[{"voucherType":"1","voucherMoney":"","maxFullSubtractionMoney":"","minFullSubtractionMoney":"","expireTime":"20180831000000","discount":"","voucherId":"201808151430670007079273","secondItemId":"","firstItemId":"","voucherName":"优惠券字数测试","voucherDesc":"码复制里重新编译试试看码复制里重新编译试试看码复制里重新编译试试看码复制里重新编译试试看码复制里重新编译试试看码复制里重新编译试试看码复制里重新编译试试看码复制里重新编译试试看码复制里重新编译试试看码复制里重新编译试试看码复制里重新编译试试看码复制里重新编译试试看码复制里重新编译试试看码","feeType":"1","derateType":"1","isValid":"0","status":"0","cityCode":"","useTime":""},{"voucherType":"0","voucherMoney":0.01,"maxFullSubtractionMoney":"","minFullSubtractionMoney":"","expireTime":"20180831000000","discount":"","voucherId":"201808151428670007079256","secondItemId":"","firstItemId":"","voucherName":"看优惠券的字数","voucherDesc":"码复制里重新编译试试看码复制里重新编译试试看码复制里重新编译试试看码复制里重新编译试试看码复制里重新编译试试看码复制里重新编译试试看码复制里重新编译试试看码复制里重新编译试试看","feeType":"1","derateType":"1","isValid":"0","status":"0","cityCode":"","useTime":""}]
    @Override
    public void onDataChanged(Object data, MCBaseRequest request) {
    }

    @Override
    public int getLayoutId() {
        return R.layout.layout_voucher;
    }

    @Override
    public void initView() {
        MyVoucherRequest request = new MyVoucherRequest();
        request.type = "3";
        request.orderId = getIntent().getStringExtra("orderId");
        listView = findViewById(R.id.voucherListView);
        listView.setListRequest(request);
        listView.setItemListener(this);
//        commitRequest(request);
    }

    @Override
    public void onItemClick(View listView, BeanBase itemData, int tag) {

        if(tag == 0){

            Map<String, Object> allMap = itemData.allMap();
            Intent intent = new Intent();
            intent.putExtra("voucherId", allMap.get("voucherId").toString());
            intent.putExtra("voucherType", allMap.get("voucherType").toString());
            intent.putExtra("voucherValue", allMap.get("voucherType").equals("0") ? allMap.get("voucherMoney").toString() : allMap.get("discount").toString());
            setResult(101, intent);
            finish();
        }
    }
}

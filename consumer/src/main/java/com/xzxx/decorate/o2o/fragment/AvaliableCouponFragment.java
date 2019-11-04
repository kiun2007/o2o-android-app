package com.xzxx.decorate.o2o.fragment;

import android.view.View;

import com.kiun.modelcommonsupport.controllers.BaseRequestFragment;
import com.kiun.modelcommonsupport.network.MCBaseRequest;
import com.kiun.modelcommonsupport.network.requests.MyVoucherRequest;
import com.kiun.modelcommonsupport.ui.views.AListView;
import com.xzxx.decorate.o2o.consumer.R;

/**
 * Created by zf on 2018/7/12.
 * 可用优惠券
 */
public class AvaliableCouponFragment extends BaseRequestFragment {

    private AListView voucherListView;
    //优惠券类型 0可用 1过期 2已使用 3维修费支付时可用券
    public String type = "0";

    @Override
    public void onDataChanged(Object data, MCBaseRequest request) {
    }

    @Override
    protected void initView(View view) {
        MyVoucherRequest request = new MyVoucherRequest();
        request.type = type;
        voucherListView = view.findViewById(R.id.voucherListView);
        voucherListView.setListRequest(request);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.layout_avaliable_coupon;
    }
}
package com.xzxx.decorate.o2o.fragment;

import android.view.View;

import com.kiun.modelcommonsupport.controllers.BaseRequestFragment;
import com.kiun.modelcommonsupport.network.MCBaseRequest;
import com.xzxx.decorate.o2o.consumer.R;

/**
 * 待评价订单页面
 */
public class ToBeEvaluateFragment extends BaseRequestFragment {

    @Override
    public void onDataChanged(Object data, MCBaseRequest request) {

    }

    @Override
    protected void initView(View view) {

    }

    @Override
    protected int getLayoutId() {
        return R.layout.layout_fragment_all_order;
    }
}

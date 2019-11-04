package com.xzxx.decorate.o2o.ui;

import android.content.Intent;
import android.view.View;

import com.kiun.modelcommonsupport.adapter.ItemListener;
import com.kiun.modelcommonsupport.controllers.BaseRequestAcitity;
import com.kiun.modelcommonsupport.controllers.OrderButtonController;
import com.kiun.modelcommonsupport.network.MCBaseRequest;
import com.kiun.modelcommonsupport.ui.views.AListView;
import com.xzxx.decorate.o2o.consumer.R;
import com.xzxx.decorate.o2o.pay.WechatPay;
import com.xzxx.decorate.o2o.pay.alipay.AliPay;
import com.xzxx.decorate.o2o.requests.order.OrderListRequest;

import org.json.JSONObject;

/**
 * Created by kiun_2007 on 2018/9/13.
 */

public class OrderListActivity extends BaseRequestAcitity implements ItemListener<JSONObject> {

    AListView mainListView;
    OrderButtonController buttonController;

    @Override
    public void onDataChanged(Object data, MCBaseRequest request) {
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_order_list;
    }

    @Override
    public void initView() {
        String status = getIntent().getStringExtra("status");
        String title = getIntent().getStringExtra("title");
        mainListView = findViewById(R.id.mainListView);
        mainListView.setItemListener(this);

        //        if(getBaseActivity() != null){
        buttonController = getButtonController();
        buttonController.setWechatPay(new WechatPay());
        buttonController.setAliPay(new AliPay(this));
        buttonController.setCommentClz(ServiceEvaluationActivity.class);
        buttonController.setSeeClz(EvaluationCompleteActivity.class);
        //        }

        OrderListRequest orderListRequest = new OrderListRequest();
        orderListRequest.status = status;

        mainListView.setListRequest(orderListRequest);
        setTitle(title);
    }

    @Override
    public void onItemClick(View listView, JSONObject itemData, int tag) {
        if(tag == 0){
            //            Intent intent = new Intent(this.getContext(), ReleaseOrderActivity.class);
            //            intent.putExtra("orderId", itemData.optString("orderId"));
            //            startActivity(intent);
            Intent intent = new Intent(this, OrderDetailActivity.class);
            intent.putExtra("orderId", itemData.optString("orderId"));
            intent.putExtra("orderStatus", itemData.optString("orderStatus"));
            if(itemData.optString("salesAfterId") != null){
                intent.putExtra("salesAfterId", itemData.optString("salesAfterId"));
            }
            startActivity(intent);
            return;
        }

        buttonController.actionTag((AListView) listView, tag, itemData);
    }
}

package com.xzxx.decorate.o2o.ui;

import android.view.View;

import com.kiun.modelcommonsupport.adapter.ItemListener;
import com.kiun.modelcommonsupport.controllers.MediaUploadActivity;
import com.kiun.modelcommonsupport.network.MCBaseRequest;
import com.kiun.modelcommonsupport.network.requests.SysDicItemRequest;
import com.kiun.modelcommonsupport.ui.views.AItemLayout;
import com.kiun.modelcommonsupport.utils.MCDialogManager;
import com.xzxx.decorate.o2o.consumer.R;
import com.xzxx.decorate.o2o.requests.order.OrderComplainRequest;

/**
 * Created by zf on 2018/7/11.
 * 我要投诉页面
 */
public class ComplaintActivity extends MediaUploadActivity {

    AItemLayout itemLayout;
    @Override
    public int getLayoutId() {
        return R.layout.layout_complaint;
    }

    @Override
    public int getMediaEditViewId() {
        return R.id.richEditView;
    }

    @Override
    public void initView() {
        super.initView();
        itemLayout = findViewById(R.id.itemLayout);
        SysDicItemRequest dicItemRequest = new SysDicItemRequest();
        dicItemRequest.key = SysDicItemRequest.Complaint;
        commitRequest(dicItemRequest);
    }

    @Override
    public void onDataChanged(Object data, MCBaseRequest request) {
        if (request instanceof SysDicItemRequest){
            fillToView(R.id.itemLayout, data, true);
        }

        if (request instanceof OrderComplainRequest){
            MCDialogManager.showMessage(this, "投诉提交成功！", "已反馈给客服，我们将认真核实", "返回订单", R.drawable.icon_laugh).setListener(new ItemListener() {
                @Override
                public void onItemClick(View listView, Object itemData, int tag) {
                    finish();
                }
            });
        }
    }

    @Override
    public void uploadComplete() {
        OrderComplainRequest complainRequest = new OrderComplainRequest();
        complainRequest.orderId = getIntent().getStringExtra("orderId");
        if(fillRequest(complainRequest, null)){
            commitRequest(complainRequest);
        }
    }
}

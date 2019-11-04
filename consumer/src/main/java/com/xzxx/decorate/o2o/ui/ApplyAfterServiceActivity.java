package com.xzxx.decorate.o2o.ui;

import android.view.View;
import android.widget.TextView;

import com.kiun.modelcommonsupport.adapter.ItemListener;
import com.kiun.modelcommonsupport.controllers.MediaUploadActivity;
import com.kiun.modelcommonsupport.network.MCBaseRequest;
import com.kiun.modelcommonsupport.network.requests.SysDicItemRequest;
import com.kiun.modelcommonsupport.ui.views.ATextView;
import com.kiun.modelcommonsupport.utils.MCDialogManager;
import com.xzxx.decorate.o2o.consumer.R;
import com.xzxx.decorate.o2o.requests.afterService.SalesAfterApplyRequest;
import com.xzxx.decorate.o2o.requests.order.OrderInfoRequest;

import org.json.JSONArray;
import org.json.JSONException;

/**
 * Created by zf on 2018/7/11.
 * 申请售后服务页面
 */
public class ApplyAfterServiceActivity extends MediaUploadActivity implements View.OnClickListener {

    private SalesAfterApplyRequest salesAfterApplyRequest;
    private JSONArray reasons = null;
    private JSONArray types = null;
    ATextView applyTypeTV;
    ATextView serviceTypeTV;

    @Override
    public int getLayoutId() {
        return R.layout.layout_apply_after_service;
    }

    @Override
    public int getMediaEditViewId() {
        return R.id.richEditView;
    }

    @Override
    public void initView() {
        super.initView();
        applyTypeTV = findViewById(R.id.applyTypeTV);
        serviceTypeTV = findViewById(R.id.serviceTypeTV);

        salesAfterApplyRequest = new SalesAfterApplyRequest();
        OrderInfoRequest orderInfoRequest = new OrderInfoRequest();
        salesAfterApplyRequest.orderId = orderInfoRequest.orderId = getIntent().getStringExtra("orderId");
        commitRequest(orderInfoRequest);

        SysDicItemRequest typeItemRequest = new SysDicItemRequest();
        typeItemRequest.key = SysDicItemRequest.After_Service_Type;
        commitRequest(typeItemRequest);

        SysDicItemRequest reasonItemRequest = new SysDicItemRequest();
        reasonItemRequest.key = SysDicItemRequest.Complain_Reason;
        commitRequest(reasonItemRequest);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        TextView textView = null;
        MCDialogManager mcDialogManager = null;
        switch (v.getId()) {
            case R.id.ll_apply_reason:
                mcDialogManager = MCDialogManager.show(this,R.layout.dialog_apply_reason, reasons);
                mcDialogManager.setListener(new ItemListener() {
                    @Override
                    public void onItemClick(View view, Object itemData, int tag) {
                        if(tag != MCDialogManager.TAG_CANCEL_BTN){
                            try {
                                salesAfterApplyRequest.applyType = reasons.optJSONObject(tag - 1).getString("sysDictItemKey");
                                applyTypeTV.setText(reasons.optJSONObject(tag - 1).getString("sysDictItemValue"));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
                textView = mcDialogManager.getViewById(R.id.dialogTitleTV);
                textView.setText("服务类型");
                View btn_apply_reason_close = mcDialogManager.getViewById(R.id.btn_apply_reason_close);
                final MCDialogManager finalMcDialogManager = mcDialogManager;
                btn_apply_reason_close.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        finalMcDialogManager.dismiss();
                    }
                });
                break;
            case R.id.ll_apply_type:
                mcDialogManager = MCDialogManager.show(this,R.layout.dialog_apply_reason, types);
                mcDialogManager.setListener(new ItemListener() {
                    @Override
                    public void onItemClick(View view, Object itemData, int tag) {
                        if(tag != MCDialogManager.TAG_CANCEL_BTN) {
                            try {
                                salesAfterApplyRequest.serviceType = types.optJSONObject(tag - 1).getString("sysDictItemKey");
                                serviceTypeTV.setText(types.optJSONObject(tag - 1).getString("sysDictItemValue"));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
                textView = mcDialogManager.getViewById(R.id.dialogTitleTV);
                textView.setText("申请理由");
                View btn_apply_reason_close1 = mcDialogManager.getViewById(R.id.btn_apply_reason_close);
                final MCDialogManager finalMcDialogManager1 = mcDialogManager;
                btn_apply_reason_close1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        finalMcDialogManager1.dismiss();
                    }
                });
                break;
        }


    }

    @Override
    public void onDataChanged(Object data, MCBaseRequest request) {
        if(request instanceof OrderInfoRequest){
            fillToView(R.id.allContentScrollView, data);
        }

        if(request instanceof SysDicItemRequest){
            if (((SysDicItemRequest)request).key.equals(SysDicItemRequest.After_Service_Type)){
                fillToView(R.id.ll_apply_type, data);
                types = (JSONArray) data;
                try {
                    salesAfterApplyRequest.serviceType = types.optJSONObject(0).getString("sysDictItemKey");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }else{
                fillToView(R.id.ll_apply_reason, data);
                reasons = (JSONArray) data;
                try {
                    salesAfterApplyRequest.applyType = reasons.optJSONObject(0).getString("sysDictItemKey");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        if(request instanceof SalesAfterApplyRequest){
            MCDialogManager.showMessage(this, "售后申请成功!", "已提交给客服，我们将与您联系", "返回订单", R.drawable.icon_laugh).setListener(new ItemListener() {
                @Override
                public void onItemClick(View listView, Object itemData, int tag) {
                    finish();
                }
            });
        }
    }

    @Override
    public void uploadComplete() {
        if (fillRequest(salesAfterApplyRequest, null)){
            commitRequest(salesAfterApplyRequest);
        }
    }
}
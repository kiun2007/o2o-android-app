package com.xzxx.decorate.o2o.ui;

import android.content.Intent;
import android.widget.Button;
import android.widget.CheckBox;

import com.kiun.modelcommonsupport.controllers.MediaUploadActivity;
import com.kiun.modelcommonsupport.network.MCBaseRequest;
import com.kiun.modelcommonsupport.network.requests.SysDicItemRequest;
import com.kiun.modelcommonsupport.ui.views.AItemLayout;
import com.xzxx.decorate.o2o.consumer.R;
import com.xzxx.decorate.o2o.requests.order.CommentAddRequest;
import com.xzxx.decorate.o2o.requests.order.OrderInfoRequest;

import org.json.JSONObject;

/**
 * Created by zf on 2018/7/7.
 * 服务评价页面
 */
public class ServiceEvaluationActivity extends MediaUploadActivity {

    private AItemLayout itemLayout;
    private CheckBox register_cb;
    CommentAddRequest commentAddRequest = new CommentAddRequest();

    @Override
    public int getLayoutId() {
        return R.layout.layout_service_evaluation;
    }

    @Override
    public int getMediaEditViewId() {
        return R.id.richEditView;
    }

    @Override
    public void initView() {
        super.initView();
        itemLayout = findViewById(R.id.id_service_eva_flow_layout);
        register_cb = findViewById(R.id.register_cb);
        SysDicItemRequest request = new SysDicItemRequest();
        request.key = SysDicItemRequest.Evaluate;
        commitRequest(request);

        OrderInfoRequest orderInfoRequest = new OrderInfoRequest();
        orderInfoRequest.orderId = getIntent().getStringExtra("orderId");
        commitRequest(orderInfoRequest);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onDataChanged(Object data, MCBaseRequest request) {

        if (request instanceof SysDicItemRequest) {
            fillToView(R.id.id_service_eva_flow_layout, data, true);
        }

        if (request instanceof OrderInfoRequest && data instanceof JSONObject) {
            fillToView(R.id.marsterInfoPanel, data);
            commentAddRequest.masterId = ((JSONObject) data).optString("masterId");
            commentAddRequest.orderId = ((JSONObject) data).optString("orderId");
        }

        if (request instanceof CommentAddRequest) {
            finish();
            Intent intent = new Intent(this, EvaluationCompleteActivity.class);
            intent.putExtra("orderId", ((CommentAddRequest) request).orderId);
            startActivity(intent);
        }
    }

    @Override
    public void onSubmitClick(Button button) {
        super.onSubmitClick(button);
    }

    @Override
    public void uploadComplete() {
        if (!fillRequest(commentAddRequest, null)) {
            return;
        }
        //        if (commentAddRequest.attutide <= 0) {
        //            ToastUtil.showToast(ServiceEvaluationActivity.this, "服务态度评分不能为0");
        //            return;
        //        }
        //
        //        if (commentAddRequest.quality <= 0) {
        //            ToastUtil.showToast(ServiceEvaluationActivity.this, "服务质量评分不能为0");
        //            return;
        //        }
        //
        //        if (commentAddRequest.efficiency <= 0) {
        //            ToastUtil.showToast(ServiceEvaluationActivity.this, "服务效率评分不能为0");
        //            return;
        //        }

        if (register_cb.isChecked()) {
            commentAddRequest.anonymous = "0";
        } else {
            commentAddRequest.anonymous = "1";
        }
        commitRequest(commentAddRequest);
    }
}

package com.xzxx.decorate.o2o.master;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.kiun.modelcommonsupport.MainApplication;
import com.kiun.modelcommonsupport.adapter.ItemListener;
import com.kiun.modelcommonsupport.controllers.OrderButtonController;
import com.kiun.modelcommonsupport.controllers.Refresher;
import com.kiun.modelcommonsupport.data.MCUserInfo;
import com.kiun.modelcommonsupport.network.MCBaseRequest;
import com.kiun.modelcommonsupport.ui.views.ActionButtonView;
import com.kiun.modelcommonsupport.utils.DecimalDigitsInputFilter;
import com.kiun.modelcommonsupport.utils.MCDialogManager;
import com.phillipcalvin.iconbutton.IconButton;
import com.xzxx.decorate.o2o.BaseActivity;
import com.xzxx.decorate.o2o.requests.MasterOrderInfoRequest;
import com.xzxx.decorate.o2o.requests.MasterSalesAfterInfoRequest;
import com.xzxx.decorate.o2o.requests.ModifyRepairMoneyRequest;

import org.json.JSONObject;

/**
 * Created by zf on 2018/7/16.
 * 订单详情页面
 */
public class OrderDetailActivity extends BaseActivity implements Refresher {

    JSONObject data = null;
    Boolean isAfter = false;
    ActionButtonView actionButton;
    TextView txt_edit;
    TextView txt_money;
    TextView txt_unit;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public int getLayoutId() {
        return R.layout.layout_order_detail;
    }

    @Override
    public void initView() {
        onRefresh(-1);
        actionButton = findViewById(R.id.actionButton);
        txt_edit = findViewById(R.id.txt_edit);
        txt_unit = findViewById(R.id.txt_unit);
        txt_money = findViewById(R.id.txt_money);
        findViewById(R.id.lineLayout).setLayerType(View.LAYER_TYPE_SOFTWARE, null);

        ActionButtonView actionButtonView = findViewById(isAfter ? R.id.actionButtonAfter : R.id.actionButton);
        getButtonController().setSendToBaseClz(SendToBaseActivity.class);

        actionButtonView.setListener(new ItemListener() {
            @Override
            public void onItemClick(View listView, Object itemData, int tag) {
                getButtonController().actionTag(OrderDetailActivity.this, tag, data);
            }
        });

        findViewById(R.id.costInfoLayout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { //师傅修改维修金额.
                String orderStatus = data.optString("orderStatus");
                if (orderStatus != null && !orderStatus.isEmpty()) {
                    int status = Integer.parseInt(orderStatus);
                    if (status != 4) {
                        return;
                    }
                } else {
                    return;
                }

                final MCDialogManager dialogManager = MCDialogManager.show(OrderDetailActivity.this, com.kiun.modelcommonsupport.R.layout.dialog_service_cost, null);
                View txt_close = dialogManager.getViewById(com.kiun.modelcommonsupport.R.id.txt_close);
                final EditText editText = dialogManager.getViewById(com.kiun.modelcommonsupport.R.id.costInputEdit);
                editText.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(2)});
                txt_close.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialogManager.dismiss();
                    }
                });
                IconButton btn_service_cost_commit = dialogManager.getViewById(com.kiun.modelcommonsupport.R.id.btn_service_cost_commit);
                btn_service_cost_commit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialogManager.dismiss();
                        String content = editText.getText().toString();
                        float cost = 0.0f;
                        if (TextUtils.isEmpty(content) || (cost = Float.parseFloat(content)) <= 0) {
                            MCDialogManager.info(OrderDetailActivity.this, "请输入一个正确的金额");
                            return;
                        }

                        ModifyRepairMoneyRequest modifyRepairMoneyRequest = new ModifyRepairMoneyRequest();
                        modifyRepairMoneyRequest.orderId = getIntent().getStringExtra("orderId");
                        modifyRepairMoneyRequest.repairMoney = String.format("%.2f", cost);
                        commitRequest(modifyRepairMoneyRequest);
                    }
                });
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    //{"customerId":"201809171519670005454605","appointmentName":"","customerHeadImg":"http:\/\/pawx04z5h.bkt.clouddn.com\/2018\/08\/ae456325-399f-419f-afcd-df5ce3f28760.png",
    // "secondItemId":"201809171558670020088354","seccondItemName":"山上有个庙","appointmentTime":"20180920171000","appointmentLocation":"泰山东村四巷与泰山路交叉口东100米泰山东村三巷5号",
    // "appointmentSex":"0","appointmentLatitudeLongitude":"22.640055,114.058358","appointmentAddress":"150","salesAfterStatus":"2","orderWords":"Fdsfadsf","orderFiles":"",
    // "repairPayMoney":0.01,"doorPayMoney":0.01,"repairMoney":0.01,"doorMoney":0.01,"doorVoucherMoney":"","repairVoucherMoney":"","tip":"","totalPayMoney":"",
    // "orderId":"201809201631670019233918","orderTime":"20180920163155","doorPayTime":"20180920163203","repairPayTime":"20180920163317","doorPayType":"1","repairPayType":"1",
    // "doorPayStatus":"1","repairPayStatus":"1","salesAfterOrderId":"201809201634670043875626","salesAfterId":"201809201633670019233923","customerHxAccount":"c13322222222"}
    @Override
    public void onDataChanged(Object data, MCBaseRequest request) {
        if (((request instanceof MasterOrderInfoRequest) || (request instanceof MasterSalesAfterInfoRequest)) && (data instanceof JSONObject)) {
            fillToView(-1, data);
            this.data = (JSONObject) data;

            try {
                JSONObject jsonObject = (JSONObject) data;
                String orderStatus = jsonObject.optString("orderStatus", "");
                if (orderStatus.equals("5")) {
                    txt_edit.setVisibility(View.GONE);
                    txt_unit.setTextColor(getResources().getColor(R.color.light_black));
                    txt_money.setTextColor(getResources().getColor(R.color.light_black));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (request instanceof ModifyRepairMoneyRequest) {
            MCDialogManager.info(this, "修改完成!");
            fillToView(R.id.repairMoneyLayout, request);
        }

        MCUserInfo userInfo = MainApplication.getInstance().getUserInfo(false);
        if (userInfo == null) {
            return;
        }
        if (userInfo.isEnterPrise.equals("0")) {//是否企业用户，0-是，1-不是.
            com.amos.modulebase.utils.LogUtil.i("是企业用户");
            if (actionButton.getLeftTextBtn().equals("发送给总部") && actionButton.getLeftTextBtnVis() == View.VISIBLE) {
                actionButton.setLeftTextBtnVis(View.GONE);
            }
        }else if (userInfo.masterType.equals("1")) {
            if (actionButton.getLeftTextBtn().equals("发送给总部") && actionButton.getLeftTextBtnVis() == View.VISIBLE) {
                actionButton.setLeftTextBtnVis(View.GONE);
            }
        }
    }

    @Override
    public void onRefresh(int tag) {
        if (tag == OrderButtonController.TAG_CANCEL || tag == OrderButtonController.TAG_DEL || tag == OrderButtonController.TAG_START) {
            setResult(tag);
            this.finish();
            return;
        }

        if (tag == OrderButtonController.TAG_SER_COM) {
            finish();
            return;
        }
        //salesAfterOrderId
        String orderId = getIntent().getStringExtra("orderId");
        String salesAfterOrderId = getIntent().getStringExtra("salesAfterOrderId");
        if (orderId != null && !orderId.isEmpty()) {
            MasterOrderInfoRequest orderInfoRequest = new MasterOrderInfoRequest();
            orderInfoRequest.orderId = orderId;
            commitRequest(orderInfoRequest);
        } else if (salesAfterOrderId != null && !salesAfterOrderId.isEmpty()) {
            MasterSalesAfterInfoRequest masterSalesAfterInfoRequest = new MasterSalesAfterInfoRequest();
            masterSalesAfterInfoRequest.salesAfterOrderId = salesAfterOrderId;
            commitRequest(masterSalesAfterInfoRequest);
            isAfter = true;
        }

    }
}

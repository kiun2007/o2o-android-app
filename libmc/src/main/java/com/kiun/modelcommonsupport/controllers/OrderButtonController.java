package com.kiun.modelcommonsupport.controllers;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.amos.modulebase.utils.IntentUtil;
import com.hyphenate.easeui.EaseConstant;
import com.kiun.modelcommonsupport.R;
import com.kiun.modelcommonsupport.adapter.ItemListener;
import com.kiun.modelcommonsupport.controllers.authority.WebBaseActivity;
import com.kiun.modelcommonsupport.data.PayDoorBean;
import com.kiun.modelcommonsupport.data.drive.MCDataField;
import com.kiun.modelcommonsupport.hx.ChatActivity;
import com.kiun.modelcommonsupport.network.MCBaseRequest;
import com.kiun.modelcommonsupport.network.MCHttpCGI;
import com.kiun.modelcommonsupport.network.MCResponse;
import com.kiun.modelcommonsupport.network.requests.AxbPhoneRequest;
import com.kiun.modelcommonsupport.network.requests.CancelRequest;
import com.kiun.modelcommonsupport.network.requests.DeleteOrderRequest;
import com.kiun.modelcommonsupport.network.requests.FinishedRepairRequest;
import com.kiun.modelcommonsupport.network.requests.OrderSubmitRequest;
import com.kiun.modelcommonsupport.network.requests.PayIsSuccessRequest;
import com.kiun.modelcommonsupport.network.requests.RepairPayRequest;
import com.kiun.modelcommonsupport.network.requests.RepairSubmitRequest;
import com.kiun.modelcommonsupport.network.requests.SalesAfterStatusRequest;
import com.kiun.modelcommonsupport.network.requests.ServicePhoneRequest;
import com.kiun.modelcommonsupport.network.requests.StartDoorRequest;
import com.kiun.modelcommonsupport.network.requests.StartServiceRequest;
import com.kiun.modelcommonsupport.network.requests.SysDicItemRequest;
import com.kiun.modelcommonsupport.network.responses.MCUIResponse;
import com.kiun.modelcommonsupport.safe.OrderPayer;
import com.kiun.modelcommonsupport.safe.PayEventer;
import com.kiun.modelcommonsupport.ui.views.AEditText;
import com.kiun.modelcommonsupport.ui.views.AItemLayout;
import com.kiun.modelcommonsupport.ui.views.UserEditText;
import com.kiun.modelcommonsupport.utils.DecimalDigitsInputFilter;
import com.kiun.modelcommonsupport.utils.MCDialogManager;
import com.kiun.modelcommonsupport.utils.MCString;
import com.phillipcalvin.iconbutton.IconButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by kiun_2007 on 2018/8/25.
 */

public class OrderButtonController implements MCUIResponse {

    public interface ActivityResulter {
        void onResult(int requestCode, int resultCode, Intent data);
    }

    public final static int TAG_SEND = 0x3001; //发送给总部.
    public final static int TAG_SER_COM = 0x3002; //服务完成.
    public final static int TAG_DEL = 0x3003; //删除订单.
    public final static int TAG_MSG_CON = 0x3004; //联系客户.
    public final static int TAG_DOOR = 0x3005; //开始上门.
    public final static int TAG_START = 0x3006; //开始服务.
    public final static int TAG_PAY = 0x3007; //立即支付.
    public final static int TAG_MSG_MAS = 0x3008; //联系师傅.
    public final static int TAG_COMMENT = 0x3009; //立即评价.
    public final static int TAG_SEE = 0x300A; //查看评价.
    public final static int TAG_PRO = 0x300B; //售后进度.
    public final static int TAG_TOSER = 0x300C; //联系客服.
    public final static int TAG_CANCEL = 0x300D; //取消订单.
    public final static int TAG_ORDER_CONTENT = 0x300E; //查看订单.
    public final static int TAG_CALLTO = 0x300F; //拨打电话.

    private Context context;
    private OrderPayer wechatPay;
    private OrderPayer aliPay;
    private ActivityResulter resulter = null;
    private Refresher refresher;
    private Object cancelData; //取消原因集合.
    private Class<? extends BaseRequestAcitity> commentClz;
    private Class<? extends BaseRequestAcitity> seeClz;
    private Class<? extends BaseRequestAcitity> progressClz;
    private Class<? extends BaseRequestAcitity> orderContentClz;
    private Class<? extends BaseRequestAcitity> sendToBaseClz;
    MCDialogManager payLoadingDialog;
    private String selfOrderId = null;
    private boolean isLastOrderInfo = false;
    float selfRepairPayMoney;

    public void setCommentClz(Class<? extends BaseRequestAcitity> commentClz) {
        this.commentClz = commentClz;
    }

    public String getOrderId() {
        return selfOrderId;
    }

    public void setSeeClz(Class<? extends BaseRequestAcitity> seeClz) {
        this.seeClz = seeClz;
    }

    public void setProgressClz(Class<? extends BaseRequestAcitity> progressClz) {
        this.progressClz = progressClz;
    }

    public void setOrderContentClz(Class<? extends BaseRequestAcitity> orderContentClz) {
        this.orderContentClz = orderContentClz;
    }

    public void setSendToBaseClz(Class<? extends BaseRequestAcitity> sendToBaseClz) {
        this.sendToBaseClz = sendToBaseClz;
    }

    public OrderButtonController(Context context) {
        this.context = context;
        if (context.getClass().getSimpleName().endsWith("OrderDetailActivity")){
            isLastOrderInfo = true;
        }
        SysDicItemRequest sysDicItemRequest = new SysDicItemRequest();
        sysDicItemRequest.key = SysDicItemRequest.isMaster() ? SysDicItemRequest.Cancel_Master : SysDicItemRequest.Cancel_User;
        commitRequest(sysDicItemRequest);
    }

    public void setWechatPay(OrderPayer wechatPay) {
        this.wechatPay = wechatPay;
    }

    public void setAliPay(OrderPayer aliPay) {
        this.aliPay = aliPay;
    }

    @Override
    public void onDataChanged(Object data, MCBaseRequest request) {

        if (request instanceof RepairPayRequest) {
            if (payLoadingDialog != null) {
                payLoadingDialog.dismiss();
            } else {
                return;
            }

            MCDialogManager.showMessage(context, "支付成功", "订单已支付", "好的", R.drawable.svg_laugh).setListener(new ItemListener() {
                @Override
                public void onItemClick(View listView, Object itemData, int tag) {
                    if (tag == MCDialogManager.TAG_RIGHT_BTN) {
                    }
                }
            });
        }

        if (request instanceof CancelRequest) {
            MCDialogManager.info(context, "订单已取消").setListener(new ItemListener() {
                @Override
                public void onItemClick(View view, Object itemData, int tag) {
                    if (refresher != null) {
                        refresher.onRefresh(TAG_CANCEL);
                    }
                }
            });
        }

        if (request instanceof FinishedRepairRequest) {
            if (refresher != null) {
                refresher.onRefresh(TAG_CANCEL);
            }
        }

        if (request instanceof ServicePhoneRequest) {
            if (data instanceof JSONArray) {
                JSONArray phones = (JSONArray) data;
                String phone = phones.optJSONObject(0).optString("phoneNo");

                Intent intent = new Intent(Intent.ACTION_DIAL);
                Uri telUrl = Uri.parse("tel:" + phone);
                intent.setData(telUrl);
                context.startActivity(intent);
            } else {
                MCDialogManager.info(context, "获取客服电话失败,请留意官网通知");
            }
        }

        if (request instanceof SysDicItemRequest) {
            cancelData = data;
        }

        if (request instanceof StartDoorRequest) {
            if (refresher != null) {
                refresher.onRefresh(TAG_SER_COM);
            }
        }

        if (request instanceof SalesAfterStatusRequest || request instanceof DeleteOrderRequest || request instanceof StartServiceRequest) {

            ItemListener itemListener = new ItemListener() {
                @Override
                public void onItemClick(View listView, Object itemData, int tag) {
                    if (refresher != null) {
                        if (request instanceof DeleteOrderRequest) {
                            refresher.onRefresh(TAG_DEL);
                        } else if (request instanceof StartServiceRequest || (request instanceof SalesAfterStatusRequest && ((SalesAfterStatusRequest) request).salesAfterOrderStatus.equals("2"))) {
                            refresher.onRefresh(TAG_START);
                        } else {
                            refresher.onRefresh(-1);
                        }
                    }
                }
            };


            if (request instanceof StartServiceRequest) {
                MCDialogManager.info(context, "已经开始服务，如果觉得价格不合适可以在订单详情修改").setListener(itemListener);
            }

            if (request instanceof DeleteOrderRequest) {
                MCDialogManager.info(context, "订单 \"" + ((DeleteOrderRequest) request).orderId + "\" 已删除").setListener(itemListener);
            }

            if (request instanceof SalesAfterStatusRequest) {
                SalesAfterStatusRequest salesAfterStatusRequest = (SalesAfterStatusRequest) request;
                int status = Integer.parseInt(salesAfterStatusRequest.salesAfterOrderStatus);
                if (status == 2) {
                    MCDialogManager.info(context, "已经开始服务,地图不再显示该订单").setListener(itemListener);
                } else if (status == 3) {
                    if (refresher != null) {
                        refresher.onRefresh(TAG_SER_COM);
                    }
                } else {
                    if (refresher != null) {
                        refresher.onRefresh(TAG_DOOR);
                    }
                }
            }
        }

        if (request instanceof AxbPhoneRequest) {
            String phone = data.toString();
            if (!phone.isEmpty()) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                Uri uri = Uri.parse("tel:" + phone);
                intent.setData(uri);
                context.startActivity(intent);
            }
        }
    }

    @Override
    public void onBeginRequest() {
    }

    @Override
    public void onError(Error error) {
        String content = error.getMessage();
        if (content.contains("=")) {
            try {
                String[] contents = content.split("=");
                content = contents[1].replace("}", "");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        MCDialogManager.error(context, content);
    }

    @Override
    public boolean isDead() {
        return false;
    }

    private void commitRequest(final MCBaseRequest request) {
        request.setResponse(new MCResponse(this) {{
            setRequest(request);
        }});
        MCHttpCGI.defaultCenter().requestCGI(request);
    }

    public void actionTag(Refresher refresher, int tag, Object data) {
        this.refresher = refresher;

        if (data instanceof JSONObject) {
            JSONObject jsonObject = (JSONObject) data;
            String orderStatus = jsonObject.optString("orderStatus");
            String orderId = jsonObject.optString("orderId");

            if (tag == TAG_PAY) { //支付订单.
                payOrder(data);
            } else if (tag == TAG_CANCEL) { //订单取消-----------------------------------------------.
                if (orderStatus != null && orderStatus.equals("0") && !MCBaseRequest.isMaster()) {
                    CancelRequest request = new CancelRequest();
                    request.cancelContent = "abc";
                    request.cancelType = "1";
                    request.orderId = orderId;
                    commitRequest(request);
                } else {
                    if (MCBaseRequest.isMaster()) {
                        String finalOrderId = orderId;
                        //MCDialogManager.showMessage(context, "是否取消订单?", "取消订单会扣10分", "暂不取消", "确认取消", R.drawable.svg_fail).setListener(new ItemListener() {
                        MCDialogManager.showMessage(context, "是否取消订单?", "", "暂不取消", "确认取消", R.drawable.svg_fail).setListener(new ItemListener() {
                            @Override
                            public void onItemClick(View listView, Object itemData, int tag) {
                                if (MCDialogManager.TAG_LEFT_BTN == tag) {
                                    cancelOrder(finalOrderId);
                                }
                            }
                        });
                    } else {
                        String masterId = jsonObject.optString("masterId", "");
                        if (TextUtils.isEmpty(masterId)) {// 师傅是否接单
                            String secondItemId = jsonObject.optString("secondItemId", "");
                            String secondItemName = jsonObject.optString("secondItemName", "");
                            MCDialogManager.showMessage(context, "是否取消订单?", "48小时内取消订单免费", "暂不取消", "重现发布", "确认取消", R.drawable.svg_icon_prompt_big).setListener(new ItemListener() {
                                @Override
                                public void onItemClick(View view, Object itemData, int tag) {
                                    //                if (tag == MCDialogManager.TAG_LEFT_BTN) {
                                    //                } else
                                    if (tag == MCDialogManager.TAG_MIDDLE_BTN) {
                                        CancelRequest cancelRequest = new CancelRequest();
                                        cancelRequest.cancelType = "1";
                                        cancelRequest.cancelContent = "用户取消订单";
                                        cancelRequest.orderId = orderId;
                                        commitRequest(cancelRequest);
                                        try {
                                            Intent intent = new Intent(context, Class.forName("com.xzxx.decorate.o2o.ui.ItemContentActivity"));
                                            intent.putExtra("secondItemId", secondItemId);
                                            intent.putExtra("secondItemName", secondItemName);
                                            context.startActivity(intent);
                                        } catch (ClassNotFoundException e) {
                                            e.printStackTrace();
                                        }
                                    } else if (tag == MCDialogManager.TAG_LEFT_BTN) {
                                        CancelRequest cancelRequest = new CancelRequest();
                                        cancelRequest.cancelType = "1";
                                        cancelRequest.cancelContent = "用户取消订单";
                                        cancelRequest.orderId = orderId;
                                        commitRequest(cancelRequest);
                                    }
                                }
                            });
                        } else {
                            cancelOrder(orderId);
                        }
                    }
                }
            } else if (tag == TAG_SEND && sendToBaseClz != null) { //发送给总部.
                Intent intent = new Intent(context, sendToBaseClz);
                intent.putExtra("orderId", orderId);
                ((Activity) context).startActivityForResult(intent, 0x3001);
            } else if (tag == TAG_SER_COM) { //服务完成.
                String salesAfterId = jsonObject.optString("salesAfterId"); //售后ID.
                String salesAfterOrderId = jsonObject.optString("salesAfterOrderId"); //售后订单ID.
                if (salesAfterId != null && !salesAfterId.isEmpty()) {
                    changSalesAfterState("3", orderId, salesAfterId, salesAfterOrderId);
                } else {
                    FinishedRepairRequest repairRequest = new FinishedRepairRequest();
                    repairRequest.orderId = orderId;
                    commitRequest(repairRequest);
                }
            } else if (tag == TAG_DEL) { //删除订单.
                DeleteOrderRequest request = new DeleteOrderRequest();
                request.orderId = orderId;
                commitRequest(request);
            } else if (tag == TAG_MSG_CON || tag == TAG_MSG_MAS) { //联系客户.
                Intent intent = new Intent(context, ChatActivity.class);
                String hxId = jsonObject.optString(tag == TAG_MSG_MAS ? "masterHxAccount" : "customerHxAccount");
                String hxName = jsonObject.optString(tag == TAG_MSG_MAS ? "masterName" : "appointmentName");
                if (tag == TAG_MSG_MAS) {
                    hxName += ("|" + jsonObject.optString("masterProfession"));
                }
                String hxHeadImg = jsonObject.optString(tag == TAG_MSG_MAS ? "masterHeadImg" : "customerHeadImg");
                intent.putExtra(EaseConstant.EXTRA_USER_ID, hxId);
                intent.putExtra(EaseConstant.EXTRA_HE_NICK_NAME, hxName);
                intent.putExtra(EaseConstant.EXTRA_HE_AVATAR, hxHeadImg);
                if(isLastOrderInfo){
                    intent.putExtra(EaseConstant.EXTRA_LAST_ORDERINFO, orderId);
                }
                context.startActivity(intent);
                //{"masterId":"201808231338670013541063","masterName":"刘春杰","masterHeadImg":"http:\/\/pawx04z5h.bkt.clouddn.com\/2018\/08\/ae456325-399f-419f-afcd-df5ce3f28760.png","masterProfession":"程序员|设计师|测试员","masterScore":5,"masterOrderNum":8,"secondItemId":"201808181406670026049262","seccondItemName":"修地图BUG","appointmentTime":"20180825114000","appointmentAddress":"404","appointmentLocation":"百瑞达公寓","appointmentLatitudeLongitude":"22.644233,114.065650","orderStatus":"7","repairMoney":0.01,"repairPayMoney":0.01,"doorPayMoney":0.02,"doorMoney":0.02,"orderId":"201808251102670010688206","totalPayMoney":0.03,"masterHxAccount":"m13751041579"}
            } else if (tag == TAG_DOOR) { //开始上门.
                String salesAfterId = jsonObject.optString("salesAfterId"); //售后ID.
                String salesAfterOrderId = jsonObject.optString("salesAfterOrderId"); //售后订单ID.
                if (salesAfterId != null && !salesAfterId.isEmpty()) {
                    changSalesAfterState("1", orderId, salesAfterId, salesAfterOrderId);
                } else {
                    StartDoorRequest request = new StartDoorRequest();
                    request.orderId = orderId;
                    commitRequest(request);
                }
            } else if (tag == TAG_START) { //开始服务.
                String salesAfterId = jsonObject.optString("salesAfterId"); //售后ID.
                String salesAfterOrderId = jsonObject.optString("salesAfterOrderId"); //售后订单ID.
                if (salesAfterId != null && !salesAfterId.isEmpty()) {
                    changSalesAfterState("2", orderId, salesAfterId, salesAfterOrderId);
                } else {
                    MCDialogManager dialogManager = MCDialogManager.show(context, R.layout.dialog_service_cost, null);
                    IconButton btn_service_cost_commit = dialogManager.getViewById(R.id.btn_service_cost_commit);
                    EditText editText = dialogManager.getViewById(R.id.costInputEdit);
                    editText.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(2)});
                    View txt_close = dialogManager.getViewById(R.id.txt_close);
                    txt_close.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialogManager.dismiss();
                        }
                    });
                    String finalOrderId1 = orderId;
                    btn_service_cost_commit.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialogManager.dismiss();
                            //                            UserEditText editText = dialogManager.getViewById(R.id.costInputEdit);
                            String content = editText.getText().toString();
                            float cost = 0.0f;
                            if (TextUtils.isEmpty(content) || (cost = Float.parseFloat(content)) <= 0) {
                                MCDialogManager.info(context, "请输入一个正确的金额");
                                return;
                            }
                            StartServiceRequest startServiceRequest = new StartServiceRequest();
                            startServiceRequest.orderId = finalOrderId1;
                            startServiceRequest.repairMoney = String.format("%.2f", cost);
                            commitRequest(startServiceRequest);
                        }
                    });
                }
            } else if (tag == TAG_COMMENT && commentClz != null) { //立即评价.
                Intent intent = new Intent(context, commentClz);
                intent.putExtra("orderId", orderId);
                context.startActivity(intent);
            } else if (tag == TAG_SEE && seeClz != null) { //查看评价.
                Intent intent = new Intent(context, seeClz);
                intent.putExtra("orderId", orderId);
                context.startActivity(intent);
            } else if (tag == TAG_PRO) { //查看进度.
                if (progressClz == null) {
                    //、、 TODO
                } else {
                    Intent intent = new Intent(context, progressClz);
                    intent.putExtra("pageIndex", 1);
                    intent.putExtra("orderId", orderId);
                    context.startActivity(intent);
                }
            } else if (tag == TAG_TOSER) { //联系客服.
                commitRequest(new ServicePhoneRequest());
            } else if (tag == TAG_ORDER_CONTENT && orderContentClz != null) { //查看订单.
                Intent intent = new Intent(context, orderContentClz);
                if (!TextUtils.isEmpty(jsonObject.optString("salesAfterId"))){
                    intent.putExtra("salesAfterId", jsonObject.optString("salesAfterId"));
                }
                intent.putExtra("orderId", orderId);
                intent.putExtra("orderStatus", orderStatus);
                ((Activity) context).startActivityForResult(intent, 5004);
            } else if (tag == TAG_CALLTO) {
                AxbPhoneRequest axbPhoneRequest = new AxbPhoneRequest();
                axbPhoneRequest.orderId = orderId;
                commitRequest(axbPhoneRequest);
            }
        } else {
            if (tag == TAG_PAY) { //支付订单.
                payOrder(data);
            }
        }
    }

    public void cancelOrder(String orderId) {
        MCDialogManager manager = MCDialogManager.show(context, R.layout.dialog_cancel_order_reason, cancelData);
        AItemLayout itemLayout = manager.getViewById(R.id.itemLayout);
        AEditText editText = manager.getViewById(R.id.contentEditText);
        manager.getViewById(R.id.btn_cancel_order_reason_commit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CancelRequest request = new CancelRequest();
                List<String> itemStrings = itemLayout.getSelectedItemArray();
                request.cancelType = MCString.arrayToString(itemStrings, "|");
                if (request.cancelType.isEmpty()) {
                    Toast.makeText(context, "请选择取消类型", Toast.LENGTH_LONG).show();
                    return;
                }

                if (editText.getText().toString().isEmpty()) {
                    Toast.makeText(context, "请填写取消订单理由", Toast.LENGTH_LONG).show();
                    return;
                }

                request.cancelContent = editText.getText().toString();
                request.orderId = orderId;
                commitRequest(request);
                manager.dismiss();
            }
        });
    }

    public void changSalesAfterState(String state, String orderId, String salesAfterId, String salesAfterOrderId) {
        SalesAfterStatusRequest salesAfter = new SalesAfterStatusRequest();
        salesAfter.orderId = orderId;
        salesAfter.saleAfterId = salesAfterId;
        salesAfter.salesAfterOrderId = salesAfterOrderId;
        salesAfter.salesAfterOrderStatus = state;
        commitRequest(salesAfter);
    }

    private void payOrder(Object data) {

        if (data instanceof OrderSubmitRequest) {
            payDoor(data);
            return;
        }

        JSONObject jsonObject = (JSONObject) data;
        String orderId = jsonObject.optString("orderId");
        String orderStatus = jsonObject.optString("orderStatus");

        boolean isPayDoor = orderStatus.equals("0");

        if (isPayDoor) {
            payDoor(jsonObject);
        } else {
            payRepair(jsonObject, orderId);
        }
    }

    private void payDoor(Object data) {
        PayDoorBean payDoorBean = new PayDoorBean();
        OrderSubmitRequest orderSubmitRequest = null;

        if (data instanceof OrderSubmitRequest) {
            orderSubmitRequest = (OrderSubmitRequest) data;
            payDoorBean.doorCost = MCString.toNumber(((OrderSubmitRequest) data).doorToFee).floatValue();
            payDoorBean.discountCost = MCString.toNumber(((OrderSubmitRequest) data).voucharMoney).floatValue();
        } else {
            JSONObject jsonObject = (JSONObject) data;
            if (jsonObject.optString("doorMoney") != null) {
                payDoorBean.doorCost = MCString.toNumber(jsonObject.optString("doorMoney")).floatValue();
                payDoorBean.tip = MCString.toNumber(jsonObject.optString("tip")).floatValue();
                payDoorBean.discountCost = MCString.toNumber(jsonObject.optString("voucharMoney")).floatValue();
            }
        }

        final MCDialogManager dialogManager = MCDialogManager.show(context, R.layout.dialog_order_pay_d, payDoorBean);
        if (data instanceof OrderSubmitRequest) {
            dialogManager.getViewById(R.id.payTipImage).setVisibility(View.VISIBLE);
            dialogManager.getViewById(R.id.id_order_content_dialog_close).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialogManager.dismiss();
                }
            });
            dialogManager.getViewById(R.id.txt_prompt).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Bundle bundle = new Bundle();
                    bundle.putString("url", "https://www.baidu.com");
                    bundle.putString("title", "收费规则");
                    IntentUtil.gotoActivity(context, WebBaseActivity.class, bundle);
                }
            });
            OrderSubmitRequest finalOrderSubmitRequest = orderSubmitRequest;
            dialogManager.getViewById(R.id.rl_tip_value).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    MCDialogManager tipDialog = MCDialogManager.show(context, R.layout.dialog_tip_the_tip, null);
                    tipDialog.getViewById(R.id.txt_close1).setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View view) {
                            tipDialog.dismiss();
                        }
                    });
                    tipDialog.getViewById(R.id.btn_confirm).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            UserEditText tipEdit = tipDialog.getViewById(R.id.tipEdit);
                            RadioGroup radioGroup = tipDialog.getViewById(R.id.tipRadioGroup);
                            RadioButton radioButton = tipDialog.getViewById(radioGroup.getCheckedRadioButtonId());
                            float tipCostNumber = Float.parseFloat(!tipEdit.getText().isEmpty() ? tipEdit.getText() : "0");
                            if (tipCostNumber == 0 && radioButton != null) {
                                tipCostNumber = Float.parseFloat(radioButton.getText().toString().replace("元", ""));
                            }
                            payDoorBean.tip = tipCostNumber;
                            if (finalOrderSubmitRequest != null) {
                                finalOrderSubmitRequest.tip = String.format("%.2f", tipCostNumber);
                            }
                            dialogManager.fillToView(payDoorBean);
                            tipDialog.dismiss();
                        }
                    });
                }
            });
        }

        OrderSubmitRequest finalOrderSubmitRequest1 = orderSubmitRequest;
        dialogManager.getViewById(R.id.btn_pay_service_commit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogManager.dismiss();
                RadioButton button = dialogManager.getViewById(R.id.radioWechatPay);
                boolean isWechat = button.isChecked();

                payLoadingDialog = MCDialogManager.showMessage(context, "订单正在支付...", "请稍后订单正在支付中",
                        "已支付完成", "未支付完成", MCDialogManager.ICON_PRO).setListener(new ItemListener() {
                    @Override
                    public void onItemClick(View listView, Object itemData, int tag) {

                        payIsSuccess(payLoadingDialog, data);
                        // payLoadingDialog = null;
                        // TODO /order/customer/payIsSuccess
                    }
                });

                if (finalOrderSubmitRequest1 != null) {
                    finalOrderSubmitRequest1.setResponse(new MCResponse(new MCUIResponse() {
                        @Override
                        public void onDataChanged(Object orderData, MCBaseRequest request) {
                            if (orderData instanceof String) {
                                selfOrderId = ((String) orderData).substring(1);
                                payOrderId = ((String) orderData).substring(1);
                                if (payDoorBean.getDoorPay() == 0) {
                                    if (refresher != null) {
                                        refresher.onRefresh(TAG_PAY);
                                    }
                                } else {
                                    payToApp(isWechat, orderData.toString(), payDoorBean.getDoorPay());
                                }
                            }
                        }

                        @Override
                        public void onBeginRequest() {
                        }

                        @Override
                        public void onError(Error error) {
                        }

                        @Override
                        public boolean isDead() {
                            return false;
                        }
                    }));
                    MCHttpCGI.defaultCenter().requestCGI(finalOrderSubmitRequest1);
                } else {
                    JSONObject jsonObject = (JSONObject) data;
                    payToApp(isWechat, "d" + jsonObject.optString("orderId"), payDoorBean.getDoorPay());
                }
            }
        });
    }

    // 缓存 订单号
    private String payOrderId = null;

    private void payRepair(JSONObject jsonObject, String orderId) {

        RepairSubmitRequest repairSubmitRequest = new RepairSubmitRequest();
        repairSubmitRequest.orderId = orderId;
        RepairPayRequest repairPayRequest = new RepairPayRequest();
        MCDataField.fillObject(repairPayRequest, jsonObject);
        float repairMoney = Float.parseFloat(jsonObject.optString("repairMoney"));
        selfRepairPayMoney = repairMoney;

        final MCDialogManager dialogManager = MCDialogManager.show(context, R.layout.dialog_order_pay_r, jsonObject);
        dialogManager.getViewById(R.id.id_order_content_dialog_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogManager.dismiss();
            }
        });
        dialogManager.getViewById(R.id.txt_prompt).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putString("url", "https://www.baidu.com");
                bundle.putString("title", "收费规则");
                IntentUtil.gotoActivity(context, WebBaseActivity.class, bundle);
            }
        });
        dialogManager.getViewById(R.id.voucherCellView).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, VoucherActivity.class);
                intent.putExtra("orderId", orderId);
                ((Activity) context).startActivityForResult(intent, 910);
            }
        });

        resulter = new ActivityResulter() {
            @Override
            public void onResult(int requestCode, int resultCode, Intent data) {
                if (requestCode == 910) {
                    String voucherType = data.getStringExtra("voucherType");
                    float voucherValue = data.getStringExtra("voucherValue").isEmpty() ? 0.0f : Float.parseFloat(data.getStringExtra("voucherValue"));
                    float voucherMoney = voucherType.endsWith("0") ? voucherValue : (repairMoney - voucherValue * repairMoney/100);
                    voucherMoney = voucherMoney > repairMoney ? repairMoney : voucherMoney;

                    repairSubmitRequest.voucherId = data.getStringExtra("voucherId");
                    repairSubmitRequest.voucherMoney = String.format("%.2f", voucherMoney);

                    selfRepairPayMoney = repairMoney - voucherMoney;
                    JSONObject showValue = new JSONObject();
                    try {
                        showValue.put("voucherMoney", voucherMoney);
                        showValue.put("repairMoney", repairMoney);
                        showValue.put("moneyCount", repairMoney - voucherMoney);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    dialogManager.fillToView(showValue);
                }
            }
        };

        dialogManager.getViewById(R.id.btn_pay_service_commit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogManager.dismiss();
                RadioButton button = dialogManager.getViewById(R.id.radioWechatPay);
                payLoadingDialog = MCDialogManager.showMessage(context, "订单正在支付...", "请稍后订单正在支付中",
                        "已支付完成", "未支付完成", MCDialogManager.ICON_PRO).setListener(new ItemListener() {
                    @Override
                    public void onItemClick(View listView, Object itemData, int tag) {
                        //                        if (tag == MCDialogManager.TAG_RIGHT_BTN) {
                        //
                        //                        } else if (tag == MCDialogManager.TAG_LEFT_BTN) {
                        //
                        //                        }
                        payIsSuccess(payLoadingDialog, jsonObject);
                        //                        payIsSuccess(payLoadingDialog);
                        //                        payLoadingDialog = null;
                        // TODO /order/customer/payIsSuccess
                    }
                });


                repairSubmitRequest.setResponse(new MCResponse(new MCUIResponse() {
                    @Override
                    public void onDataChanged(Object orderIdData, MCBaseRequest request) {
                        if (orderIdData instanceof String) {
                            payOrderId = ((String) orderIdData).substring(1);
                            if (selfRepairPayMoney == 0) {
                                if (refresher != null) {
                                    if (payLoadingDialog != null) {
                                        payLoadingDialog.dismiss();
                                    }
                                    refresher.onRefresh(TAG_PAY);
                                }
                            } else {
                                payToApp(button.isChecked(), orderIdData.toString(), selfRepairPayMoney);
                            }
                        }
                    }

                    @Override
                    public void onBeginRequest() {
                    }

                    @Override
                    public void onError(Error error) {
                    }

                    @Override
                    public boolean isDead() {
                        return false;
                    }
                }));
                MCHttpCGI.defaultCenter().requestCGI(repairSubmitRequest);
            }
        });
    }

    private void payToApp(boolean isWechat, String payOrderId, float money) {
        if (money <= 0) {
            if (refresher != null) {
                refresher.onRefresh(TAG_PAY);
            }
            return;
        }
        OrderPayer orderPayer = isWechat ? wechatPay : aliPay;
        PayEventer payEventer = new PayEventer() {
            @Override
            public void onPayComplete(int payType, int payCode) {
                payLoadingDialog.dismiss();
                if (payCode == 0) {
                    if (refresher != null) {
                        refresher.onRefresh(TAG_PAY);
                    }
                } else if (payCode == -2) {
                    MCDialogManager.showMessage(context, "支付失败", "支付被取消请重新支付", "继续支付", "取消支付", R.drawable.svg_fail).setListener(new ItemListener() {
                        @Override
                        public void onItemClick(View listView, Object itemData, int tag) {
                            if (tag == MCDialogManager.TAG_RIGHT_BTN) {
                                payToApp(isWechat, payOrderId, money);
                            }
                        }
                    });
                } else if (payCode == -3) {
                    MCDialogManager.showMessage(context, "订单已经支付", "订单状态出现问题,请联系服务人员.", "好的", R.drawable.svg_fail);
                }
            }
        };
        String payStr = isWechat ? String.format("%d", (int) (money * 100)) : String.format("%.2f", money);
        orderPayer.payStart(payOrderId, payStr);
        orderPayer.setPayEvent(payEventer);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resulter != null) {
            resulter.onResult(requestCode, resultCode, data);
        }
    }

    //            订单支付是否成功
    private void payIsSuccess(MCDialogManager dialogManager, Object data) {
        PayIsSuccessRequest payIsSuccessRequest = new PayIsSuccessRequest();
        payIsSuccessRequest.orderpayId = payOrderId;
        payIsSuccessRequest.setResponse(new MCResponse(new MCUIResponse() {
            @Override
            public void onDataChanged(Object orderIdData, MCBaseRequest request) {
                if (orderIdData instanceof Boolean) {
                    if (!(Boolean) orderIdData) {
                        MCDialogManager.showMessage(context, "支付失败", "支付失败请重新支付", "重新支付", R.drawable.svg_fail).setListener(new ItemListener() {
                            @Override
                            public void onItemClick(View listView, Object itemData, int tag) {
                                if (tag == MCDialogManager.TAG_RIGHT_BTN) {
                                    payOrder(data);
                                }
                            }
                        });
                    } else {
                        if (refresher != null) {
                            refresher.onRefresh(TAG_PAY);
                        }
                    }
                }
                dialogManager.dismiss();
            }

            @Override
            public void onBeginRequest() {
            }

            @Override
            public void onError(Error error) {
            }

            @Override
            public boolean isDead() {
                return false;
            }
        }));

        MCHttpCGI.defaultCenter().requestCGI(payIsSuccessRequest);
    }
}
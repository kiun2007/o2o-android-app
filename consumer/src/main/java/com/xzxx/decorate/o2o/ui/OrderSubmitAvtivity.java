package com.xzxx.decorate.o2o.ui;

import android.content.Intent;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.amos.modulebase.utils.IntentUtil;
import com.amos.modulebase.utils.LogUtil;
import com.amos.modulebase.utils.ToastUtil;
import com.kiun.modelcommonsupport.MainApplication;
import com.kiun.modelcommonsupport.adapter.ItemListener;
import com.kiun.modelcommonsupport.controllers.MediaUploadActivity;
import com.kiun.modelcommonsupport.controllers.OrderButtonController;
import com.kiun.modelcommonsupport.controllers.Refresher;
import com.kiun.modelcommonsupport.data.MCUserInfo;
import com.kiun.modelcommonsupport.data.drive.MCDataField;
import com.kiun.modelcommonsupport.network.MCBaseRequest;
import com.kiun.modelcommonsupport.network.requests.OrderSubmitRequest;
import com.kiun.modelcommonsupport.ui.views.AImageView;
import com.kiun.modelcommonsupport.utils.MCDialogManager;
import com.kiun.modelcommonsupport.utils.MCString;
import com.kiun.modelcommonsupport.utils.TimePickerPopWin;
import com.xzxx.decorate.o2o.consumer.MainActivity;
import com.xzxx.decorate.o2o.consumer.R;
import com.xzxx.decorate.o2o.pay.WechatPay;
import com.xzxx.decorate.o2o.pay.alipay.AliPay;
import com.xzxx.decorate.o2o.requests.address.DefaultAddressDetailRequest;
import com.xzxx.decorate.o2o.requests.order.PredictOrderRequest;
import com.xzxx.decorate.o2o.requests.order.PredictSubmtRequest;

import org.json.JSONObject;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by kiun_2007 on 2018/8/13.
 */
public class OrderSubmitAvtivity extends MediaUploadActivity implements Refresher {

    String contactName = null;
    String loaction = null;
    String contactPhone = null;
    String latitudeLongitude = null;
    String contactSex = null;
    String addressDetail = null;
    String cityName = null;
    Date newDate;
    private TextView timeTextView = null;
    private String appointmentTime = null;
    OrderSubmitRequest orderSubmitRequest = null;

    @Override
    public int getLayoutId() {
        return R.layout.actitity_submit_order;
    }

    @Override
    public int getMediaEditViewId() {
        return R.id.richEditView;
    }

    @Override
    public void initView() {
        super.initView();
        timeTextView = findViewById(R.id.timeTextView);
        updateTime();
        timeTextView.setText(MCString.formatDate("M月dd日(EEE) HH:mm", newDate));
        appointmentTime = MCString.formatDate("yyyyMMddHHmmss", newDate);
        commitRequest(new DefaultAddressDetailRequest());
        getButtonController().setWechatPay(new WechatPay());
        getButtonController().setAliPay(new AliPay(this));
        fillToView(R.id.itemContentLayout, getIntent());

        PredictOrderRequest request = new PredictOrderRequest();
        request.secondItemId = getIntent().getStringExtra("secondItemId");
        request.cityCode = "470100";
        commitRequest(request);
    }

    private void updateTime() {
        Date date = new Date();
        long addValue = (date.getTime() % (600 * 1000) > 0) ? ((600 * 1000) - (date.getTime() % (600 * 1000))) : 600 * 1000;
        newDate = new Date(date.getTime() + addValue + (60 * 30 * 1000));
    }

    @Override
    public void onDataChanged(Object data, MCBaseRequest request) {
        if (request instanceof PredictOrderRequest && data instanceof JSONObject) {
            String secondItemIcon = ((JSONObject) data).optString("secondItemUrl");
            AImageView imageView = findViewById(R.id.img_icon);
            imageView.fill(secondItemIcon);
        }

        if (request instanceof DefaultAddressDetailRequest && data instanceof JSONObject) {
            fillToView(R.id.addressPanel, data);
            JSONObject jsonObject = (JSONObject) data;
            contactName = jsonObject.optString("contactName");
            loaction = jsonObject.optString("loaction") + jsonObject.optString("addressDetail");
            contactPhone = jsonObject.optString("contactPhone");
            latitudeLongitude = jsonObject.optString("latitudeLongitude");
            contactSex = jsonObject.optString("contactSex");
            addressDetail = jsonObject.optString("addressDetail");
            cityName = jsonObject.optString("cityName");
        }

        if (request instanceof PredictSubmtRequest && data instanceof JSONObject) {
            JSONObject jsonObject = (JSONObject) data;
            orderSubmitRequest.doorToFee = jsonObject.optString("doorFee");
            MCDataField.fillObject(orderSubmitRequest, jsonObject);
            getButtonController().actionTag(this, OrderButtonController.TAG_PAY, orderSubmitRequest);
        }

        if (request instanceof OrderSubmitRequest) {
            //{"data":"d201809211200670015807999","code":"000","errorMessage":""}
            LogUtil.i(data.toString());

            if (data instanceof String && !TextUtils.isEmpty((String) data)) {
                final String orderId = ((String) data).substring(1);
                MCDialogManager.showMessage(this, "支付成功!",
                        "请等待师傅上门", "查看订单", "返回首页", R.drawable.icon_laugh).setListener(new ItemListener() {
                    @Override
                    public void onItemClick(View listView, Object itemData, int buttonTag) {
                        finish();
                        if (buttonTag == MCDialogManager.TAG_RIGHT_BTN) {
                            Intent intent = new Intent(OrderSubmitAvtivity.this, WaitServiceActivity.class);
                            intent.putExtra("orderId", orderId);
                            startActivity(intent);
                        }
                    }
                });
            }

        }
    }

    public void onClick(View v) {
        if (v.getId() == R.id.addressPanel) {
            Intent intent = new Intent(this, PersonalAddressActivity.class);
            intent.putExtra("isShow", false);
            startActivityForResult(intent, 0);
        } else if (v.getId() == R.id.timePanel) {
            //            updateTime();
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(newDate);
            TimePickerPopWin timePickerPopWin = new TimePickerPopWin.Builder(this, new TimePickerPopWin.OnTimePickListener() {
                @Override
                public void onTimePickCompleted(int hour, int minute, String AM_PM, String time) {
                    String dateStr = MCString.formatDate("yyyyMMdd", newDate) + time + ":00";
                    try {
                        Date date = com.amos.modulebase.utils.DateUtil.getSimpleDateFormat("yyyyMMddHH:mm:ss").parse(dateStr);
                        int i = date.compareTo(new Date());
                        if (i < 0) {
                            ToastUtil.showToast(OrderSubmitAvtivity.this, "上门时间必须大于当前时间");
                            return;
                        }
                        newDate = date;
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    appointmentTime = com.amos.modulebase.utils.DateUtil.formatTime(dateStr, "yyyyMMddHH:mm:ss", "yyyyMMddHHmmss");
                    timeTextView.setText(MCString.formatDate("M月dd日(EEE) HH:mm", newDate));
                    LogUtil.i("  " + time + "  " + dateStr);
                }
            }).textConfirm("确认").textCancel("取消").btnTextSize(16).viewTextSize(25).setCalendar(calendar).
                    colorCancel(Color.parseColor("#999999")).colorConfirm(Color.parseColor("#FF821C")).build();

            timePickerPopWin.showPopWin(this);
        } else if (v.getId() == R.id.btn_apply_after_service_commit) {
        }
    }

    @Override
    public void onSubmitClick(Button button) {
        if (latitudeLongitude == null) {
            Toast.makeText(this, "请选择地址", Toast.LENGTH_LONG).show();
            return;
        }
        super.onSubmitClick(button);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 0 && resultCode == 0x10) {
            contactName = data.getStringExtra("contactName");
            loaction = data.getStringExtra("loaction");
            contactPhone = data.getStringExtra("contactPhone");
            latitudeLongitude = data.getStringExtra("latitudeLongitude");
            contactSex = data.getStringExtra("contactSex");
            addressDetail = data.getStringExtra("addressDetail");
            cityName = data.getStringExtra("cityName");
            fillToView(R.id.addressPanel, data);
        }
    }

    @Override
    public void uploadComplete() {

        orderSubmitRequest = new OrderSubmitRequest();
        fillRequest(orderSubmitRequest, null);
        orderSubmitRequest.appointmentAddress = addressDetail;
        orderSubmitRequest.appointmentCity = cityName;
        orderSubmitRequest.appointmentLatitudeLongitude = latitudeLongitude;
        orderSubmitRequest.appointmentName = contactName;
        orderSubmitRequest.appointmentSex = contactSex;
        orderSubmitRequest.appointmentLocation = loaction;
        orderSubmitRequest.appointmentPhone = contactPhone;
        orderSubmitRequest.appointmentTime = appointmentTime;
        orderSubmitRequest.secondItemId = getIntent().getStringExtra("secondItemId");

        // TODO 兼容企业账户
        MCUserInfo userInfo = MainApplication.getInstance().getUserInfo(false);
        if (userInfo.isEnterPrise.equals("0")) {//是否企业用户，0-是，1-不是.
            LogUtil.i("是企业用户");
            commitRequest(orderSubmitRequest);
        } else {
            PredictSubmtRequest predictSubmtRequest = new PredictSubmtRequest();
            predictSubmtRequest.appointmentCity = cityName;
            predictSubmtRequest.appointmentTime = appointmentTime;
            commitRequest(predictSubmtRequest);
        }
    }

    @Override
    public void onRefresh(int tag) {
        if (tag == OrderButtonController.TAG_PAY) {
            MCDialogManager.showMessage(this, "支付成功!",
                    "请等待师傅上门", "查看订单", "返回首页", R.drawable.icon_laugh).setListener(new ItemListener() {
                @Override
                public void onItemClick(View listView, Object itemData, int buttonTag) {
                    finish();
                    if (buttonTag == MCDialogManager.TAG_RIGHT_BTN) {
                        Intent intent = new Intent(OrderSubmitAvtivity.this, WaitServiceActivity.class);
                        intent.putExtra("orderId", getButtonController().getOrderId());
                        startActivity(intent);
                    }
                    if (buttonTag == MCDialogManager.TAG_LEFT_BTN) {
                        //                        Intent intent = new Intent(OrderSubmitAvtivity.this, WaitServiceActivity.class);
                        //                        intent.putExtra("orderId", getButtonController().getOrderId());
                        //                        startActivity(intent);
                        IntentUtil.gotoActivityToTopAndFinish(OrderSubmitAvtivity.this, MainActivity.class);
                    }
                }
            });
        }
    }
}

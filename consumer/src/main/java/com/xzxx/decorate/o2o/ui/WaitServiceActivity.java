package com.xzxx.decorate.o2o.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.Marker;
import com.amap.api.maps2d.model.MarkerOptions;
import com.amos.modulebase.utils.IntentUtil;
import com.amos.modulebase.utils.NetWorkUtil;
import com.amos.modulebase.utils.ToastUtil;
import com.kiun.modelcommonsupport.adapter.ItemListener;
import com.kiun.modelcommonsupport.controllers.MapBaseActivity;
import com.kiun.modelcommonsupport.controllers.OrderButtonController;
import com.kiun.modelcommonsupport.controllers.Refresher;
import com.kiun.modelcommonsupport.network.MCBaseRequest;
import com.kiun.modelcommonsupport.network.requests.CancelRequest;
import com.kiun.modelcommonsupport.utils.MCDialogManager;
import com.kiun.modelcommonsupport.utils.MCString;
import com.xzxx.decorate.o2o.consumer.MainActivity;
import com.xzxx.decorate.o2o.consumer.R;
import com.xzxx.decorate.o2o.requests.afterService.SalesAfterInfoRequest;
import com.xzxx.decorate.o2o.requests.order.MasterLocationRequest;
import com.xzxx.decorate.o2o.requests.order.OrderInfoRequest;

import org.json.JSONObject;

import java.util.Date;

/**
 * 等待服务页面
 * Created by zf on 2018/6/19.
 */
public class WaitServiceActivity extends MapBaseActivity implements Refresher {

    MarkerOptions meMarker; //我的位置标记.
    MarkerOptions masterMarker; //师傅的标记.
    String masterId = null;
    String masterHeadImg = null;
    Object orderItem = null;
    TextView waitTimeTextView;
    Date appointmentTime;

    @Override
    protected boolean isLocation() {
        return true;
    }


    @Override
    protected int getMapViewId() {
        return R.id.id_map_wait_service;
    }

    @Override
    public int getLayoutId() {
        return R.layout.layout_wait_service;
    }

    @Override
    public void initView() {
        View id_wait_imageView = findViewById(R.id.id_wait_imageView);
        id_wait_imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putString("masterId", masterId);
                IntentUtil.gotoActivity(WaitServiceActivity.this, MaterInformationActivity.class, bundle);
            }
        });
        if (!NetWorkUtil.isNetworkAvailable()) {
            ToastUtil.showToast(this, "网络不可用，请检查您的网络");
        }
        if (getIntent().getStringExtra("orderId") != null && !getIntent().getStringExtra("orderId").isEmpty()) {
            OrderInfoRequest infoRequest = new OrderInfoRequest();
            infoRequest.orderId = getIntent().getStringExtra("orderId");
            showProgress(false);
            commitRequest(infoRequest);
        } else if (getIntent().getStringExtra("salesAfterId") != null && !getIntent().getStringExtra("salesAfterId").isEmpty()) {
            SalesAfterInfoRequest salesAfterInfoRequest = new SalesAfterInfoRequest();
            salesAfterInfoRequest.salesAfterId = getIntent().getStringExtra("salesAfterId");
            showProgress(false);
            commitRequest(salesAfterInfoRequest);
        }

        //getNavigatorBar().setLeftButtonVisibility(View.GONE);
        getButtonController().setOrderContentClz(OrderDetailActivity.class);
        getButtonController().setCommentClz(ServiceEvaluationActivity.class);
        addEvents(R.id.eventPanel, new ItemListener() {
            @Override
            public void onItemClick(View listView, Object itemData, int tag) {
                getButtonController().actionTag(WaitServiceActivity.this, tag, orderItem);
            }
        });
    }

    @Override
    public void onDataChanged(Object data, MCBaseRequest request) {
        dismissProgress();
        Log.e("WaitServiceActivity", "onDataChanged " + data);
        if (request instanceof MasterLocationRequest) { //师傅位置----------------------
            JSONObject jsonObject = (JSONObject) data;
            float latitude = Float.parseFloat(jsonObject.optString("latitude"));
            float longitude = Float.parseFloat(jsonObject.optString("longitude"));
            LatLng latLng = new LatLng(latitude, longitude);

            if (masterMarker == null) {
                masterMarker = addCustomMarker(latLng, masterHeadImg, R.layout.custom_info_window, R.id.marker_item_icon);
                setCenterByMarkers();
            } else {
                masterMarker.position(latLng);
            }
        }

        if (request instanceof OrderInfoRequest || request instanceof SalesAfterInfoRequest) { //订单位置----------------------
            orderItem = data;
            JSONObject jsonObject = (JSONObject) data;
            String latlong = jsonObject.optString("appointmentLatitudeLongitude");
            masterId = jsonObject.optString("masterId");
            masterHeadImg = jsonObject.optString("masterHeadImg");

            LatLng curLatLong = toLatLng(latlong);
            setMapCenter(curLatLong);

            if (meMarker == null) {
                meMarker = addCustomMarker(curLatLong, R.layout.layout_postion_me);
            } else {
                meMarker.position(curLatLong);
            }

            if (request instanceof OrderInfoRequest) {
                View markerView = markerViews.get(meMarker);
                TextView addressTipTextView = markerView.findViewById(R.id.addressTipTextView);
                addressTipTextView.setText(jsonObject.optString("appointmentLocation"));
                if (jsonObject.optString("orderStatus").endsWith("1")) {
                    appointmentTime = MCString.dateByFormat(jsonObject.optString("orderTime"), "yyyyMMddHHmmss");
                    waitTimeTextView = markerView.findViewById(R.id.waitTimeTextView);
                    markerView.findViewById(R.id.waitTimePanel).setVisibility(View.VISIBLE);
                    startWaitTime();
                } else {
                    markerView.findViewById(R.id.waitTimePanel).setVisibility(View.INVISIBLE);
                }
            }

            fillToView(-1, data);

            if (masterId != null && !masterId.isEmpty()) {
                MasterLocationRequest masterLocationRequest = new MasterLocationRequest();
                masterLocationRequest.masterId = masterId;
                showProgress(false);
                commitRequest(masterLocationRequest);
            } else {
                mapManager.getUiSettings().setMyLocationButtonEnabled(true);// 设置默认定位按钮是否显示
                setTitle("发布订单");
            }
        }

        if (request instanceof CancelRequest) {
            Intent intent = new Intent();
            String orderId = getIntent().getStringExtra("orderId");
            String salesAfterId = getIntent().getStringExtra("salesAfterId");
            if (!TextUtils.isEmpty(orderId)) {
                intent.putExtra("orderId", orderId);
            } else if (!TextUtils.isEmpty(salesAfterId)) {
                intent.putExtra("salesAfterId", salesAfterId);
            }
            intent.setAction("refresh_home_order_list");
            sendBroadcast(intent);
            MCDialogManager.info(this, "订单已取消").setListener(new ItemListener() {
                @Override
                public void onItemClick(View listView, Object itemData, int tag) {

                    finish();
                }
            });

            dismissProgress();
        }
    }


    @SuppressLint("HandlerLeak")
    Handler waitTimeHandle = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (!isDead()) {
                waitTimeHandle.sendEmptyMessageDelayed(0, 1000);
                if (appointmentTime != null && waitTimeTextView != null) {
                    long ms = new Date().getTime() - appointmentTime.getTime();
                    int ss = 1000;
                    int mi = ss * 60;
                    int hh = mi * 60;
                    int dd = hh * 24;

                    long day = ms / dd;
                    long hour = (ms - day * dd) / hh;
                    long minute = (ms - day * dd - hour * hh) / mi;
                    long second = (ms - day * dd - hour * hh - minute * mi) / ss;
                    String string = "";
                    if ((day * 24 + hour) < 10) {
                        string = string + "0" + (day * 24 + hour) + ":";
                    } else {
                        string = string + (day * 24 + hour) + ":";
                    }
                    if (minute < 10) {
                        string = string + "0" + minute + ":";
                    } else {
                        string = string + minute + ":";
                    }
                    if (second < 10) {
                        string = string + "0" + second;
                    } else {
                        string = string + second;
                    }
                    waitTimeTextView.setText(string);
                    //                    String string = (day * 24 + hour) + ":" + minute + ":" + second;
                    //                    waitTimeTextView.setText(DateUtil.formatTime(string, "HH:mm:ss", "HH:mm:ss"));
                }
            }
        }
    };

    private void startWaitTime() {
        waitTimeHandle.sendEmptyMessageDelayed(0, 1000);
    }

    @Override
    public void activate(OnLocationChangedListener var1) {
        setCenterByMarkers();
    }

    @Override
    public void onSubmitClick(Button button) {

        MCDialogManager.showMessage(this, "是否取消订单?", "48小时内取消订单免费", "暂不取消", "重新发布", "确认取消", R.drawable.svg_icon_prompt_big).setListener(new ItemListener() {
            @Override
            public void onItemClick(View view, Object itemData, int tag) {
                //                if (tag == MCDialogManager.TAG_LEFT_BTN) {
                //                } else
                if (tag == MCDialogManager.TAG_MIDDLE_BTN) {
                    CancelRequest cancelRequest = new CancelRequest();
                    cancelRequest.cancelType = "1";
                    cancelRequest.cancelContent = "用户取消订单";
                    cancelRequest.orderId = getIntent().getStringExtra("orderId");
                    commitRequest(cancelRequest);
                    Intent intent = new Intent(WaitServiceActivity.this, ItemContentActivity.class);
                    if (orderItem != null && orderItem instanceof JSONObject) {
                        intent.putExtra("secondItemId", ((JSONObject) orderItem).optString("secondItemId", ""));
                        intent.putExtra("secondItemName", ((JSONObject) orderItem).optString("secondItemName", ""));
                    }
                    startActivity(intent);
                } else if (tag == MCDialogManager.TAG_LEFT_BTN) {
                    CancelRequest cancelRequest = new CancelRequest();
                    cancelRequest.cancelType = "1";
                    cancelRequest.cancelContent = "用户取消订单";
                    cancelRequest.orderId = getIntent().getStringExtra("orderId");
                    showProgress(false);
                    commitRequest(cancelRequest);
                }
            }
        });
    }

    @Override
    public void onRefresh(int tag) {
        if (tag == OrderButtonController.TAG_CANCEL) {
            Intent intent = new Intent();
            String orderId = getIntent().getStringExtra("orderId");
            String salesAfterId = getIntent().getStringExtra("salesAfterId");
            if (!TextUtils.isEmpty(orderId)) {
                intent.putExtra("orderId", orderId);
            } else if (!TextUtils.isEmpty(salesAfterId)) {
                intent.putExtra("salesAfterId", salesAfterId);
            }
            intent.setAction("refresh_home_order_list");
            sendBroadcast(intent);
            finish();
            // TODO 服务完成
        }
    }

    //    @Override
    //    public void onRightClick() {
    //        //        super.onRightClick();
    //        IntentUtil.gotoActivityToTopAndFinish(WaitServiceActivity.this, MainActivity.class);
    //    }

    @Override
    public void onLeftClick() {
        // super.onLeftClick();
        IntentUtil.gotoActivityToTopAndFinish(WaitServiceActivity.this, MainActivity.class);
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        try {
            if (markerOptionMap.get(masterMarker).getId().equals(marker.getId())) {
                markerChanged(masterMarker, new OnMarkerViewChanged() {
                    @Override
                    public void viewChanged(View view) {
                        if(view.findViewById(R.id.masterInfoLL).getVisibility() == View.GONE){
                            view.findViewById(R.id.masterInfoLL).setVisibility(View.VISIBLE);
                        }else{
                            view.findViewById(R.id.masterInfoLL).setVisibility(View.GONE);
                        }
                    }
                });
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}

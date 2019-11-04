package com.xzxx.decorate.o2o.master;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amos.modulebase.utils.IntentUtil;
import com.amos.modulebase.utils.KeyboardUtil;
import com.amos.modulebase.utils.LogUtil;
import com.amos.modulebase.utils.NetWorkUtil;
import com.amos.modulebase.utils.PreferencesUtil;
import com.amos.modulebase.utils.ToastUtil;
import com.bumptech.glide.Glide;
import com.hyphenate.easeui.utils.EaseLoadUnread;
import com.kiun.modelcommonsupport.MainApplication;
import com.kiun.modelcommonsupport.adapter.ItemListener;
import com.kiun.modelcommonsupport.controllers.LocationActivity;
import com.kiun.modelcommonsupport.controllers.authority.LoginActivity;
import com.kiun.modelcommonsupport.controllers.authority.WebBaseActivity;
import com.kiun.modelcommonsupport.data.MCUserInfo;
import com.kiun.modelcommonsupport.data.drive.MCDataField;
import com.kiun.modelcommonsupport.hx.ContactListActivity;
import com.kiun.modelcommonsupport.network.MCBaseRequest;
import com.kiun.modelcommonsupport.network.requests.AcceptStatusReqeust;
import com.kiun.modelcommonsupport.ui.views.AListView;
import com.kiun.modelcommonsupport.utils.MCDialogManager;
import com.kiun.modelcommonsupport.utils.MCString;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;
import com.xzxx.decorate.o2o.MasterApplication;
import com.xzxx.decorate.o2o.requests.AddRealTimePositionRequest;
import com.xzxx.decorate.o2o.requests.BroadcastMasterRequest;
import com.xzxx.decorate.o2o.requests.HomeOrderRequest;
import com.xzxx.decorate.o2o.requests.MasterHomeInfoRequest;
import com.xzxx.decorate.o2o.requests.MasterInfoRequest;
import com.xzxx.decorate.o2o.view.MHorProgressBar;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.listener.OnBannerListener;
import com.youth.banner.loader.ImageLoader;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by zf on 2018/7/13.
 * 首页
 */
public class MainActivity extends LocationActivity implements View.OnClickListener, OnBannerListener, ItemListener<JSONObject> {

    private RelativeLayout rl_pause_receipt;
    Banner banner = null;

    private TextView textScore;
    private TextView txt_tips;
    private ImageView imageBehavior;
    private MHorProgressBar mHorProgressBar;
    private TextView acceptTextView;
    boolean isAccept;
    private JSONArray poiListStr = new JSONArray();
    /** 上下拉控件 */
    public SmartRefreshLayout refresh_view;

    private long step = 0;

    @Override
    public void onLocationChanged(String cityName, String cityCode, double latitude, double longitude) {

        //LogUtil.i(cityName);
        //        try {
        //            if (poiListStr.length() <= 0) {
        //                getNavigatorBar().setTitleAndDraw(cityName, R.drawable.master_arrow_down_white);
        //                new POISearchUtil().startSearch(MainActivity.this, latitude, longitude, new POISearchUtil.SearchCallBack() {
        //                    @Override
        //                    public void callBack(JSONArray str) {
        //                        poiListStr = str;
        //                    }
        //                });
        //            }
        //        } catch (Exception e) {
        //            e.printStackTrace();
        //        }

        AddRealTimePositionRequest positionRequest = new AddRealTimePositionRequest();
        positionRequest.latitude = String.format("%f", latitude);
        positionRequest.longitude = String.format("%f", longitude);
        positionRequest.type = "2";
        if (System.currentTimeMillis() - step > 1 * 60 * 1000) {
            LogUtil.i((System.currentTimeMillis() - step) + "");
            step = System.currentTimeMillis();
            commitRequest(positionRequest);
        }

    }

    @Override
    public Long getInterval() {
        return 10000l;
    }

    @Override
    public void onItemClick(View listView, JSONObject itemData, int tag) {

        if (itemData.optString("orderType").equals("0")) {
            String orderId = itemData.optString("orderId");
            String orderStatus = itemData.optString("orderStatus");

            if (Integer.parseInt(orderStatus) >= 4) { //ORDER_STATUS_ON_SERVICE
                Intent intent = new Intent(this, OrderDetailActivity.class);
                intent.putExtra("orderId", orderId);
                intent.putExtra("orderStatus", orderStatus);
                startActivity(intent);
            } else {
                Intent intent = new Intent(this, ServiceOrderActivity.class);
                intent.putExtra("orderId", orderId);
                startActivity(intent);
            }
        } else {
            String salesAfterOrderId = itemData.optString("salesAfterOrderId");
            String salesAfterStatus = itemData.optString("salesAfterStatus");
            if (Integer.parseInt(salesAfterStatus) >= 2) { //ORDER_STATUS_ON_SERVICE
                Intent intent = new Intent(this, OrderDetailActivity.class);
                intent.putExtra("salesAfterOrderId", salesAfterOrderId);
                intent.putExtra("salesAfterStatus", salesAfterStatus);
                startActivity(intent);
            } else {
                Intent intent = new Intent(this, ServiceOrderActivity.class);
                intent.putExtra("salesAfterOrderId", salesAfterOrderId);
                startActivity(intent);
            }
        }
    }

    @Override
    public void onRightClick() {
        Intent intent = new Intent(this, ContactListActivity.class);
        startActivity(intent);
    }

    @Override
    public void OnBannerClick(int position) {
        try {
            String url = BroadcastRequestData.getJSONObject(position).optString("linkUrl", "");
            if (TextUtils.isEmpty(url)) {
                return;
            }
            Bundle bundle = new Bundle();
            bundle.putString("url", url);
            bundle.putString("title", "详情");
            IntentUtil.gotoActivity(this, WebBaseActivity.class, bundle);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class GlideImageLoader extends ImageLoader {
        @Override
        public void displayImage(Context context, Object path, ImageView imageView) {
            //具体方法内容自己去选择，次方法是为了减少banner过多的依赖第三方包，所以将这个权限开放给使用者去选择
            Glide.with(context.getApplicationContext())
                    .load(path)
                    .into(imageView);
        }
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    public void onLeftClick() {
        Intent personalIntent = new Intent(this, PersonalCenterActivity.class);
        startActivity(personalIntent);
    }

    @Override
    public void initView() {
        MainApplication.acitity = this;
        if (MainApplication.getInstance().getUserInfo(true) == null) {
            finish();
            return;
        }
        txt_tips = findViewById(R.id.txt_tips);
        refresh_view = findViewById(R.id.refresh_view);
        rl_pause_receipt = findViewById(R.id.rl_pause_receipt);
        banner = findViewById(R.id.home_banner);
        textScore = findViewById(R.id.id_txt_score);
        imageBehavior = findViewById(R.id.id_image_behavior);
        mHorProgressBar = findViewById(R.id.score_progressbar);
        acceptTextView = findViewById(R.id.acceptTextView);
        //        StarBar starBar_attitude = findViewById(R.id.starBar_attitude);
        //        starBar_attitude.setStarMark(0f);
        mHorProgressBar.setDurProgress(100, imageBehavior, textScore);
        getPositioning();
        AListView listView = findViewById(R.id.orderInfoList);
        listView.setItemListener(this);
        initEvent();

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(MainApplication.LOGOUT_ACTION);
        intentFilter.addAction("refresh_home_fragment_list");
        intentFilter.addAction("refresh_home_red");
        registerReceiver(receiver, intentFilter);

        //        getNavigatorBar().setTitleListener(new View.OnClickListener() {
        //            @Override
        //            public void onClick(View view) {
        //                Bundle bundle = new Bundle();
        //                bundle.putString("data", poiListStr.toString());
        //                IntentUtil.gotoActivityForResult(MainActivity.this, PoiSearchActivity.class, bundle, 10002);
        //            }
        //        });

        refresh_view.setEnableLoadMore(false);
        refresh_view.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
            }

            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                MainActivity.this.onRefresh(false);
            }
        });
    }

    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(MainApplication.LOGOUT_ACTION)) {
                ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
                String activityName = am.getRunningTasks(1).get(0).topActivity.getClassName();
                if (!activityName.contains("LoginActivity")) {
                    Intent intent1 = new Intent(MainActivity.this, LoginActivity.class);
                    intent1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent1);
                    finish();
                }
            } else if (intent.getAction().equals("refresh_home_fragment_list")) {//
                onRefresh(false);
            } else if (intent.getAction().equals("refresh_home_red")) {//
                if (MainApplication.getInstance().getUserInfo(false) == null) {
                    getNavigatorBar().setUnRead("");
                } else {
                    //相当于Fragment的onResume
                    int count = new EaseLoadUnread().unReadCount();
                    if (count > 0) {
                        getNavigatorBar().setUnRead(String.valueOf(count));
                    } else {
                        getNavigatorBar().setUnRead("");
                    }
                }
            }
        }
    };

    @Override
    protected void onDestroy() {
        try {
            unregisterReceiver(receiver);
        } catch (Exception e) {
        }
        super.onDestroy();
    }

    @Override
    protected void onTokenInvalid() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    JSONArray BroadcastRequestData;

    @Override
    public void onDataChanged(Object data, MCBaseRequest request) {
        completeRefresh();
        refresh_view.finishLoadMore();
        refresh_view.finishRefresh();
        if (request instanceof BroadcastMasterRequest) {
            fillToView(R.id.home_banner, data, true);
            BroadcastRequestData = (JSONArray) data;
            banner.setImages(MCString.listByArray((JSONArray) data, "pictureUrl")).setImageLoader(new
                    GlideImageLoader()).setOnBannerListener(this).start();
            banner.updateBannerStyle(BannerConfig.CIRCLE_INDICATOR);
        }//masterProfession
        if (request instanceof MasterHomeInfoRequest) {
            fillToView(R.id.masterInfoPanel, data);
            final JSONObject jsonObject = (JSONObject) data;
            isAccept = jsonObject.optString("acceptStatus").equals("0");
            acceptTextView.setText(isAccept ? "暂停接单" : "开始接单");
            MainApplication.getInstance().save(MainApplication.MASTER_INFO, ((JSONObject) data).optString("masterProfession"));
            if (isAccept) {
                rl_pause_receipt.setVisibility(View.GONE);
            } else {
                rl_pause_receipt.setVisibility(View.VISIBLE);
            }
            String phone = MainApplication.getInstance().getValue("sp_key_login_phone");
            PreferencesUtil.put("acceptStatus" + phone, jsonObject.optString("acceptStatus"));


            txt_tips.post(new Runnable() {
                @Override
                public void run() {
                    try {
                        double dur = jsonObject.optDouble("weight");
                        if (dur <= 50) {
                            txt_tips.setText(txt_tips.getText().toString().replace("表现良好", "表现恶劣"));
                        } else if (dur > 50 && dur <= 90) {
                            txt_tips.setText(txt_tips.getText().toString().replace("表现良好", "表现良好"));
                        } else if (dur > 90) {
                            txt_tips.setText(txt_tips.getText().toString().replace("表现良好", "表现优秀"));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            return;
        }

        if (request instanceof MasterInfoRequest && data instanceof JSONObject) {
            MCUserInfo userInfo = MainApplication.getInstance().getUserInfo(false);
            if (userInfo != null) {
                MCDataField.fillObject(userInfo, (JSONObject) data);
                MainApplication.getInstance().saveUserInfo(userInfo);
            }
        }

        if (request instanceof HomeOrderRequest) {
            fillToView(R.id.orderInfoList, data, true);
            return;
        }

        if (request instanceof AcceptStatusReqeust) {
            isAccept = ((AcceptStatusReqeust) request).acceptStatus.equals("0");
            acceptTextView.setText(isAccept ? "暂停接单" : "开始接单");
            if (isAccept) {
                rl_pause_receipt.setVisibility(View.GONE);
            } else {
                rl_pause_receipt.setVisibility(View.VISIBLE);
            }
            String phone = MainApplication.getInstance().getValue("sp_key_login_phone");
            PreferencesUtil.put("acceptStatus" + phone, ((AcceptStatusReqeust) request).acceptStatus);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        onRefresh(false);
        startLocation();
        if (MainApplication.getInstance().getUserInfo(false) == null) {
            getNavigatorBar().setUnRead("");
        } else {
            //相当于Fragment的onResume
            int count = new EaseLoadUnread().unReadCount();
            if (count > 0) {
                getNavigatorBar().setUnRead(String.valueOf(count));
            } else {
                getNavigatorBar().setUnRead("");
            }
        }
        KeyboardUtil.hideKeyBord(rl_pause_receipt);
    }

    @Override
    public void onRefresh(boolean isPullDown) {
        if (!NetWorkUtil.isNetworkAvailable()) {
            ToastUtil.showToast(this, "网络不可用，请检查您的网络");
        }
        MCUserInfo userInfo = MasterApplication.getInstance().getUserInfo(true);
        Log.e("MainActivity", "userInfo " + userInfo);
        if (userInfo != null) {
            if (!userInfo.isPassAduit()) {
                Intent intent = new Intent(this, RegisterSecondActivity.class);
                if (userInfo.aduitType.isEmpty()) {
                    intent = new Intent(this, RegisterFirstActivity.class);
                }
                startActivity(intent);
                finish();
                return;
            }
            commitRequest(new BroadcastMasterRequest());
            commitRequest(new HomeOrderRequest());
            commitRequest(new MasterInfoRequest());
            commitRequest(new MasterHomeInfoRequest());
        } else {
            finish();
        }
    }

    public static void startActivity(Context context) {
        context.startActivity(new Intent(context, MainActivity.class));
    }

    private void initEvent() {
        rl_pause_receipt.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_pause_receipt:
                MCDialogManager.showMessage(this, isAccept ? "是否暂停接单？" : "是否开始接单？", isAccept ? "暂停接单系统将无法给您推送订单，如需开启点击“个人中心-设置-接单服务”打开开关即可" : "开始接单系统将推送订单给您",
                        isAccept ? "取消暂停" : "取消开始", isAccept ? "确认暂停" : "确认开始", R.drawable.icon_prompt).setListener(new ItemListener() {
                    @Override
                    public void onItemClick(View listView, Object itemData, int tag) {
                        if (tag == MCDialogManager.TAG_LEFT_BTN) {
                            AcceptStatusReqeust reqeust = new AcceptStatusReqeust();
                            reqeust.acceptStatus = isAccept ? "1" : "0";
                            commitRequest(reqeust);
                        }
                    }
                });
                break;
            case R.id.userHeadInfo: {
                Intent intent = new Intent(this, PersonalInfoActivity.class);
                startActivity(intent);
                break;
            }
        }
    }

    //    @Override
    //    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    //        super.onActivityResult(requestCode, resultCode, data);
    //        // 选择银行名称
    //        if (requestCode == 10002 && resultCode == RESULT_OK) {
    //            if (data != null) {
    //                Bundle bundle = data.getExtras();
    //                if (bundle != null) {
    //                    String json = bundle.getString("data", "");
    //                    try {
    //                        JSONObject jsonObject = new JSONObject(json);
    //                        String cityname = jsonObject.optString("cityname", "");
    //                        String name = jsonObject.optString("name", "");
    //                        //                        if (!TextUtils.isEmpty(name)) {
    //                        getNavigatorBar().setTitleAndDraw(cityname.replace("市","") + "•" + name, R.drawable.master_arrow_down_white);
    //                        //                        }
    //                    } catch (JSONException e) {
    //                        e.printStackTrace();
    //                    }
    //                }
    //            }
    //        }
    //    }
}

package com.xzxx.decorate.o2o.master;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.Marker;
import com.amap.api.maps2d.model.MarkerOptions;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.route.DrivePath;
import com.amos.modulebase.utils.PermissionUtils;
import com.amos.modulebase.utils.ToastUtil;
import com.kiun.modelcommonsupport.MainApplication;
import com.kiun.modelcommonsupport.adapter.ItemListener;
import com.kiun.modelcommonsupport.controllers.MapBaseActivity;
import com.kiun.modelcommonsupport.controllers.OrderButtonController;
import com.kiun.modelcommonsupport.controllers.Refresher;
import com.kiun.modelcommonsupport.data.MCUserInfo;
import com.kiun.modelcommonsupport.network.MCBaseRequest;
import com.kiun.modelcommonsupport.network.requests.SysDicItemRequest;
import com.kiun.modelcommonsupport.utils.MCActionSheet;
import com.kiun.modelcommonsupport.utils.OnActionSheetSelectListener;
import com.phillipcalvin.iconbutton.IconButton;
import com.xzxx.decorate.o2o.requests.MasterOrderInfoRequest;
import com.xzxx.decorate.o2o.requests.MasterSalesAfterInfoRequest;

import org.json.JSONObject;

import java.math.BigDecimal;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by zf on 2018/7/22.
 * 服务订单页面
 */
public class ServiceOrderActivity extends MapBaseActivity implements Refresher {

    public static final String ORDER_STATUS_WAIT_PAY_DOOR = "0"; //订单等待付上门费
    public static final String ORDER_STATUS_WAIT_ACCEPTED = "1"; //订单等待分配师傅
    public static final String ORDER_STATUS_WAIT_LEAVE = "2"; //订单师傅待上门
    public static final String ORDER_STATUS_WAIT_SERVICE = "3"; //订单师傅已出发待抵达
    public static final String ORDER_STATUS_ON_SERVICE = "4"; //师傅服务中
    public static final String ORDER_STATUS_WAIT_PAY = "5"; //师傅维修完成等待用户付款
    public static final String ORDER_STATUS_WAIT_COMMENT = "6";  //用户付款完成待评论
    public static final String ORDER_STATUS_FINISH = "7";  //订单完成
    public static final String ORDER_STATUS_CUSTOMER_CANCEL = "8";// 用户取消订单

    Object orderData;
    Object cancelDict;
    Date appointmentTime;
    TextView overTimeTextView;
    TextView overTimeTextViewTips;

    MarkerOptions meMarker;
    //    LatLng meLatLng;
    MarkerOptions customerMarker;
    View ll_service_order_cancel;
    View view_send;

    @Override
    protected int getMapViewId() {
        return R.id.map_service_order;
    }

    @Override
    protected boolean isLocation() {
        return true;
    }

    @Override
    protected boolean isLocationButton() {
        return false;
    }

    @Override
    public int getLayoutId() {
        return R.layout.layout_service_order;
    }

    @Override
    protected void onResume() {
        super.onResume();
        initData();
    }

    String getTimeDown() {
        if (appointmentTime != null) {
            Date now = new Date();
            long midTime = (appointmentTime.getTime() - now.getTime()) / 1000;
            String s = midTime < 0 ? "超过" : "";
            midTime = Math.abs(midTime);
            long hh = midTime / 60 / 60 % 60, mm = midTime / 60 % 60, ss = midTime % 60;
            return String.format("%s%02d:%02d:%02d", s, hh, mm, ss);
        }
        return "00:00:00";
    }

    Handler timeHandle = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (!isDead()) {
                timeHandle.sendEmptyMessageDelayed(0, 1000);
                overTimeTextView.setText(getTimeDown());
            }
        }
    };

    private void initData() {
        String salesAfterId = getIntent().getStringExtra("salesAfterOrderId");
        if (salesAfterId != null && !salesAfterId.isEmpty()) {
            MasterSalesAfterInfoRequest request = new MasterSalesAfterInfoRequest();
            request.salesAfterOrderId = salesAfterId;
            commitRequest(request);
            overTimeTextView.setVisibility(View.GONE);
            overTimeTextViewTips.setVisibility(View.GONE);
            return;
        }

        String orderId = getIntent().getStringExtra("orderId");
        if (orderId != null) {
            MasterOrderInfoRequest infoRequest = new MasterOrderInfoRequest();
            infoRequest.orderId = orderId;
            commitRequest(infoRequest);
            overTimeTextView.setVisibility(View.VISIBLE);
            overTimeTextViewTips.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void initView() {
        getButtonController().setOrderContentClz(OrderDetailActivity.class);
        getButtonController().setSendToBaseClz(SendToBaseActivity.class);

        overTimeTextViewTips = findViewById(R.id.overTimeTextViewTips);
        overTimeTextView = findViewById(R.id.overTimeTextView);
        ll_service_order_cancel = findViewById(R.id.ll_service_order_cancel);
        view_send = findViewById(R.id.view_send);
        addEvents(R.id.orderInfoPanel, new ItemListener() {
            @Override
            public void onItemClick(View listView, Object itemData, int tag) {
                getButtonController().actionTag(ServiceOrderActivity.this, tag, orderData);
            }
        });
    }

    @Override
    protected void onLocation(LatLng latLng) {
        //        meLatLng = latLng;
        if (meMarker == null) {
            meMarker = addCustomMarker(latLng, MainApplication.getInstance().getUserInfo(false).headImg, R.layout.custom_info_me, R.id.marker_item_icon);
        } else {
            if (markerOptionMap.get(meMarker) != null) {
                markerOptionMap.get(meMarker).setPosition(latLng);
            }
        }
        setCenterByMarkers();
        crateRoute();
    }

    private void crateRoute() {
        if ((markerOptionMap.get(meMarker) != null || meMarker != null) && customerMarker != null) {

            LatLng startLatLng = null;
            if (markerOptionMap.get(meMarker) != null) {
                startLatLng = markerOptionMap.get(meMarker).getPosition();
            }
            if (meMarker != null) {
                startLatLng = meMarker.getPosition();
            }
            if (startLatLng == null) {
                return;
            }

            searchRouteResult(new LatLonPoint(startLatLng.latitude, startLatLng.longitude),
                    new LatLonPoint(customerMarker.getPosition().latitude, customerMarker.getPosition().longitude));
        }
    }

    @Override
    public void onDataChanged(Object data, MCBaseRequest request) {

        if (request instanceof MasterOrderInfoRequest || request instanceof MasterSalesAfterInfoRequest) {
            fillToView(R.id.orderInfoPanel, data);
            orderData = data;
            JSONObject jsonObject = (JSONObject) data;
            final LatLng latLng = toLatLng(jsonObject.optString("appointmentLatitudeLongitude"));
            final String appointmentLocation = jsonObject.optString("appointmentLocation");
            String orderStatus = jsonObject.optString("orderStatus");
            String salesAfterStatus = jsonObject.optString("salesAfterStatus");
            String doorPayTime = jsonObject.optString("doorPayTime");
            String orderTime = jsonObject.optString("orderTime");
            SimpleDateFormat inDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
            try {
                appointmentTime = inDateFormat.parse(jsonObject.optString("appointmentTime"));
                timeHandle.sendEmptyMessageDelayed(0, 10);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            //            setMapCenter(latLng);
            customerMarker = addCustomMarker(latLng, R.layout.custom_info_window, data);
            TextView distanceTextView = markerViews.get(customerMarker).findViewById(R.id.distanceTextView);
            markerViews.get(customerMarker).findViewById(R.id.destIconImageView).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    LinearLayout linearLayout = markerViews.get(customerMarker).findViewById(R.id.destLayout);
                    if(linearLayout.getVisibility() == View.INVISIBLE){
                        linearLayout.setVisibility(View.VISIBLE);
                    }else{
                        linearLayout.setVisibility(View.INVISIBLE);
                    }
                }
            });

            markerViews.get(customerMarker).findViewById(R.id.naviImageView).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String[] items = {"高德地图", "百度地图"};
                    MCActionSheet.showActionSheet(ServiceOrderActivity.this, items, "取消","1", new OnActionSheetSelectListener() {
                        @Override
                        public void onItemAction(Dialog dialog, int index) {
                            dialog.dismiss();
                            if (index == 1) {
                                if (isAvilible(ServiceOrderActivity.this, "com.autonavi.minimap")) {
                                    try { //sourceApplication
                                        Intent intent = Intent.getIntent("androidamap://navi?sourceApplication=公司的名称&poiname=我的目的地&lat=" + latLng.latitude + "&lon=" + latLng.longitude + "&dev=0");
                                        startActivity(intent);
                                    } catch (URISyntaxException e) {
                                        e.printStackTrace();
                                    }
                                } else {
                                    ToastUtil.showToast(ServiceOrderActivity.this, "您尚未安装高德地图或地图版本过低");
                                }
                            } else if (index == 2) {
                                if (isAvilible(ServiceOrderActivity.this, "com.baidu.BaiduMap")) {
                                    //   com.baidu.mapapi.model.LatLng ll = bd_encrypt(latLng);
                                    double[] gaoDeToBaidu = gaoDeToBaidu(latLng);
                                    try {
                                        //                                        Intent intent1 = Intent.getIntent("intent://map/navi?location=" + gaoDeToBaidu[0] + "," + gaoDeToBaidu[1] + "&type=TIME&src=thirdapp.navi.hndist.sydt#Intent;scheme=bdapp;" + "package=com.baidu.BaiduMap;end");

                                        // * 打开百度地图导航客户端
                                        // * intent = Intent.getIntent("baidumap://map/navi?location=34.264642646862,108.95108518068&type=BLK&src=thirdapp.navi.you
                                        // * location 坐标点 location与query二者必须有一个，当有location时，忽略query
                                        // * query 搜索key 同上
                                        // * type 路线规划类型 BLK:躲避拥堵(自驾);TIME:最短时间(自驾);DIS:最短路程(自驾);FEE:少走高速(自驾);默认DIS
                                        //                                        114.072468,22.659818
                                        //                                        StringBuffer stringBuffer = new StringBuffer("baidumap://map/navi?location=").append(22.659818).append(",").append(114.072468).append("&type=TIME");
                                        //append(lat).append(",").append(lng)
                                        StringBuffer stringBuffer = new StringBuffer("baidumap://map/navi?location=").append(gaoDeToBaidu[1]).append(",").append(gaoDeToBaidu[0]).append("&type=TIME");
                                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(stringBuffer.toString()));
                                        intent.setPackage("com.baidu.BaiduMap");
                                        startActivity(intent);
                                    } catch (Exception e) {
                                        ToastUtil.showToast(ServiceOrderActivity.this, "您尚未安装百度地图或地图版本过低");
                                        e.printStackTrace();
                                    }
                                } else {
                                    ToastUtil.showToast(ServiceOrderActivity.this, "您尚未安装百度地图或地图版本过低");
                                }

                            }
                        }
                    });
                }
            });
            if (!PermissionUtils.getInstance().checkSelfPermission(ServiceOrderActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                distanceTextView.setText("请开启定位服务");
            } else {
                distanceTextView.setText("");
            }
            if ((!orderStatus.isEmpty() && ORDER_STATUS_WAIT_SERVICE.equals(orderStatus)) || (!salesAfterStatus.isEmpty() && salesAfterStatus.equals("1"))) {
                IconButton iconButton = findViewById(R.id.btn_start_to_start);
                iconButton.setText(R.string.start_to_service);
            }
            setCenterByMarkers();
            crateRoute();
        }

        if (request instanceof SysDicItemRequest) {
            cancelDict = data;
        }

        MCUserInfo userInfo = MainApplication.getInstance().getUserInfo(false);
        if ((userInfo != null) && !userInfo.masterType.endsWith("0")) {//是否企业用户，0-是，1-不是.
            if (view_send.getVisibility() == View.VISIBLE) {
                view_send.setVisibility(View.GONE);
            }
            if (ll_service_order_cancel.getVisibility() == View.VISIBLE) {
                ll_service_order_cancel.setVisibility(View.GONE);
            }
        }
    }

    @Override
    protected void onDrivePath(DrivePath drivePath) {

        TextView distanceTextView = markerViews.get(customerMarker).findViewById(R.id.distanceTextView);
        TextView serviceDistance = findViewById(R.id.text_service_distance);

        int dur = (int) (drivePath.getDuration() / 60);

        distanceTextView.setText(Html.fromHtml(String.format("<font color='#000000'>距您</font><font color='#FF0000'>%.1f</font>" +
                "<font color='#000000'>公里</font><font color='#FF0000'>%d</font><font color='#000000'>分钟</font>", drivePath.getDistance() / 1000, dur)));
        if (dur < 1) {
            distanceTextView.setText("即将抵达工作地点");
            serviceDistance.setText("即将抵达");
        } else {
            serviceDistance.setText(String.format("距您%.1fKM %d分钟", drivePath.getDistance() / 1000, dur));
        }
    }

    @Override
    public void onSubmitClick(Button button) {
        boolean isDoor = button.getText().toString().equals(getString(R.string.start_to_start));
        getButtonController().actionTag(this, isDoor ? OrderButtonController.TAG_DOOR : OrderButtonController.TAG_START, orderData);
    }

    @Override
    public void onRefresh(int tag) {
        if (tag == OrderButtonController.TAG_START || tag == OrderButtonController.TAG_CANCEL) {
            finish();
            return;
        }
        initData();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if ((requestCode == 5004 || requestCode == 0x3001) && (resultCode == OrderButtonController.TAG_START || requestCode == OrderButtonController.TAG_SEND ||
                resultCode == OrderButtonController.TAG_CANCEL)) {
            finish();
        }
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        try {
            if (markerOptionMap.get(meMarker).getId().equals(marker.getId())) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    //验证各种导航地图是否安装
    public static boolean isAvilible(Context context, String packageName) { //获取packagemanager
        final PackageManager packageManager = context.getPackageManager(); //获取所有已安装程序的包信息
        List<PackageInfo> packageInfos = packageManager.getInstalledPackages(0); //用于存储所有已安装程序的包名
        List<String> packageNames = new ArrayList<String>(); //从pinfo中将包名字逐一取出，压入pName list中
        if (packageInfos != null) {
            for (int i = 0; i < packageInfos.size(); i++) {
                String packName = packageInfos.get(i).packageName;
                packageNames.add(packName);
            }
        } //判断packageNames中是否有目标程序的包名，有TRUE，没有FALSE
        return packageNames.contains(packageName);
    }

    /**
     * 将火星坐标转变成百度坐标
     *
     * @param lngLat_gd
     *         火星坐标（高德、腾讯地图坐标等）
     *
     * @return 百度坐标
     */

    public static com.baidu.mapapi.model.LatLng bd_encrypt(LatLng lngLat_gd) {
        double x_pi = 3.14159265358979324 * 3000.0 / 180.0;
        double x = lngLat_gd.longitude, y = lngLat_gd.latitude;
        double z = Math.sqrt(x * x + y * y) + 0.00002 * Math.sin(y * x_pi);
        double theta = Math.atan2(y, x) + 0.000003 * Math.cos(x * x_pi);
        return new com.baidu.mapapi.model.LatLng(dataDigit(6, z * Math.cos(theta) + 0.0065), dataDigit(6, z * Math.sin(theta) + 0.006));
    }

    /**
     * 对double类型数据保留小数点后多少位
     * 高德地图转码返回的就是 小数点后6位，为了统一封装一下
     *
     * @param digit
     *         位数
     * @param in
     *         输入
     *
     * @return 保留小数位后的数
     */
    static double dataDigit(int digit, double in) {
        return new BigDecimal(in).setScale(6, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    private double[] gaoDeToBaidu(LatLng lngLat_gd) {
        double gd_lon = lngLat_gd.longitude, gd_lat = lngLat_gd.latitude;
        double[] bd_lat_lon = new double[2];
        double PI = 3.14159265358979324 * 3000.0 / 180.0;
        double x = gd_lon, y = gd_lat;
        double z = Math.sqrt(x * x + y * y) + 0.00002 * Math.sin(y * PI);
        double theta = Math.atan2(y, x) + 0.000003 * Math.cos(x * PI);
        bd_lat_lon[0] = z * Math.cos(theta) + 0.0065;
        bd_lat_lon[1] = z * Math.sin(theta) + 0.006;
        return bd_lat_lon;
    }

}
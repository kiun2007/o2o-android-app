package com.kiun.modelcommonsupport.controllers;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.LocationSource;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.model.BitmapDescriptor;
import com.amap.api.maps2d.model.BitmapDescriptorFactory;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.LatLngBounds;
import com.amap.api.maps2d.model.Marker;
import com.amap.api.maps2d.model.MarkerOptions;
import com.amap.api.services.core.AMapException;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.route.BusRouteResult;
import com.amap.api.services.route.DrivePath;
import com.amap.api.services.route.DriveRouteResult;
import com.amap.api.services.route.RideRouteResult;
import com.amap.api.services.route.RouteSearch;
import com.amap.api.services.route.WalkRouteResult;
import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.kiun.modelcommonsupport.MainApplication;
import com.kiun.modelcommonsupport.R;
import com.kiun.modelcommonsupport.adapter.ItemListener;
import com.kiun.modelcommonsupport.controllers.route.DrivingRouteOverLay;
import com.kiun.modelcommonsupport.data.drive.DriveBase;
import com.kiun.modelcommonsupport.ui.views.AImageView;
import com.kiun.modelcommonsupport.utils.MCDialogManager;
import com.kiun.modelcommonsupport.utils.ViewUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by kiun_2007 on 2018/8/20.
 * 高德地图专用操作Activity.
 */

public abstract class MapBaseActivity extends BaseRequestAcitity implements AMap.OnMarkerClickListener, AMap.InfoWindowAdapter,
        AMap.OnMarkerDragListener, AMap.OnMapLoadedListener, RouteSearch.OnRouteSearchListener, LocationSource, AMapLocationListener {

    protected LatLng lastLatLng;
    /**
     * 主要地图视图.
     */
    protected MapView mainMapView;

    /**
     * 地图管理工具.
     */
    protected AMap mapManager;

    private RouteSearch mRouteSearch;
    private DriveRouteResult mDriveRouteResult;
    private MCDialogManager permissionDialog;
    private AMapLocationClient mLocationClient;
    private LocationSource.OnLocationChangedListener mListener = null;
    boolean isFirstLoc = true;
    protected AMapLocation location;
    protected Map<MarkerOptions, View> markerViews = new HashMap<>();
    protected Map<Marker, View> markerLayouts = new HashMap<>();
    protected Map<MarkerOptions, Marker> markerOptionMap = new HashMap<>();

    //路径规划相关
    private final int ROUTE_TYPE_DRIVE = 2;

    /**
     * 获取地图的View id.
     *
     * @return view id.
     */
    protected abstract int getMapViewId();

    /**
     * 是否开启定位.
     *
     * @return 开启定位为true.
     */
    protected boolean isLocation() {
        return false;
    }

    protected boolean isLocationButton() {
        return false;
    }

    protected void onDrivePath(DrivePath drivePath) {
    }

    protected void onLocation(LatLng latLng) {
    }

    /**
     * 字符格式的坐标转化为坐标对象.
     *
     * @param latlngStr
     *         lat,lng 字符串.
     *
     * @return 坐标对象.
     */
    protected LatLng toLatLng(String latlngStr) {
        String[] latlngArray = latlngStr.split(",");
        if (latlngArray.length != 2) {
            return null;
        }
        return new LatLng(Double.parseDouble(latlngArray[0]), Double.parseDouble(latlngArray[1]));
    }

    protected void initView(Bundle savedInstanceState) {
        mainMapView = findViewById(getMapViewId());
        mainMapView.onCreate(savedInstanceState);
        mapManager = mainMapView.getMap();
        setUpMap();
        settingMap();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //初始化地图.
        initView(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isLocation()) {
            startLocation();
        }
    }

    private void startLocation() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (permissionDialog == null) {
                permissionDialog = MCDialogManager.showMessage(this, "需要定位权限", "应用没有定位权限,设置定位权限后才能正常工作",
                        "立即去设置", "暂时不设置", R.drawable.svg_icon_prompt_big);
                permissionDialog.setListener(new ItemListener() {
                    @Override
                    public void onItemClick(View listView, Object itemData, int tag) {
                        if (tag == MCDialogManager.TAG_RIGHT_BTN) {
                            Intent intent = new Intent();
                            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                            Uri uri = Uri.fromParts("package", MainApplication.getInstance().getPackageName(), null);
                            intent.setData(uri);
                            startActivity(intent);
                        }
                    }
                });
            }
        } else {
            if (permissionDialog != null) {
                permissionDialog.dismiss();
                permissionDialog = null;
            }
            activate(null);
        }
    }

    /**
     * 设置一些amap的属性
     */
    private void setUpMap() {
        mapManager.getUiSettings().setZoomControlsEnabled(false);
        mapManager.getUiSettings().setMyLocationButtonEnabled(isLocationButton());// 设置默认定位按钮是否显示
        mapManager.setMyLocationEnabled(isLocation());// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false

        mapManager.setOnMapLoadedListener(this);
        mapManager.setOnMarkerDragListener(this);// 设置marker可拖拽事件监听器
        mapManager.setOnMarkerClickListener(this);// 设置marker可拖拽事件监听器
        mapManager.setInfoWindowAdapter(this);
        if (isLocation()) {
            mapManager.setLocationSource(this);//startLocation();
        }
    }

    //    protected void

    /**
     * 地图参数设置.
     */
    protected void settingMap() {
    }

    /**
     * 初始化路径搜索.
     */
    protected void initRouteSearch() {
        //设置路径规划
        mRouteSearch = new RouteSearch(this);
        mRouteSearch.setRouteSearchListener(this);
    }

    /**
     * 搜索路径.
     *
     * @param mStartPoint
     * @param mEndPoint
     */
    public void searchRouteResult(LatLonPoint mStartPoint, LatLonPoint mEndPoint) {
        if (mRouteSearch == null) {
            initRouteSearch();
        }

        LatLng lowLatLng = new LatLng(mStartPoint.getLatitude(), mStartPoint.getLongitude());
        //        if(lastLatLng != null){
        //            if(AMapUtils.calculateLineDistance(lastLatLng, lowLatLng) < 10){ //与上次计算路径的出发点相差不到10米不进行路径计算.
        //                return;
        //            }
        //        }

        final RouteSearch.FromAndTo fromAndTo = new RouteSearch.FromAndTo(mStartPoint, mEndPoint);
        // 第一个参数表示路径规划的起点和终点，第二个参数表示驾车模式，第三个参数表示途经点，第四个参数表示避让区域，第五个参数表示避让道路
        RouteSearch.DriveRouteQuery query = new RouteSearch.DriveRouteQuery(fromAndTo, RouteSearch.DrivingDefault, null, null, "");
        mRouteSearch.calculateDriveRouteAsyn(query);// 异步路径规划驾车模式查询

        lastLatLng = lowLatLng;
    }

    //--------------------------------LocationSource-----------------------------------
    public void activate(LocationSource.OnLocationChangedListener var1) {
        mListener = var1;
        if (mLocationClient == null) {
            mLocationClient = new AMapLocationClient(this);
            //声明mLocationOption对象，定位参数
            AMapLocationClientOption locationOption = new AMapLocationClientOption();
            mLocationClient.setLocationListener(this);
            locationOption.setOnceLocation(true);
            locationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
            mLocationClient.setLocationOption(locationOption);
        }
        mLocationClient.startLocation();
    }

    @Override
    public void deactivate() {
    }
    //--------------------------------LocationSource-----------------------------------


    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if (aMapLocation != null) {
            if (aMapLocation.getErrorCode() == 0) {
                onLocation(new LatLng(aMapLocation.getLatitude(), aMapLocation.getLongitude()));
                location = aMapLocation;

                // 如果不设置标志位，此时再拖动地图时，它会不断将地图移动到当前的位置
                if (isFirstLoc) {
                    isFirstLoc = false;
                    //设置缩放级别
                    mapManager.moveCamera(CameraUpdateFactory.zoomTo(15));
                    //将地图移动到定位点
                    mapManager.moveCamera(CameraUpdateFactory.changeLatLng(new LatLng(aMapLocation.getLatitude(), aMapLocation.getLongitude())));
                    //点击定位按钮 能够将地图的中心移动到定位点
                    if (mListener != null) {
                        mListener.onLocationChanged(aMapLocation);
                    }
                }
            } else {
                //显示错误信息ErrCode是错误码，errInfo是错误信息，详见错误码表。
                Log.e("AmapError", "location Error, ErrCode:"
                        + aMapLocation.getErrorCode() + ", errInfo:"
                        + aMapLocation.getErrorInfo());
                if (!TextUtils.isEmpty(aMapLocation.getErrorInfo())&&aMapLocation.getErrorCode()==12) {
                    // ToastUtil.showToast(getApplicationContext(), aMapLocation.getErrorInfo());
                    permissionDialog = MCDialogManager.showMessage(this, "需要定位权限", "应用没有定位权限,设置定位权限后才能正常工作",
                            "立即去设置", "暂时不设置", R.drawable.svg_icon_prompt_big);
                    permissionDialog.setListener(new ItemListener() {
                        @Override
                        public void onItemClick(View listView, Object itemData, int tag) {
                            if (tag == MCDialogManager.TAG_RIGHT_BTN) {
                                Intent intent = new Intent();
                                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                Uri uri = Uri.fromParts("package", MainApplication.getInstance().getPackageName(), null);
                                intent.setData(uri);
                                startActivity(intent);
                            }
                        }
                    });
                }else{
                    Toast.makeText(getApplicationContext(), "定位失败", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    //----------------------------------------------RouteSearch.OnRouteSearchListener----------------------------------------------

    /**
     * 驾车路径规划结果
     *
     * @param result
     * @param errorCode
     */
    @Override
    public void onDriveRouteSearched(DriveRouteResult result, int errorCode) {
        //        mapManager.clear();
        if (errorCode == AMapException.CODE_AMAP_SUCCESS) {
            if (result != null && result.getPaths() != null) {
                if (result.getPaths().size() > 0) {
                    mDriveRouteResult = result;
                    final DrivePath drivePath = mDriveRouteResult.getPaths().get(0);
                    DrivingRouteOverLay drivingRouteOverlay = new DrivingRouteOverLay(MapBaseActivity.this, mapManager, drivePath, mDriveRouteResult.getStartPos(), mDriveRouteResult.getTargetPos(), null);
                    drivingRouteOverlay.setNodeIconVisibility(false);//设置节点marker是否显示
                    drivingRouteOverlay.setIsColorfulline(true);//是否用颜色展示交通拥堵情况，默认true
                    drivingRouteOverlay.addToMap();
                    drivingRouteOverlay.zoomToSpan();
                    onDrivePath(drivePath);
                }
            }
        }
    }

    @Override
    public void onBusRouteSearched(BusRouteResult busRouteResult, int i) {
    }

    @Override
    public void onWalkRouteSearched(WalkRouteResult walkRouteResult, int i) {
    }

    @Override
    public void onRideRouteSearched(RideRouteResult rideRouteResult, int i) {
    }
    //----------------------------------------------RouteSearch.OnRouteSearchListener----------------------------------------------

    //----------------------------------------------AMap.OnMarkerDragListener----------------------------------------------
    @Override
    public void onMarkerDragStart(Marker marker) {
    }

    @Override
    public void onMarkerDrag(Marker marker) {
    }

    @Override
    public void onMarkerDragEnd(Marker marker) {
    }
    //----------------------------------------------AMap.OnMarkerDragListener----------------------------------------------

    @Override
    public void onMapLoaded() {
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        return false;
    }

    protected void onDestroy() {
        super.onDestroy();
        mainMapView.onDestroy();
        if (mLocationClient != null) {
            mLocationClient.stopLocation();//停止定位
            mLocationClient.onDestroy();//销毁定位客户端。
        }
    }

    /**
     * 添加自定义marker
     *
     * @param latLng
     *         坐标位置.
     * @param layoutId
     *         布局ID.
     * @param data
     *         需要填充的数据.
     */
    public MarkerOptions addCustomMarker(final LatLng latLng, int layoutId, Object data) {

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("您的师傅在这");
        Bitmap bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ALPHA_8);
        View infoWindow = getLayoutInflater().inflate(layoutId, null);

        BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromBitmap(bitmap);
        markerOptions.icon(bitmapDescriptor);

        Marker marker = mapManager.addMarker(markerOptions);

        markerLayouts.put(marker, infoWindow);
        markerViews.put(markerOptions, infoWindow);

        if (data != null) {
            DriveBase.fillViewData(infoWindow, data, false);
        }

        marker.showInfoWindow();
        return markerOptions;
    }

    /**
     * 添加自定义marker
     *
     * @param latLng
     *         坐标位置.
     * @param layoutId
     *         布局ID.
     */
    public MarkerOptions addCustomMarker(final LatLng latLng, int layoutId) {
        return addCustomMarker(latLng, layoutId, null);
    }

    private interface OnMarkerIconLoadListener {
        void markerIconLoadingFinished(View view, BitmapDescriptor bitmapDescriptor);
    }

    protected interface OnMarkerViewChanged{
        void viewChanged(View view);
    }

    /**
     * 添加自定义marker
     *
     * @param latLng
     * @param url
     */
    protected MarkerOptions addCustomMarker(final LatLng latLng, String url, int layoutId, int iconId) {

        final MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("您的服务师傅在此处");

        if (!TextUtils.isEmpty(url)) {
            customizeMarkerIcon(url, layoutId, iconId, new OnMarkerIconLoadListener() {
                @Override
                public void markerIconLoadingFinished(View view, BitmapDescriptor bitmapDescriptor) {
                    Log.e("WaitServiceActivity", "markerIconLoadingFinished");
                    markerOptions.icon(bitmapDescriptor);
                    Marker marker = mapManager.addMarker(markerOptions);
                    markerOptionMap.put(markerOptions, marker);
                    markerLayouts.put(marker, view);
                    setCenterByMarkers();
                }
            });
        }
        return markerOptions;
    }

    protected Marker addMarkerByDrawable(LatLng latLng, int redId) {
        MarkerOptions markerOptions = new MarkerOptions();
        if (latLng != null) {
            markerOptions.position(latLng);
        }
        BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromResource(redId);
        markerOptions.icon(bitmapDescriptor);
        return mapManager.addMarker(markerOptions);
    }

    /**
     * 自定义带图标的marker
     *
     * @param url
     * @param listener
     */
    private void customizeMarkerIcon(String url, int layoutId, int iconId, final OnMarkerIconLoadListener listener) {
        final View markerView = LayoutInflater.from(this).inflate(layoutId, null);
        final AImageView icon = markerView.findViewById(iconId);
        RequestOptions requestOptions = new RequestOptions().centerCrop().dontAnimate()// 不使用Glide的默认动画
                .diskCacheStrategy(DiskCacheStrategy.NONE)//
                .priority(Priority.HIGH);

        Glide.with(this)
                .asBitmap()//强制指定加载静态图片
                .load(url)
                .apply(requestOptions)
                .listener(new RequestListener<Bitmap>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                        return false;
                    }
                    @Override
                    public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                        icon.setImageBitmap(resource);
                        BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromBitmap(ViewUtil.convertViewToBitmap(markerView));
                        listener.markerIconLoadingFinished(markerView, bitmapDescriptor);
                        return false;
                    }
                })
                .into(icon);

    }

    protected void markerChanged(MarkerOptions markerOptions, OnMarkerViewChanged changed){
        Marker marker = markerOptionMap.get(markerOptions);
        if (marker != null && changed != null){
            View view = markerLayouts.get(marker);
            changed.viewChanged(view);
            BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromBitmap(ViewUtil.convertViewToBitmap(view));
            marker.setIcon(bitmapDescriptor);
        }
    }

    //设置中心点---------------------------------------------------------------------------.
    public void setMapCenter(LatLng latLng) {
        mapManager.moveCamera(CameraUpdateFactory.zoomTo(15));
        mapManager.moveCamera(CameraUpdateFactory.changeLatLng(latLng));
    }

    public void setCenterByMarkers() {
        LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();//存放所有点的经纬度
        for (Marker marker : markerLayouts.keySet()) {
            boundsBuilder.include(marker.getPosition());
        }

        mapManager.moveCamera(CameraUpdateFactory.newLatLngBounds(boundsBuilder.build(), 50));//第二个参数为四周留空宽度
    }

    //-----------------------------------------------AMap.InfoWindowAdapter-------------------------------------------------
    @Override
    public View getInfoWindow(Marker marker) {
        return markerLayouts.get(marker);
    }

    @Override
    public View getInfoContents(Marker marker) {
        return null;
    }
    //-----------------------------------------------AMap.InfoWindowAdapter-------------------------------------------------
}
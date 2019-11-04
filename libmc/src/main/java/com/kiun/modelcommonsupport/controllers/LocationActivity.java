package com.kiun.modelcommonsupport.controllers;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amos.modulebase.utils.MathUtils;
import com.kiun.modelcommonsupport.MainApplication;
import com.kiun.modelcommonsupport.R;
import com.kiun.modelcommonsupport.adapter.ItemListener;
import com.kiun.modelcommonsupport.utils.MCDialogManager;

/**
 * Created by kiun_2007 on 2018/9/11.
 */

public abstract class LocationActivity extends BaseRequestAcitity {
    public AMapLocationClient mLocationClient = null;
    public AMapLocationClientOption mLocationOption = null;
    MCDialogManager permissionDialog = null;
    MCDialogManager localPermissDialog = null;

    public abstract void onLocationChanged(String cityName, String cityCode, double latitude, double longitude);

    public abstract Long getInterval();

    // 高德定位
    public void getPositioning() {

        String localCity = MainApplication.getInstance().getValue("localCity");
        String localCityCode = MainApplication.getInstance().getValue("localCityCode");
        String poiname = MainApplication.getInstance().getValue("poiname");
        String lastLatitude = MainApplication.getInstance().getValue("lastLatitude");
        String lastLongitude = MainApplication.getInstance().getValue("lastLongitude");

        if (!TextUtils.isEmpty(localCity) && !TextUtils.isEmpty(localCityCode)) {
            //onLocationChanged(localCity.replace("市", "") + "•" + poiname, localCityCode, MathUtils.str2Double(lastLatitude),  MathUtils.str2Double(lastLongitude));
            onLocationChanged(localCity.replace("市", "") , localCityCode, MathUtils.str2Double(lastLatitude),  MathUtils.str2Double(lastLongitude));
        }
        //        if()
        //初始化定位
        mLocationClient = new AMapLocationClient(this);
        //设置定位回调监听
        mLocationClient.setLocationListener(new AMapLocationListener() {
            @Override
            public void onLocationChanged(AMapLocation aMapLocation) {

                String cityCode = "";
                MainApplication.getInstance().save("location", String.format("%f,%f", aMapLocation.getLatitude(), aMapLocation.getLongitude()));
                if (!aMapLocation.getAdCode().isEmpty()) {
                    cityCode = aMapLocation.getAdCode().substring(0, 4) + "00";
                }

                if (!TextUtils.isEmpty(aMapLocation.getCity()) && !TextUtils.isEmpty(aMapLocation.getPoiName())) {
                    //LocationActivity.this.onLocationChanged(aMapLocation.getCity().replace("市", "") + "•" + aMapLocation.getPoiName(), cityCode, aMapLocation.getLatitude(), aMapLocation.getLongitude());
                    LocationActivity.this.onLocationChanged(aMapLocation.getCity().replace("市", "") , cityCode, aMapLocation.getLatitude(), aMapLocation.getLongitude());
                }
                if (!aMapLocation.getCity().isEmpty() && !cityCode.isEmpty()) {
                    MainApplication.getInstance().save("poiname", aMapLocation.getPoiName());
                    MainApplication.getInstance().save("localCity", aMapLocation.getCity());
                    MainApplication.getInstance().save("localCityCode", cityCode);
                    MainApplication.getInstance().save("lastLatitude", String.format("%f", aMapLocation.getLatitude()));
                    MainApplication.getInstance().save("lastLongitude", String.format("%f", aMapLocation.getLongitude()));
                }

                if (getInterval() < 0) {
                    mLocationClient.stopLocation();
                }
            }
        });
        //初始化AMapLocationClientOption对象
        mLocationOption = new AMapLocationClientOption();
        //获取一次定位结果：
        //该方法默认为false。
        mLocationOption.setInterval(10000);
        //设置是否返回地址信息（默认返回地址信息）
        mLocationOption.setNeedAddress(true);
        //设置是否允许模拟位置,默认为true，允许模拟位置
        mLocationOption.setMockEnable(true);
        //给定位客户端对象设置定位参数
        mLocationClient.setLocationOption(mLocationOption);
        //设置定位模式为AMapLocationMode.Hight_Accuracy，高精度模式。
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        //启动定位
        mLocationClient.startLocation();
    }

    public boolean isLocationEnabled() {
        int locationMode = 0;
        String locationProviders;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            try {
                locationMode = Settings.Secure.getInt(getContentResolver(), Settings.Secure.LOCATION_MODE);
            } catch (Settings.SettingNotFoundException e) {
                e.printStackTrace();
                return false;
            }
            return locationMode != Settings.Secure.LOCATION_MODE_OFF;
        } else {
            locationProviders = Settings.Secure.getString(getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
            return !TextUtils.isEmpty(locationProviders);
        }
    }

    public void startLocation() {

        if (!isLocationEnabled()) {
            localPermissDialog = MCDialogManager.showMessage(this, "需要打开定位", "手机没有开启定位无法正常工作",
                    "立即去打开", "暂时不打开", R.drawable.svg_icon_prompt_big);
            localPermissDialog.setListener(new ItemListener() {
                @Override
                public void onItemClick(View listView, Object itemData, int tag) {
                    if (tag == MCDialogManager.TAG_RIGHT_BTN) {
                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivityForResult(intent, 887);
                    }
                }
            });
            return;
        }

        //检测系统是否打开开启了地理定位权限
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permissionDialog = MCDialogManager.showMessage(this, "需要定位权限", "应用没有定位权限,设置定位权限才能正常工作",
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
            return;
        }
        getPositioning();
    }
}

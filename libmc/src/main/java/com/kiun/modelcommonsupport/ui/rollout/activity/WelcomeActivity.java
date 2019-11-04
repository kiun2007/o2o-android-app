package com.kiun.modelcommonsupport.ui.rollout.activity;

import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.amos.modulebase.mvp.baseclass.BaseActivity;
import com.amos.modulebase.mvp.baseclass.BaseView;
import com.amos.modulebase.utils.IntentUtil;
import com.amos.modulebase.utils.LogUtil;
import com.amos.modulebase.utils.SystemUtil;
import com.kiun.modelcommonsupport.R;

/**
 * 欢迎loading界面
 * <p>
 * <br> Author: 叶青
 * <br> Version: 1.0.0
 * <br> Date: 2016年12月11日
 * <br> Copyright: Copyright © 2016 xTeam Technology. All rights reserved.
 */
public class WelcomeActivity extends BaseActivity implements BaseView {

    private Button btn_finish;
    private ImageView img_welcome;

    @Override
    public void initVP() {
        mvpView = this;
    }


    @Override
    public int getContentViewId() {
        return R.layout.activity_welcome;
    }

    @Override
    public void findViews() {
        btn_finish = findViewByIds(R.id.btn_finish);
        img_welcome = findViewByIds(R.id.img_welcome);
        if (getPackageName().contains("master")) {
            img_welcome.setImageResource(R.drawable.img_welcome_master);
        } else {
            img_welcome.setImageResource(R.drawable.img_welcome_new);
        }
    }

    @Override
    public void init(Bundle bundle) {
        handler.postDelayed(runnable, 0);

        String message = "硬件制造商 : " + android.os.Build.MANUFACTURER + "\n" +//Meizu
                "手机制造商 : " + android.os.Build.PRODUCT + "\n" +//meizu_M5 Note
                "手机CPU架构: " + android.os.Build.CPU_ABI + "\n" +//arm64-v8a
                "手机型号   : " + android.os.Build.MODEL + "\n" +//M5 Note
                "手机系统定制商 : " + android.os.Build.BRAND + "\n" +//Meizu 编译厂商
                "手机系统版本   : " + android.os.Build.VERSION.RELEASE + "\n" +//6.0
                "手机系统版本SDK: " + android.os.Build.VERSION.SDK_INT + "\n" +//23

                "获取网络运营商  : " + SystemUtil.getCarrier() + "\n" +//中国电信,中国移动,中国联通
                "产品序列号Serial: " + android.os.Build.SERIAL + "\n" +//621QECQ93V7XD
                "UUID (IMEI)    : " + SystemUtil.getUUID() + "\n" +//C54B6C5F813DF892621QECQ93V7XD
                "屏幕密度        : " + SystemUtil.getDensity() + "\n" //XXHDPI

                //+
                //"硬件信息: " + android.os.Build.HARDWARE + "\n" +//mt6755
                //"设备参数: " + android.os.Build.DEVICE + "\n" +//M5Note
                //"主板名称: " + android.os.Build.BOARD + "\n" +//M5 Note
                //"获取系统启动程序版本号: " + android.os.Build.BOOTLOADER + "\n" +//unknown
                //"构建标识: " + android.os.Build.FINGERPRINT + "\n" +//Meizu/M1621/M1621:6.0/MRA58K/1495809194:user/release-keys
                //"显示屏参数: " + android.os.Build.DISPLAY + "\n" +//Flyme 6.1.0.0A
                //"获取编译服务器主机: " + android.os.Build.HOST + "\n" +//Mz-Builder-l3
                //"获取描述Build的标签: " + android.os.Build.TAGS + "\n" +//release-keys
                //"获取系统编译时间: " + android.os.Build.TIME + "\n" +//1495809607000
                //"获取系统编译作者: " + android.os.Build.USER + "\n" +//flyme
                //"获取开发代号: " + android.os.Build.VERSION.CODENAME + "\n" +//REL
                //"获取源码控制版本号: " + android.os.Build.VERSION.INCREMENTAL + "\n" +//1495809194
                //"修改版本列表: " + android.os.Build.ID + "\n" //MRA58K
                ;
        LogUtil.i(message);
    }

    @Override
    public void widgetListener() {
        btn_finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (recLen != -1) {
                    recLen = -1;
                    getLoginInfo();
                }
            }
        });
    }

    /** 倒计时 */
    private int recLen = 2;
    private Handler handler = new Handler();
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (recLen > 1) {
                recLen = recLen - 1;
                handler.postDelayed(runnable, 1000);
                btn_finish.setText("跳过" + String.valueOf(recLen) + "s");
            } else if (recLen == 1) {
                recLen = -1;
                getLoginInfo();
            }
        }
    };

    private void getLoginInfo() {
        try {
            if (getPackageName().contains("master")) {
                IntentUtil.gotoActivityToTopAndFinish(this, Class.forName("com.xzxx.decorate.o2o.master.MainActivity"));
            } else {
                IntentUtil.gotoActivityToTopAndFinish(this, Class.forName("com.xzxx.decorate.o2o.consumer.MainActivity"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
        handler = null;
        super.onDestroy();
    }


    // ***********************************返回键事件处理*********************************
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            if (event.getAction() == KeyEvent.ACTION_DOWN && event.getRepeatCount() == 0) {
                // 要执行的事件
                recLen = -1;
                finish();
            }
            return true;
        }
        return super.dispatchKeyEvent(event);
    }
}

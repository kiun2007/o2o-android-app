package com.xzxx.decorate.o2o.ui;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import com.kiun.modelcommonsupport.controllers.BaseRequestAcitity;
import com.kiun.modelcommonsupport.utils.MCDialogManager;
import com.xzxx.decorate.o2o.consumer.R;
import com.kiun.modelcommonsupport.utils.StatusBarUtils;

/**
 * Created by zf on 2018/8/6.
 */
public abstract class BaseActivity extends BaseRequestAcitity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
}

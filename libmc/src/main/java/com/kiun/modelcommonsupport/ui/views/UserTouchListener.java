package com.kiun.modelcommonsupport.ui.views;

import android.view.MotionEvent;
import android.view.View;

/**
 * DeviceService
 * popchain
 * @author kiun_2007 on 2018/3/31.
 * Copyright (c) 2017-2018 The Popchain Core Developers
 */

public class UserTouchListener implements View.OnTouchListener {
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if(event.getAction() == MotionEvent.ACTION_DOWN){
            v.setAlpha(0.5f);
        }else{
            v.setAlpha(1f);
        }
        return false;
    }
}

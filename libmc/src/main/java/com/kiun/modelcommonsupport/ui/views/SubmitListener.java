package com.kiun.modelcommonsupport.ui.views;

import android.view.View;
import com.kiun.modelcommonsupport.network.MethodType;

/**
 * Created by kiun_2007 on 2018/4/17.
 * 提交按钮的事件监听.
 */
public interface SubmitListener extends EventListener {
    void onSubmit(View v, String url, MethodType method);
}
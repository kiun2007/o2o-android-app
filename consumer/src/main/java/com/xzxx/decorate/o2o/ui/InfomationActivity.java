package com.xzxx.decorate.o2o.ui;

import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.kiun.modelcommonsupport.controllers.BaseRequestAcitity;
import com.kiun.modelcommonsupport.network.MCBaseRequest;
import com.xzxx.decorate.o2o.consumer.R;

/**
 * Created by kiun_2007 on 2018/9/12.
 */

public class InfomationActivity extends BaseRequestAcitity {

    WebView webView;
    @Override
    public void onDataChanged(Object data, MCBaseRequest request) {
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_information;
    }

    @Override
    public void initView() {
        webView = findViewById(R.id.webView);
        String title = getIntent().getStringExtra("title");
        if (title != null){
            setTitle(title);
        }

        webView.setWebChromeClient(new WebChromeClient());
        webView.setWebViewClient(new WebViewClient());

        String url = getIntent().getStringExtra("url");
        if (url != null){
            webView.loadUrl(url);
        }
    }
}

package com.kiun.modelcommonsupport.controllers.authority;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;

import com.kiun.modelcommonsupport.R;
import com.kiun.modelcommonsupport.camera.util.LogUtil;
import com.kiun.modelcommonsupport.controllers.BaseRequestAcitity;
import com.kiun.modelcommonsupport.network.MCBaseRequest;


/**
 * 所有静态网页加载界面的基类
 * <p>
 * <br> Author: 叶青
 * <br> Version: 1.0.0
 * <br> Date: 2016年12月11日
 * <br> Copyright: Copyright © 2016 xTeam Technology. All rights reserved.
 */
public class WebBaseActivity extends BaseRequestAcitity {

    /** 显示html 页面 */
    protected WebView webView;
    /** 加载链接---网页全路径 */
    protected String url = "";
    /** 加载链接---标题 */
    protected String title = "";

    @Override
    public int getLayoutId() {
        return R.layout.layout_web_base;
    }

    @Override
    public void initView() {
        findViews();
        init(getIntent().getExtras());
    }

    public void findViews() {
        // 在使用了这个方式后，基本上 90% 的 WebView 内存泄漏的问题可以解决
        //        webView = new CustomWebView(getApplicationContext());// webview中有弹出对话框是。则上下文不能使用getApplicationContext()
        webView = new WebView(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        webView.setLayoutParams(params);
        LinearLayout view_parent = findViewById(R.id.view_parent);
        view_parent.addView(webView);
    }

    public void init(Bundle bundle) {
        if (bundle != null) {
            url = bundle.getString("url", ""); //url
            title = bundle.getString("title", "");
            if (!TextUtils.isEmpty(title)) {
                getNavigatorBar().setTitle(title);
            }
        }

        readHtmlFormAssets();
        loadUrl();
    }

    protected void loadUrl() {
        webView.loadUrl(url);
    }

    @SuppressLint("SetJavaScriptEnabled")
    protected void readHtmlFormAssets() {
        WebSettings webSettings = webView.getSettings();


        //设置自适应屏幕，两者合用
        webSettings.setUseWideViewPort(true);// 将图片调整到适合webview的大小
        webSettings.setLoadWithOverviewMode(true);// 缩放至屏幕的大小
        webSettings.setBlockNetworkImage(false);//true 阻止图片网络数据
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);//支持通过JS打开新窗口
        webSettings.setJavaScriptEnabled(true);//如果访问的页面中要与Javascript交互，则webview必须设置支持Javascript
        webSettings.setSavePassword(false);     //关闭密码保存提醒功能
        webSettings.setAllowFileAccess(false);   //设置不可以访问文件，防止越权访问，跨域等安全问题：
        webSettings.setLoadsImagesAutomatically(true); //支持自动加载图片
        webSettings.setDefaultTextEncodingName("utf-8");//设置编码格式

        // 设置缓存模式
        //LOAD_CACHE_ONLY: 不使用网络，只读取本地缓存数据
        //LOAD_DEFAULT: （默认）根据cache-control决定是否从网络上取数据。
        //LOAD_NO_CACHE: 不使用缓存，只从网络获取数据.
        //LOAD_CACHE_ELSE_NETWORK，只要本地有，无论是否过期，或者no-cache，都使用缓存中的数据。
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);// 无缓存

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {//域控制不严格漏洞
            webSettings.setAllowFileAccessFromFileURLs(false);      //true 允许通过file域url中的Javascript读取其他本地文件
            webSettings.setAllowUniversalAccessFromFileURLs(false); //true 允许通过file域url中的Javascript访问其他的源
        }

        webSettings.setPluginState(WebSettings.PluginState.ON);//让WebView支持播放插件

        webSettings.setDomStorageEnabled(true); // 开启 DOM storage API 功能
        webSettings.setDatabaseEnabled(true);   //开启 database storage API 功能
        webSettings.setAppCacheEnabled(true);//开启 Application Caches 功能
        //        String cacheDirPath = getFilesDir().getAbsolutePath() + APP_CACAHE_DIRNAME;
        //        webSettings.setAppCachePath(cacheDirPath); //设置  Application Caches 缓存目录we

        // 设置可以支持缩放
        webSettings.setSupportZoom(true); //支持缩放，默认为true。是下面那个的前提。
        // 设置出现缩放工具
        webSettings.setBuiltInZoomControls(true); //设置内置的缩放控件。若为false，则该WebView不可缩放
        webSettings.setDisplayZoomControls(false); // 隐藏原生的缩放控件
        //        webSettings.setTextZoom(70);// 字体缩放比例
        // 自适应屏幕
        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NORMAL);

        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.removeAllCookie();
        webView.canGoBack();
        webView.clearCache(true);

        deleteDatabase("webview.db");
        deleteDatabase("webviewCache.db");
        webView.stopLoading();

        // 安全开发建议
        webView.removeJavascriptInterface("searchBoxJavaBridge_");//移除该JavaScript接口
        webView.removeJavascriptInterface("accessibility");
        webView.removeJavascriptInterface("accessibilityTraversal");
        //        webView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);// 不启用硬件加速
        webView.setLayerType(View.LAYER_TYPE_HARDWARE, null);// 启用硬件加速


        webView.setWebChromeClient(iniWebChromeClient()); //此方法必须放在initWebViewClient前面
        webView.setWebViewClient(initWebViewClient());
    }

    /**
     * 初始化 WebViewClient
     */
    protected WebViewClient initWebViewClient() {
        return new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                LogUtil.i(url);
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);
                //            if (errorCode < 0) {
                //                extracted();
                //            }
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                LogUtil.i(url);
                super.onPageFinished(view, url);
            }
        };
    }

    /**
     * 初始化 WebChromeClient
     */
    protected WebChromeClient iniWebChromeClient() {
        return new WebChromeClient() {
            @Override
            public void onReceivedTitle(WebView view, String titleStr) {
                super.onReceivedTitle(view, titleStr);
                LogUtil.i("TITLE=" + titleStr);
                if (TextUtils.isEmpty(title)) {
                    if (!TextUtils.isEmpty(titleStr) && !titleStr.equals("about:blank")) {
                        getNavigatorBar().setTitle(titleStr);
                    }
                }
            }
        };
    }

    @Override
    public void onPause() {
        if (null != webView) {
            webView.onPause();
        }
        super.onPause();
    }

    public void onResume() {
        super.onResume();
        if (null != webView) {
            webView.onResume();
        }
    }

    @Override
    public void onDestroy() {
        try {
            if (null != webView) {
                ViewGroup viewGroup = (ViewGroup) webView.getParent();
                if (viewGroup != null) {
                    viewGroup.removeView(webView);
                }
                webView.removeAllViews();
                webView.loadUrl("about:blank");
                webView.stopLoading();
                webView.setWebChromeClient(null);
                webView.setWebViewClient(null);
                webView.clearHistory();
                webView.destroy();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onDestroy();
    }

    @Override
    public void onDataChanged(Object data, MCBaseRequest request) {

    }
}


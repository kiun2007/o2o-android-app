package com.xzxx.decorate.o2o.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.text.TextUtils;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.amos.modulebase.utils.IntentUtil;
import com.kiun.modelcommonsupport.MainApplication;
import com.kiun.modelcommonsupport.camera.util.LogUtil;
import com.kiun.modelcommonsupport.controllers.authority.LoginActivity;
import com.kiun.modelcommonsupport.data.MCUserInfo;
import com.kiun.modelcommonsupport.network.MCBaseRequest;
import com.xzxx.decorate.o2o.consumer.R;

import org.json.JSONObject;

/**
 * Created by kiun_2007 on 2018/8/10.
 */

public class ItemContentActivity extends BaseActivity {

    String secondItemName = null;
    String secondItemId = null;
    String secondItemUrl = null;
    String secondItemHTMLUrl = null;
    WebView webView = null;

    @Override
    public int getLayoutId() {
        return R.layout.activity_item_content;
    }

    @Override
    public void initView() {
        webView = findViewById(R.id.webView);
        secondItemName = getIntent().getStringExtra("secondItemName");
        secondItemId = getIntent().getStringExtra("secondItemId");
        secondItemHTMLUrl = getIntent().getStringExtra("secondItemHTMLUrl");
        setTitle(secondItemName);
        //        PredictOrderRequest request = new PredictOrderRequest();
        //        request.secondItemId = secondItemId;
        //        request.cityCode = "470100";
        //        commitRequest(request);
        readHtmlFormAssets();
        if (!TextUtils.isEmpty(secondItemHTMLUrl)) {
            webView.loadUrl(secondItemHTMLUrl);
        }
    }

    //{"appointmentName":"","appointmentPhone":"","appointmentCity":"","appointmentCityName":"","appointmentAddress":"","secondItemId":"201808081633670046576939","secondItemName":"电脑安装","secondItemUrl":"http:\/\/pawx04z5h.bkt.clouddn.com\/2018\/08\/1eb4ae6b-64ef-48b6-9970-16fe07a3f191.jpg","location":"","latitudeLongitude":""}
    @Override
    public void onDataChanged(Object data, MCBaseRequest request) {
        if (data instanceof JSONObject) {
            secondItemUrl = ((JSONObject) data).optString("secondItemUrl");
        }
    }

    public void onNextClick(View view) {
        MCUserInfo userInfo = MainApplication.getInstance().getUserInfo(false);
        if (userInfo != null) {
            Intent intent = new Intent(this, OrderSubmitAvtivity.class);
            intent.putExtra("secondItemName", secondItemName);
            intent.putExtra("secondItemId", secondItemId);
            if (secondItemUrl != null) {
                intent.putExtra("secondItemIcon", secondItemUrl);
            }
            startActivity(intent);
            //            finish();
        } else {
            IntentUtil.gotoActivity(ItemContentActivity.this, LoginActivity.class);
        }
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


        //        webView.setWebChromeClient(iniWebChromeClient()); //此方法必须放在initWebViewClient前面
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
}

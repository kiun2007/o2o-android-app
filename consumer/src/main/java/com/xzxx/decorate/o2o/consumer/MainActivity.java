package com.xzxx.decorate.o2o.consumer;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.FragmentTabHost;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.TextView;

import com.amos.modulebase.utils.KeyboardUtil;
import com.kiun.modelcommonsupport.MainApplication;
import com.kiun.modelcommonsupport.controllers.LocationActivity;
import com.kiun.modelcommonsupport.controllers.authority.LoginActivity;
import com.kiun.modelcommonsupport.network.MCBaseRequest;


public class MainActivity extends LocationActivity implements TabHost.OnTabChangeListener {

    private FragmentTabHost fragmentTabHost;

    private int texts[] = {R.string.home, R.string.classify, R.string.order, R.string.profile};
    private int imageButton[] = {R.drawable.selector_home, R.drawable.selector_classify, R.drawable.selector_order, R.drawable.selector_personal};
    private Class fragmentArray[] = {HomeFragment.class, ClassifyFragment.class, OrderFragment.class, PersonalFragment.class};


    @Override
    public int getLayoutId() {
        return R.layout.layout_activity_main;
    }

    @Override
    public void initView() {
        MainApplication.acitity = this;
        fragmentTabHost = findViewById(android.R.id.tabhost);
        KeyboardUtil.hideKeyBord(fragmentTabHost);
        fragmentTabHost.setup(this, getSupportFragmentManager(), R.id.home_container);
        fragmentTabHost.setOnTabChangedListener(this);
        for (int i = 0; i < texts.length; i++) {
            TabHost.TabSpec spec = fragmentTabHost.newTabSpec(getString(texts[i])).setIndicator(getView(i));
            fragmentTabHost.addTab(spec, fragmentArray[i], null);
        }
        fragmentTabHost.getTabWidget().setShowDividers(LinearLayout.SHOW_DIVIDER_NONE);
        fragmentTabHost.setCurrentTab(0);

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(MainApplication.LOGOUT_ACTION);
        registerReceiver(receiver, intentFilter);
    }

    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(MainApplication.LOGOUT_ACTION)) {
                new Handler(Looper.getMainLooper()) {
                    @Override
                    public void handleMessage(Message msg) {
                        fragmentTabHost.setCurrentTab(0);
                        ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
                        String activityName = am.getRunningTasks(1).get(0).topActivity.getClassName();

                        if (!activityName.contains("LoginActivity")) {
                            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                        }
                    }
                }.sendEmptyMessageDelayed(0, 500);
            }
        }
    };

    @Override
    protected void onDestroy() {
        try {
            unregisterReceiver(receiver);
        } catch (Exception e) {
        }
        super.onDestroy();
    }

    public void setTabHostFragment(int tab) {
        fragmentTabHost.setCurrentTab(tab);
    }

    @Override
    public void onTabChanged(String tabId) {
        if (tabId.equals("首页") || tabId.equals("分类")) {
            return;
        }
        if (MainApplication.getInstance().getUserInfo(true) == null) {
            setTabHostFragment(0);
        }
    }

    private View getView(int i) {
        View view = View.inflate(this, R.layout.tabcontent, null);
        ImageView imageView = view.findViewById(R.id.tab_image);
        TextView textView = view.findViewById(R.id.tab_text);
        imageView.setImageResource(imageButton[i]);
        textView.setText(getString(texts[i]));
        return view;
    }

    @Override
    public void onDataChanged(Object data, MCBaseRequest request) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            startLocation();
        }
        try {
            KeyboardUtil.hideKeyBord(fragmentTabHost);
            ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onLocationChanged(String cityName, String cityCode, double latitude, double longitude) {
        // onLocationChanged

        Intent intent = new Intent();
        intent.putExtra("cityName",cityName);
        intent.putExtra("cityCode",cityCode);
        intent.setAction("refresh_city");
        sendBroadcast(intent);
    }

    @Override
    public Long getInterval() {
        return 1L;
    }
}
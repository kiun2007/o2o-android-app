package com.kiun.modelcommonsupport.controllers;

import android.content.Intent;
import android.graphics.Color;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.amos.modulebase.utils.ScreenUtil;
import com.kiun.modelcommonsupport.R;
import com.kiun.modelcommonsupport.network.MCBaseRequest;
import com.kiun.modelcommonsupport.network.requests.HistoryCityRequest;
import com.kiun.modelcommonsupport.network.requests.OpenedCityRequest;
import com.kiun.modelcommonsupport.ui.views.FlowLayout;
import com.kiun.modelcommonsupport.utils.BasicUtils;

import org.json.JSONArray;

/**
 * 选择服务城市列表页面
 * Created by zf on 2018/6/16.
 */
public class SelectCityActivity extends LocationActivity implements View.OnClickListener {

    private FlowLayout mFlowLayoutHistoryCity;
    private FlowLayout mFlowLayoutOpenCity;
    private Button locationButton;
    private Button localCityNameButton;

    JSONArray allCitys;

    @Override
    public int getLayoutId() {
        return R.layout.layout_select_city;
    }


    @Override
    public void initView() {
        mFlowLayoutHistoryCity = (FlowLayout) findViewById(R.id.flow_layout_history_city);
        mFlowLayoutOpenCity = (FlowLayout) findViewById(R.id.flow_layout_open_city);
        locationButton = findViewById(R.id.locationButton);
        localCityNameButton = findViewById(R.id.localCityNameButton);
        localCityNameButton.setTag("");
        localCityNameButton.setOnClickListener(this);
        locationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                locationButton.setText("正在定位...");
                startLocation();
            }
        });

        commitRequest(new OpenedCityRequest());
        commitRequest(new HistoryCityRequest());
    }

    @Override
    protected void onResume() {
        super.onResume();
        startLocation();
    }

    private void initViewGroup(JSONArray citys, FlowLayout flowLayout) {

        flowLayout.removeAllViews();
        for (int i = 0; i < citys.length(); i++) {
            //            int ranHeight = BasicUtils.dip2px(this, 30);
            int ranWidth = (ScreenUtil.getScreenWidthPx() - ScreenUtil.dip2px(40)) / 3 - ScreenUtil.dip2px(15);
            ViewGroup.MarginLayoutParams lp = new ViewGroup.MarginLayoutParams(ranWidth, ViewGroup.LayoutParams.WRAP_CONTENT);
            lp.setMargins(BasicUtils.dip2px(this, 7), 0, BasicUtils.dip2px(this, 7), 0);
            TextView tv = new TextView(this);
            //            tv.setPadding(BasicUtils.dip2px(this, 15), 0, BasicUtils.dip2px(this, 15), 0);
            tv.setTextColor(Color.parseColor("#000000"));
            tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 17);
            tv.setText(citys.optJSONObject(i).optString("name"));
            tv.setTag(citys.optJSONObject(i).optString("code"));
            tv.setGravity(Gravity.CENTER_VERTICAL);
            tv.setLines(1);
            tv.setOnClickListener(this);
            flowLayout.addView(tv, lp);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    //[{"code":"340300","name":"蚌埠市"},{"code":"340400","name":"淮南市"},{"code":"340500","name":"马鞍山市"},{"code":"340600","name":"淮北市"},{"code":"340700","name":"铜陵市"},{"code":"340800","name":"安庆市"},{"code":"341000","name":"黄山市"},
    // {"code":"341100","name":"滁州市"},{"code":"341200","name":"阜阳市"},{"code":"341800","name":"宣城市"},{"code":"440300","name":"深圳市"},{"code":"340200","name":"芜湖市"}]
    @Override
    public void onDataChanged(Object data, MCBaseRequest request) {
        if (request instanceof OpenedCityRequest && data instanceof JSONArray) {
            initViewGroup((JSONArray) data, mFlowLayoutOpenCity);
            allCitys = (JSONArray) data;
        }
        if (request instanceof HistoryCityRequest && data instanceof JSONArray) {
            initViewGroup((JSONArray) data, mFlowLayoutHistoryCity);
        }
    }

    @Override
    public void onClick(View view) {

        if (view.getTag().equals("")) {
            return;
        }

        Intent intent = new Intent();
        intent.putExtra("code", view.getTag().toString());
        intent.putExtra("name", ((TextView) view).getText());
        setResult(11, intent);
        finish();
    }

    @Override
    public void onLocationChanged(String cityName, String cityCode, double latitude, double longitude) {

        locationButton.setText("重新定位");
        if (cityCode.isEmpty()) {
            localCityNameButton.setText("定位失败");
            return;
        }

        for (int i = 0; allCitys != null && (i < allCitys.length()); i++) {
            if (allCitys.optJSONObject(i).optString("code").equals(cityCode)) {
                localCityNameButton.setText(allCitys.optJSONObject(i).optString("name"));
                localCityNameButton.setTag(cityCode);
                return;
            }
        }
        localCityNameButton.setText(cityName+"(未开通)");
    }

    @Override
    public Long getInterval() {
        return -1l;
    }
}
package com.xzxx.decorate.o2o.master;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import com.kiun.modelcommonsupport.adapter.ItemListener;
import com.kiun.modelcommonsupport.controllers.BaseRequestAcitity;
import com.kiun.modelcommonsupport.network.MCBaseRequest;
import com.kiun.modelcommonsupport.ui.views.AListView;

import org.json.JSONArray;
import org.json.JSONException;

/**
 * Created by zf on 2018/7/13.
 * 周边搜索
 */
public class PoiSearchActivity extends BaseRequestAcitity {


    AListView poi_list;

    @Override
    public int getLayoutId() {
        return R.layout.activity_poi_search;
    }

    @Override
    public void onLeftClick() {
        finish();
    }

    @Override
    public void initView() {
        poi_list = findViewById(R.id.poi_list);

        final Bundle bundle = this.getIntent().getExtras();
        if (bundle != null) {
            String content = bundle.getString("data", "");
            if (!TextUtils.isEmpty(content)) {
                try {
                    poi_list.fill(new JSONArray(content));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        poi_list.setItemListener(new ItemListener() {
            @Override
            public void onItemClick(View listView, Object itemData, int tag) {
                Bundle bundle1 = new Bundle();
                bundle1.putString("data", itemData.toString());
                setResult(RESULT_OK, new Intent().putExtras(bundle1));
                finish();
            }
        });
    }

    @Override
    public void onDataChanged(Object data, MCBaseRequest request) {

    }
}

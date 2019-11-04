package com.xzxx.decorate.o2o.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.model.CameraPosition;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.Marker;
import com.amap.api.services.core.AMapException;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.kiun.modelcommonsupport.controllers.MapBaseActivity;
import com.kiun.modelcommonsupport.controllers.SelectCityActivity;
import com.kiun.modelcommonsupport.network.MCBaseRequest;
import com.kiun.modelcommonsupport.network.requests.OpenedCityRequest;
import com.xzxx.decorate.o2o.adapter.SearchResultAdapter;
import com.xzxx.decorate.o2o.consumer.R;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

/**
 * 地图选点
 * Created by zf on 2018/8/16.
 */
public class MapSearchActivity extends MapBaseActivity implements PoiSearch.OnPoiSearchListener, View.OnClickListener {

    private ListView listView;
    private ListView listview_full_search;
    private EditText searchText;
    private int currentPage = 0;
    private PoiSearch.Query query;// Poi查询条件类
    private PoiSearch poiSearch;
    private List<PoiItem> poiItems;// poi数据
    private List<PoiItem> resultData;
    private SearchResultAdapter searchResultAdapter;
    private SearchResultAdapter searchFullResultAdapter;
    private LinearLayout ll_map_search;
    private ImageView image_search_close;
    private TextView selectCityText;
    private JSONArray allCitys;
    Marker localMarker;
    String city = "深圳";

    @Override
    protected int getMapViewId() {
        return R.id.map;
    }
    @Override
    protected boolean isLocation() {
        return true;
    }
    @Override
    public int getLayoutId() {
        return R.layout.layout_map_search_activity;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);

        listView = findViewById(R.id.listview);
        listview_full_search = findViewById(R.id.listview_full_search);
        ll_map_search = findViewById(R.id.ll_map_search);
        image_search_close = findViewById(R.id.id_search_close);
        searchResultAdapter = new SearchResultAdapter(MapSearchActivity.this);
        searchFullResultAdapter = new SearchResultAdapter(MapSearchActivity.this);
        listView.setAdapter(searchResultAdapter);
        listview_full_search.setAdapter(searchFullResultAdapter);
        listView.setOnItemClickListener(onItemClickListener);
        listview_full_search.setOnItemClickListener(onItemClickListener);
        image_search_close.setOnClickListener(this);
        selectCityText = findViewById(R.id.selectCityText);
        selectCityText.setOnClickListener(this);

        resultData = new ArrayList<>();
        searchText = findViewById(R.id.keyWord);

        searchText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                ll_map_search.setVisibility(hasFocus ? View.GONE : View.VISIBLE);
                listview_full_search.setVisibility(hasFocus ? View.VISIBLE : View.GONE);
            }
        });

        searchText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    doSearchQuery(null, v.getText().toString());
                }
                return false;
            }
        });

        mapManager.setOnCameraChangeListener(new AMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition cameraPosition) {
            }
            @Override
            public void onCameraChangeFinish(CameraPosition cameraPosition) {
                doSearchQuery(new LatLonPoint(cameraPosition.target.latitude, cameraPosition.target.longitude));
            }
        });

        localMarker = addMarkerByDrawable(null, R.drawable.icon_situ);
        commitRequest(new OpenedCityRequest());

        new Handler(Looper.getMainLooper()){
            @Override
            public void handleMessage(Message msg) {
                String latlngStr = getIntent().getStringExtra("latlng");
                if(latlngStr != null && !latlngStr.isEmpty()){
                    LatLng curLatLng = toLatLng(latlngStr);
                    setMapCenter(curLatLng);
                }
            }
        }.sendEmptyMessageDelayed(0, 1000);
    }

    public void initView() {
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 999 && requestCode != 0){
            try {
                selectCityText.setText(data.getStringExtra("name"));
                city = data.getStringExtra("name");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onLocation(LatLng latLng) {
        doSearchQuery(new LatLonPoint(latLng.latitude, latLng.longitude));
        localMarker.setPosition(latLng);
        setMapCenter(latLng);
    }

    protected void doSearchQuery(LatLonPoint searchLatlonPoint){
        doSearchQuery(searchLatlonPoint, "");
    }

    /**
     * 开始进行poi搜索
     */
    protected void doSearchQuery(LatLonPoint searchLatlonPoint, String searchKey) {
        currentPage = 0;
        query = new PoiSearch.Query(searchKey, "", searchLatlonPoint == null ? city : "");// 第一个参数表示搜索字符串，第二个参数表示poi搜索类型，第三个参数表示poi搜索区域（空字符串代表全国）
        query.setCityLimit(true);
        query.setPageSize(20);
        query.setPageNum(currentPage);

        poiSearch = new PoiSearch(this, query);
        poiSearch.setOnPoiSearchListener(this);
        if (searchLatlonPoint != null) {
            poiSearch.setBound(new PoiSearch.SearchBound(searchLatlonPoint, 1000, true));//
        }
        poiSearch.searchPOIAsyn();
    }

    /**
     * POI搜索结果回调
     *
     * @param poiResult  搜索结果
     * @param resultCode 错误码
     */
    @Override
    public void onPoiSearched(PoiResult poiResult, int resultCode) {
        if (resultCode == AMapException.CODE_AMAP_SUCCESS) {
            if (poiResult != null && poiResult.getQuery() != null) {
                if (poiResult.getQuery().equals(query)) {
                    poiItems = poiResult.getPois();
                    if (poiItems != null && poiItems.size() > 0) {
                        updateListview(poiItems);
                    } else {
                        Toast.makeText(MapSearchActivity.this, "无搜索结果", Toast.LENGTH_SHORT).show();
                    }
                }
            } else {
                Toast.makeText(MapSearchActivity.this, "无搜索结果", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * 更新列表中的item
     *
     * @param poiItems
     */
    private void updateListview(List<PoiItem> poiItems) {
        resultData.clear();
        searchResultAdapter.setSelectedPosition(0);
        resultData.addAll(poiItems);

        if (listView.getVisibility() == View.VISIBLE) {
            searchResultAdapter.setData(resultData);
            searchResultAdapter.notifyDataSetChanged();
        }

        if (listview_full_search.getVisibility() == View.VISIBLE) {
            searchFullResultAdapter.setData(resultData);
            searchFullResultAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onPoiItemSearched(PoiItem poiItem, int i) {
    }

    AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            PoiItem poiItem = (PoiItem) searchResultAdapter.getItem(position);
            if(!isOpenCity(poiItem.getCityName())){
                Toast.makeText(MapSearchActivity.this, "城市未开通，地址无法作为服务地址", Toast.LENGTH_SHORT).show();
                return;
            }
            LatLng curLatlng = new LatLng(poiItem.getLatLonPoint().getLatitude(), poiItem.getLatLonPoint().getLongitude());
            Intent intent = new Intent();
            intent.putExtra("poiItem", poiItem);
            setResult(1, intent);
            finish();
        }
    };

    @Override
    public void onDataChanged(Object data, MCBaseRequest request) {
        if(request instanceof OpenedCityRequest && data instanceof JSONArray){
            allCitys = (JSONArray) data;
        }
    }

    public boolean isOpenCity(String cityName){
        for(int i = 0; allCitys != null && (i < allCitys.length()); i ++){
            if(allCitys.optJSONObject(i).optString("name").equals(cityName)){
                return true;
            }
        }
        return false;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.id_search_close:
                searchText.getText().clear();
                ll_map_search.setVisibility(View.VISIBLE);
                listview_full_search.setVisibility(View.GONE);
                break;
            case R.id.selectCityText:
                Intent intent = new Intent(this, SelectCityActivity.class);
                startActivityForResult(intent, 999);
                break;
        }
    }

    protected boolean isLocationButton() {
        return true;
    }
}

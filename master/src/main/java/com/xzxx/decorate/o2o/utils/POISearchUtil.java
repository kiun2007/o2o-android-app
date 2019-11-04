package com.xzxx.decorate.o2o.utils;

import android.content.Context;

import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class POISearchUtil {

    public void startSearch(Context context, double latitude, double longitude, final SearchCallBack callBack) {

        PoiSearch.Query query = new PoiSearch.Query("", "生活服务", "");
        query.setPageSize(20);
        PoiSearch search = new PoiSearch(context, query);
        search.setBound(new PoiSearch.SearchBound(new LatLonPoint(latitude, longitude), 10000));
        search.setOnPoiSearchListener(new PoiSearch.OnPoiSearchListener() {
            @Override
            public void onPoiSearched(PoiResult result, int i) {
                //                PoiSearch.Query query = result.getQuery();
                ArrayList<PoiItem> pois = result.getPois();
                JSONArray jsonArray = new JSONArray();
                for (PoiItem poi : pois) {
                    String name = poi.getCityName();
                    String snippet = poi.getSnippet();
                    //                    LocationInfo info = new LocationInfo();
                    //                    info.setAddress(snippet);
                    //                    LatLonPoint point = poi.getLatLonPoint();
                    //
                    //                    info.setLatitude(point.getLatitude());
                    //                    info.setLonTitude(point.getLongitude());
                    //                    mList.add(info);
                    //                    Log.d("haha", "poi" + snippet);
                    //                    LogUtil.i(name + " 0000" + "0000 " + snippet);
                    try {
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("cityname", name);
                        jsonObject.put("name", snippet);
                        jsonArray.put(jsonObject);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                callBack.callBack(jsonArray);
            }

            @Override
            public void onPoiItemSearched(PoiItem poiItem, int i) {

            }
        });
        search.searchPOIAsyn();

    }

    public interface SearchCallBack {
        void callBack(JSONArray str);
    }

}

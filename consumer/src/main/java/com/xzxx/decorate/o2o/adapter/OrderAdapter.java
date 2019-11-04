package com.xzxx.decorate.o2o.adapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by kiun_2007 on 2018/9/7.
 */

public class OrderAdapter {

    public static void orderConvert(JSONArray array){
        for (int i = 0; i < array.length(); i ++){
            JSONObject jsonObject = array.optJSONObject(i);

            if(!jsonObject.optString("salesAfterStatus").isEmpty()){
                try {
                    jsonObject.put("orderStatus", jsonObject.optString("salesAfterStatus"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}

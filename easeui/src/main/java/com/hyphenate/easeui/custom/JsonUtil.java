package com.hyphenate.easeui.custom;

import android.text.TextUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;

/**
 * Json工具类：用于json相关操作
 * <p>
 * <br> Author: 叶青
 * <br> Version: 1.0.0
 * <br> Date: 2016年12月31日
 * <br> Copyright: Copyright © 2016 xTeam Technology. All rights reserved.
 */
public class JsonUtil {

    // TODO ******************************************************************将json转换成map对象
    /**
     * 解析JsonObject.
     * <p>
     * <br> Version: 1.0.0
     * <br> CreateTime: 2016年12月31日,上午2:46:19
     * <br> UpdateTime: 2016年12月31日,上午2:46:19
     * <br> CreateAuthor: 叶青
     * <br> UpdateAuthor: 叶青
     * <br> UpdateInfo: (此处输入修改内容,若无修改可不写.)
     *
     * @param json
     *         需要解析的json字符串
     *
     * @return 解析后的map集合
     */
    public static HashMap<String, String> jsonObjectStr2Map(String json) {
        HashMap<String, String> map = new HashMap<>();
        try {
            JSONObject jsonObject = new JSONObject(json);
            Iterator<?> iterator = jsonObject.keys();
            while (iterator.hasNext()) {
                String key = iterator.next().toString();
                map.put(key, jsonObject.getString(key));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }

    /**
     * 解析JsonArray.
     * <p>
     * <br> Version: 1.0.0
     * <br> CreateTime: 2016年12月31日,上午2:47:28
     * <br> UpdateTime: 2016年12月31日,上午2:47:28
     * <br> CreateAuthor: 叶青
     * <br> UpdateAuthor: 叶青
     * <br> UpdateInfo: (此处输入修改内容,若无修改可不写.)
     *
     * @param json
     *         需要解析的json字符串
     *
     * @return 解析后的map列表集合
     */
    public static ArrayList<HashMap<String, String>> jsonArrayStr2maps(String json) {
        ArrayList<HashMap<String, String>> list = new ArrayList<>();
        try {
            JSONArray jsonArray = new JSONArray(json);
            for (int i = 0; i < jsonArray.length(); i++) {
                list.add(jsonObjectStr2Map(jsonArray.getString(i)));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    // TODO ******************************************************************将map对象打包成json
    /**
     * 将map对象打包成json字符串
     * <p>
     * <br> Version: 1.0.0
     * <br> CreateTime: 2016年12月31日,上午2:48:38
     * <br> UpdateTime: 2016年12月31日,上午2:48:38
     * <br> CreateAuthor: 叶青
     * <br> UpdateAuthor: 叶青
     * <br> UpdateInfo: (此处输入修改内容,若无修改可不写.)
     *
     * @param map
     *         需要封装车jsonObject的map集合
     *
     * @return 转换之后的json字符串
     */
    public static String map2JsonObjectStr(HashMap<String, String> map) {
        return map2JsonObject(map).toString();
    }

    public static JSONObject map2JsonObject(HashMap<String, String> map) {
        JSONObject jsonObject = new JSONObject();
        try {
            Set<Entry<String, String>> set = map.entrySet();
            for (Entry<String, String> entry : set) {
                jsonObject.put(entry.getKey(), entry.getValue());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    /**
     * 将map列表转换成jsonArray字符串
     * <p>
     * <br> Version: 1.0.0
     * <br> CreateTime: 2016年12月31日,上午2:50:37
     * <br> UpdateTime: 2016年12月31日,上午2:50:37
     * <br> CreateAuthor: 叶青
     * <br> UpdateAuthor: 叶青
     * <br> UpdateInfo: (此处输入修改内容,若无修改可不写.)
     *
     * @param maps
     *         待转换的map列表
     *
     * @return 转换之后的jsonArray字符串
     */
    public static String maps2JsonArrayStr(ArrayList<HashMap<String, String>> maps) {
        return maps2JsonArray(maps).toString();
    }

    public static JSONArray maps2JsonArray(ArrayList<HashMap<String, String>> maps) {
        JSONArray jsonArray = new JSONArray();
        try {
            for (int i = 0; i < maps.size(); i++) {
                jsonArray.put(map2JsonObject(maps.get(i)));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonArray;
    }

    // TODO ******************************************************************json解析字符串
    /**
     * json解析字符串
     * <p>
     * <br> Version: 1.0.0
     * <br> CreateTime: 2016年12月31日,上午2:50:37
     * <br> UpdateTime: 2016年12月31日,上午2:50:37
     * <br> CreateAuthor: 叶青
     * <br> UpdateAuthor: 叶青
     * <br> UpdateInfo: (此处输入修改内容,若无修改可不写.)
     *
     * @param jsonObject
     *         JSONObject
     * @param keyName
     *         key
     * @param fallback
     *         默认值
     *
     * @return 解析后的String
     */
    public static String optString(JSONObject jsonObject, String keyName, String fallback) {
        String value = jsonObject.optString(keyName, fallback);
        if (TextUtils.isEmpty(value) || value.toLowerCase().equals("null")) {
            value = "";
        }
        return value;
    }

    public static String optString(JSONObject jsonObject, String keyName) {
        return optString(jsonObject, keyName, "");
    }

}
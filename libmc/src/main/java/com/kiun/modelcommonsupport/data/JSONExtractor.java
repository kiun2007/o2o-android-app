package com.kiun.modelcommonsupport.data;

import com.kiun.modelcommonsupport.utils.JexlUtil;
import com.kiun.modelcommonsupport.utils.MCString;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by kiun_2007 on 2018/4/14.
 * JSON 数据提取器.
 */
public class JSONExtractor extends ExtractorBase<Object> {

    public JSONExtractor(Object jsonObject){
        super(jsonObject);
    }

    @Override
    protected Object extractNoExpression(String path) {
        String[] paths = path.split("\\.");

        Object root = mData;
        for (int i = 0; i < paths.length && root != null; i ++){
            String p = paths[i];
            if(p.indexOf('[') >= 0 && p.indexOf(']') >=0){
                String indexStr = p.substring(p.indexOf('[') + 1,  p.indexOf(']'));
                int index = Integer.parseInt(indexStr);
                if((root instanceof JSONArray) && index < ((JSONArray)root).length()){
                    try {
                        root = ((JSONArray)root).getJSONObject(index);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }else{
                    root = null;
                }
            }else {
                if (root != null && ((JSONObject)root).has(p)) {
                    if (i == paths.length - 1) {
                        return ((JSONObject)root).opt(p);
                    }
                    if (root instanceof JSONObject) {
                        root = ((JSONObject)root).opt(p);
                    }
                }
            }
        }
        return null;
    }

}

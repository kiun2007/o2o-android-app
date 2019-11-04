package com.kiun.modelcommonsupport.data;

/**
 * Created by kiun_2007 on 2018/4/16.
 */

public class KeyValue {
    private String mKey = null;
    private Object mValue = null;
    private String mError = null;

    public KeyValue(String key, Object value, String error){
        mKey = key;
        mValue = value;
        mError = error;
    }

    public KeyValue(String key, Object value){
        this(key, value, null);
    }

    public Object getValue() {
        return mValue;
    }

    public String getKey(){
        return mKey;
    }

    public String getError() {
        return mError;
    }
}

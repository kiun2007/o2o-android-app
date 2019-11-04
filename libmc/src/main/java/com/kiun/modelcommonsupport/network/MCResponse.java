package com.kiun.modelcommonsupport.network;

import com.kiun.modelcommonsupport.MainApplication;
import com.kiun.modelcommonsupport.ServiceError;
import com.kiun.modelcommonsupport.network.responses.MCUIResponse;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

/**
 * Created by kiun_2007 on 2018/7/23.
 */

public class MCResponse {

    private MCUIResponse uiResponse;

    private MCBaseRequest request;

    /**
     * 请求响应者.
     */
    public String requestUrl;

    /**
     * 当数据返回时回调函数.
     *
     * @param data 请求到的数据.
     */
    public void onResponse(String data) {
        Object objc = jsonDataResponse(data);
        if(objc != null && !this.uiResponse.isDead()) {
            this.uiResponse.onDataChanged(objc, request);
        }
    }

    /**
     * 开始请求数据之前回调.
     */
    public void onBeginRequest() {
        this.uiResponse.onBeginRequest();
    }

    /**
     * 请求发生错误.
     *
     * @param error 错误相关信息.
     */
    public void onError(Error error) {
        if(!this.uiResponse.isDead()){
            this.uiResponse.onError(error);
        }
    }

    public Object jsonDataResponse(String data) {
        JSONTokener jsonParser = new JSONTokener(data);

        JSONObject json = null;
        try {
            Object nextValue = jsonParser.nextValue();
            if (nextValue instanceof JSONObject) {
                json = (JSONObject) nextValue;
            }
            if (json != null) {
                if (json.has("code") && json.getString("code").equals("000")) {
                    MainApplication.getInstance().saveRequest(request, data);
                    return json.get("data");
                }else {
                    String msg = null;
                    int code = 0;
                    if(json.has("code") && json.has("errorMessage")){
                        msg = json.optString("errorMessage");
                        code = json.optInt("code");
                    }
                    onError(new ServiceError(msg, code));
                    return null;
                }
            }
        } catch (JSONException e) {
            this.onError(new Error(e.getMessage(), e));
        }
        return null;
    }

    public MCResponse(MCUIResponse uiResponse) {
        this.uiResponse = uiResponse;
    }

    public void setRequest(MCBaseRequest request) {
        this.request = request;
    }
}

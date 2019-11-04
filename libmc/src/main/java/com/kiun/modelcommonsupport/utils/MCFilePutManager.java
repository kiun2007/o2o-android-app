package com.kiun.modelcommonsupport.utils;

import com.kiun.modelcommonsupport.network.MCBaseRequest;
import com.kiun.modelcommonsupport.network.MCHttpCGI;
import com.kiun.modelcommonsupport.network.MCResponse;
import com.kiun.modelcommonsupport.network.requests.MCFileTokenRequest;
import com.kiun.modelcommonsupport.network.responses.MCUIResponse;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UploadManager;

import org.json.JSONObject;

/**
 * Created by kiun_2007 on 2018/8/17.
 */

public class MCFilePutManager implements MCUIResponse{

    String tokenId;
    UploadManager uploadManager = new UploadManager();
    byte[] bytes = null;
    UpCompletionHandler completionHandler;
    String upLoadUrl = null;
    private MCFilePutManager(){
    }

    public static MCFilePutManager manager(){
        return new MCFilePutManager();
    }

    public void putFileByData(byte[] data, UpCompletionHandler completionHandler){
        bytes = data;
        this.completionHandler = completionHandler;
        MCFileTokenRequest request = new MCFileTokenRequest();
        request.setResponse(new MCResponse(this));
        MCHttpCGI.defaultCenter().requestCGI(request);
    }

    public void putFileByData(String url, UpCompletionHandler completionHandler){
        upLoadUrl = url;
        this.completionHandler = completionHandler;
        MCFileTokenRequest request = new MCFileTokenRequest();
        request.setResponse(new MCResponse(this));
        MCHttpCGI.defaultCenter().requestCGI(request);
    }

    @Override
    public void onDataChanged(Object data, MCBaseRequest request) {
        JSONObject jsonObject = (JSONObject) data;
        tokenId = jsonObject.optString("token");

        if(tokenId != null && completionHandler != null){
            if(bytes != null){
                uploadManager.put(bytes, null, tokenId, completionHandler, null);
            }else if(upLoadUrl != null){
                uploadManager.put(upLoadUrl, null, tokenId, completionHandler, null);
            }
        }
    }

    @Override
    public void onBeginRequest() {
    }

    @Override
    public void onError(Error error) {
    }

    @Override
    public boolean isDead() {
        return false;
    }
}

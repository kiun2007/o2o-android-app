package com.kiun.modelcommonsupport.network;

import android.os.Build;

import com.kiun.modelcommonsupport.data.drive.MCDataField;
import com.kiun.modelcommonsupport.utils.AppUtil;
import com.kiun.modelcommonsupport.utils.ByteUtil;
import com.kiun.modelcommonsupport.utils.DateUtil;
import com.kiun.modelcommonsupport.utils.MD5Util;
import com.kiun.modelcommonsupport.utils.MapUtil;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by kiun_2007 on 2018/7/23.
 * 数据请求基础.
 */
public abstract class MCBaseRequest {

    //基础请求参数-----------------------------------------.
    /**
     设备系统类型.
     */
    public int sysType;

    /**
     设备唯一标志.
     */
    public String deviceNo;

    /**
     手机型号.
     */
    public String phoneModel;

    /**
     应用版本号.
     */
    public int versionNo;

    /**
     客户端应用显示版本号.
     */
    public String appVersion;

    /**
     移动操作系统版本号/浏览器版本.
     */
    public String osVersion;

    /**
     服务端分配.
     */
    public String appKey;

    /**
     时间戳 格式为yyyy-MM-dd HH:mm:ss.
     */
    public String timestamp;

    /**
     客户端类型.
     */
    public int appType;

    /**
     用户登陆后的唯一标志,很多接口都需要使用字段.
     */
    public String tokenId;

    /**
     请求的URL.
     */
    private String url;

    private String requestUrl;

    /**
     * 数据是否是在线数据.
     */
    private boolean online = true;

    private static int appTypeBase = 0; //师傅端还是个人端的配置.
    private static String tokenIdOnly;

//    public static String serviceIp = "116.62.215.104:8090"; //测试服务器
    public static String serviceIp = "47.98.56.121:8090"; //灰度测试

    private MCResponse response;

    private List allFiles;
    private String _appsecret = "44d6d569341947ec947c711a18574de5";
    protected List<Map> files;

    public MCResponse getResponse() {
        return response;
    }

    public void setResponse(MCResponse response) {
        this.response = response;
    }

    /**
     * 请求路径.
     * @return 子类返回要请求的路径.
     */
    public abstract String requestPath();

    /**
     * 是否是师傅端.
     * @return 师傅端true,个人端false.
     */
    public static boolean isMaster(){
        return appTypeBase != 0;
    }

    public static void configType(int type){
        appTypeBase = type;
    }

    public static void configTokenId(String tokenId){
        tokenIdOnly = tokenId;
    }

    public void addFiles(List<String> urls, String fileType){
        for (String urlItem : urls) {
            Map<String, String> updateFile = new HashMap<>();

            updateFile.put("fileUrl", urlItem);
            updateFile.put("fileType", fileType);
            files.add(updateFile);
        }
    }

    public void dataInit(){
    }

    public List<Map> allFiles(){
        return files;
    }

    public MCBaseRequest(){
        requestUrl = String.format("http://%s/api/%s", serviceIp, this.requestPath());
        sysType = 2; //Android.
        deviceNo = AppUtil.getDeviceNo();
        phoneModel = Build.BRAND + "_" + Build.MODEL;

        versionNo = AppUtil.getVersionCode(AppUtil.curContext());
        appVersion = "1.0.1";
        osVersion = android.os.Build.VERSION.RELEASE;
        appKey = "888888"; //Key 和appsecret 是一对的.
        appType = appTypeBase;
        if(tokenIdOnly != null){
            tokenId = tokenIdOnly;
        }
        files = new ArrayList<>();
    }

    public String getUrl(){
        return url;
    }

    public boolean isOnline(){
        return online;
    }

    public void inOnline(){
        online = true;
    }

    public void inOffline(){
        online = false;
    }

    public String nowTimestamp() {
        return DateUtil.DateToString(new Date(), "yyyyMMddHHmmssSSS");
    }

    private String dictParams(Map map){
        Map<String, Object> newMap = MapUtil.sortMapByKey(map);
        StringBuilder sb = new StringBuilder(9000);

        for (String key : newMap.keySet()) {
            String value = null;
            if(newMap.get(key) instanceof Map){
                value = dictParams((Map) newMap.get(key));
            }else if(newMap.get(key) instanceof List){
                StringBuilder arrayString = new StringBuilder(5000);
                List<Map<String, Object>> mapList = (List<Map<String, Object>>) newMap.get(key);
                for (Map itemMap : mapList) {
                    arrayString.append(dictParams(itemMap));
                }
                value = arrayString.toString();
            }else{
                value = newMap.get(key).toString();
            }

            if(value != null){
                sb.append(String.format("%s=%s", key, value));
            }
        }
        return sb.toString();
    }

    public String requestBody(){
        timestamp = this.nowTimestamp();
        Map<String, Object> map = MCDataField.objToDict(this, new String[]{"url", "files"});
        String appSecretStr = dictParams(map) + _appsecret;
        String sign = ByteUtil.bytesToHexString(MD5Util.MD5(appSecretStr)).toLowerCase();

        if(requestUrl != null){
            url = String.format("%s?sign=%s", requestUrl, sign);
        }

        JSONObject jsonObject = new JSONObject(map);
        return jsonObject.toString();
    }

    /**
     * 获取缓存路径.
     * @return 请求的缓存路径.
     */
    public String cacheFileName(){
        Map<String, Object> map = MCDataField.objToDict(this, new String[]{"url", "files", "timestamp"});
        String appSecretStr = dictParams(map) + requestPath();
        String sign = ByteUtil.bytesToHexString(MD5Util.MD5(appSecretStr)).toLowerCase();
        return sign;
    }
}
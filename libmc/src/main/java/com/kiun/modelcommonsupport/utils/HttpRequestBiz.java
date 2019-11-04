package com.kiun.modelcommonsupport.utils;

import android.os.Build;

import com.amos.modulebase.utils.SystemUtil;
import com.kiun.modelcommonsupport.MainApplication;
import com.kiun.modelcommonsupport.network.MCBaseRequest;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class HttpRequestBiz {

    //        订单进度入参
    public String requestUrl = String.format("http://%s/api/", MCBaseRequest.serviceIp);
    //                    appKey (string, optional),//         服务端分配.
    public String appKey = "888888"; //Key 和appsecret 是一对的.
    //                    appType (string, optional),        //         客户端类型.
    public String appType = "0";////师傅端还是个人端的配置.0
    //                    appVersion (string, optional),//         客户端应用显示版本号.
    public String appVersion = SystemUtil.getAppVersionName();
    //                    tokenId (string, optional),        //         用户登陆后的唯一标志,很多接口都需要使用字段.
    public String tokenId = MainApplication.getInstance().getUserInfo(false).tokenId;
    //                    versionNo (integer, optional)//         应用版本号.
    public String versionNo = String.valueOf(SystemUtil.getAppVersionCode());
    //    //                    orderId (string, optional)://            订单id ,
    //    String orderId = getArguments().getString("orderId");
    //                    deviceNo (string, optional),//         设备唯一标志.
    public String deviceNo = AppUtil.getDeviceNo();
    //                    osVersion (string, optional),//         移动操作系统版本号/浏览器版本.
    public String osVersion = Build.VERSION.RELEASE;
    //                    timestamp (string, optional),
    public String timestamp = DateUtil.DateToString(new Date(), "yyyyMMddHHmmssSSS"); //         时间戳 格式为yyyy-MM-dd HH:mm:ss.
    //                    phoneModel (string, optional),//         手机型号.
    public String phoneModel = Build.BRAND + "_" + Build.MODEL;
    //                    sysType (string, optional),//         设备系统类型.
    public String sysType = "2"; //Android.

    //                    deviceToken (string, optional),
    //                    orderBy (string, optional),
    //                    orderParam (string, optional),
    //                    pageNo (integer, optional),
    //                    pageSize (integer, optional),
    //        List<Map> files = new ArrayList<>();
    public String _appsecret = "44d6d569341947ec947c711a18574de5";

    /**
     * 添加post请求的固定参数
     * <p>
     * <br> Version: 1.0.0
     * <br> CreateTime: 2016年12月31日,上午5:00:05
     * <br> UpdateTime: 2016年12月31日,上午5:00:05
     * <br> CreateAuthor: 叶青
     * <br> UpdateAuthor: 叶青
     * <br> UpdateInfo: (此处输入修改内容,若无修改可不写.)
     *
     * @param method
     *         METHOD
     *
     * @return 含固定参数的HashMap
     */
    public HashMap<String, String> getBaseParams(String method, HashMap<String, String> params) {
        HashMap<String, String> baseParams = getBaseParams();
        Set<Map.Entry<String, String>> set = params.entrySet();
        for (Map.Entry<String, String> entry : set) {
            baseParams.put(entry.getKey(), entry.getValue());
        }
        requestUrl = requestUrl + method;
        addSign(baseParams);
        return baseParams;
    }

    public HashMap<String, String> getBaseParams() {
        HashMap<String, String> params = new HashMap<>();
        params.put("appKey", appKey);
        params.put("appType", appType);
        params.put("appVersion", appVersion);
        params.put("tokenId", tokenId);
        params.put("versionNo", versionNo);
        //        params.put("orderId", orderId);
        params.put("deviceNo", deviceNo);
        params.put("osVersion", osVersion);
        params.put("timestamp", timestamp);
        params.put("phoneModel", phoneModel);
        params.put("sysType", sysType);

        return params;
    }

    private void addSign(HashMap<String, String> map) {
        String appSecretStr = dictParams(map) + _appsecret;
        String sign = ByteUtil.bytesToHexString(MD5Util.MD5(appSecretStr)).toLowerCase();
        requestUrl = String.format("%s?sign=%s", requestUrl, sign);
    }

    private String dictParams(Map map) {
        Map<String, Object> newMap = MapUtil.sortMapByKey(map);
        StringBuilder sb = new StringBuilder(9000);

        for (String key : newMap.keySet()) {
            String value = null;
            if (newMap.get(key) instanceof Map) {
                value = dictParams((Map) newMap.get(key));
            } else if (newMap.get(key) instanceof List) {
                StringBuilder arrayString = new StringBuilder(5000);
                List<Map<String, Object>> mapList = (List<Map<String, Object>>) newMap.get(key);
                for (Map itemMap : mapList) {
                    arrayString.append(dictParams(itemMap));
                }
                value = arrayString.toString();
            } else {
                value = newMap.get(key).toString();
            }

            if (value != null) {
                sb.append(String.format("%s=%s", key, value));
            }
        }
        return sb.toString();
    }
}

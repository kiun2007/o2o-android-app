package com.hyphenate.easeui.custom;

import java.security.MessageDigest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HttpRequestBiz {

    //        订单进度入参
    public String requestUrl = "";
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
        requestUrl = requestUrl + method;
        addSign(params);
        return params;
    }


    private void addSign(HashMap<String, String> map) {
        String appSecretStr = dictParams(map) + _appsecret;
        String sign = bytesToHexString(MD5(appSecretStr)).toLowerCase();
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

    /**
     * 生成一个字符串的MD5.
     * @param s
     * @return
     */
    public static byte[] MD5(String s) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] bytes = md.digest(s.getBytes("utf-8"));
            return bytes;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 字节转化为HEX字符串(使用公钥加密).
     * @param src 需要转化的字节数组.
     * @return 加密后的字节HEX字符串.
     */
    public static String bytesToHexString(byte[] src){

        StringBuilder stringBuilder = new StringBuilder(src.length * 3);
        stringBuilder.append("");
        if (src == null || src.length <= 0) {
            return null;
        }
        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv.toUpperCase());
        }
        return stringBuilder.toString();
    }
}

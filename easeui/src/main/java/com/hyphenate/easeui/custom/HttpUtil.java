package com.hyphenate.easeui.custom;

import android.util.Log;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

/**
 * 网络操作类.
 * <p>
 * 用于网络的POST 、 GET 、 download、upload等操作
 * <br> Author: 叶青
 * <br> Version: 1.0.0
 * <br> Date: 2016年3月30日
 * <br> Copyright: Copyright © 2016 xTeam Technology. All rights reserved.
 */
public class HttpUtil {

    private static HttpUtil httpUtil;

    public static HttpUtil getInstance() {
        if (httpUtil == null) {
            httpUtil = new HttpUtil();
        }
        return httpUtil;
    }

    /** 加解密统一使用的编码方式 */
    private final static String ENCODING = "UTF-8";

    /**
     * 像指定地址发送post请求提交数据.
     * <p>
     * <br> Version: 1.0.0
     * <br> CreateTime: 2016年12月31日,上午2:06:53
     * <br> UpdateTime: 2016年12月31日,上午2:06:53
     * <br> CreateAuthor: 叶青
     * <br> UpdateAuthor: 叶青
     * <br> UpdateInfo: (此处输入修改内容,若无修改可不写.)
     *
     * @param path
     *         数据提交路径.
     * @param params
     *         发送请求参数,key为属性名,value为属性值.
     *
     * @return 服务器的响应信息, 当发生错误时返回响应码.
     *
     * @throws IOException
     *         网络连接错误时抛出IOException.
     * @throws TimeoutException
     *         网络连接超时时抛出TimeoutException.
     */
    public String sendPost(String path, Map<String, String> params) throws IOException, TimeoutException {
        try {
            URL url = new URL(path);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setUseCaches(false); // 设置是否启用缓存,post请求不能使用缓存.
            conn.setDoOutput(true); // 设置输出,post请求必须设置.
            conn.setDoInput(true); // 设置输入,post请求必须设置.
            // 设置通用的请求属性 // 请求头数据
            conn.setRequestProperty("Content-Type", "application/json;charset=utf-8");//有时候你需要给 connection 指定 Content-type
            conn.setRequestProperty("accept", "*/*");
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("charset", ENCODING);
            conn.setConnectTimeout(30000);
            conn.setReadTimeout(30000);
            conn.setRequestMethod("POST");
            conn.connect(); // 打开网络链接.

            // 输出流 */
            DataOutputStream output = new DataOutputStream(conn.getOutputStream());
            output.writeBytes(getParams(params)); // 将请求参数写入网络链接.
            output.flush();
            output.close();

            //            Cancel.checkInterrupted();
            return readResponse(conn);
        } catch (SocketTimeoutException e) {
            throw new TimeoutException(e.getMessage());
        }
    }


    /**
     * 读取服务器响应信息.
     * <p>
     * <br> Version: 1.0.0
     * <br> CreateTime: 2016年12月31日,上午3:04:49
     * <br> UpdateTime: 2016年12月31日,上午3:04:49
     * <br> CreateAuthor: 叶青
     * <br> UpdateAuthor: 叶青
     * <br> UpdateInfo: (此处输入修改内容,若无修改可不写.)
     *
     * @return 服务器的响应信息, 当发生错误时返回响应码.
     *
     * @throws IOException
     *         读取信息发生错误时抛出IOException.
     */
    private String readResponse(HttpURLConnection conn) throws IOException {
        // 返回结果
        String result;
        int responseCode = conn.getResponseCode();
        Log.i("123456", conn.getURL() + "\n" + "conn.getResponseCode()---->>>>" + responseCode);
        if (responseCode == HttpURLConnection.HTTP_OK) { // 若响应码以2开头则读取响应头总的返回信息
            //返回打开连接读取的输入流
            InputStreamReader inputStreamReader = new InputStreamReader(conn.getInputStream());
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String line;
            StringBuilder stringBuffer = new StringBuilder();
            while ((line = bufferedReader.readLine()) != null) {
                stringBuffer.append(line);
            }
            //result = EncodingUtils.getString(stringBuffer.toString().getBytes(), ENCODING);
            result = new String(stringBuffer.toString().getBytes(), ENCODING);
            inputStreamReader.close();
            bufferedReader.close();
        } else { // 若响应码不以2开头则返回错误信息.
            result = "error";
        }

        conn.disconnect();
        return result;
    }

    /**
     * 将发送请求的参数构造为指定格式.
     * <p>
     * <br> Version: 1.0.0
     * <br> CreateTime: 2016年12月31日,上午3:05:28
     * <br> UpdateTime: 2016年12月31日,上午3:05:28
     * <br> CreateAuthor: 叶青
     * <br> UpdateAuthor: 叶青
     * <br> UpdateInfo: (此处输入修改内容,若无修改可不写.)
     *
     * @param params
     *         发送请求的参数,key为属性名,value为属性值.
     *
     * @return 指定格式的请求参数.
     */
    private String getParams(Map<String, String> params) {
        if (params.size() <= 0) {
            return "";
        }
        //        StringBuilder stringBuilder = new StringBuilder();
        //        // 取出所有参数进行构造
        //        for (Map.Entry<String, String> entry : params.entrySet()) {
        //            try {
        //                String param = entry.getKey() + "=" + entry.getValue() + "&";
        //                // String param = entry.getKey() + "=" + URLEncoder.encode(entry.getValue(), ENCODING) + "&";
        //                stringBuilder.append(param);
        //            } catch (Exception e) {
        //                e.printStackTrace();
        //            }
        //        }
        // 返回构造结果
        return JsonUtil.map2JsonObjectStr((HashMap<String, String>) params);
    }
}
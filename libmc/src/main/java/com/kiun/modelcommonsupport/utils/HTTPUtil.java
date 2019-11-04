package com.kiun.modelcommonsupport.utils;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by kiun_2007 on 2018/7/23.
 */

public class HTTPUtil {

    public static void configBase(String base){

    }

//    public static JSONObject postPath(String path, Object params){
//
//    }

    public static String postURL(String url, String data){

        try {
            URL getterUrl = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) getterUrl.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setConnectTimeout(10000);
            OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream());
            out.write(data);
            out.flush();
            out.close();

            InputStream in = connection.getInputStream();
            //下面对获取到的输入流进行读取
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            StringBuilder response = new StringBuilder();
            String line = null;
            while((line = reader.readLine())!=null){
                response.append(line);
            }
            return response.toString();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}

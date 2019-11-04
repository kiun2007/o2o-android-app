package com.kiun.modelcommonsupport.network;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.kiun.modelcommonsupport.MainApplication;
import com.kiun.modelcommonsupport.ServiceError;
import com.kiun.modelcommonsupport.utils.HTTPUtil;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * Created by kiun_2007 on 2018/7/23.
 */

public class MCHttpCGI {

    private class RequestObj {
        public MCBaseRequest baseRequest;
        public Object result;
    }

    BlockingQueue<MCBaseRequest> requestQueue;
    private static MCHttpCGI defaultCGI = null;

    public static MCHttpCGI defaultCenter() {
        if (defaultCGI == null) {
            defaultCGI = new MCHttpCGI();
        }
        return defaultCGI;
    }

    Handler uiHandle = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            RequestObj requestObj = (RequestObj) msg.obj;

            if (requestObj.result instanceof String) {
                String result = (String) requestObj.result;
                //                if (result.contains("8041")) {// 抢登
                if (result.contains("8041") || result.contains("8035") || result.contains("8036")) {// 抢登
                    ActivityManager am = (ActivityManager) MainApplication.getInstance().getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
                    String activityName = am.getRunningTasks(1).get(0).topActivity.getClassName();
                    if (!activityName.contains("LoginActivity")) {
                        Toast.makeText(MainApplication.getInstance(), "请重新登录", Toast.LENGTH_SHORT).show();
                    }
                    MainApplication.getInstance().logOut();
                    return;
                }
                requestObj.baseRequest.getResponse().onResponse(result);
            }

            if (requestObj.result instanceof Error) {
                Error error = (Error) requestObj.result;
                requestObj.baseRequest.getResponse().onError(error);
            }
        }
    };

    private class RequestRunner implements Runnable {

        @Override
        public void run() {
            while (true) {
                MCBaseRequest request = null;
                long startTime = System.currentTimeMillis();
                try {
                    request = requestQueue.poll(10000000, TimeUnit.MILLISECONDS);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (request.getResponse() != null) {
                    request.getResponse().onBeginRequest();
                }

                //----------消费请求对象.
                String body = request.requestBody();
                String url = request.getUrl();
                boolean isCacheFile = false;

                if (!request.isOnline()) { //如果没有请求网络数据之前读取本地数据.
                    String localValue = MainApplication.getInstance().readRequest(request);
                    if (localValue != null) {
                        MCBaseRequest finalRequest = request;
                        new Handler(Looper.getMainLooper()) {
                            @Override
                            public void handleMessage(Message msg) {
                                finalRequest.getResponse().onResponse(localValue);
                                finalRequest.inOnline();
                            }
                        }.sendEmptyMessage(0);
                    }
                }

                String result = null;
                if (body != null && url != null) {
                    result = HTTPUtil.postURL(url, body);
                    if (result != null) {
                        int useTime = (int) (System.currentTimeMillis() - startTime);
                        Log.i("Result:", "use time:" + useTime + "ms," + result); //打印网络请求的时间
                    }
                }

                if (result != null) {
                    if (request.getResponse() != null) {

                        Message msg = Message.obtain();
                        RequestObj obj = new RequestObj();
                        request.getResponse().setRequest(request);
                        obj.baseRequest = request;
                        obj.result = result;
                        msg.obj = obj;
                        uiHandle.sendMessage(msg);
                    }
                } else {
                    if (request.getResponse() != null) {
                        Message msg = Message.obtain();
                        RequestObj obj = new RequestObj();
                        request.getResponse().setRequest(request);
                        obj.baseRequest = request;
                        obj.result = new ServiceError("网络请求错误，请检查您的网络情况.", -1);
                        msg.obj = obj;
                        uiHandle.sendMessage(msg);
                    }
                }
            }
        }
    }

    public MCHttpCGI() {
        requestQueue = new LinkedBlockingQueue(50);

        for (int i = 0; i < 5; i++) { //启动五个消费者同时处理网络事件.
            new Thread(new RequestRunner()).start();
        }
    }

    public void requestCGI(MCBaseRequest request) {
        //提供请求对象.
        try {
            requestQueue.put(request);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

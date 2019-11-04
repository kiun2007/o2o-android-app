package com.xzxx.decorate.o2o.pay.alipay;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.alipay.sdk.app.AuthTask;
import com.alipay.sdk.app.PayTask;
import com.kiun.modelcommonsupport.controllers.BaseRequestAcitity;
import com.kiun.modelcommonsupport.network.MCBaseRequest;
import com.kiun.modelcommonsupport.safe.OrderPayer;
import com.kiun.modelcommonsupport.safe.PayEventer;
import com.kiun.modelcommonsupport.utils.MCString;
import com.xzxx.decorate.o2o.pay.alipay.util.OrderInfoUtil2_0;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by kiun_2007 on 2018/9/12.
 */

public class AliPay implements OrderPayer {

    /** 支付宝支付业务：入参app_id */
    public static final String APPID = "2016121404274013";

    /** 支付宝账户登录授权业务：入参pid值 */
    public static final String PID = "";
    /** 支付宝账户登录授权业务：入参target_id值 */
    public static final String TARGET_ID = "";

    /** 商户私钥，pkcs8格式 */
    /** 如下私钥，RSA2_PRIVATE 或者 RSA_PRIVATE 只需要填入一个 */
    /** 如果商户两个都设置了，优先使用 RSA2_PRIVATE */
    /** RSA2_PRIVATE 可以保证商户交易在更加安全的环境下进行，建议使用 RSA2_PRIVATE */
    /** 获取 RSA2_PRIVATE，建议使用支付宝提供的公私钥生成工具生成， */
    /** 工具地址：https://doc.open.alipay.com/docs/doc.htm?treeId=291&articleId=106097&docType=1 */
    public static final String RSA2_PRIVATE = "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQCZdwT00xvQldo+3dHZNviijLVOB9tJlykqjMMWIds4s4v2c+D95K4zwjZx61McdQxmATOkUpybvRb5DZp/HA6JYS4D30ylruxnpfi28/ldfGzJdYdo3y/jgzV2XrLO8BLCWQ5rJ5iakWKzkJEf1PGfaxFVmQafT3/NbmuwqHOA6pgvf1e759xRZSckgdLS9ORx4/04EV87WN75sv/I/P3VY3qKpOKKEGHaU9pWkg7SPqqEM/QohiotOVRsTOTFKncXZJDKnM/YczJXcVuc46xJzaf5JWoQ2D0J6LSdk30bWrPMTMeyiPGsXarycZ+71eKYA6uqFV48+Ka5GVwXzzcFAgMBAAECggEAUVgnBmaIDc4AeuY0/0AGFpMi3xOHBsDbDzTBi4+ylaGGBMJzdeCq/cBp/4RO73yTb9YfO3CWlm5qKOUvCUUj52ndbCwGgqPvpxjwdnlnqkfzme10MwBqb9vykKWtfcb/CokN/Xplhzic6rUsV4UV9fxYmdidDAClq4xmBKOlZaUgv8dQmw/XwWQVl5BBgnuuAED06v3Bk6fTvcbYAK5TqYAPxC47sMu3jZX9sEgTYa7zzfOOM0EBouvPMJOQ0A0hKHvG/NdB6YZKdWGvQksR1XCxdDEBhEzmHNpo/EAlyNO9C1aBiVnlZ4x+S+6u7958QcAHiMhN93lW7ALWhsSdgQKBgQDY3UAB6skM1RmnKD3QrECXGpK5N0zcsgqKUMiIG5erhWfZbvOHV6/WbdkN5CJapHfY5RCTg0rRWLuiC7ifcqeShg+0QYd8YMgBfS90k9ZfRU85gbUQIoCYF1DoFmGOxIowxzsSFQJva7OzVWokvMqm8FD1+lx8To+ldfWpu/VgcQKBgQC1KNSjMX6mzAxdsMnymKCT8OAA1pR091YWerSCjuJCvBR/z6tnFWAVzDZTt/npuh41pd0lQaWboJWtGQ96T72l3wr26d8zAbJxR1qOUnMDWUhr7MlgXL+YQl7sNj2anW6d8Jmjz9GZEo+36KHwEDRHqmDidQw9gXcfM6aPOWIJ1QKBgDYqZN9Vtiu3WjXvDil9rJBROc57Kn6BFbLgZIXiZmqX4o1jcf4e/7NOMoWOsqnzJGAPLqm5hElvoUXjKZThvUsSov+B0oZVYf9FNkIm3sdi91vEsuW5yGa0hCfD11AidOWkDXQBvV75BqRHNEDCytcRdRdLFiiSMrzADsD7SllxAoGBAJTZL53HFSFHlwGEOOozrfzgda06olBYyUs/FwIPFKlwkAdUQqk4TvKaEClJiPYaDqhV2Ba8ka4Fi/XmGWmOC202jnPCRbpLMqsQzb8Sz514xqCmte+PRX19SzEagBOCN1leT17han4zWhLp+eSd69bWjzSCp8QBlDb24Wvc4kjhAoGADMnC7N92/JQW8+ZpJy+E/yI55DL2BzIrRzMim366sPhEpv/8TM1hb59EQBRVTc55pgHHJSyC3d7vi/vVtxsP+46TZfhE7+y0k4BWi15zRBd9Pk3RgBMsrZxvxS3qfaD4jkwU+TexARAvwS700/FVV1ufpP1k5xfcNkNMwEQmDHs=";

    private static final int SDK_PAY_FLAG = 1;
    private static final int SDK_AUTH_FLAG = 2;

    private PayEventer payEventer;

    private BaseRequestAcitity acitity;

    public AliPay(BaseRequestAcitity acitity){
        this.acitity = acitity;
    }

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @SuppressWarnings("unused")
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SDK_PAY_FLAG: {
                    @SuppressWarnings("unchecked")
                    PayResult payResult = new PayResult((Map<String, String>) msg.obj);
                    /**
                     对于支付结果，请商户依赖服务端的异步通知结果。同步通知结果，仅作为支付结束的通知。
                     */
                    String resultInfo = payResult.getResult();// 同步返回需要验证的信息
                    String resultStatus = payResult.getResultStatus();
                    // 判断resultStatus 为9000则代表支付成功

                    if(payEventer != null){
                        if (TextUtils.equals(resultStatus, "9000")) {
                            // 该笔订单是否真实支付成功，需要依赖服务端的异步通知。
                            payEventer.onPayComplete(PayEventer.AliPay, 0);
                        } else {
                            // 该笔订单真实的支付结果，需要依赖服务端的异步通知。
                            payEventer.onPayComplete(PayEventer.AliPay, -2);
                        }
                    }
                    break;
                }
                default:
                    break;
            }
        };
    };

    /**
     * 支付宝账户授权业务
     *
     * @param v
     */
    public void authV2(View v) {
        if (TextUtils.isEmpty(PID) || TextUtils.isEmpty(APPID) || (TextUtils.isEmpty(RSA2_PRIVATE) || TextUtils.isEmpty(TARGET_ID))) {
            new AlertDialog.Builder(acitity).setTitle("警告").setMessage("需要配置PARTNER |APP_ID| RSA_PRIVATE| TARGET_ID")
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialoginterface, int i) {
                        }
                    }).show();
            return;
        }

        /**
         * 这里只是为了方便直接向商户展示支付宝的整个支付流程；所以Demo中加签过程直接放在客户端完成；
         * 真实App里，privateKey等数据严禁放在客户端，加签过程务必要放在服务端完成；
         * 防止商户私密数据泄露，造成不必要的资金损失，及面临各种安全风险；
         *
         * authInfo的获取必须来自服务端；
         */
        boolean rsa2 = (RSA2_PRIVATE.length() > 0);
        Map<String, String> authInfoMap = OrderInfoUtil2_0.buildAuthInfoMap(PID, APPID, TARGET_ID, rsa2);
        String info = OrderInfoUtil2_0.buildOrderParam(authInfoMap);

        String privateKey = RSA2_PRIVATE;
        String sign = OrderInfoUtil2_0.getSign(authInfoMap, privateKey, rsa2);
        final String authInfo = info + "&" + sign;
        Runnable authRunnable = new Runnable() {

            @Override
            public void run() {
                // 构造AuthTask 对象
                AuthTask authTask = new AuthTask(acitity);
                // 调用授权接口，获取授权结果
                Map<String, String> result = authTask.authV2(authInfo, true);

                Message msg = new Message();
                msg.what = SDK_AUTH_FLAG;
                msg.obj = result;
                mHandler.sendMessage(msg);
            }
        };

        // 必须异步调用
        Thread authThread = new Thread(authRunnable);
        authThread.start();
    }

    /**
     * get the sdk version. 获取SDK版本号
     *
     */
    public void getSDKVersion() {
        PayTask payTask = new PayTask(acitity);
        String version = payTask.getVersion();
        Toast.makeText(acitity, version, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void setPayEvent(PayEventer payEvent) {
        payEventer = payEvent;
    }

    @Override
    public void payStart(String tradeNo, String total) {
        if (TextUtils.isEmpty(APPID) || (TextUtils.isEmpty(RSA2_PRIVATE))) {
            return;
        }

        /**
         * 这里只是为了方便直接向商户展示支付宝的整个支付流程；所以Demo中加签过程直接放在客户端完成；
         * 真实App里，privateKey等数据严禁放在客户端，加签过程务必要放在服务端完成；
         * 防止商户私密数据泄露，造成不必要的资金损失，及面临各种安全风险；
         *
         * orderInfo的获取必须来自服务端；
         */
        boolean rsa2 = (RSA2_PRIVATE.length() > 0);

        Map<String, String> keyValues = new HashMap<String, String>();

        keyValues.put("app_id", APPID);
        keyValues.put("biz_content",
                "{\"timeout_express\":\"30m\",\"product_code\":\"QUICK_MSECURITY_PAY\"," +
                        "\"total_amount\":\"" + total + "\",\"subject\":\"小装小修-服务费\"," +
                        "\"body\":\"小装小修服务费用\",\"out_trade_no\":\"" + tradeNo +  "\"}");
        keyValues.put("charset", "utf-8");
        keyValues.put("method", "alipay.trade.app.pay");
        keyValues.put("sign_type", "RSA2");
        keyValues.put("notify_url", String.format("http://%s/api/%s", MCBaseRequest.serviceIp, "order/customer/aliPayNotify"));
        keyValues.put("timestamp", MCString.formatDate("yyyy-MM-dd hh:mm:ss", new Date()));
        keyValues.put("version", "1.0");

        String orderParam = OrderInfoUtil2_0.buildOrderParam(keyValues);

        String privateKey = RSA2_PRIVATE;
        String sign = OrderInfoUtil2_0.getSign(keyValues, privateKey, rsa2);
        final String orderInfo = orderParam + "&" + sign;

        Runnable payRunnable = new Runnable() {

            @Override
            public void run() {
                PayTask alipay = new PayTask(acitity);
                Map<String, String> result = alipay.payV2(orderInfo, true);

                Message msg = new Message();
                msg.what = SDK_PAY_FLAG;
                msg.obj = result;
                mHandler.sendMessage(msg);
            }
        };

        Thread payThread = new Thread(payRunnable);
        payThread.start();
    }
}

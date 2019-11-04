package com.xzxx.decorate.o2o.pay;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.kiun.modelcommonsupport.data.drive.MCDataField;
import com.kiun.modelcommonsupport.network.MCBaseRequest;
import com.kiun.modelcommonsupport.safe.OrderPayer;
import com.kiun.modelcommonsupport.safe.PayEventer;
import com.kiun.modelcommonsupport.utils.ByteUtil;
import com.kiun.modelcommonsupport.utils.HTTPUtil;
import com.kiun.modelcommonsupport.utils.MD5Util;
import com.kiun.modelcommonsupport.utils.MapUtil;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.StringReader;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Created by kiun_2007 on 2018/8/13.
 */

public class WechatPay implements OrderPayer{

    private static WechatPay currentPay;
    private PayEventer payEventer;
    public WechatPay(){
        currentPay = this;
    }

    public void setPayEvent(PayEventer payEvent){
        payEventer = payEvent;
    }

    public static void callPayEvent(int code){
        if(currentPay != null && currentPay.payEventer != null){
            currentPay.payEventer.onPayComplete(PayEventer.WechatPay, code);
        }
        currentPay = null;
    }

    public static void setIwxapi(IWXAPI iwxapi) {
        WechatPay.iwxapi = iwxapi;
        iwxapi.registerApp("wxd66a79fe9676fd49");
    }

    private static class PayUnifiedorder{
        /**
         应用ID,wxd678efh567hg6787微信开放平台审核通过的应用APPID.
         */
        public String appid;

        /**
         商户号1230000109微信支付分配的商户号.
         */
        public String mch_id;

        /**
         随机字符串随机字符串，不长于32位。推荐随机数生成算法
         */
        public String nonce_str;

        /**
         签名，详见签名生成算法
         */
        public String sign;

        /**
         商品描述,腾讯充值中心-QQ会员充值.
         */
        public String body;

        /**
         商户订单号.
         */
        public String out_trade_no;

        /**
         总金额.
         */
        public String total_fee;

        /**
         终端IP.
         */
        public String spbill_create_ip;

        /**
         通知地址. String(256)	http://www.weixin.qq.com/wxpay/pay.php	接收微信支付异步通知回调地址，通知url必须为直接可访问的url，不能携带参数。
         */
        public String notify_url;

        /**
         交易类型,APP.
         */
        public String trade_type;

        public PayUnifiedorder(){
        }
    }

    private  static IWXAPI iwxapi; //微信支付api

    public static String getIPAddress(){
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()) {
                        return "127.96.85.74";
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static String dictParams(Map dict){
        Map<String, Object> newMap = MapUtil.sortMapByKey(dict);
        StringBuilder content = new StringBuilder(9000);
        for (String keyName : newMap.keySet()) {
            content.append(keyName.toLowerCase() + "=" + newMap.get(keyName) + "&");
        }
        content.append("key=Aajmmj102030Aajmmj102030Aajmmj11");
        return content.toString();
    }

    public void payStart(String tradeNo , String total){

        PayUnifiedorder payUnifiedorder = new PayUnifiedorder();

        payUnifiedorder.appid = "wxd66a79fe9676fd49";
        payUnifiedorder.mch_id = "1425358302";
        payUnifiedorder.spbill_create_ip = getIPAddress();
        payUnifiedorder.body = "小装小修-服务费";
        payUnifiedorder.out_trade_no = tradeNo;
        payUnifiedorder.total_fee = total;
        payUnifiedorder.notify_url = String.format("http://%s/api/order/customer/notifyUrl", MCBaseRequest.serviceIp);
        payUnifiedorder.nonce_str = UUID.randomUUID().toString().replace("-", "");
        payUnifiedorder.trade_type = "APP";
        Map map = MCDataField.objToDict(payUnifiedorder, new String[]{"sign"});

        String sign = ByteUtil.bytesToHexString(MD5Util.MD5(dictParams(map))).toUpperCase();

        final StringBuilder sb = new StringBuilder(3000);
        sb.append("<xml>");
        sb.append("<appid>wxd66a79fe9676fd49</appid>");
        sb.append("<mch_id>1425358302</mch_id>");
        sb.append("<spbill_create_ip>" + payUnifiedorder.spbill_create_ip + "</spbill_create_ip>");
        sb.append("<body>小装小修-服务费</body>");
        sb.append("<out_trade_no>" + tradeNo + "</out_trade_no>");
        sb.append("<total_fee>" + total + "</total_fee>");
        sb.append("<notify_url>" + payUnifiedorder.notify_url +"</notify_url>");
        sb.append("<nonce_str>" + payUnifiedorder.nonce_str + "</nonce_str>");
        sb.append("<trade_type>APP</trade_type>");
        sb.append("<sign>" + sign + "</sign>");
        sb.append("</xml>");

        new Thread(new Runnable() {
            @Override
            public void run() {
                String data = HTTPUtil.postURL("https://api.mch.weixin.qq.com/pay/unifiedorder", sb.toString());

                SAXReader reader = new SAXReader();
                try {
                    Document doc = reader.read(new StringReader(data));
                    Map value = Dom2Map(doc);
//<xml><return_code><![CDATA[SUCCESS]]></return_code><return_msg><![CDATA[OK]]></return_msg><appid><![CDATA[wxd66a79fe9676fd49]]></appid><mch_id><![CDATA[1425358302]]></mch_id><nonce_str><![CDATA[DTJuWvQoq2Kdphs5]]></nonce_str><sign><![CDATA[77075B3EBFCB22FD0532BE36F7433B93]]></sign><result_code><![CDATA[FAIL]]></result_code><err_code><![CDATA[ORDERPAID]]></err_code><err_code_des><![CDATA[该订单已支付]]></err_code_des></xml>
                    if (value.get("result_code").toString().equals("FAIL")){
                        new Handler(Looper.getMainLooper()){
                            @Override
                            public void handleMessage(Message msg) {
                                payEventer.onPayComplete(PayEventer.WechatPay, -3);
                            }
                        }.sendEmptyMessage(0);
                        return;
                    }

                    final PayReq req = new PayReq();
                    req.partnerId = "1425358302";
                    req.prepayId = value.get("prepay_id").toString();
                    req.packageValue = "Sign=WXPay";
                    req.nonceStr = UUID.randomUUID().toString().replace("-", "");
                    req.timeStamp = String.format("%d", System.currentTimeMillis()/1000);
                    req.appId = "wxd66a79fe9676fd49";

                    Map<String, String> newMap = new HashMap<>();
                    newMap.put("partnerid", req.partnerId);
                    newMap.put("prepayid", req.prepayId);
                    newMap.put("package", req.packageValue);
                    newMap.put("noncestr", req.nonceStr);
                    newMap.put("timestamp", req.timeStamp);
                    newMap.put("appid", "wxd66a79fe9676fd49");

                    req.sign = ByteUtil.bytesToHexString(MD5Util.MD5(dictParams(newMap))).toUpperCase();
                    if(req.checkArgs()){

                        Handler handler = new Handler(Looper.getMainLooper()){
                            @Override
                            public void handleMessage(Message msg) {
                                super.handleMessage(msg);
                                boolean isOK = iwxapi.sendReq(req);
                            }
                        };
                        handler.sendEmptyMessage(0);
                    }
                } catch (DocumentException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public static Map<String, Object> Dom2Map(Document doc){
        Map<String, Object> map = new HashMap<String, Object>();
        if(doc == null)
            return map;
        Element root = doc.getRootElement();
        for (Iterator iterator = root.elementIterator(); iterator.hasNext();) {
            Element e = (Element) iterator.next();
            //System.out.println(e.getName());
            List list = e.elements();
            if(list.size() > 0){
                map.put(e.getName(), Dom2Map((Document) e));
            }else
                map.put(e.getName(), e.getText());
        }
        return map;
    }
}

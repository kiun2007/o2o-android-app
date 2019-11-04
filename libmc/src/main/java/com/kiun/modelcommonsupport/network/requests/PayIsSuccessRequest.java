package com.kiun.modelcommonsupport.network.requests;

import com.kiun.modelcommonsupport.network.MCBaseRequest;

/**
 * Created by kiun_2007 on 2018/9/12.
 * 订单支付是否成功
 */

public class PayIsSuccessRequest extends MCBaseRequest {

    /**
     订单编号.
     */
    public String orderpayId;


    @Override
    public String requestPath() {
        return "order/customer/payIsSuccess";
    }
}

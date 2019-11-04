package com.kiun.modelcommonsupport.network.requests;

import com.kiun.modelcommonsupport.network.MCBaseRequest;

/**
 * Created by kiun_2007 on 2018/9/6.
 */

public class AxbPhoneRequest extends MCBaseRequest {

    /**
     订单id.
     */
    public String orderId;

    @Override
    public String requestPath() {
        return "order/customer/axbPhone";
    }
}

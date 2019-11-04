package com.xzxx.decorate.o2o.requests.order;

import com.kiun.modelcommonsupport.network.MCBaseRequest;

/**
 * Created by kiun_2007 on 2018/9/14.
 */

public class OrderProgressRequest extends MCBaseRequest {

    /**
     订单id.
     */
    public String orderId;

    @Override
    public String requestPath() {
        return "order/customer/progress";
    }
}

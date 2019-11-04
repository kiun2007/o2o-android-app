package com.kiun.modelcommonsupport.network.requests;

import com.kiun.modelcommonsupport.network.MCBaseRequest;

/**
 * Created by kiun_2007 on 2018/8/26.
 */

public class DeleteOrderRequest extends MCBaseRequest {

    /**
     * 订单ID.
     */
    public String orderId;

    @Override
    public String requestPath() {
        return isMaster() ? "order/master/delete" : "order/customer/delete";
    }
}

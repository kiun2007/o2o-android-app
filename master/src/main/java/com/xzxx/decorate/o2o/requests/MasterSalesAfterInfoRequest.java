package com.xzxx.decorate.o2o.requests;

import com.kiun.modelcommonsupport.network.MCBaseRequest;

/**
 * Created by kiun_2007 on 2018/9/7.
 */

public class MasterSalesAfterInfoRequest extends MCBaseRequest {

    /**
     售后订单id,.
     */
    public String salesAfterOrderId;

    @Override
    public String requestPath() {
        return "order/master/salesAfterInfo";
    }
}

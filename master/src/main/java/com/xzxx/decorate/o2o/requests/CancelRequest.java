package com.xzxx.decorate.o2o.requests;

import com.kiun.modelcommonsupport.network.MCBaseRequest;

/**
 * Created by kiun_2007 on 2018/8/25.
 */

public class CancelRequest extends MCBaseRequest {

    /**
     取消内容.
     */
    public String cancelContent;

    /**
     取消类型.
     */
    public String cancelType;

    /**
     * 订单ID.
     */
    public String orderId;

    @Override
    public String requestPath() {
        return "order/master/cancel";
    }
}

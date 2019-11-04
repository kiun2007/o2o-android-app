package com.kiun.modelcommonsupport.network.requests;

import com.kiun.modelcommonsupport.network.MCBaseRequest;

/**
 * Created by kiun_2007 on 2018/8/26.
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

    public String orderId;

    @Override
    public String requestPath() {
        return isMaster() ? "order/master/cancel" : "order/customer/orderCancel";
    }
}

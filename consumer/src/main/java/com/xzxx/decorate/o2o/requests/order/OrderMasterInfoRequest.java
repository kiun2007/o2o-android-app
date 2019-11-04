package com.xzxx.decorate.o2o.requests.order;

import com.kiun.modelcommonsupport.network.MCBaseRequest;

/**
 * Created by kiun_2007 on 2018/9/28.
 * 师傅资料师傅评价师傅印象
 */

public class OrderMasterInfoRequest extends MCBaseRequest {

    /**
     师傅id.
     */
    public String masterId;
    @Override
    public String requestPath() {
        return "order/customer/masterInfo";
    }
}

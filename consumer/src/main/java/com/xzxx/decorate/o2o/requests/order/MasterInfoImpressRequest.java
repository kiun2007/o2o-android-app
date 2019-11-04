package com.xzxx.decorate.o2o.requests.order;

import com.kiun.modelcommonsupport.network.MCBaseRequest;

/**
 * Created by kiun_2007 on 2018/9/28.
 *  客户端查看师傅资料
 */
public class MasterInfoImpressRequest extends MCBaseRequest {
    /**
     师傅id.
     */
    public String masterId;
    @Override
    public String requestPath() {
        return "order/customer/masterInfoImpress";
    }
}

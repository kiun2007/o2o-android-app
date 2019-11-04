package com.xzxx.decorate.o2o.requests.order;

import com.kiun.modelcommonsupport.network.MCListRequest;

/**
 * Created by kiun_2007 on 2018/9/28.
 * 客户端查看师傅评价
 */
public class MasterInfoCommentRequest extends MCListRequest {
    /**
     * 师傅id.
     */
    public String masterId;

    @Override
    public String requestPath() {
        return "/order/customer/comment";
    }
}

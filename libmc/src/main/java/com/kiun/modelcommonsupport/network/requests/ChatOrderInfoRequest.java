package com.kiun.modelcommonsupport.network.requests;

import com.kiun.modelcommonsupport.network.MCBaseRequest;

public class ChatOrderInfoRequest extends MCBaseRequest {

    /**
     环信账号.
     */
    public String hxAccount;

    @Override
    public String requestPath() {
        return MCBaseRequest.isMaster() ? "order/master/chatOrderInfo" : "order/customer/chatOrderInfo";
    }
}

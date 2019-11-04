package com.kiun.modelcommonsupport.network.requests;

import com.kiun.modelcommonsupport.network.MCBaseRequest;

/**
 * Created by kiun_2007 on 2018/9/7.
 */

public class AcceptStatusReqeust extends MCBaseRequest {
    /**
     接单状态 0可接单 1 不可接单.
     */
    public String acceptStatus;

    @Override
    public String requestPath() {
        return "home/master/acceptStatus";
    }
}

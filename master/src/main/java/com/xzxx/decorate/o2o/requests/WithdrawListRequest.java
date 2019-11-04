package com.xzxx.decorate.o2o.requests;

import com.kiun.modelcommonsupport.network.MCListRequest;

/**
 * Created by kiun_2007 on 2018/9/3.
 */

public class WithdrawListRequest extends MCListRequest {

    @Override
    public String requestPath() {
        return "user/master/withdrawList";
    }
}

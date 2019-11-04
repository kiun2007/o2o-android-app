package com.kiun.modelcommonsupport.network.requests;

import com.kiun.modelcommonsupport.network.MCBaseRequest;

/**
 * Created by kiun_2007 on 2018/8/26.
 */

public class ServicePhoneRequest extends MCBaseRequest {
    @Override
    public String requestPath() {
        return "user/userInfo/servicePhone";
    }
}

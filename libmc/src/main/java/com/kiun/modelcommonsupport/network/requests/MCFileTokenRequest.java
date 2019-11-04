package com.kiun.modelcommonsupport.network.requests;

import com.kiun.modelcommonsupport.network.MCBaseRequest;

/**
 * Created by kiun_2007 on 2018/8/17.
 */

public class MCFileTokenRequest extends MCBaseRequest {
    @Override
    public String requestPath() {
        return "user/userInfo/fileToken";
    }
}

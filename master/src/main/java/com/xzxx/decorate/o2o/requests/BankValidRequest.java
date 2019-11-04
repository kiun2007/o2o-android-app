package com.xzxx.decorate.o2o.requests;

import com.kiun.modelcommonsupport.network.MCBaseRequest;

/**
 * Created by kiun_2007 on 2018/8/19.
 */

public class BankValidRequest extends MCBaseRequest {

    /**
     银行预留手机号.
     */
    public String bankPhone;

    /**
     持卡人.
     */
    public String cardHolder;

    /**
     银行卡号.
     */
    public String cardNo;

    /**
     短信验证码类型.
     */
    public String codeType = "4";


    @Override
    public String requestPath() {
        return "user/master/bankValid";
    }
}

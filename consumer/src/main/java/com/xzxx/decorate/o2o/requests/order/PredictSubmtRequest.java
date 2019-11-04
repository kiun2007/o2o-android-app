package com.xzxx.decorate.o2o.requests.order;

import com.kiun.modelcommonsupport.network.MCBaseRequest;

/**
 * Created by kiun_2007 on 2018/9/12.
 */

public class PredictSubmtRequest extends MCBaseRequest {

    /**
     预约时间.
     */
    public String appointmentTime;

    /**
     预约城市 编码.
     */
    public String appointmentCity;

    @Override
    public String requestPath() {
        return "order/customer/predictSubmit";
    }
}

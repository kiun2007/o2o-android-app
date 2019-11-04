package com.xzxx.decorate.o2o.requests.order;

import com.kiun.modelcommonsupport.network.MCBaseRequest;

/**
 * Created by kiun_2007 on 2018/9/6.
 */

public class PredictOrderRequest extends MCBaseRequest {

    /**
     当前定位城市编码.
     */
    public String cityCode;

    /**
     二级类目id.
     */
    public String secondItemId;

    @Override
    public String requestPath() {
            return "order/customer/predict";
    }
}

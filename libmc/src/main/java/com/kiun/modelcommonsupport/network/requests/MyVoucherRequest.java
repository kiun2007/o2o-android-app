package com.kiun.modelcommonsupport.network.requests;

import com.kiun.modelcommonsupport.network.MCBaseRequest;
import com.kiun.modelcommonsupport.network.MCListRequest;

/**
 * Created by kiun_2007 on 2018/8/25.
 */

public class MyVoucherRequest extends MCListRequest {

    /**
     优惠券类型 0可用 1过期 2已使用 3维修费支付时可用券,.
     */
    public String type;

    /**
     订单id.
     */
    public String orderId;

    @Override
    public String requestPath() {
        return "user/userInfo/myVoucher";
    }
}
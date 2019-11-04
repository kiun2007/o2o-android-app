package com.kiun.modelcommonsupport.network.requests;

import com.kiun.modelcommonsupport.network.MCBaseRequest;

/**
 * Created by kiun_2007 on 2018/9/12.
 */

public class RepairSubmitRequest extends MCBaseRequest {

    /**
     订单编号.
     */
    public String orderId;

    /**
     优惠券编号.
     */
    public String voucherId;

    /**
     优惠金额.
     */
    public String voucherMoney;

    @Override
    public String requestPath() {
        return "order/customer/repairSubmit";
    }
}

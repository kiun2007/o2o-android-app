package com.kiun.modelcommonsupport.data;

import com.kiun.modelcommonsupport.network.FieldValue;

/**
 * Created by kiun_2007 on 2018/9/12.
 */

public class PayDoorBean extends BeanBase {

    public float tip = 0; //小费.

    public float doorCost; //上门费用.

    public float discountCost; //优惠金额.

    public float getDoorPay(){ //实际需要支付的金额.
        float count = tip + doorCost - discountCost;
        return count < 0 ? 0 : count;
    }
}

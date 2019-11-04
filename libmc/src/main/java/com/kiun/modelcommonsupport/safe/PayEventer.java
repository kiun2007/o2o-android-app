package com.kiun.modelcommonsupport.safe;

/**
 * Created by kiun_2007 on 2018/8/14.
 */

public interface PayEventer {

    public static final int WechatPay = 0;
    public static final int AliPay = 1;

    /**
     * 支付回调完成.
     * @param payType 支付类型.
     * @param payCode 返回结果.
     */
    void onPayComplete(int payType, int payCode);
}
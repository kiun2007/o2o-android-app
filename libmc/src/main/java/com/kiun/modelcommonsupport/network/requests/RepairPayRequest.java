package com.kiun.modelcommonsupport.network.requests;
import com.kiun.modelcommonsupport.network.MCBaseRequest;

/**
* Created by kiun_2007 on 2018/8/8.
 * 支付维修费请求.
 */
public class RepairPayRequest extends MCBaseRequest {

	/**
	  订单id. 
	 */
	public String orderId;

	/**
	  支付状态. 
	 */
	public String payStatus;

	/**
	  支付时间yyyyMMddHHmmss. 
	 */
	public String payTime;

	/**
	  支付方式. 
	 */
	public String payType;

	/**
	  维修费. 
	 */
	public String repairMoney;

	/**
	  维修费支付金额. 
	 */
	public String repairPayMony;

	/**
	  维修费优惠券id. 
	 */
	public String repairVoucherId;

	/**
	  优惠金额 . 
	 */
	public String voucherMoney;

	@Override 
	public String requestPath() {
		return "order/customer/repairPay";
	}
}

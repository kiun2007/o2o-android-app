package com.kiun.modelcommonsupport.network.requests;
import com.kiun.modelcommonsupport.network.MCBaseRequest;

/**
* Created by kiun_2007 on 2018/8/8.
 * 上门费支付请求.
 */
public class DoorPayRequest extends MCBaseRequest {

	/**
	  上门费. 
	 */
	public String doorToFee;

	/**
	  订单id. 
	 */
	public String orderId;

	/**
	  支付金额. 
	 */
	public String payMoney;

	/**
	  付款方式. 
	 */
	public String payType;

	/**
	  小费. 
	 */
	public String tip;

	/**
	  优惠券id. 
	 */
	public String voucharId;

	/**
	  优惠金额. 
	 */
	public String voucharMoney;

	@Override 
	public String requestPath() {
		return "order/customer/doorPay";
	}
}

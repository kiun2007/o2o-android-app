package com.kiun.modelcommonsupport.network.requests;
import com.kiun.modelcommonsupport.network.MCBaseRequest;

/**
* Created by kiun_2007 on 2018/8/8.
 */
public class StartServiceRequest extends MCBaseRequest {

	/**
	  订单id. 
	 */
	public String orderId;

	/**
	  维修费. 
	 */
	public String repairMoney;

	@Override 
	public String requestPath() {
		return "order/master/startService";
	}
}

package com.kiun.modelcommonsupport.network.requests;
import com.kiun.modelcommonsupport.network.MCBaseRequest;

/**
* Created by kiun_2007 on 2018/8/8.
 */
public class StartDoorRequest extends MCBaseRequest {

	/**
	  订单id,. 
	 */
	public String orderId;

	@Override 
	public String requestPath() {
		return "order/master/startDoor";
	}
}

package com.xzxx.decorate.o2o.requests;
import com.kiun.modelcommonsupport.network.MCBaseRequest;

/**
* Created by kiun_2007 on 2018/8/8.
 */
public class MasterOrderInfoRequest extends MCBaseRequest {

	/**
	  订单id,. 
	 */
	public String orderId;

	@Override 
	public String requestPath() {
		return "order/master/info";
	}
}

package com.xzxx.decorate.o2o.requests.afterService;
import com.kiun.modelcommonsupport.network.MCBaseRequest;

/**
* Created by kiun_2007 on 2018/8/8.
 * 售后信息.
 */
public class SalesAfterInfoRequest extends MCBaseRequest {

	/**
	  售后申请id,. 
	 */
	public String salesAfterId;

	@Override 
	public String requestPath() {
		return "order/customer/salesAfterInfo";
	}
}

package com.xzxx.decorate.o2o.requests.afterService;
import com.kiun.modelcommonsupport.network.MCListRequest;

/**
* Created by kiun_2007 on 2018/8/8.
 * 售后订单列表.
 */
public class SalesAfterListRequest extends MCListRequest {

	@Override 
	public String requestPath() {
		return "order/customer/salesAfterList";
	}
}

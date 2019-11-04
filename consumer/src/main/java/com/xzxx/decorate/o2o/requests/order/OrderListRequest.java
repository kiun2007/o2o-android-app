package com.xzxx.decorate.o2o.requests.order;
import com.kiun.modelcommonsupport.network.MCListRequest;

/**
* Created by kiun_2007 on 2018/8/8.
 * 订单列表请求.
 */
public class OrderListRequest extends MCListRequest {

	/**
	  订单状态 待维修,. 
	 */
	public String status;

	@Override 
	public String requestPath() {
		return "order/customer/list";
	}
}

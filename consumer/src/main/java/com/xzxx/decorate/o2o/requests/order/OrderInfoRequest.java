package com.xzxx.decorate.o2o.requests.order;
import com.kiun.modelcommonsupport.network.MCBaseRequest;

/**
* Created by kiun_2007 on 2018/8/8.
 * 订单详情请求.
 */
public class OrderInfoRequest extends MCBaseRequest {

	public static final String ORDER_ID = "orderId";
	/**
	  订单id. 
	 */
	public String orderId;

	@Override 
	public String requestPath() {
		return "order/customer/info";
	}

	public OrderInfoRequest(){
		super();
		inOffline();
	}
}

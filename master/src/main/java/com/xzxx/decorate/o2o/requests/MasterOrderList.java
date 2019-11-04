package com.xzxx.decorate.o2o.requests;
import com.kiun.modelcommonsupport.network.MCListRequest;

/**
* Created by kiun_2007 on 2018/8/8.
 */
public class MasterOrderList extends MCListRequest {

	/**
	  订单状态,. 
	 */
	public String status;

	@Override 
	public String requestPath() {
		return "order/master/list";
	}
}

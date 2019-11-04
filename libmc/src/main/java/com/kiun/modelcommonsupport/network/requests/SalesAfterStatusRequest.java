package com.kiun.modelcommonsupport.network.requests;
import com.kiun.modelcommonsupport.network.MCBaseRequest;

/**
* Created by kiun_2007 on 2018/8/8.
 */
public class SalesAfterStatusRequest extends MCBaseRequest {

	/**
	  原订单id. 
	 */
	public String orderId;

	/**
	  售后申请id. 
	 */
	public String saleAfterId;

	/**
	  售后订单id. 
	 */
	public String salesAfterOrderId;

	/**
	  售后订单状态0-待上门，1-待服务，2-服务中，3-服务完成'. 
	 */
	public String salesAfterOrderStatus;

	@Override 
	public String requestPath() {
		return "order/master/salesAfterStatus";
	}
}

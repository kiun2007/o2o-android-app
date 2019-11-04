package com.xzxx.decorate.o2o.requests.order;
import com.kiun.modelcommonsupport.network.MCBaseRequest;

/**
* Created by kiun_2007 on 2018/8/8.
 * 获取订单评论信息.
 */
public class CommentInfoRequest extends MCBaseRequest {

	/**
	  订单id. 
	 */
	public String orderId;

	@Override 
	public String requestPath() {
		return "order/customer/commentInfo";
	}
}

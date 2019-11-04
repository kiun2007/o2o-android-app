package com.xzxx.decorate.o2o.requests.order;
import com.kiun.modelcommonsupport.network.MCListRequest;

/**
* Created by kiun_2007 on 2018/8/8.
 * 师傅所有评论信息.
 */
public class CommentRequest extends MCListRequest {

	/**
	  师傅ID. 
	 */
	public String masterId;

	@Override 
	public String requestPath() {
		return "order/customer/comment";
	}
}

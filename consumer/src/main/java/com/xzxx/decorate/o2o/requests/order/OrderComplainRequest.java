package com.xzxx.decorate.o2o.requests.order;
import com.kiun.modelcommonsupport.network.MCBaseRequest;

import java.util.List;

/**
* Created by kiun_2007 on 2018/8/8.
 * 投诉请求.
 */
public class OrderComplainRequest extends MCBaseRequest {

	/**
	  投诉内容. 
	 */
	public String compainContent;

	/**
	  投诉类型. 
	 */
	public String compainType;

	/**
	  投诉文件. 
	 */
	public List orderFiles;

	/**
	  订单id,. 
	 */
	public String orderId;

	@Override 
	public String requestPath() {
		return "order/customer/orderComplain";
	}
}

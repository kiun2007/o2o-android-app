package com.xzxx.decorate.o2o.requests.afterService;
import com.kiun.modelcommonsupport.network.MCBaseRequest;

import java.util.List;

/**
* Created by kiun_2007 on 2018/8/8.
 * 售后申请.
 */
public class SalesAfterApplyRequest extends MCBaseRequest {

	/**
	  售后留言. 
	 */
	public String applyContent;

	/**
	  申请类型. 
	 */
	public String applyType;

	/**
	  售后文件. 
	 */
	public List orderFiles;

	/**
	  订单id. 
	 */
	public String orderId;

	/**
	  售后类型. 
	 */
	public String serviceType;

	@Override 
	public String requestPath() {
		return "order/customer/salesAfterApply";
	}
}

package com.kiun.modelcommonsupport.network.requests;
import com.kiun.modelcommonsupport.network.MCBaseRequest;

/**
* Created by kiun_2007 on 2018/8/8.
 * 系统字典请求.
 */
public class SysDicItemRequest extends MCBaseRequest {

	/**
	 * 发给总部.
	 */
	public static final String Send_HD = "send_hd";

	/**
	 * 订单取消原因(师傅端).
	 */
	public static final String Cancel_Master = "order_cancel_reason_master";

	/**
	 * 评价.
	 */
	public static final String Evaluate = "evaluate";

	/**
	 * 订单取消原因(用户端).
	 */
	public static final String Cancel_User = "order_cancel_reason_user";

	/**
	 * 投诉.
	 */
	public static final String Complaint = "complaint";

	/**
	 * 售后申请原因.
	 */
	public static final String Complain_Reason = "order_complain_reason";

	/**
	 * 售后服务类型.
	 */
	public static final String After_Service_Type = "sale_after_service_type";

	/**
	  数据字典id,. 
	 */
	public String key;

	@Override 
	public String requestPath() {
		return "order/customer/sysDicItem";
	}
}

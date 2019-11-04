package com.kiun.modelcommonsupport.network.requests;
import com.kiun.modelcommonsupport.network.FieldValue;
import com.kiun.modelcommonsupport.network.MCBaseRequest;

import java.util.List;
import java.util.Map;

/**
* Created by kiun_2007 on 2018/8/8.
 * 提交订单请求.
 */
public class OrderSubmitRequest extends MCBaseRequest {

	/**
	  预约地址门牌号. 
	 */
	public String appointmentAddress;

	/**
	  预约定位地址. 
	 */
	public String appointmentLocation;

	/**
	  预约定位地址经纬度,. 
	 */
	public String appointmentLatitudeLongitude;

	/**
	  预约城市. 
	 */
	public String appointmentCity;

	/**
	  预约人. 
	 */
	public String appointmentName;

	/**
	  预约人性别. 
	 */
	public String appointmentSex;

	/**
	  预约电话. 
	 */
	public String appointmentPhone;

	/**
	  预约时间. 
	 */
	public String appointmentTime;

	/**
	  订单留言. 
	 */
	public String orderWords;

	/**
	  二级类目id. 
	 */
	public String secondItemId;

	/**
	 订单文件.
	 */
	public List<Map> orderFiles;

	/**
	 上门费.
	 */
	public String doorToFee;

	/**
	 小费.
	 */
	public String tip;

	/**
	 优惠券id.
	 */
	public String voucharId;

	/**
	 优惠金额.
	 */
	public String voucharMoney;

	@Override 
	public String requestPath() {
		return "order/customer/submit";
	}
}

package com.xzxx.decorate.o2o.requests.address;
import com.kiun.modelcommonsupport.network.MCBaseRequest;

/**
* Created by kiun_2007 on 2018/8/8.
 * 新增地址.
 */
public class AddressAddRequest extends MCBaseRequest {

	/**
	  门牌号详细描述. 
	 */
	public String addressDetail;

	/**
	  城市. 
	 */
	public String cityCode;

	/**
	  联系人. 
	 */
	public String contactName;

	/**
	  联系电话. 
	 */
	public String contactPhone;

	/**
	  联系人性别. 
	 */
	public String contactSex;

	/**
	  是否默认 0-默认 1-非默认. 
	 */
	public String isDefault;

	/**
	  经纬度 ，隔开. 
	 */
	public String latitudeLongitude;

	/**
	  定位地址,. 
	 */
	public String location;

	@Override 
	public String requestPath() {
		return "user/customer/addressAdd";
	}
}

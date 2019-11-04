package com.xzxx.decorate.o2o.requests.address;
import com.kiun.modelcommonsupport.network.MCBaseRequest;

/**
* Created by kiun_2007 on 2018/8/8.
 * 获取地址,根据地址ID.
 */
public class AddressDetailRequest extends MCBaseRequest {

	/**
	 * 地址ID.
	 */
	public String id;

	@Override 
	public String requestPath() {
		return "user/customer/addressDetail";
	}
}

package com.xzxx.decorate.o2o.requests.address;
import com.kiun.modelcommonsupport.network.MCBaseRequest;

/**
* Created by kiun_2007 on 2018/8/8.
 * 获取默认地址.
 */
public class DefaultAddressDetailRequest extends MCBaseRequest {

	/**
	 * 地址id.
	 */
	public String id;

	@Override 
	public String requestPath() {
		return "user/customer/defaultAddressDetail";
	}
}

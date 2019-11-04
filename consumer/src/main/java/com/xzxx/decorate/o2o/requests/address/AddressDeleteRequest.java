package com.xzxx.decorate.o2o.requests.address;
import com.kiun.modelcommonsupport.network.MCBaseRequest;

/**
* Created by kiun_2007 on 2018/8/8.
 * 删除地址.
 */
public class AddressDeleteRequest extends MCBaseRequest {

	/**
	 * 地址id.
	 */
	public String id;

	@Override 
	public String requestPath() {
		return "user/customer/addressDelete";
	}
}

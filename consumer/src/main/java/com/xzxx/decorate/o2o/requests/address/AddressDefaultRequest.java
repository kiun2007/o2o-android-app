package com.xzxx.decorate.o2o.requests.address;
import com.kiun.modelcommonsupport.network.MCBaseRequest;

/**
* Created by kiun_2007 on 2018/8/8.
 * 设置默认地址.
 */
public class AddressDefaultRequest extends MCBaseRequest {

	@Override 
	public String requestPath() {
		return "user/customer/addressDefault";
	}
}

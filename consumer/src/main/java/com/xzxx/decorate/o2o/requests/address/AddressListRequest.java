package com.xzxx.decorate.o2o.requests.address;
import com.kiun.modelcommonsupport.network.MCListRequest;

/**
* Created by kiun_2007 on 2018/8/8.
 * 获取地址列表.
 */
public class AddressListRequest extends MCListRequest {

	@Override 
	public String requestPath() {
		return "user/customer/addressList";
	}
}

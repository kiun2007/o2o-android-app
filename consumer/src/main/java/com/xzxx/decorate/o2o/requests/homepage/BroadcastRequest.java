package com.xzxx.decorate.o2o.requests.homepage;
import com.kiun.modelcommonsupport.network.MCBaseRequest;

/**
* Created by kiun_2007 on 2018/8/8.
 * 轮播图请求.
 */
public class BroadcastRequest extends MCBaseRequest {

	@Override 
	public String requestPath() {
		return "home/customer/broadcast";
	}
}

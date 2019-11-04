package com.xzxx.decorate.o2o.requests.userinfo;
import com.kiun.modelcommonsupport.network.MCBaseRequest;

/**
* Created by kiun_2007 on 2018/8/8.
 * 获取用户基本信息请求.
 */
public class InfoRequest extends MCBaseRequest {

	public InfoRequest(){
		super();
		inOffline();
	}

	@Override 
	public String requestPath() {
		return "user/customer/info";
	}
}

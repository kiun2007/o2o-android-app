package com.xzxx.decorate.o2o.requests.homepage;
import com.kiun.modelcommonsupport.network.MCListRequest;

/**
* Created by kiun_2007 on 2018/8/8.
 * 首页活动图标请求.
 */
public class HomeActivityRequest extends MCListRequest {

	@Override 
	public String requestPath() {
		return "home/customer/activity";
	}

	public HomeActivityRequest(){
		super();
		inOffline();
	}
}

package com.xzxx.decorate.o2o.requests.classify;
import com.kiun.modelcommonsupport.network.MCListRequest;

/**
* Created by kiun_2007 on 2018/8/8.
 * 一级类目列表.
 */
public class FirstItemRequest extends MCListRequest {

	public FirstItemRequest(){
		super();
		inOffline();
	}

	@Override
	public String requestPath() {
		return "classify/itme/firstItem";
	}
}

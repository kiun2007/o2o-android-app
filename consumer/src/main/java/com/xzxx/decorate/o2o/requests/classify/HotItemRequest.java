package com.xzxx.decorate.o2o.requests.classify;
import com.kiun.modelcommonsupport.network.MCListRequest;

/**
* Created by kiun_2007 on 2018/8/8.
 * 热门类目.
 */
public class HotItemRequest extends MCListRequest {

	public HotItemRequest(){
		super();
		inOffline();
	}

	@Override 
	public String requestPath() {
		return "classify/itme/hotItem";
	}
}

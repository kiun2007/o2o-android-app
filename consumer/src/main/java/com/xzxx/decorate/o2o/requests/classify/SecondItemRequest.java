package com.xzxx.decorate.o2o.requests.classify;
import com.kiun.modelcommonsupport.network.MCListRequest;

/**
* Created by kiun_2007 on 2018/8/8.
 * 二级类目.
 */
public class SecondItemRequest extends MCListRequest {

	/**
	  一级类目id. 
	 */
	public String firstItemId;

	@Override 
	public String requestPath() {
		return "classify/itme/secomdItem";
	}

	public SecondItemRequest(){
		super();
		inOffline();
	}
}

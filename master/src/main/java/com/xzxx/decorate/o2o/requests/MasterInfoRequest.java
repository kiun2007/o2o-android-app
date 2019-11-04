package com.xzxx.decorate.o2o.requests;
import com.kiun.modelcommonsupport.network.MCBaseRequest;

/**
* Created by kiun_2007 on 2018/8/8.
 */
public class MasterInfoRequest extends MCBaseRequest {

	@Override 
	public String requestPath() {
		return "user/master/info";
	}

	public MasterInfoRequest(){
		super();
		inOffline();
	}
}

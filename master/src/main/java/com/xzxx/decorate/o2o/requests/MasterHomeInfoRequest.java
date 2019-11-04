package com.xzxx.decorate.o2o.requests;
import com.kiun.modelcommonsupport.network.MCBaseRequest;

/**
* Created by kiun_2007 on 2018/8/8.
 */
public class MasterHomeInfoRequest extends MCBaseRequest {

	@Override 
	public String requestPath() {
		return "home/master/info";
	}

	public MasterHomeInfoRequest(){
		super();
		inOffline();
	}
}

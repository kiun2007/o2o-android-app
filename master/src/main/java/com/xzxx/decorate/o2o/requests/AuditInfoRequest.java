package com.xzxx.decorate.o2o.requests;
import com.kiun.modelcommonsupport.network.MCListRequest;

/**
* Created by kiun_2007 on 2018/8/8.
 */
public class AuditInfoRequest extends MCListRequest {

	@Override 
	public String requestPath() {
		return "user/master/auditInfo";
	}
}

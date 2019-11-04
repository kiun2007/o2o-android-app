package com.xzxx.decorate.o2o.requests;
import com.kiun.modelcommonsupport.network.MCBaseRequest;

/**
* Created by kiun_2007 on 2018/8/8.
 */
public class CodeRequest extends MCBaseRequest {

	/**
	  电话号码. 
	 */
	public String phone;

	/**
	  短信作用类型. 
	 */
	public String type;

	@Override 
	public String requestPath() {
		return "user/userInfo/code";
	}
}

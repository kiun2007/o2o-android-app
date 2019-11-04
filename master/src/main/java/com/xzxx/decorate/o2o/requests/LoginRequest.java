package com.xzxx.decorate.o2o.requests;
import com.kiun.modelcommonsupport.network.MCBaseRequest;

/**
* Created by kiun_2007 on 2018/8/8.
 */
public class LoginRequest extends MCBaseRequest {

	/**
	  账户. 
	 */
	public String account;

	/**
	  密码的MD5值. 
	 */
	public String password;

	@Override 
	public String requestPath() {
		return "user/userInfo/passwordLogin";
	}
}

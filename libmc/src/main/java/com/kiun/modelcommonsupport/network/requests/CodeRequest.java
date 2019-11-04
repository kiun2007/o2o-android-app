package com.kiun.modelcommonsupport.network.requests;
import com.kiun.modelcommonsupport.network.MCBaseRequest;

/**
* Created by kiun_2007 on 2018/8/8.
 * 获取验证码请求.
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

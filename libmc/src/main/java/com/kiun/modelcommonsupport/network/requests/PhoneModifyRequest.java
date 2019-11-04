package com.kiun.modelcommonsupport.network.requests;
import com.kiun.modelcommonsupport.network.MCBaseRequest;

/**
* Created by kiun_2007 on 2018/8/8.
 * 修改手机号.
 */
public class PhoneModifyRequest extends MCBaseRequest {

	/**
	  新手机验证码. 
	 */
	public String code;

	/**
	  验证码类型. 
	 */
	public String codeType = "5";

	/**
	  新手机号. 
	 */
	public String phone;

	@Override 
	public String requestPath() {
		return "user/userInfo/phoneModify";
	}
}

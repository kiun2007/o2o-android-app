package com.kiun.modelcommonsupport.network.requests;
import com.kiun.modelcommonsupport.network.MCBaseRequest;

/**
* Created by kiun_2007 on 2018/8/8.
 * 手机号验证,短信验证.
 */
public class PhoneValidRequest extends MCBaseRequest {

	/**
	  验证码. 
	 */
	public String code;

	/**
	  验证码类型 1 用户登入 2用户注册 3 忘记密码 4银行卡 5 修改手机号验证. 
	 */
	public String codeType;

	/**
	  手机号码. 
	 */
	public String phone;

	@Override 
	public String requestPath() {
		return "user/userInfo/phoneValid";
	}
}

package com.xzxx.decorate.o2o.requests;
import com.kiun.modelcommonsupport.network.MCBaseRequest;

/**
* Created by kiun_2007 on 2018/8/8.
 */
public class ForgetRequest extends MCBaseRequest {

	/**
	  用户密码. 
	 */
	public String password;

	/**
	  用户手机号. 
	 */
	public String phone;

	/**
	  短信验证码. 
	 */
	public String code;

	/**
	  短信验证码类型, 1用户验证码登入 2用户注册 3忘记密码 4银行卡验证. 
	 */
	public String codeType = "3";

	@Override 
	public String requestPath() {
		return "user/userInfo/passwordForget";
	}
}

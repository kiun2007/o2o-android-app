package com.kiun.modelcommonsupport.network.requests;
import com.kiun.modelcommonsupport.network.MCBaseRequest;

/**
* Created by kiun_2007 on 2018/8/8.
 * 验证码登录请求.
 */
public class CodeLoginRequest extends MCBaseRequest {

	/**
	  用户手机号. 
	 */
	public String phone;

	/**
	  短信验证码. 
	 */
	public String account;

	/**
	  短信验证码. 
	 */
	public String code;

	/**
	  短信验证码类型, 1用户验证码登入 2用户注册 3忘记密码 4银行卡验证. 
	 */
	public String codeType = "1";

	@Override 
	public String requestPath() {
		return "user/userInfo/codeLogin";
	}
}

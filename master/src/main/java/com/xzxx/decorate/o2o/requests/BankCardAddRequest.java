package com.xzxx.decorate.o2o.requests;
import com.kiun.modelcommonsupport.network.MCBaseRequest;

/**
* Created by kiun_2007 on 2018/8/8.
 */
public class BankCardAddRequest extends MCBaseRequest {

	/**
	  银行预留手机号. 
	 */
	public String bankPhone;

	/**
	  所属区域. 
	 */
	public String bankRegion;

	/**
	  开户行. 
	 */
	public String cardBank;

	/**
	  持卡人. 
	 */
	public String cardHolder;

	/**
	  银行卡号. 
	 */
	public String cardNo;

	/**
	  银行卡类型 0储蓄卡. 
	 */
	public String cardType = "0";

	/**
	  验证码类型1 登入验证 2 用户注册 3忘记密码 4银行四要素认证. 
	 */
	public String codeType = "4";

	/**
	  手机验证码. 
	 */
	public String phoneCode;

	@Override 
	public String requestPath() {
		return "user/master/bankCardAdd";
	}
}

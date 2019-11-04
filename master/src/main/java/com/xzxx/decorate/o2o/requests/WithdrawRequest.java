package com.xzxx.decorate.o2o.requests;
import com.kiun.modelcommonsupport.network.MCBaseRequest;

/**
* Created by kiun_2007 on 2018/8/8.
 */
public class WithdrawRequest extends MCBaseRequest {

	/**
	  提现金额. 
	 */
	public String amount;

	/**
	  提现银行卡. 
	 */
	public String bankCardId;

	@Override 
	public String requestPath() {
		return "user/master/withdraw";
	}
}

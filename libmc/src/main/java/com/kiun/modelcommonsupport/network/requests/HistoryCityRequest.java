package com.kiun.modelcommonsupport.network.requests;
import com.kiun.modelcommonsupport.network.MCListRequest;

/**
* Created by kiun_2007 on 2018/8/8.
 * 历史城市.
 */
public class HistoryCityRequest extends MCListRequest {

	@Override 
	public String requestPath() {
		return "user/userInfo/historyCity";
	}
}

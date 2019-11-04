package com.kiun.modelcommonsupport.network.requests;
import android.util.Log;

import com.kiun.modelcommonsupport.network.MCListRequest;

/**
* Created by kiun_2007 on 2018/8/8.
 * 开通城市
 */
public class OpenedCityRequest extends MCListRequest {

	@Override 
	public String requestPath() {
		return "user/userInfo/openedCity";
	}

	public OpenedCityRequest(){
		super();
		inOffline();
	}
}

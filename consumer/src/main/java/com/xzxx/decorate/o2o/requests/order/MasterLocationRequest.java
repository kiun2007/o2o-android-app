package com.xzxx.decorate.o2o.requests.order;
import com.kiun.modelcommonsupport.network.MCBaseRequest;

/**
* Created by kiun_2007 on 2018/8/8.
 * 师傅当前位置获取.
 */
public class MasterLocationRequest extends MCBaseRequest {

	/**
	  师傅id,. 
	 */
	public String masterId;

	@Override 
	public String requestPath() {
		return "location/position/masterLocation";
	}
}

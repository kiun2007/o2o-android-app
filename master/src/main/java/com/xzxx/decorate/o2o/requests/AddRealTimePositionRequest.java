package com.xzxx.decorate.o2o.requests;
import com.kiun.modelcommonsupport.network.MCBaseRequest;

/**
* Created by kiun_2007 on 2018/8/8.
 */
public class AddRealTimePositionRequest extends MCBaseRequest {

	/**
	  纬度. 
	 */
	public String latitude;

	/**
	  经度. 
	 */
	public String longitude;

	/**
	  师傅的状态0上门中 1暂停接单 2接单中. 
	 */
	public String type;

	@Override 
	public String requestPath() {
		return "location/position/addRealTimePosition";
	}
}

package com.xzxx.decorate.o2o.requests;
import com.kiun.modelcommonsupport.network.MCBaseRequest;

/**
* Created by kiun_2007 on 2018/8/8.
 */
public class MasterInfoModifyRequest extends MCBaseRequest {

	/**
	  城市. 
	 */
	public String city;

	/**
	  师傅头像. 
	 */
	public String masterHeadImg;

	@Override 
	public String requestPath() {
		return "user/master/infoModify";
	}
}

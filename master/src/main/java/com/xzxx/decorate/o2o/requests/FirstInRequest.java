package com.xzxx.decorate.o2o.requests;
import com.kiun.modelcommonsupport.network.MCBaseRequest;

/**
* Created by kiun_2007 on 2018/8/8.
 */
public class FirstInRequest extends MCBaseRequest {

	/**
	  城市. 
	 */
	public String city;

	/**
	  性别 0男 1 女. 
	 */
	public String gender;

	/**
	  手持省份证url. 
	 */
	public String handOfIdCard;

	/**
	  身份证. 
	 */
	public String idcardNo;

	/**
	  身份证正面url. 
	 */
	public String faceOfIdcard;

	/**
	  身份证反面url. 
	 */
	public String oppositeOfIdCard;

	/**
	  师傅姓名. 
	 */
	public String masterName;

	/**
	  职业id 多个用|隔开. 
	 */
	public String profession;

	@Override 
	public String requestPath() {
		return "user/master/firstIn";
	}
}

package com.xzxx.decorate.o2o.requests.userinfo;
import com.kiun.modelcommonsupport.network.MCBaseRequest;

/**
* Created by kiun_2007 on 2018/8/8.
 * 修改用户基本信息请求.
 */
public class InfoModifyRequest extends MCBaseRequest {

	/**
	  出生日期. 
	 */
	public String birthday;

	/**
	  用户头像. 
	 */
	public String headImg;

	/**
	  性别. 
	 */
	public String sex;

	/**
	  姓名. 
	 */
	public String userName;

	@Override 
	public String requestPath() {
		return "user/customer/infoModify";
	}

}

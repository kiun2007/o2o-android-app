package com.xzxx.decorate.o2o.requests;
import com.kiun.modelcommonsupport.MainApplication;
import com.kiun.modelcommonsupport.network.MCBaseRequest;

import java.util.List;

/**
* Created by kiun_2007 on 2018/8/8.
 */
public class SendHDReqeust extends MCBaseRequest {

	/**
	  内容描述. 
	 */
	public String content;

	/**
	  师傅位置. 
	 */
	public String masterLocation = MainApplication.getInstance().getValue("location");

	/**
	  发送文件. 
	 */
	public List orderFiles;

	/**
	  订单id. 
	 */
	public String orderId;

	/**
	  类型. 
	 */
	public String type;

	@Override 
	public String requestPath() {
		return "order/master/sendHD";
	}
}

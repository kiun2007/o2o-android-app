package com.xzxx.decorate.o2o.requests.homepage;
import com.kiun.modelcommonsupport.network.MCListRequest;

/**
* Created by kiun_2007 on 2018/8/8.
 * 首页订单列表请求.
 */
public class HomePageOrderList extends MCListRequest {

	@Override 
	public String requestPath() {
		return "home/customer/order";
	}
}

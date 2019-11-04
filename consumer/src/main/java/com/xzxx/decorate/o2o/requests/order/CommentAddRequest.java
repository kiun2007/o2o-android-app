package com.xzxx.decorate.o2o.requests.order;

import com.kiun.modelcommonsupport.network.MCBaseRequest;

import java.util.List;
import java.util.Map;

/**
 * Created by kiun_2007 on 2018/8/8.
 * 评论订单请求.
 */
public class CommentAddRequest extends MCBaseRequest {

    /**
     * 是否匿名0匿名 1实名.
     */
    public String anonymous = "1";

    /**
     * 态度.
     */
    public int attutide;

    /**
     * 时效.
     */
    public int efficiency;

    /**
     * 质量,.
     */
    public int quality;

    /**
     * 评价内容.
     */
    public String content;

    /**
     * 印象.
     */
    public List impression;

    /**
     * 师傅id.
     */
    public String masterId;

    //	/**
    //	  评价订单文件.
    //	 */
    //	@FieldValue(fieldName = "orderFiles")
    //	public List getOrderFiles(){
    //		return this.files;
    //	}
    /**
     * 订单文件.
     */
    public List<Map> orderFiles;
    /**
     * 订单id.
     */
    public String orderId;

    @Override
    public String requestPath() {
        return "order/customer/commentAdd";
    }
}

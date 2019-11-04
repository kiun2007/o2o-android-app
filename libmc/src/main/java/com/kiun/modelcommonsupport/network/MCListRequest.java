package com.kiun.modelcommonsupport.network;

/**
 * Created by kiun_2007 on 2018/8/8.
 * 列表请求基类.
 */

public abstract class MCListRequest extends MCBaseRequest {
    /**
     页码.
     */
    public int pageNo = 1;

    /**
     每一页的数量.
     */
    public int pageSize = 10;

    public MCListRequest(){
        inOnline();
    }
}

package com.kiun.modelcommonsupport.network.responses;

import com.kiun.modelcommonsupport.network.MCBaseRequest;

/**
 * Created by kiun_2007 on 2018/8/8.
 */

public interface MCUIResponse {

    /**
     * 请求数据成功时返回.
     * @param data 成功的数据.
     * @param request 请求.
     */
    void onDataChanged(Object data, MCBaseRequest request);

    /**
     * 请求之前调用.
     */
    void onBeginRequest();

    /**
     * 请求过程中发生错误.
     * @param error 错误信息.
     */
    void onError(Error error);

    /**
     * 响应对象已被回收.
     * @return 返回true 已回收.
     */
    boolean isDead();
}

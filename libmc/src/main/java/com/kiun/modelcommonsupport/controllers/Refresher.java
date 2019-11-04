package com.kiun.modelcommonsupport.controllers;

/**
 * Created by kiun_2007 on 2018/8/28.
 */

public interface Refresher {

    /**
     * 刷新内容.
     */
    void onRefresh(int tag);
}

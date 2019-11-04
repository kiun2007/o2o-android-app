package com.kiun.modelcommonsupport.controllers;

/**
 * Created by kiun_2007 on 2018/8/9.
 */

public interface FillViewer {
    /**
     * 填充数据到视图.
     * @param resId 视图ID.
     * @param data 填充数据.
     */
    void fillToView(int resId, Object data);
}

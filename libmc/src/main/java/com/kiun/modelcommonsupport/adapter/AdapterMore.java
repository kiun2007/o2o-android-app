package com.kiun.modelcommonsupport.adapter;

import org.json.JSONArray;

import java.util.List;

/**
 * Created by kiun_2007 on 2018/8/10.
 */

public interface AdapterMore {

    /**
     * 添加数据.
     * @param datas 新的数据可能是数组或者JSONArray.
     */
    void addItems(Object datas);

    /**
     * 填充所有数据.
     * @param allDatas
     */
    void fillItems(Object allDatas);

    /**
     * 清除所有数据.
     */
    void clearAll();
}

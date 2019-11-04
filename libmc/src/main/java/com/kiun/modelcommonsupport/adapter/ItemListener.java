package com.kiun.modelcommonsupport.adapter;

import android.view.View;

/**
 * Created by Administrator on 2018/3/27.
 */
public interface ItemListener<T extends Object> {
    /**
     * 列表单项被点击.
     * @param itemData 列表每一项的数据.
     * @param tag 点中View的标记，如果点击一项最顶级元素则为0.
     */
    void onItemClick(View listView, T itemData, int tag);
}
package com.kiun.modelcommonsupport.ui.views;

/**
 * Created by kiun_2007 on 2018/4/17.
 * 有动作的View.
 */
public interface ActionView<T extends EventListener> {

    /**
     * 设置事件监听器.
     * @param listener 监听器.
     */
    void setEventListener(T listener);
}

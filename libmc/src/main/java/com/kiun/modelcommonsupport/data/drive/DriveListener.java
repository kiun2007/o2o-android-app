package com.kiun.modelcommonsupport.data.drive;

import com.kiun.modelcommonsupport.data.KeyValue;

/**
 * Created by kiun_2007 on 2018/4/14.
 * 数据驱动接口.
 */

public interface DriveListener {

    /**
     * 获取数据驱动路径.
     * @return 数据驱动路径.
     */
    String getPath();

    /**
     * 使用数据填充.
     * @param data 数据.
     */
    void fill(Object data);

    /**
     * 如果控件要支持事件, Tag请返回>0⃣️.
     * @return 事件标记.
     */
    int eventTag();

    /**
     * 提交表单时候使用的参数.
     * @return 参数内容.
     */
    KeyValue getParam();
}

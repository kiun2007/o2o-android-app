package com.kiun.modelcommonsupport.data;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import java.util.List;

/**
 * Created by kiun_2007 on 2018/7/23.
 */

@Target(ElementType.TYPE)
public @interface ObjectTable {
    /**
     * 获取表格名称.
     * @return 表格名称.
     */
    String tableName();

    /**
     * 获取表格的主键.
     * @return 主键的名称(字段名称).
     */
    String keyName();

    /**
     * 搜寻时使用到的字段集合.
     * @return 字段名称集合.
     */
    String searchFields();
}

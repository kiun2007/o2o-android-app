package com.kiun.modelcommonsupport.adapter;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by kiun_2007 on 2018/4/11.
 * 字段绑定到列表的某个Item的界面控件上的标注.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ListItem {

    /**
     * 设置该字段对于布局的ID.
     * @return 布局内控件的ID.
     */
    int id();

    /**
     * 字段绑定控件所取的类型(默认为TextView 的 setText).
     * @return 控件取值的类型.
     */
    BindType type() default BindType.TEXT;

    /**
     * 字段格式化的字符串(默认为不设置字符串格式化).
     * @return 格式化字符串.
     */
    String format() default "";
}

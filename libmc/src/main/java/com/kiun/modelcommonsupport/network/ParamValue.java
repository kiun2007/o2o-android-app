package com.kiun.modelcommonsupport.network;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by kiun_2007 on 2018/8/17.
 */

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ParamValue {

    /**
     * 字段名称.
     * @return 方法被标记后返回的值将被当作此值使用,类似于字段get方法.
     */
    String fieldName();
}

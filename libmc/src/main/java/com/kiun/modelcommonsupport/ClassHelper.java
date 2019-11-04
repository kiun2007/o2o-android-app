package com.kiun.modelcommonsupport;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * Created by kiun_2007 on 2018/4/16.
 * 动态类型生成帮助.
 */

public class ClassHelper {

    /**
     * 初始化一个参数的实例构造函数.
     * @param className 类名.
     * @param typeClass 参数类型.
     * @param value 构造函数传递的参数.
     * @param <T> 构造的基类类型.
     * @return 被构造的实例.
     */
    public static <T> T newOneParam(String className, Class<?> typeClass, Object value) {
        try {
            Class<? extends T> clz = (Class<? extends T>) Class.forName(className);
            Constructor<? extends T> constructor = clz.getConstructor(typeClass);
            if(constructor != null){
                return constructor.newInstance(value);
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }
}

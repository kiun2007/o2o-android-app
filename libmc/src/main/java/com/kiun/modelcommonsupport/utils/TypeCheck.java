package com.kiun.modelcommonsupport.utils;

/**
 * Created by kiun_2007 on 2018/4/11.
 * 类型检测器.
 */
public class TypeCheck {

    //八大基本类型.
    private static final Class[] valueTypes = new Class[]{int.class, Integer.class, float.class, Float.class, char.class, byte.class, Byte.class, boolean.class, Boolean.class,
                                             double.class, Double.class, long.class, Long.class, short.class, Short.class};

    /**
     * 是否属于值类型.
     * @param obj 要判断的对象.
     * @return 值类型.
     */
    public static boolean isValType(Object obj) {
        if(obj == null){
            return false;
        }

        for (Class clz : valueTypes) {
            if(clz == obj.getClass()){
                return true;
            }
        }
        return false;
    }

    public static boolean isValType(Class clasz) {

        for (Class clz : valueTypes) {
            if(clz == clasz){
                return true;
            }
        }
        return false;
    }

    public static boolean isInteger(Object obj){
        return obj.getClass() == int.class || obj.getClass() == Integer.class;
    }

    public static boolean isFloat(Object obj){
        return obj.getClass() == float.class || obj.getClass() == Float.class || obj.getClass() == Double.class || obj.getClass() == double.class;
    }
}

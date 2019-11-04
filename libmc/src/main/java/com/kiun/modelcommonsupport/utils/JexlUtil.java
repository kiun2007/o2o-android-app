package com.kiun.modelcommonsupport.utils;

import org.apache.commons.jexl2.Expression;
import org.apache.commons.jexl2.JexlContext;
import org.apache.commons.jexl2.JexlEngine;
import org.apache.commons.jexl2.MapContext;

import java.util.Map;

/**
 * Created by kiun_2007 on 2018/8/12.
 */

public class JexlUtil {

    /**
     *
     * @author: Longjun
     * @Description: 使用commons的jexl可实现将字符串变成可执行代码的功能
     * @date:2016年3月21日 下午1:45:13
     */
    public static Object convertToCode(String jexlExp,Map<String,Object> map){
        JexlEngine jexl=new JexlEngine();
        Expression e = jexl.createExpression(jexlExp);
        JexlContext jc = new MapContext();
        for(String key:map.keySet()){
            jc.set(key, map.get(key));
        }
        if(null==e.evaluate(jc)){
            return "";
        }
        return e.evaluate(jc);
    }
}

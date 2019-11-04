package com.kiun.modelcommonsupport.data;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by kiun_2007 on 2018/8/25.
 */

public class BeanExtractor extends ExtractorBase<BeanBase> {

    public BeanExtractor(BeanBase dataSource) {
        super(dataSource);
    }

    @Override
    protected Object extractNoExpression(String path) {

        try {
            Field field = mData.getClass().getField(path);
            if(field != null){
                return field.get(mData);
            }

        }catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            Method method = null;
            try {
                method = mData.getClass().getMethod(upperCase(path));
                if (method == null) {
                    return null;
                }
                return method.invoke(mData);
            } catch (NoSuchMethodException e1) {
                e1.printStackTrace();
            } catch (IllegalAccessException e1) {
                e1.printStackTrace();
            } catch (InvocationTargetException e1) {
                e1.printStackTrace();
            }
        }
        return null;
    }

    public String upperCase(String str) {
        return "get" + str.substring(0, 1).toUpperCase() + str.substring(1);
    }
}

package com.kiun.modelcommonsupport.data.drive;

import android.content.Intent;
import android.text.TextUtils;

import com.kiun.modelcommonsupport.data.BeanBase;
import com.kiun.modelcommonsupport.data.BeanExtractor;
import com.kiun.modelcommonsupport.data.IntentExtractor;
import com.kiun.modelcommonsupport.network.FieldValue;
import com.kiun.modelcommonsupport.utils.MCString;
import com.kiun.modelcommonsupport.utils.TypeCheck;

import org.json.JSONObject;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by kiun_2007 on 2018/7/24.
 */

public class MCDataField {

    public String name;

    public TableDataType type;

    public String className;

    public MCDataField(String name, TableDataType type, String className){
        this.name = name;
        this.type = type;
        this.className = className;
    }

    public static List<Field> getFields(Class objClass){
        return getFields(objClass, (String) null);
    }

    public static List<Field> getFields(Class objClass, final String nokey){
        return getFields(objClass, new String[]{nokey});
    }

    public static  boolean isKeyInExclude(String[] array, String key){

        if(array == null){
            return false;
        }
        if(key.equals("$change")){
            return true;
        }
        for (String exclude : array) {
            if(key.equals(exclude)){
                return true;
            }
        }
        return false;
    }

    public static List<Field> getFields(Class objClass, String[] nokeys){

        ArrayList<Field> allFields = new ArrayList<>();
        Field[] fields = objClass.getFields();
        for(Field field : fields) {
            if(isKeyInExclude(nokeys, field.getName()) || Modifier.isStatic(field.getModifiers())){
                continue;
            }
            allFields.add(field);
        }
        return allFields;
    }

    public static Map<String, Object> objToDict(Object object, String[] onKeys){

        Map dict = new HashMap();
        List<Field> fields = getFields(object.getClass(), onKeys);

        for (Field field : fields) {
            try {
                Object value = field.get(object);
                if(value != null){
                    dict.put(field.getName(), value);
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        for (Method method : object.getClass().getMethods()){
            FieldValue fieldValue = method.getAnnotation(FieldValue.class);
            if(fieldValue != null){
                try {
                    dict.put(fieldValue.fieldName(), method.invoke(object));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        }
        return dict;
    }

    /**
     * 使用MAP填充对象.
     * @param object 对象.
     * @param dict MAP对象.
     */
    public static void fillObject(Object object, Map<String, Object> dict){

        for (Map.Entry<String, Object> entry : dict.entrySet()){
            try {
                Field field = object.getClass().getField(entry.getKey());
                field.set(object, entry.getValue());
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 使用JSON对象填充对象.
     * @param object 目标对象.
     * @param jsonObject JSON对象.
     */
    public static void fillObject(Object object, JSONObject jsonObject){

        List<Field> fields = getFields(object.getClass());

        for (Field field : fields) {
            Object value = jsonObject.opt(field.getName());
            try {
                if(TypeCheck.isValType(field.getType())){
                    if(value != null && "".equals(value)){
                        field.set(object, 0);
                    }
                }else{
                    if(value != null){
                        if(field.getType() == String.class && TypeCheck.isFloat(value)){
                            field.set(object, String.format("%.2f", value));
                        }else if (field.getType() == String.class && TypeCheck.isInteger(value)){
                            field.set(object, String.format("%d", value));
                        }else{
                            field.set(object, value);
                        }
                    }
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    public static void fillBean(BeanBase bean, JSONObject jsonObject){

        Field[] fields = bean.AllFields();
        for (Field field : fields) {
            Object value = jsonObject.opt(field.getName());
            try {
                if(TypeCheck.isValType(field.getType())){ //字段是值类型.
                    if(value instanceof String && TextUtils.isEmpty((String)value)){
                        field.set(bean, 0);
                    }else if (TypeCheck.isValType(value)){
                        if (value instanceof Number){
                            Number number = (Number) value;
                            if(field.getType() == int.class || field.getType() == Integer.class){
                                field.set(bean, number.intValue());
                            }
                            if(field.getType() == double.class || field.getType() == Double.class){
                                field.set(bean, number.doubleValue());
                            }
                            if(field.getType() == float.class || field.getType() == Float.class){
                                field.set(bean, number.floatValue());
                            }
                            if(field.getType() == short.class || field.getType() == Short.class){
                                field.set(bean, number.shortValue());
                            }
                        }
                    }else if (value instanceof String){
                        field.set(bean, MCString.toNumber(value.toString()));
                    }
                }else{
                    if(value != null){ //字段是引用类型.
                        if(field.getType() == String.class && TypeCheck.isFloat(value)){
                            field.set(bean, String.format("%.2f", value));
                        }else if (field.getType() == String.class && TypeCheck.isInteger(value)){
                            field.set(bean, String.format("%d", value));
                        }else{
                            field.set(bean, value);
                        }
                    }
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 使用Intent填充Bean.
     * @param object Bean.
     * @param intent
     */
    public static void fillObject(BeanBase object, Intent intent){

        Method[] methods = object.getClass().getMethods();
        IntentExtractor intentExtractor = new IntentExtractor(intent);
        for (Method method : methods){
            String path = lowerCase(method.getName());
            Object value = intentExtractor.extract(path);

            if(value != null){
                try {
                    method.invoke(object, value);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static String lowerCase(String str) {
        str = str.replace("set", "");
        return str.substring(0, 1).toLowerCase() + str.substring(1);
    }
}

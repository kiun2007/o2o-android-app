package com.kiun.modelcommonsupport.data;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;

import com.kiun.modelcommonsupport.data.drive.MCDataField;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by kiun_2007 on 2018/4/19.
 */

public class BeanBase {

    Field[] myFields;

    public void viewChange(View view){
    }

    public Map<String,Object> allMap(){
        Map<String,Object> dict = new HashMap();

        Field[] allFields = AllFields();
        for (Field field : allFields){
            try {
                dict.put(field.getName(), field.get(this));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return dict;
    }

    public Field[] AllFields(){
        if(myFields != null){
            return myFields;
        }
        List<Field> fieldList = new ArrayList<>();
        Field[] allFields = this.getClass().getDeclaredFields();
        for (Field field : allFields){
            boolean isStatic = Modifier.isStatic(field.getModifiers());
            if(!isStatic){
                field.setAccessible(true);
                fieldList.add(field);
            }
        }
        myFields = new Field[fieldList.size()];
        fieldList.toArray(myFields);
        return myFields;
    }
}

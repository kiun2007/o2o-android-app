package com.kiun.modelcommonsupport.adapter;

import android.view.ViewParent;
import android.widget.CompoundButton;

import com.kiun.modelcommonsupport.ui.views.ACheckBox;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kiun_2007 on 2018/8/18.
 */

public class ItemManager implements CompoundButton.OnCheckedChangeListener {

    private int selectNumber = 3;
    private List<JSONObject>  jsonObjects = new ArrayList<>();
    private boolean isMutex = false;
    private ViewParent viewParent;

    public ItemManager(int maxSelected){
        selectNumber = maxSelected;
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

        if(!(compoundButton instanceof ACheckBox)){
            return;
        }

        ACheckBox checkBox = (ACheckBox) compoundButton;
        JSONObject jsonObject = (JSONObject) checkBox.getData();
        if(isMutex){
            if(viewParent == null){
                viewParent = checkBox.getParent();
            }else {
                if(viewParent != checkBox.getParent()){
                    compoundButton.setChecked(false);
                    return;
                }
            }
        }

        if(b){
            if(selectNumber <= 0){
                compoundButton.setChecked(false);
                return;
            }
            selectNumber --;
            jsonObjects.add(jsonObject);
        }else {
            selectNumber ++;
            jsonObjects.remove(jsonObject);
            if (jsonObjects.isEmpty()){
                viewParent = null;
            }
        }
    }

    public List<JSONObject> allSelectedItems(){
        return jsonObjects;
    }

    public String splicingFromKey(String key, String splicingStr){

        if(jsonObjects.size() == 0){
            return null;
        }

        StringBuilder stringBuilder = new StringBuilder(50000);
        for (JSONObject jsonObject : jsonObjects){
            String value = jsonObject.optString(key);
            if(value == null){
                return null;
            }
            stringBuilder.append(value);
            stringBuilder.append(splicingStr);
        }

        return stringBuilder.toString();
    }

    public void setMutex(boolean mutex) {
        isMutex = mutex;
    }
}

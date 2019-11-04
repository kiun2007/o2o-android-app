package com.kiun.modelcommonsupport.ui.views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.CheckBox;

import com.kiun.modelcommonsupport.R;
import com.kiun.modelcommonsupport.data.KeyValue;
import com.kiun.modelcommonsupport.data.drive.DriveListener;

import org.json.JSONObject;

/**
 * Created by kiun_2007 on 2018/8/12.
 */

@SuppressLint("AppCompatCustomView")
public class ACheckBox extends CheckBox implements DriveListener{

    private String dataPath = null;
    private String dataTitle = null;
    private String dataValue = null;
    private Object data;

    public ACheckBox(Context context) {
        this(context, null);
    }

    public ACheckBox(Context context, AttributeSet attrs) {
        this(context, attrs, android.R.attr.checkboxStyle);
    }

    public ACheckBox(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.ACheckBox);
        dataPath = array.getString(R.styleable.ACheckBox_dataPath);
        dataTitle = array.getString(R.styleable.ACheckBox_dataTitle);
        dataValue = array.getString(R.styleable.ACheckBox_dataValue);
    }

    @Override
    public String getPath() {
        return dataPath;
    }

    @Override
    public void fill(Object data) {
        if(dataPath != null && dataTitle != null){
            this.data = data;
            if(data instanceof JSONObject){
                setText(((JSONObject)data).optString(dataTitle));
            }
        }
    }

    public Object getData(){
        return data;
    }

    @Override
    public int eventTag() {
        return -1;
    }

    @Override
    public KeyValue getParam() {
        return null;
    }
}

package com.kiun.modelcommonsupport.ui.views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;

import com.kiun.modelcommonsupport.R;
import com.kiun.modelcommonsupport.data.KeyValue;
import com.kiun.modelcommonsupport.data.drive.DriveListener;
import com.kiun.modelcommonsupport.network.MethodType;


/**
 * Created by kiun_2007 on 2018/4/14.
 * 自动按钮.
 */
@SuppressLint("AppCompatCustomView")
public class AButton extends Button implements DriveListener, ActionView<SubmitListener> {

    private SubmitListener listener = null;
    private String mSubmitUrl = null;
    private String dataPath = null;
    private MethodType methodType = MethodType.POST;
    public AButton(Context context) {
        this(context, null);
    }
    public AButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.AButton);

        dataPath = array.getString(R.styleable.AButton_dataPath);
        mSubmitUrl = array.getString(R.styleable.AButton_netUrl);
        methodType = MethodType.valueOf(array.getInt(R.styleable.AButton_method, 0));

        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(listener != null){
                    if(mSubmitUrl == null){
                        listener.onClick(v);
                    }else{
                        listener.onSubmit(v, mSubmitUrl, methodType);
                    }
                }
            }
        });
    }

    @Override
    public String getPath() {
        return dataPath;
    }

    @Override
    public void fill(Object data) {
    }

    @Override
    public int eventTag() {
        return 0;
    }

    @Override
    public KeyValue getParam() {
        return null;
    }

    @Override
    public void setEventListener(SubmitListener listener) {
        this.listener = listener;
    }
}

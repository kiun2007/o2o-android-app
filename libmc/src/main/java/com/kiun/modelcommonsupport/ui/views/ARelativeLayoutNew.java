package com.kiun.modelcommonsupport.ui.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.kiun.modelcommonsupport.R;
import com.kiun.modelcommonsupport.data.KeyValue;
import com.kiun.modelcommonsupport.data.drive.DriveListener;

/**
 * Created by kiun_2007 on 2018/4/14.
 */
public class ARelativeLayoutNew extends RelativeLayout implements DriveListener {

    private String dataPath = null;
    private int eventTag = -1;

    public ARelativeLayoutNew(Context context) {
        this(context, null);
    }
    public ARelativeLayoutNew(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ARelativeLayoutNew(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.ALinearLayout);

        dataPath = array.getString(R.styleable.ALinearLayout_dataPath);
        eventTag = array.getInteger(R.styleable.ALinearLayout_eventTag, -1);
    }

    @Override
    public String getPath() {
        return dataPath;
    }

    @Override
    protected void attachViewToParent(View child, int index, ViewGroup.LayoutParams params) {
        super.attachViewToParent(child, index, params);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
    }

    @Override
    public void fill(Object data) {
    }

    @Override
    public int eventTag() {
        return eventTag;
    }

    @Override
    public KeyValue getParam() {
        return null;
    }

}

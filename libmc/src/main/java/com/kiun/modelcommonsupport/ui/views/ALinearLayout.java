package com.kiun.modelcommonsupport.ui.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import com.kiun.modelcommonsupport.R;
import com.kiun.modelcommonsupport.data.KeyValue;
import com.kiun.modelcommonsupport.data.drive.DriveBase;
import com.kiun.modelcommonsupport.data.drive.DriveListener;
import com.kiun.modelcommonsupport.utils.MCString;
import com.kiun.modelcommonsupport.utils.ViewUtil;

/**
 * Created by kiun_2007 on 2018/4/14.
 */
public class ALinearLayout extends LinearLayout implements DriveListener {

    private String dataPath = null;
    private String goneFormula = null;
    private int checkMax = 0;
    private ATextView tintLabel = null;
    private int eventTag = -1;
    Path mPath = new Path();
    RectF mRect = new RectF();
    int radius = 0;
    float ratio = 0;

    public ALinearLayout(Context context) {
        this(context, null);
    }
    public ALinearLayout(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ALinearLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.ALinearLayout);

        dataPath = array.getString(R.styleable.ALinearLayout_dataPath);
        goneFormula = array.getString(R.styleable.ALinearLayout_goneFormula);
        checkMax = array.getInteger(R.styleable.ALinearLayout_checkMax, 0);
        eventTag = array.getInteger(R.styleable.ALinearLayout_eventTag, -1);
        radius = array.getDimensionPixelOffset(R.styleable.ALinearLayout_radius, 0);
        ratio = array.getFloat(R.styleable.ALinearLayout_ratio, 0);
        int labelResid = array.getResourceId(R.styleable.ALinearLayout_tintColorLabel, -1);
        if(labelResid > -1){
        }
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
        if(goneFormula == null){
            return;
        }
        setVisibility(ViewUtil.getVisibleByFormula(data, goneFormula));
    }

    @Override
    public int eventTag() {
        return eventTag;
    }

    @Override
    public KeyValue getParam() {
        return null;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mPath.reset();
        mRect.set(0, 0, w, h);
        mPath.addRoundRect(mRect, radius, radius, Path.Direction.CW);
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        DriveBase.disableSubControls(this, isEnabled());
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        canvas.save();
        canvas.clipPath(mPath);
        super.dispatchDraw(canvas);
        canvas.restore();
    }
}

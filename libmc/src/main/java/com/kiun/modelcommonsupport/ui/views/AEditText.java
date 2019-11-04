package com.kiun.modelcommonsupport.ui.views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.text.Layout;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.EditText;

import com.kiun.modelcommonsupport.R;
import com.kiun.modelcommonsupport.data.KeyValue;
import com.kiun.modelcommonsupport.data.drive.DriveListener;
import com.qiniu.android.bigdata.pipeline.Points;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.internal.cache.DiskLruCache;

/**
 * Created by kiun_2007 on 2018/4/14.
 */

@SuppressLint("AppCompatCustomView")
public class AEditText extends EditText implements DriveListener{

    private String dataPath = null;
    private String paramName = null;
    private String matchExp = null;
    private String matchError = null;
    private Drawable drawable = null;
    private String hintText = null;

    public AEditText(Context context) {
        this(context, null);
    }

    public AEditText(Context context, AttributeSet attrs) {
        this(context, attrs, android.R.attr.editTextStyle);
    }

    public AEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.AEditText);
        dataPath = array.getString(R.styleable.AEditText_dataPath);
        paramName = array.getString(R.styleable.AEditText_paramName);
        matchExp = array.getString(R.styleable.AEditText_matchExp);
        matchError = array.getString(R.styleable.AEditText_matchError);
        drawable = array.getDrawable(R.styleable.AEditText_hintIcon);
        hintText = array.getString(R.styleable.AEditText_hintText);

        array.recycle();
    }

    @Override
    public String getPath() {
        return dataPath;
    }

    @Override
    public void fill(Object data) {
        if (data instanceof String){
            this.setText(data.toString());
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (getText().length() == 0){
            Paint paint = new Paint();
            paint.setTextSize(getTextSize());
            paint.setColor(0xFF999999);
            paint.setStyle(Paint.Style.STROKE);
            if(drawable != null){
                drawable.setBounds(getPaddingLeft(), getPaddingTop() + 3,
                        getPaddingLeft() + (int) getTextSize(),
                        getPaddingTop() + (int) getTextSize() + 3);
                drawable.draw(canvas);
            }

            if(hintText != null){
                canvas.drawText(hintText, getPaddingLeft() + getTextSize() + 5, getPaddingTop() + getTextSize(), paint);
            }
        }
    }

    @Override
    public int eventTag() {
        return -1;
    }

    @Override
    public KeyValue getParam() {
        String error = null;
        if(matchExp != null){
            Pattern pattern = Pattern.compile(matchExp);
            if(!TextUtils.isEmpty(hintText) && TextUtils.isEmpty(getText())){
                return new KeyValue(dataPath, null, hintText);
            }
            Matcher matcher = pattern.matcher(this.getText().toString());
            if(!matcher.matches()){
                if(matchError != null){
                    return new KeyValue(dataPath, null, matchError);
                }else{
                    return new KeyValue(dataPath, null, "请输入正确的值");
                }
            }
        }

        if(paramName != null){
            return new KeyValue(paramName, this.getText().toString());
        }
        return new KeyValue(dataPath, this.getText().toString());
    }
}

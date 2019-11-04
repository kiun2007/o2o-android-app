package com.kiun.modelcommonsupport.ui.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.View;

import com.kiun.modelcommonsupport.R;
import com.kiun.modelcommonsupport.data.KeyValue;
import com.kiun.modelcommonsupport.data.drive.DriveListener;

public class ADrawView extends View implements DriveListener {

    int radius;
    String dataPath;
    String[] colors;
    int backgroundColor;

    public ADrawView(Context context) {
        super(context);
    }

    public ADrawView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context, attrs);
    }

    public ADrawView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context, attrs);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public ADrawView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initView(context, attrs);
    }

    private void initView(Context context, @Nullable AttributeSet attrs){

        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.ADrawView);
        dataPath = array.getString(R.styleable.ADrawView_dataPath);
        radius = array.getDimensionPixelOffset(R.styleable.ADrawView_radius, 0);
        String colorString = array.getString(R.styleable.ADrawView_colors);
        if(colorString != null) {
            colors = colorString.split(",");
        }
        array.recycle();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Paint paint = new Paint();
        paint.setColor(backgroundColor);
        paint.setStrokeWidth(1);
        canvas.drawRoundRect(new RectF(0,0,this.getWidth(), this.getHeight()), radius, radius, paint);
    }

    @Override
    public String getPath() {
        return dataPath;
    }

    @Override
    public void fill(Object data) {
        if (data instanceof Integer && colors != null && colors.length > 0){
            Integer indexPos = (Integer) data;
            int index = indexPos % colors.length;
            String str = colors[index];
            int color = 0;
            if(str.startsWith("0x")){
                String colorStr = str.substring(2);
                color = (int) Long.parseLong(colorStr, 16);
            }else{
                color = Integer.parseInt(str);
            }
            backgroundColor = color;
            postInvalidate();
        }
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

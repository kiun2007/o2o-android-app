package com.xzxx.decorate.o2o.view;

import android.content.Context;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.widget.LinearLayout;

public class LocationMeView extends LinearLayout {

    public LocationMeView(Context context) {
        super(context);
    }
    public LocationMeView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }
    public LocationMeView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public LocationMeView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int height = getHeight();
        setTop(t + height / 2);
        setBottom(b + height / 2);
        super.onLayout(changed, l, t, r, b);
    }

}

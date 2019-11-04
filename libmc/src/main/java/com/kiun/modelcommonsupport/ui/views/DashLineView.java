package com.kiun.modelcommonsupport.ui.views;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by kiun_2007 on 2018/9/12.
 */

public class DashLineView extends View {
    public DashLineView(Context context) {
        super(context);
        initView();
    }

    public DashLineView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public DashLineView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView(){
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);
    }
}

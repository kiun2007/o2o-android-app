package com.kiun.modelcommonsupport.ui.views;

import android.content.Intent;
import android.view.View;

/**
 * Created by kiun_2007 on 2018/8/16.
 */

public interface ACellViewEventer {

    public static final int NO_Activity = 0;
    public static final int NOMAL_Activity = 1;
    public static final int RESULT_Activity = 2;

    /**
     * 发起调用Activity之前填充参数.
     * @param intent 传递参数对象.
     * @param tag 区分按钮.
     * @return 返回0将不会调用Activity, 返回1调用Activity,返回2 回调的Activity.
     */
    int fillParams(Intent intent, int tag);

    void onCellClick(View targetView, int tag);
}

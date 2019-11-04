package com.kiun.modelcommonsupport.adapter;

import android.view.View;

/**
 * Created by kiun_2007 on 2018/8/19.
 */

public interface ListItenListener extends ItemListener {

    void onItemDataChanged(View view, Object data);
}

package com.kiun.modelcommonsupport.ui.views;

import android.graphics.Bitmap;

/**
 * Created by kiun_2007 on 2018/8/21.
 */

public interface MediaChanger {

    public static final int MEDIA_INPUT_TAG = 0x1001;
    public static final String MEDIA_INPUT_ID = "MEDIA_INPUT_ID";

    void onMediaEnter(String imageUrl, String videoUrl);
}

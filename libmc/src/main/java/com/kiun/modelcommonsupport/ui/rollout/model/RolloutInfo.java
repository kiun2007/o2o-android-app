package com.kiun.modelcommonsupport.ui.rollout.model;

import android.graphics.Bitmap;

import java.io.Serializable;

/**
 * Created by arthur on 2017/3/13.
 * 可以继承它改变属性
 */

public class RolloutInfo implements Serializable {

    /**
     * 图片路径，如果是视频，则是视频缩略图.
     */
    public String url;

    /**
     * 视频本地路径.
     */
    public String videoUrl;
    /**
     * 图片本身的宽 和 高,（清晰显示前的宽高），可以自己设定可以根据屏幕设定，可以根据图片大小设定
     */
    public float width;
    public float height;

    public boolean isUpload = false;
}

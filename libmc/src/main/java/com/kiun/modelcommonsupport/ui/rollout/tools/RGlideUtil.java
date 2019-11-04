package com.kiun.modelcommonsupport.ui.rollout.tools;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

/**
 * Created by arthur on 2017/3/13.
 * glide 工具
 */

public class RGlideUtil {
    /**
     * 加载图片
     *
     * @param context
     *         上下文
     * @param url
     *         路径
     * @param imageView
     *         view
     */
    public static void setImage(Context context, String url, ImageView imageView) {
        //        Glide.with(context).load(url).centerCrop().into(imageView);
        RequestOptions requestOptions = new RequestOptions()
                .centerCrop();

        Glide.with(context)
                .asBitmap()//强制指定加载静态图片
                .load(url)
                .apply(requestOptions)
                .into(imageView);
    }

    /**
     * 清楚内存缓存,必须在UI线程调用
     */
    public static void clearMemory(Context context) {
        Glide.get(context).clearMemory();
    }
}

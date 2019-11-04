package com.kiun.modelcommonsupport.utils;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.view.View;
import android.view.animation.Animation;

/**
 *@ClassName MCAnimationUtil
 *@Description TODO
 *@author konghui
 *@date 2018-08-31 00:32
 *@version 1.0
 **/
public class MCAnimationUtil{

    public static void translateUp(Context context, View view) {
        ObjectAnimator
                .ofFloat(view, "translationY", view.getMeasuredHeight(), 0)
                .setDuration(300).start();

    }

    public static void translateDown(Context context, View view, final MCAnimationListener listener) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(view, "translationY",
                0, view.getMeasuredHeight()).setDuration(300);
        animator.addListener(new Animator.AnimatorListener() {

            @Override
            public void onAnimationStart(Animator animation) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onAnimationRepeat(Animator animation) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (listener != null) {
                    listener.animationFinish();
                }

            }

            @Override
            public void onAnimationCancel(Animator animation) {
                // TODO Auto-generated method stub

            }
        });
        animator.start();

    }

}

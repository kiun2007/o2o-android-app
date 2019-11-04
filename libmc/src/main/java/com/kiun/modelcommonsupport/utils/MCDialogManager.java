package com.kiun.modelcommonsupport.utils;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.kiun.modelcommonsupport.R;
import com.kiun.modelcommonsupport.adapter.ItemListener;
import com.kiun.modelcommonsupport.controllers.BaseRequestAcitity;
import com.kiun.modelcommonsupport.data.drive.DriveBase;
import com.kiun.modelcommonsupport.data.drive.DriveListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kiun_2007 on 2018/8/12.
 */

public class MCDialogManager {

    private Context mContext;
    private View mView;
    private ItemListener listener;
    AlertDialog alertDialog;

    public static final int ICON_PRO = -100;

    public static final int TAG_CANCEL_BTN = -1;

    /**
     * 左边按钮.
     */
    public static final int TAG_LEFT_BTN = 1;

    /**
     * 中间按钮.
     */
    public static final int TAG_MIDDLE_BTN = 2;

    /**
     * 右边按钮.
     */
    public static final int TAG_RIGHT_BTN = 3;


    private MCDialogManager(Context context, int resId, final Object data) {

        if (context instanceof BaseRequestAcitity) {
            if (((BaseRequestAcitity) context).isDead()) {
                return;
            }
        }

        alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.show();
        alertDialog.setContentView(resId);
        alertDialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);


        Window window = alertDialog.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawableResource(android.R.color.white);
        window.setGravity(Gravity.BOTTOM);
        window.setWindowAnimations(R.style.AlertDialog_AppCompat);
        mView = window.getDecorView();
        if (data != null) {
            DriveBase.fillViewData(mView, data, false);
        }

        alertDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                if (listener != null) {
                    listener.onItemClick(mView, null, TAG_CANCEL_BTN);
                }
            }
        });

        List<DriveListener> driveListeners = new ArrayList<>();
        DriveBase.findLoop((ViewGroup) mView, driveListeners, null);

        for (final DriveListener driveListener : driveListeners) {
            if (driveListener.eventTag() > -1) {
                final View eventView = (View) driveListener;
                eventView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        alertDialog.dismiss();
                        if (listener != null) {
                            listener.onItemClick(eventView, null, driveListener.eventTag());
                        }
                    }
                });
            }
        }
    }

    public static MCDialogManager show(Context context, int resId, Object data) {
        return new MCDialogManager(context, resId, data);
    }

    public final <T extends View> T getViewById(int resId) {
        return mView.findViewById(resId);
    }

    public void fillToView(Object data) {
        DriveBase.fillViewData(mView, data, false);
    }

    public MCDialogManager setListener(ItemListener listener) {
        this.listener = listener;
        return this;
    }

    public static MCDialogManager error(Context context, String errorInfo) {
        return showMessage(context, "", errorInfo, "好的", R.drawable.svg_fail);
    }

    public static MCDialogManager info(Context context, String info) {
        return showMessage(context, "提示", info, "好的", R.drawable.svg_laugh);
    }

    public static MCDialogManager showMessage(Context context, String title, String content, String right, int iconId) {
        return showMessage(context, title, content, right, null, iconId);
    }

    public static MCDialogManager showMessage(Context context, String title, String content, String right, String left, int iconId) {
        return showMessage(context, title, content, right, null, left, iconId);
    }

    public static MCDialogManager showMessage(Context context, String title, String content, String right, String middle, String left, int iconId) {
        MCDialogManager dialogManager = new MCDialogManager(context, R.layout.dialog_message, null);
        try {
            TextView titleTV = dialogManager.getViewById(R.id.titleTV);
            TextView contentTV = dialogManager.getViewById(R.id.contentTV);
            TextView mssageboxBtnLeft = dialogManager.getViewById(R.id.mssageboxBtnLeft);
            TextView mssageboxBtnMed = dialogManager.getViewById(R.id.mssageboxBtnMed);
            TextView mssageboxBtnRight = dialogManager.getViewById(R.id.mssageboxBtnRight);
            ImageView imageView = dialogManager.getViewById(R.id.iconTitleIV);
            ProgressBar progressBar = dialogManager.getViewById(R.id.progressBar);

            if (iconId > 0) {
                imageView.setImageDrawable(context.getResources().getDrawable(iconId));
            } else {
                if (iconId == ICON_PRO) {
                    imageView.setVisibility(View.GONE);
                    progressBar.setVisibility(View.VISIBLE);
                }
            }

            if (TextUtils.isEmpty(title)) {
                titleTV.setVisibility(View.GONE);
            } else {
                titleTV.setVisibility(View.VISIBLE);
            }
            titleTV.setText(title);
            contentTV.setText(content);
            if (left != null) {
                mssageboxBtnLeft.setText(left);
            } else {
                ((ViewGroup) mssageboxBtnLeft.getParent()).setVisibility(View.GONE);
            }
            if (middle != null) {
                mssageboxBtnMed.setText(middle);
            } else {
                ((ViewGroup) mssageboxBtnMed.getParent()).setVisibility(View.GONE);
            }
            if (right != null) {
                mssageboxBtnRight.setText(right);
            } else {
                mssageboxBtnRight.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dialogManager;
    }

    public void dismiss() {
        alertDialog.dismiss();
    }
}

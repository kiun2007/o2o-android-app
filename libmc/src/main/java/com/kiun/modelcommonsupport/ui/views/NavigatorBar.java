package com.kiun.modelcommonsupport.ui.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.amos.modulebase.utils.ScreenUtil;
import com.kiun.modelcommonsupport.R;

/**
 * DeviceService
 * popchain
 *
 * @author kiun_2007 on 2018/3/29.
 * Copyright (c) 2017-2018 The Popchain Core Developers
 */
public class NavigatorBar extends LinearLayout {

    public static final String TAG = "NAVIGATOR_BAR";

    private ImageView leftButton;
    private ImageView rightImageView;
    private TextView titleTextView;
    private TextView leftTitleTextView;
    private TextView rightTextView;
    private TextView unread_msg_number;
    private LinearLayout rightButton;
    private LinearLayout leftButtonLayout;
    private NavigatorListener mListener;

    public void setListener(NavigatorListener listener) {
        mListener = listener;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public NavigatorBar(Context context) {
        this(context, null);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public NavigatorBar(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public NavigatorBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater.from(context).inflate(R.layout.action_bar, this);
        leftButtonLayout = findViewById(R.id.leftButtonLayout);
        leftButton = findViewById(R.id.backImageView);
        rightImageView = findViewById(R.id.rightImageView);
        titleTextView = findViewById(R.id.titleTextView);
        rightButton = findViewById(R.id.rightButtonLL);
        rightTextView = findViewById(R.id.rightTitleTextView);
        unread_msg_number = findViewById(R.id.unread_msg_number);

        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.NavigatorBar);
        String title = array.getString(R.styleable.NavigatorBar_barTitle);
        if (title != null) {
            titleTextView.setText(title);
        }

        int resId = array.getResourceId(R.styleable.NavigatorBar_barRightImage, -1);

        if (resId != -1) {
            rightImageView.setVisibility(View.VISIBLE);
            rightImageView.setImageDrawable(getResources().getDrawable(resId));
            rightButton.setVisibility(VISIBLE);
        } else {
            rightImageView.setVisibility(View.GONE);
        }

        Drawable drawable = array.getDrawable(R.styleable.NavigatorBar_barLeftImage);
        if (drawable != null) {
            leftButton.setImageDrawable(drawable);
        }

        drawable = array.getDrawable(R.styleable.NavigatorBar_titleImage);
        if (drawable != null) {
            titleTextView.setBackground(drawable);
        }


        boolean isNoBack = array.getBoolean(R.styleable.NavigatorBar_barNoback, false);
        if (leftButtonLayout != null) {

            leftButtonLayout.setOnTouchListener(new UserTouchListener());
            leftButtonLayout.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mListener != null) {
                        mListener.onLeftClick();
                    }
                }
            });
            leftButton.setVisibility(isNoBack ? View.GONE : View.VISIBLE);
        }

        String leftTitle = array.getString(R.styleable.NavigatorBar_barLeftTitle);
        if (leftTitle != null) {
            leftTitleTextView = findViewById(R.id.leftTitleTextView);
            leftTitleTextView.setText(leftTitle);
            leftTitleTextView.setVisibility(VISIBLE);
        }

        String rightTitle = array.getString(R.styleable.NavigatorBar_barRightTitle);
        if (rightTitle != null) {
            rightButton.setVisibility(VISIBLE);
            rightTextView.setVisibility(VISIBLE);
            rightTextView.setText(rightTitle);
        }

        if (rightButton != null) {
            rightButton.setOnTouchListener(new UserTouchListener());
            rightButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mListener != null) {
                        mListener.onRightClick();
                    }
                }
            });
        }

        int type = array.getInteger(R.styleable.NavigatorBar_barStyle, 0);
        switch (type) {
            case 2:
                titleTextView.setTextColor(0xFF000000);
                rightTextView.setTextColor(0xFF000000);
                titleTextView.setTextColor(0xFF000000);
                leftButton.setImageDrawable(getResources().getDrawable(R.drawable.svg_left_arrow_black));
            case 1:
                this.findViewById(R.id.mainLinear).setBackgroundResource(0);
                break;
        }
        setTag(TAG);
    }

    public LinearLayout getRightButton() {
        return rightButton;
    }

    public void setTitle(String title) {
        titleTextView.setText(title);
    }

    public void setTitleAndDraw(String title, int resId) {
        Drawable drawable = getResources().getDrawable(resId);
        titleTextView.setCompoundDrawablesWithIntrinsicBounds(null, null, drawable, null);
        titleTextView.setCompoundDrawablePadding(ScreenUtil.dip2px(5));
        titleTextView.setText(title);
        titleTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 17);
        titleTextView.setMaxEms(11);
        titleTextView.setMaxLines(1);
    }

    public void setTitleListener(OnClickListener listener) {
        titleTextView.setOnClickListener(listener);
    }

    public void setTitle(int title) {
        titleTextView.setText(title);
    }

    public void setLeftButtonVisibility(int visibility) {
        leftButton.setVisibility(visibility);
    }

    public void setRightButtonText(String text) {
        rightTextView.setText(text);
    }

    public void setRightButtonImg(int text) {
        rightImageView.setImageResource(text);
    }

    public void setLeftButtonText(String text) {
        leftTitleTextView.setText(text);
    }

    public void setUnRead(String text) {
        unread_msg_number.post(new Runnable() {
            @Override
            public void run() {
                if (TextUtils.isEmpty(text)) {
                    unread_msg_number.setVisibility(GONE);
                } else {
                    unread_msg_number.setVisibility(VISIBLE);
                }
                unread_msg_number.setText(text);
            }
        });
    }


}

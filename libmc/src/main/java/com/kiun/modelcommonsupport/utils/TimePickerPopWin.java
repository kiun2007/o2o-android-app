package com.kiun.modelcommonsupport.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.PopupWindow;

import com.kiun.modelcommonsupport.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class TimePickerPopWin extends PopupWindow implements View.OnClickListener {

    private Button cancelBtn;
    private Button confirmBtn;
    private LoopView hourLoopView;
    private LoopView minuteLoopView;
    private LoopView meridianLoopView;
    private View pickerContainerV;
    private View contentView;

    private int hourPos = 0;
    private int minutePos = 0;
    private int meridianPos = 0;

    private Context mContext;
    private String textCancel;
    private String textConfirm;
    private int colorCancel;
    private int colorConfirm;
    private int btnTextsize;
    private int viewTextSize;

    List<String> hourList = new ArrayList();
    List<String> minList = new ArrayList();
    List<String> meridianList = new ArrayList();

    public static class Builder {
        private Context context;
        private OnTimePickListener listener;
        private Calendar calendar;

        public Builder(Context context, OnTimePickListener listener) {
            this.context = context;
            this.listener = listener;
            this.calendar = Calendar.getInstance();
        }

        //Optional Parameters
        private String textCancel = "Cancel";
        private String textConfirm = "Confirm";
        private int colorCancel = Color.parseColor("#999999");
        private int colorConfirm = Color.parseColor("#FF7D00");
        private int btnTextSize = 16;//text btnTextsize of cancel and confirm button
        private int viewTextSize = 25;

        public Builder textCancel(String textCancel) {
            this.textCancel = textCancel;
            return this;
        }

        public Builder textConfirm(String textConfirm) {
            this.textConfirm = textConfirm;
            return this;
        }

        public Builder colorCancel(int colorCancel) {
            this.colorCancel = colorCancel;
            return this;
        }

        public Builder colorConfirm(int colorConfirm) {
            this.colorConfirm = colorConfirm;
            return this;
        }

        public Builder btnTextSize(int textSize) {
            this.btnTextSize = textSize;
            return this;
        }

        public Builder viewTextSize(int textSize) {
            this.viewTextSize = textSize;
            return this;
        }

        public Builder setCalendar(Calendar calendar){
            this.calendar = calendar;
            return this;
        }

        public TimePickerPopWin build() {
            return new TimePickerPopWin(this);
        }
    }

    private Builder builder;

    public TimePickerPopWin(Builder builder) {
        this.textCancel = builder.textCancel;
        this.textConfirm = builder.textConfirm;
        this.mContext = builder.context;
        this.mListener = builder.listener;
        this.colorCancel = builder.colorCancel;
        this.colorConfirm = builder.colorConfirm;
        this.btnTextsize = builder.btnTextSize;
        this.viewTextSize = builder.viewTextSize;
        this.builder = builder;
        initView();
    }

    private OnTimePickListener mListener;

    private void initView() {
        contentView = LayoutInflater.from(mContext).inflate(R.layout.layout_time_picker, null);
        cancelBtn = (Button) contentView.findViewById(R.id.btn_cancel);
        cancelBtn.setTextColor(colorCancel);
        cancelBtn.setTextSize(btnTextsize);
        confirmBtn = (Button) contentView.findViewById(R.id.btn_confirm);
        confirmBtn.setTextColor(colorConfirm);
        confirmBtn.setTextSize(btnTextsize);
        hourLoopView = (LoopView) contentView.findViewById(R.id.picker_hour);
        minuteLoopView = (LoopView) contentView.findViewById(R.id.picker_minute);
        meridianLoopView = (LoopView) contentView.findViewById(R.id.picker_meridian);
        pickerContainerV = contentView.findViewById(R.id.container_picker);
        meridianLoopView.setVisibility(View.GONE);

        hourLoopView.setLoopListener(new LoopScrollListener() {
            @Override
            public void onItemSelect(int item) {
                hourPos = item;
            }
        });

        minuteLoopView.setLoopListener(new LoopScrollListener() {
            @Override
            public void onItemSelect(int item) {
                minutePos = item;
            }
        });

        meridianLoopView.setLoopListener(new LoopScrollListener() {
            @Override
            public void onItemSelect(int item) {
                meridianPos = item;
            }
        });

        initPickerViews();  // init hour and minute loop view


        cancelBtn.setOnClickListener(this);
        confirmBtn.setOnClickListener(this);
        contentView.setOnClickListener(this);

        if (!TextUtils.isEmpty(textConfirm)) {
            confirmBtn.setText(textConfirm);
        }

        if (!TextUtils.isEmpty(textCancel)) {
            cancelBtn.setText(textCancel);
        }

        setTouchable(true);
        setFocusable(true);

        setBackgroundDrawable(new BitmapDrawable());
        //        setAnimationStyle(R.style.FadeInPopWin);
        setContentView(contentView);
        setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
    }

    private void initPickerViews() {

        meridianPos = this.builder.calendar.get(Calendar.AM_PM);
        hourPos = this.builder.calendar.get(Calendar.HOUR) + meridianPos * 12;
        minutePos = this.builder.calendar.get(Calendar.MINUTE);

        minutePos = (minutePos / 10);

        if (minutePos >= 6) {
            hourPos += 1;
        }

        minutePos %= 6;

        //        for (int i = 1; i <= 12; i++) {
        for (int i = 0; i < 24; i++) {
            hourList.add(String.valueOf(i));
        }

        for (int j = 0; j < 60; j += 10) {
            minList.add(format2LenStr(j));
        }

        meridianList.add("上午");
        meridianList.add("下午");

        hourLoopView.setDataList(hourList);
        hourLoopView.setInitPosition(hourPos);

        minuteLoopView.setDataList(minList);
        minuteLoopView.setInitPosition(minutePos);

        meridianLoopView.setDataList(meridianList);
        meridianLoopView.setInitPosition(meridianPos);
    }


    @Override
    public void onClick(View v) {

        if (v == contentView || v == cancelBtn) {
            dismissPopWin();
        } else if (v == confirmBtn) {

            if (null != mListener) {
                String amPm = meridianList.get(meridianPos);
                StringBuffer sb = new StringBuffer();
                sb.append(String.valueOf(hourList.get(hourPos)));
                sb.append(":");
                sb.append(String.valueOf(minList.get(minutePos)));
                //                sb.append(amPm);
                mListener.onTimePickCompleted(hourPos + 1, minutePos, amPm, sb.toString());
            }
            dismissPopWin();
        }
    }

    /**
     * Show time picker popWindow
     *
     * @param activity
     */
    public void showPopWin(Activity activity) {

        if (null != activity) {

            TranslateAnimation trans = new TranslateAnimation(
                    Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF,
                    0, Animation.RELATIVE_TO_SELF, 1,
                    Animation.RELATIVE_TO_SELF, 0);

            showAtLocation(activity.getWindow().getDecorView(), Gravity.BOTTOM,
                    0, 0);
            trans.setDuration(400);
            trans.setInterpolator(new AccelerateDecelerateInterpolator());

            pickerContainerV.startAnimation(trans);
        }
    }

    /**
     * Dismiss time picker popWindow
     */
    public void dismissPopWin() {

        TranslateAnimation trans = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0,
                Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 1);

        trans.setDuration(400);
        trans.setInterpolator(new AccelerateInterpolator());
        trans.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {

                dismiss();
            }
        });

        pickerContainerV.startAnimation(trans);
    }

    /**
     * Transform int to String with prefix "0" if less than 10
     *
     * @param num
     */
    public static String format2LenStr(int num) {

        return (num < 10) ? "0" + num : String.valueOf(num);
    }

    public interface OnTimePickListener {

        /**
         * Listener when date been selected
         *
         * @param time
         */
        void onTimePickCompleted(int hour, int minute, String AM_PM, String time);
    }
}

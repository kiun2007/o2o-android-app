package com.xzxx.decorate.o2o.view;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xzxx.decorate.o2o.consumer.R;

import java.util.List;

public class VerticalStepBlanceView extends LinearLayout implements VerticalStepViewIndicator.OnDrawIndicatorListener {
    private RelativeLayout mTextContainer;
    private RelativeLayout mTimeTextContainer;
    private VerticalStepViewIndicator mStepsViewIndicator;

    private List<String> mTexts;

    private List<String> mTextTime;

    private int mComplectingPosition;

    private int mUnComplectedTextColor = ContextCompat.getColor(getContext(), R.color.gray);

    private int mComplectingTextColor = ContextCompat.getColor(getContext(), R.color.orange);

    private int mComplectedTextColor = ContextCompat.getColor(getContext(), R.color.black);

    private int mTextSize = 14;//default textSize
    private TextView mTextView;
    private TextView mTimeTextView;

    public VerticalStepBlanceView(Context context) {
        this(context, null);
    }

    public VerticalStepBlanceView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VerticalStepBlanceView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        View rootView = LayoutInflater.from(getContext()).inflate(R.layout.widget_vertical_stepsview, this);
        mStepsViewIndicator = (VerticalStepViewIndicator) rootView.findViewById(R.id.steps_indicator);
        mStepsViewIndicator.setOnDrawListener(this);
        mTextContainer = (RelativeLayout) rootView.findViewById(R.id.rl_text_container);
        mTimeTextContainer = (RelativeLayout) rootView.findViewById(R.id.rl_text_time_container);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    /**
     * 设置显示的文字
     *
     * @param texts
     */
    public VerticalStepBlanceView setStepViewTexts(List<String> texts) {
        mTexts = texts;
        if (texts != null) {
            mStepsViewIndicator.setStepNum(mTexts.size());
        } else {
            mStepsViewIndicator.setStepNum(0);
        }
        return this;
    }

    public VerticalStepBlanceView setStepViewTimeTexts(List<String> texts) {
        mTextTime = texts;
        return this;
    }

    /**
     * 设置正在进行的position
     *
     * @param complectingPosition
     */
    public VerticalStepBlanceView setStepsViewIndicatorComplectingPosition(int complectingPosition) {
        mComplectingPosition = complectingPosition;
        mStepsViewIndicator.setComplectingPosition(complectingPosition);
        return this;
    }

    /**
     * 设置未完成文字的颜色
     *
     * @param unComplectedTextColor
     */
    public VerticalStepBlanceView setStepViewUnComplectedTextColor(int unComplectedTextColor) {
        mUnComplectedTextColor = unComplectedTextColor;
        return this;
    }

    /**
     * 设置完成文字的颜色
     *
     * @param complectedTextColor
     */
    public VerticalStepBlanceView setStepViewComplectedTextColor(int complectedTextColor) {
        this.mComplectedTextColor = complectedTextColor;
        return this;
    }

    /**
     * 设置StepsViewIndicator未完成线的颜色
     *
     * @param unCompletedLineColor
     */
    public VerticalStepBlanceView setStepsViewIndicatorUnCompletedLineColor(int unCompletedLineColor) {
        mStepsViewIndicator.setUnCompletedLineColor(unCompletedLineColor);
        return this;
    }

    /**
     * 设置StepsViewIndicator完成线的颜色
     *
     * @param completedLineColor
     */
    public VerticalStepBlanceView setStepsViewIndicatorCompletedLineColor(int completedLineColor) {
        mStepsViewIndicator.setCompletedLineColor(completedLineColor);
        return this;
    }

    /**
     * 设置StepsViewIndicator默认图片
     *
     * @param defaultIcon
     */
    public VerticalStepBlanceView setStepsViewIndicatorDefaultIcon(Drawable defaultIcon) {
        mStepsViewIndicator.setDefaultIcon(defaultIcon);
        return this;
    }

    /**
     * 设置StepsViewIndicator已完成图片
     *
     * @param completeIcon
     */
    public VerticalStepBlanceView setStepsViewIndicatorCompleteIcon(Drawable completeIcon) {
        mStepsViewIndicator.setCompleteIcon(completeIcon);
        return this;
    }

    /**
     * 设置StepsViewIndicator正在进行中的图片
     *
     * @param attentionIcon
     */
    public VerticalStepBlanceView setStepsViewIndicatorAttentionIcon(Drawable attentionIcon) {
        mStepsViewIndicator.setAttentionIcon(attentionIcon);
        return this;
    }

    /**
     * is reverse draw 是否倒序画
     *
     * @param isReverSe
     *         default is true
     */
    public VerticalStepBlanceView reverseDraw(boolean isReverSe) {
        this.mStepsViewIndicator.reverseDraw(isReverSe);
        return this;
    }

    /**
     * set linePadding  proportion 设置线间距的比例系数
     *
     * @param linePaddingProportion
     */
    public VerticalStepBlanceView setLinePaddingProportion(float linePaddingProportion) {
        this.mStepsViewIndicator.setIndicatorLinePaddingProportion(linePaddingProportion);
        return this;
    }

    /**
     * set textSize
     *
     * @param textSize
     */
    public VerticalStepBlanceView setTextSize(int textSize) {
        if (textSize > 0) {
            mTextSize = textSize;
        }
        return this;
    }

    @Override
    public void ondrawIndicator() {
        if (mTextContainer != null) {
            mTextContainer.removeAllViews();
            List<Float> complectedXPosition = mStepsViewIndicator.getCircleCenterPointPositionList();
            if (mTexts != null && complectedXPosition != null && complectedXPosition.size() > 0) {
                for (int i = 0; i < mTexts.size(); i++) {
                    mTextView = new TextView(getContext());
                    mTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, mTextSize);
                    mTextView.setText(mTexts.get(i));
                    mTextView.setY((complectedXPosition.get(i) - mStepsViewIndicator.getCircleRadius() / 2) - 17);
                    mTextView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT));
                    if (i < mComplectingPosition) {
                        mTextView.setTextColor(mComplectedTextColor);
                    } else if (i == mComplectingPosition) {
                        mTextView.setTextColor(mComplectingTextColor);
                    } else if (i > mComplectingPosition) {
                        mTextView.setTextColor(mUnComplectedTextColor);
                    }
                    mTextContainer.addView(mTextView);
                }
            }
        }
        if (mTimeTextContainer != null) {
            mTimeTextContainer.removeAllViews();
            List<Float> complectedXPosition = mStepsViewIndicator.getCircleCenterPointPositionList();
            if (mTextTime != null && complectedXPosition != null && complectedXPosition.size() > 0) {
                for (int i = 0; i < mTextTime.size(); i++) {
                    mTimeTextView = new TextView(getContext());
                    mTimeTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, mTextSize);
                    mTimeTextView.setText(mTextTime.get(i));
                    mTimeTextView.setY((complectedXPosition.get(i) - mStepsViewIndicator.getCircleRadius() / 2) - 17);

                    mTimeTextView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT));
                    mTextView.setTextColor(mUnComplectedTextColor);
                    mTimeTextContainer.addView(mTimeTextView);
                }
            }
        }
    }
}

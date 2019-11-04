package com.kiun.modelcommonsupport.ui.views;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kiun.modelcommonsupport.R;
/**
 * Created by kiun_2007 on 2018/8/16.
 */

public class ACellView extends LinearLayout {

    TextView leftTitleTextView;
    TextView tipTitleTextView;
    ATextView rightTitleTextView;
    LinearLayout contentPanel;
    ImageView rightImageView;
    View bottomLineView;
    String dataPath;
    int eventTag;
    Class aClass;
    ACellViewEventer eventer = null;

    public ACellView(Context context) {
        this(context, null);
    }

    public ACellView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ACellView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        LayoutInflater.from(context).inflate(R.layout.item_cellview, this);
        leftTitleTextView = findViewById(R.id.leftTitleTextView);
        tipTitleTextView = findViewById(R.id.tipTitleTextView);
        rightTitleTextView = findViewById(R.id.rightTitleTextView);
        contentPanel = findViewById(R.id.contentPanel);
        rightImageView = findViewById(R.id.rightImageView);
        bottomLineView = findViewById(R.id.bottomLineView);

        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.ACellView);

        String leftTitle = array.getString(R.styleable.ACellView_leftTitle);
        String rightTitle = array.getString(R.styleable.ACellView_rightTitle);
        dataPath = array.getString(R.styleable.ACellView_dataPath);
        if(dataPath != null){
            rightTitleTextView.setPath(dataPath);
        }
        Drawable rightImage = array.getDrawable(R.styleable.ACellView_rightImage);
        eventTag = array.getInteger(R.styleable.ACellView_eventTag, -1);
        String className = array.getString(R.styleable.ACellView_activityClass);
        int padding = array.getDimensionPixelOffset(R.styleable.ACellView_paddingLR, -1);
        int rightTitleleft = array.getDimensionPixelOffset(R.styleable.ACellView_rightTitleleft, -1);

        if(rightTitleleft > 0){
            LinearLayout linearLayout = findViewById(R.id.rightPanelLL);

            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) linearLayout.getLayoutParams();
            layoutParams.width = RelativeLayout.LayoutParams.MATCH_PARENT;
            layoutParams.leftMargin = rightTitleleft;
            linearLayout.setLayoutParams(layoutParams);
        }

        if(padding > 0){
            contentPanel.setPadding(padding, 0,padding,0);
        }

        if(className != null){
            try {
                aClass = Class.forName(className);
                if(!Activity.class.isAssignableFrom(aClass)){
                    aClass = null;
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }

        boolean hideRight = array.getBoolean(R.styleable.ACellView_hideRight, false);
        int lineType = array.getInt(R.styleable.ACellView_lineType, 0);
        rightImageView.setVisibility(hideRight ? GONE : VISIBLE);
        array.recycle();

        switch (lineType) {
            case 1: {
                bottomLineView.setBackgroundResource(R.drawable.background_dash_line);
                break;
            }
            case 2: {
                bottomLineView.setVisibility(INVISIBLE);
                break;
            }
        }

        if (leftTitle != null) {
            leftTitleTextView.setVisibility(VISIBLE);
            leftTitleTextView.setText(leftTitle);
        }

        if (rightTitle != null) {
            rightTitleTextView.setVisibility(VISIBLE);
            rightTitleTextView.setText(rightTitle);
        }

        if (rightImage != null) {
            rightImageView.setImageDrawable(rightImage);
        }
    }

    @Override
    protected void attachViewToParent(View child, int index, ViewGroup.LayoutParams params) {
        super.attachViewToParent(child, index, params);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        if (eventer == null && getContext() instanceof ACellViewEventer){
            eventer = (ACellViewEventer) getContext();
        }

        if(eventer != null && (eventTag > -1 || aClass != null)){

            this.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                     if(aClass != null){
                         Intent intent = new Intent(getContext(), aClass);
                         int acitityType = eventer.fillParams(intent, eventTag);
                         if(acitityType == ACellViewEventer.NOMAL_Activity){
                             getContext().startActivity(intent);
                         }else if (acitityType == ACellViewEventer.RESULT_Activity){
                             ((Activity)getContext()).startActivityForResult(intent, eventTag);
                         }
                     }else{
                         eventer.onCellClick(ACellView.this, eventTag);
                     }
                }
            });
        }
    }

    public void setEventer(ACellViewEventer eventer){
        this.eventer = eventer;
    }

    public void setLeftTitle(String title){
        leftTitleTextView.setText(title);
    }

    public void setRightTitle(String title){
        rightTitleTextView.setText(title);
    }

    public void setTipColorTitle(int color, String title){
        tipTitleTextView.setText(title);
        tipTitleTextView.setVisibility(VISIBLE);
    }
}

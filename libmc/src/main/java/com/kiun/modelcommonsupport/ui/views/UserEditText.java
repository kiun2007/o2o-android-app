package com.kiun.modelcommonsupport.ui.views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.text.method.TransformationMethod;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kiun.modelcommonsupport.R;
import com.kiun.modelcommonsupport.data.KeyValue;
import com.kiun.modelcommonsupport.data.drive.DriveListener;
import com.kiun.modelcommonsupport.utils.ByteUtil;
import com.kiun.modelcommonsupport.utils.MD5Util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * DeviceService
 * popchain
 * @author kiun_2007 on 2018/3/29.
 * Copyright (c) 2017-2018 The Popchain Core Developers
 * 自定义输入框
 */
public class UserEditText extends LinearLayout implements DriveListener {

    public static final String TAG = "USER_EDIT";
    public static final int ACTION_TAG = 0xEA00;

    public EditText editText = null;
    private TextView titleText = null;
    private TextView topTitleTextView = null;
    private Button codeButton = null;
    private ImageView iconImageView = null;
    private ImageView bottomImageView = null;
    private EditListenter listenter = null;
    private LinearLayout topPanel = null;
    private String dataPath = null;
    private String paramName = null;
    private String matchExp = null;
    private String matchError = null;
    private CheckBox eyeCheckBox = null;
    String hint = null;
    int inputType = 0;
    int time = 0;
    double limit = 0.0;

    public UserEditText(Context context) {
        this(context, null);
    }

    public UserEditText(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public void setListenter(EditListenter listenter){
        this.listenter = listenter;
    }

    public UserEditText(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater.from(context).inflate(R.layout.item_text_edit, this);
        topPanel = findViewById(R.id.topPanel);
        editText = findViewById(R.id.inputEdit);
        titleText = findViewById(R.id.nameTxt);
        codeButton = findViewById(R.id.codeButton);
        eyeCheckBox = findViewById(R.id.eyeCheckBox);
        iconImageView = findViewById(R.id.iconImageView);
        bottomImageView = findViewById(R.id.bottomImageView);
        topTitleTextView = findViewById(R.id.topTitleTextView);

        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.UserEditText);

        boolean isTopTitle = array.getBoolean(R.styleable.UserEditText_topTitle, true);

        if(array.getBoolean(R.styleable.UserEditText_notUnderline, false)){
            findViewById(R.id.underline).setVisibility(GONE);
        }

        limit = array.getFloat(R.styleable.UserEditText_limitValue, 0.0f);
        String title = array.getString(R.styleable.UserEditText_editTitle); //标题.
        if(title != null){
            titleText.setText(title);
            topTitleTextView.setText(title);
        }

        if(array.getBoolean(R.styleable.UserEditText_isWrapContent, false)){
            ViewGroup.LayoutParams layoutParams = titleText.getLayoutParams();
            layoutParams.width = -2;
            titleText.setLayoutParams(layoutParams);
        }

        Drawable drawable = array.getDrawable(R.styleable.UserEditText_icon);
        if(drawable != null){
            iconImageView.setImageDrawable(drawable);
            bottomImageView.setImageDrawable(drawable);
            if(!isTopTitle){
                bottomImageView.setVisibility(View.VISIBLE);
                iconImageView.setVisibility(View.GONE);
                topTitleTextView.setVisibility(View.GONE);
            }
        }else{
            iconImageView.setVisibility(View.GONE);
            bottomImageView.setVisibility(View.GONE);
        }

        hint = array.getString(R.styleable.UserEditText_editHint);
        if(hint != null){
            editText.setHint(hint);
        }

        dataPath = array.getString(R.styleable.UserEditText_dataPath);
        paramName = array.getString(R.styleable.UserEditText_paramName);
        matchExp = array.getString(R.styleable.UserEditText_matchExp);
        matchError = array.getString(R.styleable.UserEditText_matchError);

        boolean topTitle = array.getBoolean(R.styleable.UserEditText_topTitle, true);

        if(!topTitle){
            topPanel.setVisibility(GONE);
            if (title != null){
                titleText.setVisibility(VISIBLE);
            }
        }

        int textHeight = array.getDimensionPixelOffset(R.styleable.UserEditText_textHeight, -1);

        if(textHeight > -1){
            findViewById(R.id.contentLayout).setMinimumHeight(textHeight);
            findViewById(R.id.contentLayout).setMinimumWidth(getWidth());
        }else {
            float scale = context.getResources().getDisplayMetrics().density;
            findViewById(R.id.contentLayout).setMinimumHeight((int)(40 * scale+0.5f));
            findViewById(R.id.contentLayout).setMinimumWidth(getWidth());
        }

        inputType = array.getInteger(R.styleable.UserEditText_editType, 0);
        switch (inputType){
            case 0: //默认文本输入框.
                editText.setInputType(InputType.TYPE_CLASS_TEXT);
                break;
            case 1: //密文文本输入框.
                eyeCheckBox.setVisibility(VISIBLE);
                eyeCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                        // 83 《小装小修内测版Android_1.3.12》个人端：点击明码按钮后，输入指示符号会跳到最左边
                        if(eyeCheckBox.isChecked()){
                            HideReturnsTransformationMethod method1 = HideReturnsTransformationMethod.getInstance();
                            editText.setTransformationMethod(method1);
                        }else{
                            TransformationMethod method = PasswordTransformationMethod.getInstance();
                            editText.setTransformationMethod(method);
                        }

                        try {
                            editText.postInvalidate();
                            //切换后将EditText光标置于末尾
                            editText.setSelection(editText.getText().toString().length());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
                editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(16),new SpaceFilter()});
                break;
            case 2: //数字输入框.
                editText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                editText.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }
                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                    }
                    @Override
                    public void afterTextChanged(Editable s) {
                        if(s.toString().isEmpty()){
                            return;
                        }
                        double value = Double.parseDouble(s.toString());
                        if(value > limit){
                            s.clear();
                            s.append(String.format("%.2f", limit));
                        }
                    }
                });
                break;
            case 3: //验证码输入框.
                codeButton.setVisibility(View.VISIBLE);
                editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(4)});
                editText.setInputType(InputType.TYPE_CLASS_NUMBER);
                codeButton.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(listenter == null || listenter.onCodeSend(UserEditText.this)){
                            startTime(60);
                        }
                    }
                });
                break;
            case 4: //电话号
                editText.setInputType(InputType.TYPE_CLASS_NUMBER);
                editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(11)});
                break;
        }


        int maxLength = array.getInt(R.styleable.UserEditText_editMaxLength, -1);
        if(maxLength > 0){
            InputFilter[] filters = {new InputFilter.LengthFilter(maxLength)};
            editText.setFilters(filters);
        }
    }

    @SuppressLint("HandlerLeak")
    private Handler timeHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.what <= 1){
                codeButton.setText("发送验证码");
                codeButton.setEnabled(true);
            }
            else {
                codeButton.setText(String.format("%d S", msg.what - 1));
                timeHandler.sendEmptyMessageDelayed(msg.what - 1, 1000);
            }
        }
    };

    public void startTime(int sec){
        codeButton.setEnabled(false);
        codeButton.setText(String.format("%d S", sec));
        timeHandler.sendEmptyMessageDelayed(sec, 1000);
    }

    public void reSetBtn(){
        timeHandler.removeCallbacksAndMessages(null);
        timeHandler.sendEmptyMessage(1);
    }

    public void setLimit(double limit){
        this.limit = limit;
    }

    public String getMd5(){
        String md5Txt = ByteUtil.bytesToHexString(MD5Util.MD5(editText.getText().toString()));
        return md5Txt;
    }

    public void setHint(String hint){
        editText.setHint(hint);
    }


    @Override
    public String getPath() {
        return dataPath;
    }

    @Override
    public void fill(Object data) {
        if(data instanceof String){
            editText.setText(data.toString());
        }
    }

    @Override
    public int eventTag() {
        return -1;
    }

    @Override
    public KeyValue getParam() {
        String error = null;
        if(!TextUtils.isEmpty(hint) && TextUtils.isEmpty(getText())){
            return new KeyValue(paramName != null ? paramName : dataPath, null, hint);
        }
        if(matchExp != null){
            Pattern pattern = Pattern.compile(matchExp);
            Matcher matcher = pattern.matcher(editText.getText().toString());
            if(!matcher.matches()){
                if(matchError != null){
                    return new KeyValue(paramName != null ? paramName : dataPath , null, matchError);
                }else{
                    return new KeyValue(paramName != null ? paramName : dataPath, null, "请输入正确的值");
                }
            }
        }

        String textValue = editText.getText().toString();
        if(inputType == 1){ //密码.
            textValue = getMd5();
        }
        if(paramName != null){
            return new KeyValue(paramName, textValue);
        }
        return new KeyValue(dataPath, textValue);
    }

    public String getText(){
        return editText.getText().toString();
    }


    /** 禁止输入空格 * * @return */
    class SpaceFilter implements InputFilter {
        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) { //返回null表示接收输入的字符,返回空字符串表示不接受输入的字符
            if (source.equals(" "))
                return "";
            return null;
        }
    }
}
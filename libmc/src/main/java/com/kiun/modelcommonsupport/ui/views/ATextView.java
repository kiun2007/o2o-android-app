package com.kiun.modelcommonsupport.ui.views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.text.Html;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.widget.TextView;

import com.kiun.modelcommonsupport.MainApplication;
import com.kiun.modelcommonsupport.R;
import com.kiun.modelcommonsupport.data.KeyValue;
import com.kiun.modelcommonsupport.data.drive.DriveListener;
import com.kiun.modelcommonsupport.utils.MCString;
import com.kiun.modelcommonsupport.utils.TypeCheck;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by kiun_2007 on 2018/4/14.
 * 自动设置文本.
 */
@SuppressLint("AppCompatCustomView")
public class ATextView extends TextView implements DriveListener {

    private String paramName = null;
    private String dataPath = null;
    private String format = null;
    boolean noDataGone = false;
    private int eventTag = -1;
    private int maskType = 0;
    private boolean isHtml = false;
    private String textValue = null;

    public ATextView(Context context) {
        this(context, null);
    }

    public ATextView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ATextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.ATextView);

        paramName = array.getString(R.styleable.ATextView_paramName);
        dataPath = array.getString(R.styleable.ATextView_dataPath);
        format = array.getString(R.styleable.ATextView_format);
        eventTag = array.getInteger(R.styleable.ATextView_eventTag, -1);
        noDataGone = array.getBoolean(R.styleable.ATextView_noDataGone, false);
        maskType = array.getInt(R.styleable.ATextView_maskType, -1);
        isHtml = array.getBoolean(R.styleable.ATextView_isHtml, false);
        String srcName = array.getString(R.styleable.ATextView_srcName);
        if(srcName != null){
            String strValue = MainApplication.getInstance().getStringByName(srcName);
            if(strValue != null){
                if (format != null){
                    setText(String.format(format, strValue));
                }else{
                    setText(strValue);
                }
            }
        }
        array.recycle();
    }

    @Override
    public String getPath() {
        return dataPath;
    }

    public void setPath(String path) {
        dataPath = path;
    }

    @Override
    public void fill(Object data) {
        if (data != null && !data.equals("")) {
            textValue = data.toString();
            setVisibility(VISIBLE);
            if(isHtml && data instanceof String){
                this.setText(Html.fromHtml(data.toString()));
                return;
            }
            if (format != null) {
                if (format.startsWith("[date]")) {
                    String[] matchs = new String[]{"\\[date\\].*?\\[in\\](.+?)\\[/in\\].*?\\[/date\\]", "\\[date\\].*?\\[out\\](.+?)\\[/out\\].*?\\[/date\\]"};
                    matchs = MCString.patternValues(matchs, format);
                    String inFormat = matchs[0];
                    String outFormat = matchs[1];

                    if (inFormat != null && outFormat != null) {
                        SimpleDateFormat inDateFormat = new SimpleDateFormat(inFormat);
                        SimpleDateFormat outDateFormat = new SimpleDateFormat(outFormat, Locale.CHINA);
                        try {
                            Date date = inDateFormat.parse((String) data);
                            setText(outDateFormat.format(date));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                } else if (format.startsWith("[items]")) {
                    String[] matchs = new String[]{"\\[items\\].*?\\[item\\](.+?)\\[/item\\].*?\\[/items\\]",
                            "\\[items\\].*?\\[start\\](.+?)\\[/start\\].*?\\[/items\\]",
                            "\\[items\\].*?\\[color\\](.+?)\\[/color\\].*?\\[/items\\]"};
                    matchs = MCString.patternValues(matchs, format);
                    int start = matchs[1] != null ? Integer.parseInt(matchs[0]) : 0;
                    String itemString = matchs[0];
                    if (itemString != null && !"".equals(itemString)) {
                        String[] items = itemString.split(",");
                        int index = Integer.parseInt(data.toString()) - start;
                        if (index >= 0 && index < items.length) {
                            setText(items[index]);
                        }
                    }
                    String colorString = matchs[2];
                    if (colorString != null && !"".equals(colorString)) {
                        String[] colors = colorString.split(",");
                        int index = Integer.parseInt(data.toString()) - start;
                        if (index >= 0 && index < colors.length) {
                            int color = Integer.parseInt(colors[index], 16);
                            setTextColor(0xFF000000 | color);

                            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                setBackgroundTintList(ColorStateList.valueOf(0x33000000 | color));
                            } else {
                                ViewCompat.setBackgroundTintList(this, ColorStateList.valueOf(0x33000000 | color));
                            }
                        }
                    }
                } else {
                    if (format.matches(".*?\\%(.*?)d.*?")) {
                        int iValue = 0;
                        if (!data.equals("")) {
                            iValue = MCString.toNumber(data.toString()).intValue();
                        }
                        setText(String.format(format, iValue));
                    } else if (format.matches(".*?\\%(.*?)f.*?")) {
                        float fValue = 0.0f;
                        if (!data.equals("")) {
                            fValue = Float.parseFloat(data.toString());
                        }
                        setText(String.format(format, fValue));
                    } else {
                        setText(String.format(format, data));
                    }
                }
            } else {
                if (maskType > -1) {
                    String maskValue = data.toString();
                    if (maskType == 0) { //手机号
                        maskValue = maskValue.replaceAll("(\\d{3})\\d{4}(\\d{4})", "$1****$2");
                    } else if (maskType == 1) { //银行卡.
                        maskValue = MCString.hideCardNo(maskValue);
                    } else if (maskType == 2) { //银行卡尾数.
                        maskValue = MCString.bankCardTail(maskValue);
                    }
                    super.setText(maskValue);
                    return;
                }
                super.setText(data.toString());
            }
            if (noDataGone) {
                setVisibility(VISIBLE);
            }

            try {
                if (noDataGone && TypeCheck.isFloat(data) && (Float) data <= 0) {
                    super.setText("");
                }
            } catch (Exception e) {
            }
        } else {
            if (noDataGone) {
                setVisibility(GONE);
            }
        }
    }

    @Override
    public int eventTag() {
        return eventTag;
    }

    @Override
    public KeyValue getParam() {
        if (paramName == null || getText().toString().isEmpty()) {
            return null;
        }
        return new KeyValue(paramName, textValue);
    }
}

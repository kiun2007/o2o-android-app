package com.kiun.modelcommonsupport.ui.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;

import com.amos.modulebase.utils.ScreenUtil;
import com.kiun.modelcommonsupport.R;
import com.kiun.modelcommonsupport.data.KeyValue;
import com.kiun.modelcommonsupport.data.drive.DriveListener;
import com.kiun.modelcommonsupport.utils.MCString;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by kiun_2007 on 2018/8/25.
 */

public class AItemLayout extends LinearLayout implements DriveListener {

    private String dataPath;
    private String keyName;
    private String valueName;
    private int maxItem;
    private int lineItemNumber;
    private List<String> keyArrays = new ArrayList<>();
    private List<CheckBox> checkBoxes = new ArrayList<>();
    private int styleId = -1;
    private int styleValue = 0;
    int paddingLR = 0;
    private String separate;
    private String paramName;
    private String outArrayKey;
    private String matchError;

    private int canCheckNum = maxItem;

    private int[] styles = new int[]{R.attr.checkNormalStyle, R.attr.checkButtonStyle};

    public AItemLayout(Context context) {
        super(context);
        initView(context, null);
    }

    public AItemLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context, attrs);
    }

    public AItemLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context, attrs);
    }

    private void initView(Context context, AttributeSet attrs) {
        if (attrs == null) {
            return;
        }

        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.AItemLayout);

        dataPath = array.getString(R.styleable.AItemLayout_dataPath);
        keyName = array.getString(R.styleable.AItemLayout_keyName);
        valueName = array.getString(R.styleable.AItemLayout_valueName);
        maxItem = array.getInt(R.styleable.AItemLayout_maxItem, 3);
        lineItemNumber = array.getInt(R.styleable.AItemLayout_lineItemNumber, 1);
        styleId = array.getResourceId(R.styleable.AItemLayout_itemStyle, -1);
        styleValue = array.getInt(R.styleable.AItemLayout_itemStyleValue, 0);
        paddingLR = array.getDimensionPixelOffset(R.styleable.AItemLayout_paddingLR, 0);
        separate = array.getString(R.styleable.AItemLayout_separate);
        paramName = array.getString(R.styleable.AItemLayout_paramName);
        outArrayKey = array.getString(R.styleable.AItemLayout_outArrayKey);
        matchError = array.getString(R.styleable.AItemLayout_matchError);
        canCheckNum = maxItem;
        array.recycle();
    }

    @Override
    public String getPath() {
        return dataPath;
    }

    @Override
    public void fill(Object data) {

        if (keyName != null && valueName != null)
            if (data instanceof JSONArray) {
                JSONArray array = (JSONArray) data;
                LinearLayout linearLayoutP = null;
                for (int i = 0; i < array.length(); i++) {
                    final JSONObject jsonObject = array.optJSONObject(i);

                    CheckBox checkBox = null;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        checkBox = new CheckBox(this.getContext(), null, 0, styleId);
                    } else {
                        checkBox = new CheckBox(this.getContext(), null, styles[styleValue]);
                    }
                    checkBoxes.add(checkBox);

                    checkBox.setText(jsonObject.optString(valueName));
                    checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                            if (b) {
                                if (maxItem == 1) {
                                    keyArrays.clear();
                                    keyArrays.add(jsonObject.optString(keyName));
                                    canCheckNum = 0;
                                    for (int j = 0; j < checkBoxes.size(); j++) {
                                        checkBoxes.get(j).setChecked(false);
                                    }
                                    compoundButton.setChecked(true);
                                } else {
                                    if (canCheckNum <= 0) {
                                        compoundButton.setChecked(false);
                                        return;
                                    }
                                    keyArrays.add(jsonObject.optString(keyName));
                                    canCheckNum--;
                                }
                            } else {
                                keyArrays.remove(jsonObject.optString(keyName));
                                canCheckNum++;
                            }
                        }
                    });

                    int width = LayoutParams.MATCH_PARENT;
                    if (lineItemNumber > 1) {
                        width = getWidth() / lineItemNumber;
                        LinearLayout linearLayout = new LinearLayout(getContext());
                        linearLayout.addView(checkBox, LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
                        linearLayout.setPadding(paddingLR, 0, paddingLR, ScreenUtil.dip2px(5));
                        if (i % lineItemNumber == 0) {
                            linearLayoutP = new LinearLayout(getContext());
                        }
                        linearLayoutP.addView(linearLayout, width, LayoutParams.WRAP_CONTENT);
                        //                        LinearLayout.LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
                        //                        params.setMargins(0,0,0,ScreenUtil.dip2px(5));
                        //                        linearLayoutP.setLayoutParams(params);

                        if (i % lineItemNumber == 2 || i == array.length() - 1) {
                            this.addView(linearLayoutP, LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
                        }
                    } else {
                        LinearLayout linearLayout = new LinearLayout(getContext());
                        linearLayout.addView(checkBox, LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
                        linearLayout.setPadding(paddingLR, 0, paddingLR, 0);
                        this.addView(linearLayout, width, LayoutParams.WRAP_CONTENT);
                    }
                }

                this.setOrientation(LinearLayout.VERTICAL);
            }
    }

    public List<String> getSelectedItemArray() {
        return keyArrays;
    }

    @Override
    public int eventTag() {
        return 0;
    }

    @Override
    public KeyValue getParam() {
        if (paramName != null) {
            if (keyArrays.size() == 0) {
                if (TextUtils.isEmpty(matchError)) {
                    return new KeyValue(null, null, "至少选择一个");
                } else {
                    return new KeyValue(null, null, matchError);
                }
            }
            if (outArrayKey != null) {
                List<Map<String, String>> valueArray = new ArrayList<>();
                for (String keyValue : keyArrays) {
                    valueArray.add(new HashMap<String, String>() {{
                        put(outArrayKey, keyValue);
                    }});
                }
                return new KeyValue(paramName, valueArray);
            }
            return new KeyValue(paramName, MCString.arrayToString(keyArrays, separate == null ? "|" : separate));
        }
        return null;
    }
}

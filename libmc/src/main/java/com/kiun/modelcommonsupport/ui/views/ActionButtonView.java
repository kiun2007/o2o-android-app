package com.kiun.modelcommonsupport.ui.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kiun.modelcommonsupport.MainApplication;
import com.kiun.modelcommonsupport.R;
import com.kiun.modelcommonsupport.adapter.ItemListener;
import com.kiun.modelcommonsupport.data.KeyValue;
import com.kiun.modelcommonsupport.data.drive.DriveListener;
import com.kiun.modelcommonsupport.utils.MCString;

/**
 * Created by kiun_2007 on 2018/8/11.
 */
public class ActionButtonView extends LinearLayout implements DriveListener {

    private static final String TAG = "ActionButtonView";
    private ARelativeLayout actionViewLeftBtn = null;
    private ARelativeLayout actionViewRightBtn = null;

    private ImageView actionLeftIV = null;
    private ImageView actionRightIV = null;

    private TextView actionLeftTV = null;
    private TextView actionRightTV = null;

    private String actionTags = null;
    private String dataPath = null;
    private int startValue = 0;
    private ItemListener listener;

    public ActionButtonView(Context context) {
        this(context, null);
    }

    public ActionButtonView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ActionButtonView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater.from(context).inflate(R.layout.item_actionview, this);
        actionViewLeftBtn = findViewById(R.id.actionViewLeftBtn);
        actionViewRightBtn = findViewById(R.id.actionViewRightBtn);
        actionViewLeftBtn.setEventTag(0x1000000);
        actionViewRightBtn.setEventTag(0x1000001);

        actionLeftIV = findViewById(R.id.actionLeftIV);
        actionRightIV = findViewById(R.id.actionRightIV);

        actionLeftTV = findViewById(R.id.actionLeftTextView);
        actionRightTV = findViewById(R.id.actionRightTV);

        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.ActionButtonView);
        actionTags = array.getString(R.styleable.ActionButtonView_actionTags);
        dataPath = array.getString(R.styleable.ActionButtonView_dataPath);
        startValue = array.getInt(R.styleable.ActionButtonView_startValue, 0);
    }

    @Override
    public String getPath() {
        return dataPath;
    }

    @Override
    public void fill(Object data) {
        if (data != null && actionTags != null) {
            this.setVisibility(VISIBLE);
            int status = Integer.parseInt(data.toString()) - startValue;
            String[] matchs = new String[]{"\\[tags\\].*?\\[tag\\](.+?)\\[/tag\\].*?\\[/tags\\]",    //事件集合.
                    "\\[tags\\].*?\\[icon\\](.+?)\\[/icon].*?\\[/tags\\]",     //图标集合.
                    "\\[tags\\].*?\\[title\\](.+?)\\[/title\\].*?\\[/tags\\]", //标题集合.
                    "\\[tags\\].*?\\[color\\](.+?)\\[/color\\].*?\\[/tags\\]"};//颜色集合.
            matchs = MCString.patternValues(matchs, actionTags);

            if (matchs[0] == null || matchs[1] == null || matchs[2] == null || matchs[3] == null) {
                setVisibility(GONE);
                Log.e(TAG, "匹配公式错误,格式应该为[tags][icon]value1,value2_1|value2_2[/icon]...[/tags]");
                return;
            }
            String[] allTags = matchs[0].split(",");
            String[] allIcons = matchs[1].split(",");
            String[] allTitles = matchs[2].split(",");
            String[] allColors = matchs[3].split(",");
            if (status >= allTags.length || status >= allIcons.length
                    || status >= allTitles.length || status >= allColors.length) {
                setVisibility(GONE);
                Log.e(TAG, "超出索引, 公式错误,格式应该为[tags][icon]value1,value2_1|value2_2[/icon]...[/tags]");
                return;
            }
            String[] tags = allTags[status].split("\\|");
            String[] icons = allIcons[status].split("\\|");
            String[] titles = allTitles[status].split("\\|");
            String[] colors = allColors[status].split("\\|");
            if (tags.length > 2 || tags.length != icons.length || tags.length != titles.length
                    || tags.length != titles.length || tags.length != colors.length) {
                setVisibility(GONE);
                Log.e(TAG, "请仔细检查每个集合的数据.");
                return;
            }
            if (tags.length == 1 && tags[0].isEmpty()) {
                setVisibility(GONE);
                return;
            }
            setVisibility(VISIBLE);

            actionViewRightBtn.setVisibility(tags.length == 2 ? View.VISIBLE : View.GONE);

            ARelativeLayout[] actionButtons = new ARelativeLayout[]{actionViewLeftBtn, actionViewRightBtn};
            ImageView[] actionIVs = new ImageView[]{actionLeftIV, actionRightIV};
            TextView[] actionTVs = new TextView[]{actionLeftTV, actionRightTV};
            for (int i = 0; i < tags.length; i++) {

                actionButtons[i].setEventTag(MCString.asInt(tags[i]));
                actionTVs[i].setText(titles[i]);
                actionTVs[i].setTextColor(MCString.asInt(colors[i]) | 0xFF000000);
                Drawable bitmap = MainApplication.getInstance().getBitmapByName(icons[i]);
                if (bitmap != null) {
                    actionIVs[i].setImageDrawable(bitmap);
                }
                if (this.listener != null) {
                    int finalI = i;
                    actionButtons[i].setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            listener.onItemClick(ActionButtonView.this, null, actionButtons[finalI].eventTag());
                        }
                    });
                }
            }
        }else{
            this.setVisibility(GONE);
        }
    }

    @Override
    public int eventTag() {
        return -1;
    }

    @Override
    public KeyValue getParam() {
        return null;
    }

    public void setListener(ItemListener listener) {
        this.listener = listener;
    }

    public String getLeftTextBtn() {
        return actionLeftTV.getText().toString();
    }

    public void setLeftTextBtnVis(int i) {
        findViewById(R.id.view_line).setVisibility(i);
        actionViewLeftBtn.setVisibility(i);
    }

    public int getLeftTextBtnVis() {
        return actionViewLeftBtn.getVisibility();
    }
}

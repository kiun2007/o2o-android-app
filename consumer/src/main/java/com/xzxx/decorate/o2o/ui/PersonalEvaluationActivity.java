package com.xzxx.decorate.o2o.ui;

import android.graphics.Color;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.kiun.modelcommonsupport.network.MCBaseRequest;
import com.phillipcalvin.iconbutton.IconButton;
import com.xzxx.decorate.o2o.consumer.R;
import com.kiun.modelcommonsupport.ui.views.FlowLayout;

import com.kiun.modelcommonsupport.utils.BasicUtils;

/**
 * 我的评价页面
 */
public class PersonalEvaluationActivity extends BaseActivity implements View.OnClickListener {

    private FlowLayout flowLayout;

    private String[] lables = new String[]{"态度良好", "效率很高", "技术专业"};

    private IconButton btn_evaluation_delete;

    @Override
    public int getLayoutId() {
        return R.layout.layout_my_evaluation;
    }

    @Override
    public void initView() {
        flowLayout = findViewById(R.id.id_evaluation_tags);
        btn_evaluation_delete = findViewById(R.id.btn_evaluation_delete);
        btn_evaluation_delete.setOnClickListener(this);
        initData();
    }

    private void initData() {
        showFlowText(lables);
    }


    public void showFlowText(String[] data) {
        for (int i = 0; i < data.length; i++) {
            int ranHeight = BasicUtils.dip2px(this, 30);
            ViewGroup.MarginLayoutParams lp = new ViewGroup.MarginLayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                    ranHeight);
            lp.setMargins(BasicUtils.dip2px(this, 10), BasicUtils.dip2px(this, 2), BasicUtils.dip2px(this, 10), BasicUtils.dip2px(this, 10));
            TextView tv = new TextView(this);
            tv.setPadding(BasicUtils.dip2px(this, 12), 0, BasicUtils.dip2px(this, 12), 0);
            tv.setTextColor(Color.parseColor("#000000"));
            tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
            tv.setText(data[i]);
            tv.setGravity(Gravity.CENTER_VERTICAL);
            tv.setLines(1);
            tv.setBackgroundResource(R.drawable.bg_flow_layout_tag);
            flowLayout.addView(tv, lp);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_evaluation_delete:
                Toast.makeText(getApplicationContext(), "评价删除成功", Toast.LENGTH_LONG).show();
                finish();
                break;
        }
    }

    @Override
    public void onDataChanged(Object data, MCBaseRequest request) {

    }
}

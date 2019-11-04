package com.xzxx.decorate.o2o.master;

import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.amos.modulebase.utils.DateUtil;
import com.kiun.modelcommonsupport.network.MCBaseRequest;
import com.xzxx.decorate.o2o.BaseActivity;
import com.xzxx.decorate.o2o.view.VerticalStepView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zf on 2018/7/19.
 * 余额提现进度
 */
public class BalanceProgressActivity extends BaseActivity {

    private VerticalStepView verticalStepView;

    @Override
    public int getLayoutId() {
        return R.layout.layout_balance_progress;
    }

    @Override
    public void initView() {
        verticalStepView = findViewById(R.id.id_balance_stepview);
        View btn_balance_complete = findViewById(R.id.btn_balance_complete);
        btn_balance_complete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        List<String> list = new ArrayList<>();
        list.add("发起提现申请");
        list.add("平台处理中");
        list.add("到账成功");

        Intent intent = getIntent();

        String date = DateUtil.getDate("yyyyMMddHHmmss");
        if (intent != null) {
            String extra = intent.getStringExtra("extra");
            if (!TextUtils.isEmpty(extra)) {
                try {
                    JSONObject jsonObject = new JSONObject(extra);
                    //                    fillToView(R.id.id_balance_amount, jsonObject, true);
                    fillToView(R.id.id_balance_bank_name, jsonObject, true);
                    fillToView(R.id.id_balance_tail_num, jsonObject, true);
                    fillToView(R.id.id_balance_apply_time, jsonObject, true);
                    date = jsonObject.optString("applyTime", "");
                    ((TextView) findViewById(R.id.id_balance_amount)).setText("¥" + jsonObject.optString("withdrawMoney", ""));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        date = DateUtil.getDateAfter(DateUtil.formatTime(date, "yyyyMMddHHmmss", "yyyy-MM-dd"), 1);
        date = DateUtil.formatTime(date, "yyyy-MM-dd", "MM-dd " + "23:59");
        verticalStepView.setStepsViewIndicatorComplectingPosition(1)//设置完成的步数
                .reverseDraw(false)
                .setStepViewTexts(list)
                .setTextSize(15)
                .setDate(date)
                .setLinePaddingProportion(1)//设置indicator线与线间距的比例系数
                .setStepsViewIndicatorCompletedLineColor(ContextCompat.getColor(this, R.color.light_black)
                )//设置StepsViewIndicator完成线的颜色
                .setStepsViewIndicatorUnCompletedLineColor(ContextCompat.getColor(this, R.color.light_black))
                //设置StepsViewIndicator未完成线的颜色
                //.setStepViewComplectedTextColor(ContextCompat.getColor(getActivity(), R.color.orange))
                //设置StepsView text完成线的颜色
                //.setStepViewUnComplectedTextColor(ContextCompat.getColor(getActivity(), R.color.black))
                // 设置StepsView text未完成线的颜色
                .setStepsViewIndicatorCompleteIcon(ContextCompat.getDrawable(this, R.drawable.icon_sign))
                //设置StepsViewIndicator CompleteIcon
                .setStepsViewIndicatorDefaultIcon(ContextCompat.getDrawable(this, R.drawable.default_icon))
                //设置StepsViewIndicator DefaultIcon
                .setStepsViewIndicatorAttentionIcon(ContextCompat.getDrawable(this, R.drawable.icon_clock));
    }

    @Override
    public void onRightClick() {
        Intent balanceRecordIntent = new Intent(BalanceProgressActivity.this, BalanceRecordActivity.class);
        startActivity(balanceRecordIntent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onDataChanged(Object data, MCBaseRequest request) {

    }
}

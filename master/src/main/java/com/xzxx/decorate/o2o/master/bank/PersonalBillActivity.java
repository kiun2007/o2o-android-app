package com.xzxx.decorate.o2o.master.bank;

import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.amos.modulebase.utils.IntentUtil;
import com.amos.modulebase.utils.LogUtil;
import com.kiun.modelcommonsupport.adapter.ListItenListener;
import com.kiun.modelcommonsupport.network.MCBaseRequest;
import com.kiun.modelcommonsupport.ui.views.AListViewNew;
import com.kiun.modelcommonsupport.utils.DatePickerPopWinNew;
import com.xzxx.decorate.o2o.BaseActivity;
import com.xzxx.decorate.o2o.master.BillDetailActivity;
import com.xzxx.decorate.o2o.master.R;
import com.xzxx.decorate.o2o.requests.BillListRequest;

import org.json.JSONObject;

import java.util.Date;

/**
 * Created by zf on 2018/7/22.
 * 我的账单
 */
public class PersonalBillActivity extends BaseActivity implements View.OnClickListener, ListItenListener {

    int year = 0;
    int month = 0;
    TextView monthTextView;
    AListViewNew listView = null;
    BillListRequest billListRequest;

    @Override
    public int getLayoutId() {
        return R.layout.layout_personal_bill;
    }

    @Override
    public void initView() {
        findViewById(R.id.billCellView).setOnClickListener(this);
        monthTextView = findViewById(R.id.monthTextView);
        listView = findViewById(R.id.lv_personal_bill);
        listView.setItemListener(this);

        Date date = new Date();
        year = 1900 + date.getYear();
        month = date.getMonth() + 1;
        initListView(year, month);

        getRefreshView().setAutoLoadMore(true);
    }

    private void initListView(int year, int month) {
        this.year = year;
        this.month = month;

        monthTextView.setText(String.format("%04d年%02d(本月)", year, month));
        billListRequest = new BillListRequest();
        billListRequest.date = String.format("%04d%02d", year, month);

        //        commitRequest(billListRequest);
        //        listView.setListRequest(billListRequest);
        onRefresh(true);
    }

    @Override
    public void onDataChanged(Object data, MCBaseRequest request) {
        Log.e("PersonalBillActivity", "onDataChanged request" + request);
        Log.e("PersonalBillActivity", "onDataChanged data" + data);
        dismissProgress();
        if (request instanceof BillListRequest) {
            try {
                listView.onDataChanged(data, request);
                JSONObject jsonObject = (JSONObject) data;
                String totalIncome = jsonObject.optString("totalIncome", "0");
                totalIncome = TextUtils.isEmpty(totalIncome) ? "0" : totalIncome;
                ((TextView) findViewById(R.id.txt_totalIncome)).setText("收入¥" + totalIncome);
                String totaloutCome = jsonObject.optString("totaloutCome", "0");
                totaloutCome = TextUtils.isEmpty(totaloutCome) ? "0" : totaloutCome;
                ((TextView) findViewById(R.id.txt_totaloutCome)).setText("提现¥" + totaloutCome);

            } catch (Exception e) {
                ((TextView) findViewById(R.id.txt_totalIncome)).setText("收入¥" + 0.00);
                ((TextView) findViewById(R.id.txt_totaloutCome)).setText("提现¥" + 0.00);
                e.printStackTrace();
            }
        }

        completeRefresh();
    }

    @Override
    public void onClick(View view) {
        //        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
        //            @Override
        //            public void onDateSet(DatePicker startDatePicker, int startYear, int startMonthOfYear, int startDayOfMonth) {
        //                initListView(startYear, startMonthOfYear);
        //            }
        //        }, year, month, 1);
        //        datePickerDialog.show();

        DatePickerPopWinNew timePickerPopWin = new DatePickerPopWinNew.Builder(this, new DatePickerPopWinNew.OnDatePickedListener() {
            @Override
            public void onDatePickCompleted(int year, int month, int day, String dateDesc) {
                LogUtil.i("  " + dateDesc);
                initListView(year, month);
            }

        }).textConfirm("确认").textCancel("取消").btnTextSize(16)
                .viewTextSize(25).colorCancel(Color.parseColor("#999999"))
                .colorConfirm(Color.parseColor("#FF821C"))
                .build();
        timePickerPopWin.showPopWin(this);
    }

    @Override
    public void onItemDataChanged(View view, Object data) {

    }

    @Override
    public void onItemClick(View listView, Object itemData, int tag) {

        Bundle bundle = new Bundle();
        bundle.putString("tranFundLogId", ((JSONObject) itemData).optString("tranFundLogId", ""));
        IntentUtil.gotoActivity(PersonalBillActivity.this, BillDetailActivity.class, bundle);
    }

    @Override
    public void onRefresh(boolean isSilence) {
        showProgress(false);
        commitRequest(billListRequest);
    }

    @Override
    public void onLoadMore(boolean isSilence) {
        billListRequest.pageNo++;
        commitRequest(billListRequest);
    }
}

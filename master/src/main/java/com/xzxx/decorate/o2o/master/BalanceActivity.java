package com.xzxx.decorate.o2o.master;

import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.kiun.modelcommonsupport.adapter.ItemListener;
import com.kiun.modelcommonsupport.network.MCBaseRequest;
import com.kiun.modelcommonsupport.ui.views.AListView;
import com.kiun.modelcommonsupport.ui.views.UserEditText;
import com.kiun.modelcommonsupport.utils.MCDialogManager;
import com.kiun.modelcommonsupport.utils.MCString;
import com.phillipcalvin.iconbutton.IconButton;
import com.xzxx.decorate.o2o.BaseActivity;
import com.xzxx.decorate.o2o.master.bank.AddBandCardActivity;
import com.xzxx.decorate.o2o.requests.AvailBalanRequest;
import com.xzxx.decorate.o2o.requests.BankCardListRequest;
import com.xzxx.decorate.o2o.requests.WithdrawRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by zf on 2018/7/18.
 * 余额提现
 */
public class BalanceActivity extends BaseActivity implements View.OnClickListener {

    UserEditText balanceInput;
    private TextView id_balance_bank_card;
    private TextView txt_all;
    private IconButton btn_balance;
    private Object listObject;
    private String bankCardId;
    private String balanNumber = "";

    @Override
    public int getLayoutId() {
        return R.layout.layout_balance;
    }

    @Override
    public void initView() {
        id_balance_bank_card = findViewById(R.id.id_balance_bank_cardName);
        txt_all = findViewById(R.id.txt_all);
        btn_balance = findViewById(R.id.btn_balance);
        balanceInput = findViewById(R.id.balanceInput);
        initEvent();

        commitRequest(new AvailBalanRequest());
        commitRequest(new BankCardListRequest());
    }

    private void initEvent() {
        id_balance_bank_card.setOnClickListener(this);
        btn_balance.setOnClickListener(this);
        txt_all.setOnClickListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.id_balance_bank_cardName:
                if (listObject != null) {
                    if (listObject instanceof String) {
                        Intent addBandCardIntent = new Intent(this, AddBandCardActivity.class);
                        startActivity(addBandCardIntent);
                        return;
                    }
                    final MCDialogManager dialog = MCDialogManager.show(this, R.layout.dialog_select_to_band_card, listObject);
                    AListView listView = dialog.getViewById(R.id.list_balance_to_card);
                    listView.setItemListener(new ItemListener() {
                        @Override
                        public void onItemClick(View listView, Object itemData, int tag) {
                            fillToView(R.id.id_balance_bank_cardName, itemData, true);
                            fillToView(R.id.id_balance_bank_cardNo, itemData, true);
                            JSONObject jsonObject = (JSONObject) itemData;
                            bankCardId = jsonObject.optString("bankCardId");
                            dialog.dismiss();
                        }
                    });
                }
                break;
            case R.id.btn_balance:
                if (TextUtils.isEmpty(bankCardId)) {
                    Toast.makeText(this, "请绑定银行卡先", Toast.LENGTH_LONG).show();
                    return;
                }
                String money = balanceInput.getText();
                if (TextUtils.isEmpty(money)) {
                    Toast.makeText(this, "提现金额不能为空", Toast.LENGTH_LONG).show();
                    return;
                }

                WithdrawRequest withdrawRequest = new WithdrawRequest();
                withdrawRequest.amount = balanceInput.getText();
                withdrawRequest.bankCardId = bankCardId;
                commitRequest(withdrawRequest);
                break;
            case R.id.txt_all:
                balanceInput.editText.setText(balanNumber);
                break;
        }
    }

    @Override
    public void onRightClick() {
        Intent balanceRecordIntent = new Intent(BalanceActivity.this, BalanceRecordActivity.class);
        startActivity(balanceRecordIntent);
    }

    @Override
    public void onDataChanged(Object data, MCBaseRequest request) {
        Log.e("BalanceActivity", "data " + data);
        if (request instanceof AvailBalanRequest) {
            balanNumber = MCString.toNumber(data.toString()).toString();
            balanceInput.setLimit(Double.parseDouble(balanNumber));
            fillToView(R.id.getMaxBalance, data, true);
        }
        if (request instanceof BankCardListRequest) {
            listObject = data;
            if (!TextUtils.isEmpty(data.toString())) {
                JSONArray jsonArray = (JSONArray) data;
                if (jsonArray.length() > 0) {
                    try {
                        JSONObject jsonObject = jsonArray.getJSONObject(0);
                        bankCardId = jsonObject.optString("bankCardId");
                        fillToView(R.id.id_balance_bank_cardName, jsonObject, true);
                        fillToView(R.id.id_balance_bank_cardNo, jsonObject, true);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        if (request instanceof WithdrawRequest) {
            Intent balanceIntent = new Intent(BalanceActivity.this, BalanceProgressActivity.class);
            balanceIntent.putExtra("extra", data.toString());
            startActivity(balanceIntent);
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        commitRequest(new AvailBalanRequest());
        commitRequest(new BankCardListRequest());
    }
}

package com.xzxx.decorate.o2o.master.bank;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.kiun.modelcommonsupport.adapter.ItemListener;
import com.kiun.modelcommonsupport.network.MCBaseRequest;
import com.kiun.modelcommonsupport.network.requests.CodeRequest;
import com.kiun.modelcommonsupport.ui.views.ACellView;
import com.kiun.modelcommonsupport.ui.views.EditListenter;
import com.kiun.modelcommonsupport.ui.views.UserEditText;
import com.kiun.modelcommonsupport.utils.BankCardUtil;
import com.kiun.modelcommonsupport.utils.MCDialogManager;
import com.xzxx.decorate.o2o.BaseActivity;
import com.xzxx.decorate.o2o.master.R;
import com.xzxx.decorate.o2o.master.WriteCardInfoActivity;
import com.xzxx.decorate.o2o.requests.BankCardAddRequest;
import com.xzxx.decorate.o2o.requests.BankValidRequest;
import com.xzxx.decorate.o2o.view.AddressPickerView;

/**
 * Created by zf on 2018/7/21.
 * 添加银行卡页面
 */
public class AddBandCardActivity extends BaseActivity{

    int step = 0;
    String cityName = null;
    String bankName = null;
    ACellView cityCellView;
    ACellView cardTypeCell;
    private UserEditText codeEditText = null;

    BankValidRequest bankValidRequest = new BankValidRequest();
    BankCardAddRequest bankCardAddRequest = new BankCardAddRequest();

    @Override
    public void onRightClick() {
        Intent writeCardIntent = new Intent(this, WriteCardInfoActivity.class);
        startActivity(writeCardIntent);
    }

    @Override
    public int getLayoutId() {
        return R.layout.layout_add_band_card;
    }

    @Override
    public void initView() {
    }

    @Override
    public void onCellClick(View targetView, int tag) {
        final MCDialogManager mcDialogManager = MCDialogManager.show(this,R.layout.dialog_city, null);
        AddressPickerView addressPickerView = mcDialogManager.getViewById(R.id.apvAddress);

        addressPickerView.setOnAddressPickerSure(new AddressPickerView.OnAddressPickerSureListener() {
            @Override
            public void onSureClick(String address, String provinceCode, String cityCode, String districtCode) {
                String[] adds = address.split(" ");
                cityName = adds[0] + adds[1];
                mcDialogManager.dismiss();
                cityCellView.setRightTitle(cityName);
                bankCardAddRequest.bankRegion = cityName;
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
    @Override
    public void onDataChanged(Object data, MCBaseRequest request) {

        if(request instanceof BankValidRequest){
            setContentView(R.layout.layout_add_band_card_suc);
            //137****2615
            TextView sendMsgToTextView = findViewById(R.id.sendMsgToTextView);
            sendMsgToTextView.setText("绑定银行卡需要短信确认,点击发送验证码"); //
            addSubmitEvent();
            step++;
            codeEditText = findViewById(R.id.phoneCodeEdit);
            codeEditText.setListenter(new EditListenter() {
                @Override
                public boolean onCodeSend(UserEditText editText) {
                    CodeRequest request = new CodeRequest();
                    request.phone = bankCardAddRequest.bankPhone;
                    request.type = "4";
                    commitRequest(request);
                    return true;
                }
            });
        }
        if(request instanceof  BankCardAddRequest){
            MCDialogManager.info(this, "银行卡添加成功!").setListener(new ItemListener() {
                @Override
                public void onItemClick(View listView, Object itemData, int tag) {
                    finish();
                }
            });
        }

        if(request instanceof CodeRequest){
            TextView sendMsgToTextView = findViewById(R.id.sendMsgToTextView);
            String maskValue = bankCardAddRequest.bankPhone.replaceAll("(\\d{3})\\d{4}(\\d{4})", "$1****$2");
            sendMsgToTextView.setText("绑定银行卡需要短信确认,验证码已发送至手机:" + maskValue); //
        }
    }

    @Override
    public void onSubmitClick(Button button) {

        if(!fillRequest(bankCardAddRequest, null)){
            return;
        }
        if(step == 0){
            fillRequest(bankValidRequest, null);
            bankName = BankCardUtil.getBankName(bankCardAddRequest.cardNo);
            if(bankName == null){
                MCDialogManager.error(this, "请仔细核对您的银行卡号以免无法提取现金!");
                return;
            }
            setContentView(R.layout.layout_add_band_card_sec);
            addSubmitEvent();
            bankCardAddRequest.cardBank = bankName;
            cityCellView = findViewById(R.id.cityCellView);
            cardTypeCell = findViewById(R.id.cardTypeCell);
            cardTypeCell.setRightTitle(bankName + " 储蓄卡");
            step++;
        }else if (step == 1){
            if (cityName == null){
                MCDialogManager.error(this, "请选择发卡号地区!");
                return;
            }
            fillRequest(bankValidRequest, null);
            commitRequest(bankValidRequest);
        }else {
            commitRequest(bankCardAddRequest);
        }
    }
}

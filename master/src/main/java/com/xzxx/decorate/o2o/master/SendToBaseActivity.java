package com.xzxx.decorate.o2o.master;

import android.view.View;
import android.widget.Button;
import com.kiun.modelcommonsupport.adapter.ItemListener;
import com.kiun.modelcommonsupport.controllers.BaseRequestAcitity;
import com.kiun.modelcommonsupport.network.MCBaseRequest;
import com.kiun.modelcommonsupport.network.requests.SysDicItemRequest;
import com.kiun.modelcommonsupport.ui.views.AItemLayout;
import com.kiun.modelcommonsupport.utils.MCDialogManager;
import com.kiun.modelcommonsupport.utils.MCString;
import com.xzxx.decorate.o2o.requests.SendHDReqeust;
/**
 * Created by kiun_2007 on 2018/8/28.
 */
public class SendToBaseActivity extends BaseRequestAcitity {

    AItemLayout itemLayout = null;
    @Override
    public void onDataChanged(Object data, MCBaseRequest request) {
        if(request instanceof SysDicItemRequest && !(data instanceof String)){
            fillToView(R.id.itemLayout, data, true);
        }
        if(request instanceof SendHDReqeust){
            MCDialogManager.info(this, "已经通知给总部").setListener(new ItemListener() {
                @Override
                public void onItemClick(View listView, Object itemData, int tag) {
                    setResult(0x321);
                    finish();
                }
            });
        }
    }

    @Override
    public int getLayoutId() {
        return R.layout.layout_send_to_base;
    }

    @Override
    public void initView() {
        itemLayout = findViewById(R.id.itemLayout);

        SysDicItemRequest sysDicItemRequest = new SysDicItemRequest();
        sysDicItemRequest.key = SysDicItemRequest.Send_HD;
        commitRequest(sysDicItemRequest);
    }

    @Override
    public void onSubmitClick(Button button) {
        SendHDReqeust sendHDReqeust = new SendHDReqeust();
        sendHDReqeust.orderId = getIntent().getStringExtra("orderId");
        sendHDReqeust.type = MCString.arrayToString(itemLayout.getSelectedItemArray(), "|");

        if(fillRequest(sendHDReqeust, null)){
            commitRequest(sendHDReqeust);
        }
    }
}

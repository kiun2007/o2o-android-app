package com.xzxx.decorate.o2o.master;

import android.content.Intent;
import android.text.TextUtils;

import com.kiun.modelcommonsupport.network.MCBaseRequest;
import com.kiun.modelcommonsupport.ui.views.ACellView;
import com.kiun.modelcommonsupport.ui.views.ACellViewEventer;
import com.kiun.modelcommonsupport.ui.views.ALinearLayout;
import com.kiun.modelcommonsupport.ui.views.ATextView;
import com.kiun.modelcommonsupport.utils.MCDialogManager;
import com.xzxx.decorate.o2o.BaseActivity;
import com.xzxx.decorate.o2o.requests.MasterInfoModifyRequest;
import com.xzxx.decorate.o2o.requests.MasterInfoRequest;

import org.json.JSONObject;

/**
 * Created by zf on 2018/7/16.
 * 个人资料
 */
public class PersonalInfoActivity extends BaseActivity {

    String cityCode = null;
    private boolean isEdit = false;
    private ACellView cityCellView;
    private ATextView txt_phone;

    @Override
    public int getLayoutId() {
        return R.layout.layout_personal_info;
    }

    @Override
    public void initView() {
        commitRequest(new MasterInfoRequest());
        cityCellView = findViewById(R.id.cityCellView);
        txt_phone = findViewById(R.id.txt_phone);
    }

    @Override
    public int fillParams(Intent intent, int tag) {
        if (!isEdit) {
            return ACellViewEventer.NO_Activity;
        }
        return ACellViewEventer.RESULT_Activity;
    }

    @Override
    public void onRightClick() {
        isEdit = !isEdit;
        ALinearLayout contentLayout = findViewById(R.id.infoContentLayout);
        contentLayout.setEnabled(isEdit);
        getNavigatorBar().setRightButtonText(isEdit ? "保存" : "编辑");
        if (!isEdit) {
            MasterInfoModifyRequest infoModifyRequest = new MasterInfoModifyRequest();
            infoModifyRequest.city = cityCode;
            if (fillRequest(infoModifyRequest, null)) {
                commitRequest(infoModifyRequest);
            }
        }
    }

    @Override
    public void onDataChanged(Object data, MCBaseRequest request) {

        if (request instanceof MasterInfoRequest) {
            fillToView(-1, data);
            cityCode = ((JSONObject) data).optString("cityCode");
            String phone =  txt_phone.getText().toString();

            try {
                if (!TextUtils.isEmpty(phone)) {
                    txt_phone.setText(phone.replace(phone.substring(3, 7), "****"));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (request instanceof MasterInfoModifyRequest) {
            MCDialogManager.info(this, "资料保存成功");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 10 && requestCode > 0) {
            try {
                cityCode = data.getStringExtra("code");
                cityCellView.setRightTitle(data.getStringExtra("name"));
            } catch (Exception e) {
            }
        }
    }
}

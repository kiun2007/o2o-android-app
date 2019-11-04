package com.xzxx.decorate.o2o.ui;

import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.services.core.PoiItem;
import com.amos.modulebase.utils.RegexUtil;
import com.amos.modulebase.utils.ToastUtil;
import com.kiun.modelcommonsupport.data.drive.MCDataField;
import com.kiun.modelcommonsupport.network.MCBaseRequest;
import com.xzxx.decorate.o2o.bean.AddressBean;
import com.xzxx.decorate.o2o.consumer.R;
import com.xzxx.decorate.o2o.requests.address.AddressAddRequest;
import com.xzxx.decorate.o2o.requests.address.AddressModifyRequest;

/**
 * 添加地址页面
 * Created by zf on 2018/6/17.
 */
public class AddAddressActivity extends BaseActivity implements View.OnClickListener {

    private LinearLayout ll_personal_address_detail;
    private LinearLayout ll_select_location;
    private CheckBox set_default_add_checkbox;
    private TextView text_location;
    private RadioGroup radioGroup;
    private RadioButton radio_male;
    private RadioButton radio_female;
    private String contactSex = "0";
    private PoiItem poiItem;
    AddressBean editAddressBean;
    // 是否显示 默认按钮
    private boolean isShow=true;

    @Override
    public int getLayoutId() {
        return R.layout.layout_add_address;
    }

    @Override
    public void initView() {
        ll_select_location = findViewById(R.id.ll_select_location);
        ll_personal_address_detail = findViewById(R.id.ll_personal_address_detail);
        set_default_add_checkbox = findViewById(R.id.id_set_default_add_checkbox);
        ll_select_location = findViewById(R.id.ll_select_location);
        radio_male = findViewById(R.id.radio_male);
        radio_female = findViewById(R.id.radio_female);
        radioGroup = findViewById(R.id.radioGroup);
        text_location = findViewById(R.id.text_location);

        ll_select_location.setOnClickListener(this);
        ll_personal_address_detail.setOnClickListener(this);

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton radioButton = findViewById(checkedId);
                contactSex = (String) radioButton.getTag();
            }
        });

        isShow= getIntent().getBooleanExtra("isShow",true);

        if (isShow){
            set_default_add_checkbox.setVisibility(View.VISIBLE);
        }else {
            set_default_add_checkbox.setVisibility(View.GONE);
        }
        AddressBean addressBean = new AddressBean();
        MCDataField.fillObject(addressBean, getIntent());
        if (addressBean.getId() != null) {
            fillToView(-1, addressBean);
            editAddressBean = addressBean;
            text_location.setText(editAddressBean.getLoaction() + editAddressBean.getAddressDetail());
            contactSex = addressBean.getContactSex();
            boolean isMan = "0".equals(contactSex);
            radio_male.setChecked(isMan);
            radio_female.setChecked(!isMan);
            set_default_add_checkbox.setChecked("0".equals(editAddressBean.getIsDefault()));
        }
    }

    @Override
    public void onRightClick() {
        //        if (poiItem == null && editAddressBean == null) {
        //            MCDialogManager.error(this, "请输入11姓名!");
        //            return;
        //        }

        AddressModifyRequest addressModifyRequest = new AddressModifyRequest();
        AddressAddRequest addressAddRequest = new AddressAddRequest();
        addressModifyRequest.isDefault = addressAddRequest.isDefault = set_default_add_checkbox.isChecked() ? "0" : "1";
        addressModifyRequest.contactSex = addressAddRequest.contactSex = contactSex;
        if (poiItem != null) {
            addressModifyRequest.latitudeLongitude = addressAddRequest.latitudeLongitude = poiItem.getLatLonPoint().getLatitude() + "," + poiItem.getLatLonPoint().getLongitude();
        } else if (editAddressBean != null) {
            addressModifyRequest.latitudeLongitude = editAddressBean.getLatitudeLongitude();
        }
        if (poiItem != null) {
            addressModifyRequest.location = addressAddRequest.location = poiItem.getTitle();
        } else if (editAddressBean != null) {
            addressModifyRequest.location = editAddressBean.getLoaction();
        }
        if (poiItem != null) {
            addressModifyRequest.cityCode = addressAddRequest.cityCode = poiItem.getAdCode().substring(0, 4) + "00";
        } else if (editAddressBean != null) {
            addressModifyRequest.cityCode = editAddressBean.getCityCode();
        }
        fillRequest(addressModifyRequest, null);
        fillRequest(addressAddRequest, null);

        if (TextUtils.isEmpty(addressModifyRequest.contactName)) {
            ToastUtil.showToast(this, "请输入姓名!");
            return;
        }

        if (TextUtils.isEmpty(addressModifyRequest.contactPhone)) {
            ToastUtil.showToast(this, "请输入联系电话!");
            return;
        }
        if (!RegexUtil.isMobile(addressModifyRequest.contactPhone)) {
            ToastUtil.showToast(AddAddressActivity.this, "请输入正确的手机号码！");
            return;
        }
        if (TextUtils.isEmpty(addressModifyRequest.latitudeLongitude)) {
            ToastUtil.showToast(this, "请选择地区!");
            return;
        }
        if (TextUtils.isEmpty(addressModifyRequest.addressDetail)) {
            ToastUtil.showToast(this, "请输入详细地址!");
            return;
        }

        if (editAddressBean != null) {
            addressModifyRequest.id = editAddressBean.getId();
            commitRequest(addressModifyRequest);
        } else {
            commitRequest(addressAddRequest);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode > 0) {
            poiItem = data.getParcelableExtra("poiItem");
            text_location.setText(poiItem.getTitle());
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_select_location:
                Intent selectCityIntent = new Intent(this, MapSearchActivity.class);
                if (poiItem != null) {
                    selectCityIntent.putExtra("latlng", poiItem.getLatLonPoint().getLatitude() + "," + poiItem.getLatLonPoint().getLongitude());
                } else if (editAddressBean != null) {
                    selectCityIntent.putExtra("latlng", editAddressBean.getLatitudeLongitude());
                }
                startActivityForResult(selectCityIntent, 1);
                break;
            case R.id.ll_personal_address_detail:
                break;
        }
    }

    @Override
    public void onDataChanged(Object data, MCBaseRequest request) {
        if (request instanceof AddressAddRequest) {
            Log.e("AddAddressActivity", "onDataChanged");
            Toast.makeText(getApplicationContext(), "添加地址成功!", Toast.LENGTH_LONG).show();
            finish();
        }

        if (request instanceof AddressModifyRequest) {
            Toast.makeText(getApplicationContext(), "地址修改成功!", Toast.LENGTH_LONG).show();
            finish();
        }
    }
}

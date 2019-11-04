package com.xzxx.decorate.o2o.ui;

import android.content.Intent;
import android.view.View;
import android.widget.LinearLayout;

import com.kiun.modelcommonsupport.adapter.ItemListener;
import com.kiun.modelcommonsupport.network.MCBaseRequest;
import com.kiun.modelcommonsupport.ui.views.AListView;
import com.kiun.modelcommonsupport.utils.MCDialogManager;
import com.xzxx.decorate.o2o.consumer.R;
import com.xzxx.decorate.o2o.requests.address.AddressDeleteRequest;
import com.xzxx.decorate.o2o.requests.address.AddressListRequest;

import org.json.JSONObject;

/**
 * 我的地址页面
 * Created by zf on 2018/6/17.
 */
public class PersonalAddressActivity extends BaseActivity implements View.OnClickListener, ItemListener<JSONObject> {

    private AListView listView;
    private LinearLayout ll_personal_address_add;

    @Override
    public int getLayoutId() {
        return R.layout.layout_personal_address;
    }

    @Override
    public void initView() {
        listView = findViewById(R.id.list_personal_address);
        ll_personal_address_add = findViewById(R.id.ll_personal_address_add);
        ll_personal_address_add.setOnClickListener(this);
        listView.setItemListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        onRefresh(false);
    }

    @Override
    public void onRefresh(boolean isPullDown) {
        listView.setListRequest(new AddressListRequest());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
         switch (v.getId()) {
             case R.id.ll_personal_address_add:
                 Intent addAddressintent = new Intent(this,AddAddressActivity.class);
                 addAddressintent.putExtra("isShow",false);
                //                 addAddressintent.putExtra("isShow",getIntent().getBooleanExtra("isShow",true));
                 startActivity(addAddressintent);
                 break;
         }
    }

    @Override
    public void onDataChanged(Object data, MCBaseRequest request) {
        if (request instanceof AddressDeleteRequest){
            onRefresh(false);
        }
    }

    @Override
    public void onItemClick(View listView, final JSONObject itemData, int tag) {

        Intent intent = new Intent();
        intent.putExtra("contactName", itemData.optString("contactName"));
        intent.putExtra("loaction", itemData.optString("loaction"));
        intent.putExtra("contactPhone", itemData.optString("contactPhone"));
        intent.putExtra("latitudeLongitude", itemData.optString("latitudeLongitude"));
        intent.putExtra("contactSex", itemData.optString("contactSex"));
        intent.putExtra("addressDetail", itemData.optString("addressDetail"));
        intent.putExtra("cityName", itemData.optString("cityName"));
        intent.putExtra("cityCode", itemData.optString("cityCode"));
        intent.putExtra("isDefault", itemData.optString("isDefault"));
        intent.putExtra("id", itemData.optString("id"));

        if(tag == 0){
            setResult(0x0010, intent);
            finish();
            return;
        }

        if(tag == 1){

            String content = "是否删除地址\"" + itemData.optString("loaction") +"\"";
            MCDialogManager.showMessage(this, "删除地址", content, "确认删除","取消", R.drawable.svg_icon_prompt_big).setListener(new ItemListener() {
                @Override
                public void onItemClick(View listView, Object data, int tag) {

                    if(tag == MCDialogManager.TAG_RIGHT_BTN){
                        AddressDeleteRequest request = new AddressDeleteRequest();
                        request.id = itemData.optString("id");
                        commitRequest(request);
                    }
                }
            });
            return;
        }

        if(tag == 2){
            intent.setClass(this, AddAddressActivity.class);
            intent.putExtra("isShow",getIntent().getBooleanExtra("isShow",true));
            startActivity(intent);
        }
    }
}
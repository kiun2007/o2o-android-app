package com.xzxx.decorate.o2o.master;

import android.content.Intent;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.kiun.modelcommonsupport.adapter.ItemCreater;
import com.kiun.modelcommonsupport.adapter.ItemManager;
import com.kiun.modelcommonsupport.adapter.ListBaseAdapter;
import com.kiun.modelcommonsupport.network.MCBaseRequest;
import com.kiun.modelcommonsupport.ui.views.ACheckBox;
import com.kiun.modelcommonsupport.ui.views.AListView;
import com.kiun.modelcommonsupport.utils.MCDialogManager;
import com.xzxx.decorate.o2o.BaseActivity;
import com.xzxx.decorate.o2o.requests.ProfessionRequest;

/**
 * Created by kiun_2007 on 2018/8/18.
 */

public class SkillItemActivity extends BaseActivity implements ItemCreater {

    public final static int RESULT_CODE = 20;
    public final static String SKILL_IDS = "SKILL_IDS";
    public final static String SKILL_NAMES = "SKILL_NAMES";

    AListView listView = null;
    ItemManager itemManager = new ItemManager(3);

    @Override
    public int getLayoutId() {
        return R.layout.activity_skill_item;
    }

    @Override
    public void initView() {
        listView = findViewById(R.id.itemListView);

        if(listView != null){
            ListBaseAdapter listBaseAdapter = (ListBaseAdapter) listView.getAdapter();
            listBaseAdapter.setItemCreate(this);
        }
        commitRequest(new ProfessionRequest());
        itemManager.setMutex(true);
    }

    @Override
    public void onRightClick() {

        String items = itemManager.splicingFromKey("professionId", "|");
        if(items == null){
            MCDialogManager.showMessage(this, "提示", "至少选择一个职业", "好的", R.drawable.icon_fail);
            return;
        }

        Intent intent = new Intent();
        intent.putExtra(SKILL_IDS, items);
        intent.putExtra(SKILL_NAMES, itemManager.splicingFromKey("professionName", ";"));
        setResult(RESULT_CODE, intent);
        finish();
    }
//[{"firstItemId":"201808081629670046576798","firstItemName":"家电维修","firstItemOrderNum":"1","bannerImgUrl":"","linkUrl":"","professions":""},{"firstItemId":"201808081629670046576808","firstItemName":"上门安装","firstItemOrderNum":"2","bannerImgUrl":"","linkUrl":"","professions":[{"professionId":"201808081640670046577011","professionName":"家电安装工"}]},{"firstItemId":"201808081631670046576817","firstItemName":"房屋装修","firstItemOrderNum":"3","bannerImgUrl":"","linkUrl":"","professions":[{"professionId":"201808081640670046577017","professionName":"房屋维修工"}]}]
    @Override
    public void onDataChanged(Object data, MCBaseRequest request) {
        fillToView(R.id.itemListView, data, true);
    }

    @Override
    public void onItemCreate(View view) {
        ACheckBox checkBox = view.findViewById(R.id.item_checkbox);
        checkBox.setOnCheckedChangeListener(itemManager);
    }
}

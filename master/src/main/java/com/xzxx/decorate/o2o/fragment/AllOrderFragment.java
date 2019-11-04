package com.xzxx.decorate.o2o.fragment;

import android.content.Intent;
import android.view.View;

import com.kiun.modelcommonsupport.adapter.ItemListener;
import com.kiun.modelcommonsupport.network.MCBaseRequest;
import com.kiun.modelcommonsupport.network.MCListRequest;
import com.kiun.modelcommonsupport.ui.views.AListView;
import com.xzxx.decorate.o2o.master.OrderDetailActivity;
import com.xzxx.decorate.o2o.master.R;
import com.xzxx.decorate.o2o.master.SendToBaseActivity;
import com.xzxx.decorate.o2o.requests.MasterOrderList;
import com.xzxx.decorate.o2o.requests.SalesAfterListRequest;

import org.json.JSONObject;

/**
 * 全部订单页面
 */
public class AllOrderFragment extends BaseFragment implements ItemListener<JSONObject> {

    private AListView mainListView;

    private int pageIndex = 0;

    @Override
    public void onDataChanged(Object data, MCBaseRequest request) {
    }

    @Override
    protected void initView(View view) {
        mainListView = view.findViewById(R.id.mainListView);
        mainListView.setItemListener(this);
        MCListRequest listRequest = null;
        if(pageIndex == 0){
            listRequest = new MasterOrderList();
        }else if (pageIndex == 1){
            MasterOrderList orderListRequest = new MasterOrderList();
            orderListRequest.status = "5";
            listRequest = orderListRequest;
        }else{
            listRequest = new SalesAfterListRequest();
        }
        mainListView.setListRequest(listRequest);
        if(getBaseActivity() != null){
            getBaseActivity().getButtonController().setSendToBaseClz(SendToBaseActivity.class);
        }
    }

    @Override
    protected int getLayoutId() {
        return pageIndex == 2 ? R.layout.layout_fragment_all_order_after : R.layout.layout_fragment_all_order;
    }

    public void setPageIndex(int pageIndex) {
        this.pageIndex = pageIndex;
    }

    @Override
    public void onItemClick(View listView, JSONObject itemData, int tag) {
//{"customerId":"201809171519670005454605","appointmentName":"刘春杰","customerHeadImg":"http:\/\/pawx04z5h.bkt.clouddn.com\/2018\/08\/ae456325-399f-419f-afcd-df5ce3f28760.png",
// "secondItemId":"201809171558670020088354","seccondItemName":"山上有个庙","appointmentTime":"20180920171000","appointmentLocation":"泰山东村四巷与泰山路交叉口东100米泰山东村三巷5号",
// "appointmentSex":"0","appointmentLatitudeLongitude":"22.640055,114.058358","appointmentAddress":"150","salesAfterStatus":"2","repairPayMoney":0.01,"doorPayMoney":0.01,
// "orderId":"201809201631670019233918","totalPayMoney":"","salesAfterOrderId":"201809201634670043875626","salesAfterId":"201809201633670019233923","customerHxAccount":"c13322222222"}
        if(tag == 0){
            if(pageIndex != 2){
                Intent intent = new Intent(this.getContext(), OrderDetailActivity.class);
                intent.putExtra("orderId", itemData.optString("orderId"));
                intent.putExtra("orderStatus", itemData.optString("orderStatus"));
                startActivity(intent);
            }else{
                Intent intent = new Intent(this.getContext(), OrderDetailActivity.class);
                intent.putExtra("salesAfterOrderId", itemData.optString("salesAfterOrderId"));
                intent.putExtra("salesAfterStatus", itemData.optString("salesAfterStatus"));
                startActivity(intent);
            }
            return;
        }
        getBaseActivity().getButtonController().actionTag(mainListView, tag, itemData);
    }
}

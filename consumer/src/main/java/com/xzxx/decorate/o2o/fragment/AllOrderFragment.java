package com.xzxx.decorate.o2o.fragment;

import android.content.Intent;
import android.view.View;

import com.kiun.modelcommonsupport.adapter.ItemListener;
import com.kiun.modelcommonsupport.controllers.BaseRequestFragment;
import com.kiun.modelcommonsupport.controllers.OrderButtonController;
import com.kiun.modelcommonsupport.network.MCBaseRequest;
import com.kiun.modelcommonsupport.network.MCListRequest;
import com.kiun.modelcommonsupport.ui.views.AListView;
import com.xzxx.decorate.o2o.consumer.R;
import com.xzxx.decorate.o2o.pay.WechatPay;
import com.xzxx.decorate.o2o.pay.alipay.AliPay;
import com.xzxx.decorate.o2o.requests.afterService.SalesAfterListRequest;
import com.xzxx.decorate.o2o.requests.order.OrderListRequest;
import com.xzxx.decorate.o2o.ui.EvaluationCompleteActivity;
import com.xzxx.decorate.o2o.ui.OrderDetailActivity;
import com.xzxx.decorate.o2o.ui.ServiceEvaluationActivity;

import org.json.JSONObject;

/**
 * 全部订单页面
 */
public class AllOrderFragment extends BaseRequestFragment implements ItemListener<JSONObject> {

    private AListView mainListView;
    private int pageIndex = 0;
    OrderButtonController buttonController;

    @Override
    public void onDataChanged(Object data, MCBaseRequest request) {
    }

    @Override
    protected void initView(View view) {
        mainListView = view.findViewById(R.id.mainListView);
        mainListView.setItemListener(this);

    }
    /** 订单状态 **/
    public static final String ORDER_STATUS_WAIT_PAY_DOOR = "0"; //订单等待付上门费
    public static final String ORDER_STATUS_WAIT_ACCEPTED = "1"; //订单等待分配师傅
    public static final String ORDER_STATUS_WAIT_LEAVE = "2"; //订单师傅待上门
    public static final String ORDER_STATUS_WAIT_SERVICE = "3"; //订单师傅已出发待抵达
    public static final String ORDER_STATUS_ON_SERVICE = "4"; //师傅服务中
    public static final String ORDER_STATUS_WAIT_PAY = "5"; //师傅维修完成等待用户付款
    public static final String ORDER_STATUS_WAIT_COMMENT = "6";  //用户付款完成待评论
    public static final String ORDER_STATUS_FINISH = "7";  //订单完成
    public static final String ORDER_STATUS_CUSTOMER_CANCEL = "8";// 用户取消订单

    @Override
    public void onResume() {
        super.onResume();
        if (getBaseActivity() != null) {
            buttonController = getBaseActivity().getButtonController();
            buttonController.setWechatPay(new WechatPay());
            buttonController.setAliPay(new AliPay(this.getBaseActivity()));
            buttonController.setCommentClz(ServiceEvaluationActivity.class);
            buttonController.setSeeClz(EvaluationCompleteActivity.class);
        }
        MCListRequest listRequest = null;
        if (pageIndex == 0) {
            listRequest = new OrderListRequest();
        } else if (pageIndex == 1) {
            OrderListRequest orderListRequest = new OrderListRequest();
            orderListRequest.status = ORDER_STATUS_WAIT_PAY_DOOR;
            listRequest = orderListRequest;
        } else if (pageIndex == 2) {
            OrderListRequest orderListRequest = new OrderListRequest();
            orderListRequest.status = ORDER_STATUS_WAIT_COMMENT;
            listRequest = orderListRequest;
        } else {
            listRequest = new SalesAfterListRequest();
        }

        mainListView.setListRequest(listRequest);
    }

    @Override
    protected int getLayoutId() {
        return pageIndex != 3 ? R.layout.layout_fragment_all_order : R.layout.layout_fragment_all_order_after;
    }

    public void setPageIndex(int pageIndex) {
        this.pageIndex = pageIndex;
    }

    //{"masterId":"201808231338670013541063","masterName":"刘春杰","masterHeadImg":"http:\/\/pawx04z5h.bkt.clouddn.com\/2018\/08\/ae456325-399f-419f-afcd-df5ce3f28760.png","masterProfession":"程序员|设计师|测试员","masterScore":5,"masterOrderNum":8,"secondItemId":"201808181406670026049262","seccondItemName":"修地图BUG","appointmentLocation":"百瑞达公寓","appointmentLatitudeLongitude":"22.644233,114.065650","appointmentTime":"20180823233000","appointmentAddress":"404","salesAfterStatus":"2","repairPayMoney":0.01,"doorPayMoney":0.01,"orderId":"201808232258670000948075","totalPayMoney":0.02,"salesAfterOrderId":"201808281554670030284561","salesAfterId":"201808240956670040843301","masterHxAccount":"m13751041579","salesType":"rework","refuseReason":""}
    @Override
    public void onItemClick(View listView, JSONObject itemData, int tag) {

        if (tag == 0) {
            //            Intent intent = new Intent(this.getContext(), ReleaseOrderActivity.class);
            //            intent.putExtra("orderId", itemData.optString("orderId"));
            //            startActivity(intent);
            Intent intent = new Intent(this.getContext(), OrderDetailActivity.class);
            intent.putExtra("orderId", itemData.optString("orderId"));
            intent.putExtra("orderStatus", itemData.optString("orderStatus"));
            if (itemData.optString("salesAfterId") != null) {
                intent.putExtra("salesAfterId", itemData.optString("salesAfterId"));
            }
            startActivity(intent);
            return;
        } else if (tag == OrderButtonController.TAG_PRO) { //查看进度.
            Intent intent = new Intent(this.getContext(), OrderDetailActivity.class);
            intent.putExtra("orderId", itemData.optString("orderId"));
            intent.putExtra("orderStatus", itemData.optString("orderStatus"));
            if (itemData.optString("salesAfterId") != null) {
                intent.putExtra("salesAfterId", itemData.optString("salesAfterId"));
            }
            intent.putExtra("curItem", 1);
            startActivity(intent);
            return;
        }
        buttonController.actionTag((AListView) listView, tag, itemData);
    }
}
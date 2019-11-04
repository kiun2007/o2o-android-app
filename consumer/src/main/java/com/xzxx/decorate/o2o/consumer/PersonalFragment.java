package com.xzxx.decorate.o2o.consumer;

import android.content.Intent;
import android.view.View;
import com.kiun.modelcommonsupport.controllers.BaseRequestFragment;
import com.kiun.modelcommonsupport.data.MCUserInfo;
import com.kiun.modelcommonsupport.data.drive.MCDataField;
import com.kiun.modelcommonsupport.network.MCBaseRequest;
import com.kiun.modelcommonsupport.ui.views.ACellViewEventer;
import com.xzxx.decorate.o2o.ConsumerApplication;
import com.xzxx.decorate.o2o.requests.userinfo.InfoRequest;
import com.xzxx.decorate.o2o.ui.OrderListActivity;
import com.xzxx.decorate.o2o.ui.PersonalInfoActivity;

import org.json.JSONObject;

public class PersonalFragment extends BaseRequestFragment{

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
    protected void initView(View view) {
        view.findViewById(R.id.infoContentLayout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), PersonalInfoActivity.class);
                startActivity(intent);
            }
        });
        View.OnClickListener clickListener = new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                String status = "";
                String title = "全部订单";
                switch (view.getId()){
                    case R.id.ll_personal_wait_service:
                        status = ORDER_STATUS_WAIT_SERVICE;
                        title = "待服务";
                        break;
                    case R.id.ll_personal_wait_pay:
                        status = ORDER_STATUS_ON_SERVICE + "|" + ORDER_STATUS_WAIT_PAY;
                        title = "待支付";
                        break;
                    case R.id.ll_personal_wait_evaluation:
                        status = ORDER_STATUS_WAIT_COMMENT;
                        title = "待评价";
                        break;
                }
                Intent intent = new Intent(getContext(), OrderListActivity.class);
                intent.putExtra("status", status);
                intent.putExtra("title", title);
                startActivity(intent);
            }
        };
        view.findViewById(R.id.ll_personal_wait_service).setOnClickListener(clickListener);
        view.findViewById(R.id.ll_personal_wait_pay).setOnClickListener(clickListener);
        view.findViewById(R.id.ll_personal_wait_evaluation).setOnClickListener(clickListener);
        view.findViewById(R.id.txt_personal_all_order).setOnClickListener(clickListener);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_personal;
    }

    @Override
    public void onResume() {
        super.onResume();
        MCUserInfo userInfo = ConsumerApplication.getInstance().getUserInfo(false);
        if (userInfo != null){
            commitRequest(new InfoRequest());
        }
    }


    @Override
    public void onDataChanged(Object data, MCBaseRequest request) {
        if(request instanceof InfoRequest){
            fillToView(R.id.infoContentLayout, data);
            MCUserInfo userInfo = ConsumerApplication.getInstance().getUserInfo(false);
            MCDataField.fillObject(userInfo, (JSONObject) data);
            ConsumerApplication.getInstance().saveUserInfo(userInfo);
        }
    }

    @Override
    public int fillParams(Intent intent, int tag) {
        return ConsumerApplication.getInstance().getUserInfo(true) != null ? ACellViewEventer.NOMAL_Activity : ACellViewEventer.NO_Activity;
    }
}

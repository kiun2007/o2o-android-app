package com.xzxx.decorate.o2o.fragment;

import android.text.TextUtils;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kiun.modelcommonsupport.adapter.ItemListener;
import com.kiun.modelcommonsupport.controllers.BaseRequestFragment;
import com.kiun.modelcommonsupport.controllers.OrderButtonController;
import com.kiun.modelcommonsupport.controllers.Refresher;
import com.kiun.modelcommonsupport.network.MCBaseRequest;
import com.kiun.modelcommonsupport.ui.views.ActionButtonView;
import com.xzxx.decorate.o2o.consumer.R;
import com.xzxx.decorate.o2o.pay.WechatPay;
import com.xzxx.decorate.o2o.pay.alipay.AliPay;
import com.xzxx.decorate.o2o.requests.afterService.SalesAfterInfoRequest;
import com.xzxx.decorate.o2o.requests.order.OrderInfoRequest;
import com.xzxx.decorate.o2o.ui.EvaluationCompleteActivity;
import com.xzxx.decorate.o2o.ui.OrderDetailActivity;
import com.xzxx.decorate.o2o.ui.ServiceEvaluationActivity;
import com.xzxx.decorate.o2o.view.CommonDialog;

import org.json.JSONObject;

/**
 * Created by zf on 2018/7/3.
 * 订单详情之订单内容
 */
public class OrderContentFragment extends BaseRequestFragment implements View.OnClickListener, Refresher {

    private RelativeLayout rl_view;
    private Object data;
    OrderButtonController buttonController;
    private TextView txt_tips;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.id_order_content_cost:
                CommonDialog.costDialog(getActivity(), data);
                break;
            default:
                break;
        }
    }

    public void onDataChanged(Object data, MCBaseRequest request) {
        completeRefresh();
        if ((request instanceof OrderInfoRequest || request instanceof SalesAfterInfoRequest) && (data instanceof JSONObject)) {
            fillToView(-1, data);
            this.data = data;
            if (request instanceof SalesAfterInfoRequest) {
                //售后申请已提交,申请通过退款中,申请通过待上门,申请通过待服务,服务中,售后完成,申请失败未通过[/item][/items]"
                String salesAfterStatus = ((JSONObject) data).optString("salesAfterStatus", "");
                String salesType = ((JSONObject) data).optString("salesType", "");//"salesType": "refund",
                String totalRefundAmount = ((JSONObject) data).optString("totalRefundAmount", "");//"totalRefundAmount": 0.02
                //                public static final String SALES_AFTER_STATUS_COMMIT = "0"; //待处理
                //                public static final String SALES_AFTER_STATUS_REFUND = "1"; //通过退款中
                //                public static final String SALES_AFTER_STATUS_WAIT_DOOR = "2"; //通过待上门
                //                public static final String SALES_AFTER_STATUS_WAIT_SERVICE = "3"; //通过待服务
                //                public static final String SALES_AFTER_STATUS_ON_SERVICE = "4"; //服务中
                //                public static final String SALES_AFTER_STATUS_FINISHED = "5"; //完成
                //                public static final String SALES_AFTER_STATUS_REFUSED = "6"; //拒绝
                if (salesAfterStatus.equals("0")) {
                    txt_tips.setText("小装小修提示您售后申请已提交，请耐心等待！");
                } else if (salesAfterStatus.equals("1")) {
                    txt_tips.setText("小装小修提示您售后申请已通过，平台退款处理中！");
                } else if (salesAfterStatus.equals("2")) {
                    txt_tips.setText("小装小修提示您售后申请已通过，师傅将再次上门服务请注意保持与师傅联系！");
                } else if (salesAfterStatus.equals("3")) { //通过待服务
                    txt_tips.setText("小装小修提示您售后申请已通过，师傅将再次上门服务请注意保持与师傅联系！");
                } else if (salesAfterStatus.equals("4")) { //服务中
                    txt_tips.setText("小装小修提示您售后申请已通过，师傅将再次上门服务请注意保持与师傅联系！");
                } else if (salesAfterStatus.equals("5")) {//完成
                    if (!TextUtils.isEmpty(salesType)) {
                        if (salesType.equals("refund")) {
                            txt_tips.setText("小装小修提示您" + totalRefundAmount + "元退款已到账" + "，给您带来的不便我们感到非常抱歉！");
                        } else {
                            txt_tips.setText("小装小修提示您售后申请已完成，给您带来的不便我们感到非常抱歉！");
                        }
                    }
                } else if (salesAfterStatus.equals("6")) {//拒绝
                    txt_tips.setText("小装小修提示您售后申请未通过，请根据实际情况申请售后服务，谢谢您的支持！");
                }
            }
        }
    }

    @Override
    protected void initView(View view) {
        rl_view = view.findViewById(R.id.id_order_content_cost);
        txt_tips = view.findViewById(R.id.txt_tips);
        rl_view.setOnClickListener(this);
        ActionButtonView actionButtonView = view.findViewById(R.id.actionButton);
        if (getBaseActivity() != null) {
            buttonController = getBaseActivity().getButtonController();
            buttonController.setWechatPay(new WechatPay());
            buttonController.setAliPay(new AliPay(this.getBaseActivity()));
            buttonController.setCommentClz(ServiceEvaluationActivity.class);
            buttonController.setSeeClz(EvaluationCompleteActivity.class);
        }

        actionButtonView.setListener(new ItemListener() {
            @Override
            public void onItemClick(View listView, Object itemData, int tag) {
                if (tag == OrderButtonController.TAG_PRO) { //查看进度.
                    ((OrderDetailActivity) getActivity()).mViewPager.setCurrentItem(1);
                    return;
                }
                if (buttonController != null) {
                    buttonController.actionTag(OrderContentFragment.this, tag, data);
                }
            }
        });
    }

    @Override
    public void onRefresh(boolean isPullDown) {
        if (!TextUtils.isEmpty(getArguments().getString("salesAfterId"))) {
            SalesAfterInfoRequest salesAfterInfoRequest = new SalesAfterInfoRequest();
            salesAfterInfoRequest.salesAfterId = getArguments().getString("salesAfterId");
            commitRequest(salesAfterInfoRequest);
            return;
        }
        OrderInfoRequest infoRequest = new OrderInfoRequest();
        infoRequest.orderId = getArguments().getString("orderId");
        commitRequest(infoRequest);
    }

    @Override
    public void onResume() {
        super.onResume();
        onRefresh(false);
    }

    @Override
    protected int getLayoutId() {
        if (getArguments().getString("salesAfterId") != null && !getArguments().getString("salesAfterId").isEmpty()) {
            return R.layout.layout_order_content_after;
        }
        return R.layout.layout_order_content;
    }

    @Override
    public void onRefresh(int tag) {
        if (tag == OrderButtonController.TAG_DEL || tag == OrderButtonController.TAG_CANCEL) {
            getBaseActivity().finish();
            return;
        }
        onRefresh(false);
    }
}

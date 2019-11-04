package com.xzxx.decorate.o2o.fragment;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;

import com.amos.modulebase.bean.ResponseBean;
import com.amos.modulebase.mvp.model.HttpOkBiz;
import com.amos.modulebase.utils.DateUtil;
import com.amos.modulebase.utils.LogUtil;
import com.amos.modulebase.utils.gson.GsonUtil;
import com.amos.modulebase.utils.http.HttpRequestCallBack;
import com.kiun.modelcommonsupport.controllers.BaseRequestFragment;
import com.kiun.modelcommonsupport.network.MCBaseRequest;
import com.kiun.modelcommonsupport.utils.HttpRequestBiz;
import com.xzxx.decorate.o2o.bean.OrderProgressBean;
import com.xzxx.decorate.o2o.consumer.R;
import com.xzxx.decorate.o2o.view.VerticalStepView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by zf on 2018/7/4.
 * 订单详情之订单进度
 */
public class OrderProgressFragment extends BaseRequestFragment {

    private LinearLayout view_parent;

    @Override
    public void onDataChanged(Object data, MCBaseRequest request) {

    }

    @Override
    protected void initView(View view) {
        view_parent = view.findViewById(R.id.view_parent);

        initViewData();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_vertical_stepview;
    }

    /** 售后申请状态 **/
    public static final String SALES_AFTER_STATUS_COMMIT = "0"; //待处理
    public static final String SALES_AFTER_STATUS_REFUND = "1"; //通过退款中
    public static final String SALES_AFTER_STATUS_WAIT_DOOR = "2"; //通过待上门
    public static final String SALES_AFTER_STATUS_WAIT_SERVICE = "3"; //通过待服务
    public static final String SALES_AFTER_STATUS_ON_SERVICE = "4"; //服务中
    public static final String SALES_AFTER_STATUS_FINISHED = "5"; //完成
    public static final String SALES_AFTER_STATUS_REFUSED = "6"; //拒绝

    /** 订单进度状态 **/
    public static final String ORDER_PROGRESS_STATUS_WAIT_ACCEPTED = "0"; //待接单
    public static final String ORDER_PROGRESS_STATUS_MASTER_ACCPETED = "1"; //师傅接单
    public static final String ORDER_PROGRESS_STATUS_MASTER_DOOR = "2"; //师傅上门中
    public static final String ORDER_PROGRESS_STATUS_WAIT_PAY = "3"; //待付款
    public static final String ORDER_PROGRESS_STATUS_FINISHED = "4"; //师傅完成维修
    public static final String ORDER_PROGRESS_STATUS_COMMENT = "5"; //待评价
    public static final String ORDER_PROGRESS_STATUS_COMMENT_FINISH = "6"; //已评价

    public void initViewData() {
        //        OrderProgressRequest infoRequest = new OrderProgressRequest();
        //        infoRequest.orderId = getArguments().getString("orderId");
        //        commitRequest(infoRequest);
        final Bundle bundle = getArguments();
        if (bundle == null) {
            return;
        }
        HashMap<String, String> map = new HashMap<>();
        HttpRequestBiz httpRequestBiz = new HttpRequestBiz();
        HashMap<String, String> params;
        if (!TextUtils.isEmpty(getArguments().getString("salesAfterId"))) {
            map.put("salesAfterId", getArguments().getString("salesAfterId"));
            params = httpRequestBiz.getBaseParams("order/customer/salesAfterProgress", map);
        } else {
            map.put("orderId", getArguments().getString("orderId"));
            params = httpRequestBiz.getBaseParams("order/customer/progress", map);
        }

        //        HttpOkBiz.getInstance().sendPost(httpRequestBiz.requestUrl, params, new HttpRequestCallBack(OrderProgressBean.class, true, "") {
        HttpOkBiz.getInstance().sendPost(httpRequestBiz.requestUrl, params, new HttpRequestCallBack() {

            @Override
            public void onSuccess(ResponseBean result) {
                result.setObject(GsonUtil.getInstance().json2ListBean((String) result.getObject(), OrderProgressBean.class, ""));
                ArrayList<OrderProgressBean> arrayList = new ArrayList<>();
                if (result.getObject() != null) {
                    arrayList = (ArrayList<OrderProgressBean>) result.getObject();
                }

                List<String> list = new ArrayList<>();
                List<String> list_time = new ArrayList<>();
                if (TextUtils.isEmpty(bundle.getString("salesAfterId", ""))) {
                    list.add("待接单");
                    list.add("师傅已接单");
                    list.add("师傅上门中");
                    list.add("订单待付款");
                    list.add("订单已完成");
                    list.add("订单待评价");
                    list.add("订单已评价");
                }
                for (int i = 0; i < arrayList.size(); i++) {
                    OrderProgressBean bean = arrayList.get(i);
                    String progressTime = DateUtil.formatTime(arrayList.get(i).getProgressTime(), "yyyyMMddHHmmss", "MM月dd日 HH:mm");
                    list_time.add(progressTime);
                    String status = bean.getStatus();
                    if (!TextUtils.isEmpty(bundle.getString("salesAfterId", ""))) {
                        switch (status) {
                            case SALES_AFTER_STATUS_COMMIT://
                                list.add("售后申请已提交");
                                break;
                            case SALES_AFTER_STATUS_REFUND://
                                list.add("申请通过退款中");
                                break;
                            case SALES_AFTER_STATUS_WAIT_DOOR://
                                list.add("申请通过待上门");
                                break;
                            case SALES_AFTER_STATUS_WAIT_SERVICE://
                                list.add("待服务");
                                break;
                            case SALES_AFTER_STATUS_ON_SERVICE://
                                list.add("服务中");
                                break;
                            case SALES_AFTER_STATUS_FINISHED://
                                list.add("售后完成");
                                break;
                            case SALES_AFTER_STATUS_REFUSED://
                                list.add("售后拒绝");
                                break;
                            default:// 默认
                                break;
                        }
                        //                    } else {
                        //                        switch (status) {
                        //                            case ORDER_PROGRESS_STATUS_WAIT_ACCEPTED://
                        //                                list.add("待接单");
                        //                                break;
                        //                            case ORDER_PROGRESS_STATUS_MASTER_ACCPETED://
                        //                                list.add("师傅已接单");
                        //                                break;
                        //                            case ORDER_PROGRESS_STATUS_MASTER_DOOR://
                        //                                list.add("师傅上门中");
                        //                                break;
                        //                            case ORDER_PROGRESS_STATUS_WAIT_PAY://
                        //                                list.add("订单待付款");
                        //                                break;
                        //                            case ORDER_PROGRESS_STATUS_FINISHED://
                        //                                list.add("订单已完成");
                        //                                break;
                        //                            case ORDER_PROGRESS_STATUS_COMMENT://
                        //                                list.add("订单待评价");
                        //                                break;
                        //                            case ORDER_PROGRESS_STATUS_COMMENT_FINISH://
                        //                                list.add("订单已评价");
                        //                                break;
                        //                            default:// 默认
                        //                                break;
                        //                        }
                    }
                }

                int position = 0;
                if (arrayList.size() > 0) {
                    position = arrayList.size() - 1;
                }

                if (list_time.size() > list.size()) {
                    list_time = list_time.subList(0, list.size() - 1);
                }

                VerticalStepView verticalStepView = new VerticalStepView(getActivity());
                verticalStepView.setStepsViewIndicatorComplectingPosition(position)//设置完成的步数
                        .reverseDraw(false)
                        .setStepViewTexts(list)
                        .setStepViewTimeTexts(list_time)
                        .setTextSize(14)
                        .setLinePaddingProportion(1)//设置indicator线与线间距的比例系数
                        .setStepsViewIndicatorCompletedLineColor(ContextCompat.getColor(getActivity(), R.color.light_black)
                        )//设置StepsViewIndicator完成线的颜色
                        .setStepsViewIndicatorUnCompletedLineColor(ContextCompat.getColor(getActivity(), R.color.light_black))//设置StepsViewIndicator未完成线的颜色
                        //.setStepViewComplectedTextColor(ContextCompat.getColor(getActivity(), R.color.orange))
                        //设置StepsView text完成线的颜色
                        //.setStepViewUnComplectedTextColor(ContextCompat.getColor(getActivity(), R.color.black))//设置StepsView text未完成线的颜色
                        .setStepsViewIndicatorCompleteIcon(ContextCompat.getDrawable(getActivity(), R.drawable.default_icon))
                        //设置StepsViewIndicator CompleteIcon
                        .setStepsViewIndicatorDefaultIcon(ContextCompat.getDrawable(getActivity(), R.drawable.default_icon))
                        //设置StepsViewIndicator DefaultIcon
                        .setStepsViewIndicatorAttentionIcon(ContextCompat.getDrawable(getActivity(), R.drawable.icon_sign));

                view_parent.removeAllViews();
                view_parent.addView(verticalStepView);
            }

            @Override
            public void onFail(ResponseBean result) {
                //                callBack.onFail(result);
            }
        });
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        LogUtil.i("setUserVisibleHint-----------------");
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            //相当于Fragment的onResume
            initViewData();
        } else {
            //相当于Fragment的onPause
        }
    }
}

package com.xzxx.decorate.o2o.master;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.amos.modulebase.bean.ResponseBean;
import com.amos.modulebase.mvp.baseclass.BaseActivity;
import com.amos.modulebase.mvp.baseclass.BaseView;
import com.amos.modulebase.mvp.model.HttpOkBiz;
import com.amos.modulebase.utils.DateUtil;
import com.amos.modulebase.utils.http.HttpRequestCallBack;
import com.amos.modulebase.utils.picasso.PicassoUtil;
import com.kiun.modelcommonsupport.ui.views.NavigatorBar;
import com.kiun.modelcommonsupport.ui.views.NavigatorListener;
import com.kiun.modelcommonsupport.utils.HttpRequestBiz;
import com.kiun.modelcommonsupport.utils.MCDialogManager;
import com.kiun.modelcommonsupport.utils.StatusBarUtils;
import com.xzxx.decorate.o2o.bean.BillDetail;

import java.util.HashMap;

/**
 * Created by zf on 2018/7/22.
 * 账单详情
 */
public class BillDetailActivity extends BaseActivity implements BaseView {

    private NavigatorBar title_bar;
    private ImageView img_logo;
    private TextView txt_desc;
    private TextView txt_tranAmt;
    private TextView txt_tranType;
    private TextView txt_payType;
    private TextView txt_desc_1;
    private TextView txt_feeName;
    private TextView txt_tranDate;
    private TextView txt_busiId;

    @Override
    protected void setTranslucentStatus(boolean on) {
        StatusBarUtils.with(this)
                .setColor(-1)
                .init();
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
    }

    @Override
    public void initVP() {
        mvpView = this;
    }

    @Override
    public int getContentViewId() {
        return R.layout.layout_bill_detail;
    }

    @Override
    public void findViews() {
        title_bar = findViewByIds(R.id.title_bar);
        img_logo = findViewByIds(R.id.img_logo);
        txt_desc = findViewByIds(R.id.txt_desc);
        txt_tranAmt = findViewByIds(R.id.txt_tranAmt);
        txt_tranType = findViewByIds(R.id.txt_tranType);
        txt_payType = findViewByIds(R.id.txt_payType);
        txt_desc_1 = findViewByIds(R.id.txt_desc_1);
        txt_feeName = findViewByIds(R.id.txt_feeName);
        txt_tranDate = findViewByIds(R.id.txt_tranDate);
        txt_busiId = findViewByIds(R.id.txt_busiId);
    }

    @Override
    public void init(Bundle bundle) {
        if (bundle == null) {
            return;
        }
        String tranFundLogId = bundle.getString("tranFundLogId", "");
        initViewData(tranFundLogId);
    }

    @Override
    public void widgetListener() {
        title_bar.setListener(new NavigatorListener() {
            @Override
            public void onLeftClick() {
                finishActivity();
            }

            @Override
            public void onRightClick() {

            }
        });
    }

    private void initViewData(String id) {
        showProgress(false);
        HashMap<String, String> map = new HashMap<>();
        HttpRequestBiz httpRequestBiz = new HttpRequestBiz();
        HashMap<String, String> params;
        map.put("acntTranDetailId", id);

        params = httpRequestBiz.getBaseParams("user/master/billInfo", map);

        HttpOkBiz.getInstance().sendPost(httpRequestBiz.requestUrl, params, new HttpRequestCallBack(BillDetail.class) {

            @Override
            public void onSuccess(ResponseBean result) {
                BillDetail bean = (BillDetail) result.getObject();
                //                        "logo": "http:\/\/pawx04z5h.bkt.clouddn.com\/FjB2qQLnM82r2_MsgAif_7ivOISR",
                //                        "desc": "端木皮怎修电视机",
                //                        "tranAmt": 0.01,
                //                        "payType": "0",
                //                        "feeName": "提现",
                //                        "tranType": "0",
                //                        "tranDate": "20180918",
                //                        "tranTime": "20180918",
                //                        "busiId": "201809181418670027831395"
                if(!TextUtils.isEmpty(bean.getLogo())){
                    PicassoUtil.loadCircleImage(activity, bean.getLogo(), img_logo);
                }
                txt_desc.setText(bean.getDesc());
                txt_tranAmt.setText(bean.getFeeName().equals("收款") ? "+" : "-" + bean.getTranAmt());
//                txt_tranType.setText(bean.getTranType().equals("0") ? "交易成功" : "交易失败");
                txt_tranType.setText("交易成功");
                if(bean.getPayType().isEmpty()){
                    txt_payType.setText("个人余额");
                }else{
                    txt_payType.setText(bean.getPayType().equals("0") ? "微信" : "支付宝");
                }
                txt_desc_1.setText(bean.getTranType().endsWith("0") ? "收入" : "支出");
                txt_feeName.setText(bean.getFeeName());
                txt_tranDate.setText(DateUtil.formatTime(bean.getTranDate() + bean.getTranTime(), "yyyyMMddHHmmss", "yyyy-MM-dd HH:mm"));
                txt_busiId.setText(bean.getBusiId());
                dismissProgress();
            }

            @Override
            public void onFail(ResponseBean result) {
                String content = result.getInfo();
                if (content.contains("=")) {
                    try {
                        String[] contents = content.split("=");
                        content = contents[1].replace("}", "");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                MCDialogManager.error(activity, content);
                dismissProgress();
            }
        });
    }
}

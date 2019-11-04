package com.kiun.modelcommonsupport.controllers.authority;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.amos.modulebase.bean.ResponseBean;
import com.amos.modulebase.mvp.model.HttpOkBiz;
import com.amos.modulebase.utils.PreferencesUtil;
import com.amos.modulebase.utils.http.HttpRequestCallBack;
import com.kiun.modelcommonsupport.MainApplication;
import com.kiun.modelcommonsupport.R;
import com.kiun.modelcommonsupport.adapter.ItemListener;
import com.kiun.modelcommonsupport.controllers.BaseRequestAcitity;
import com.kiun.modelcommonsupport.network.MCBaseRequest;
import com.kiun.modelcommonsupport.network.requests.AcceptStatusReqeust;
import com.kiun.modelcommonsupport.ui.views.ACellView;
import com.kiun.modelcommonsupport.utils.HttpRequestBiz;
import com.kiun.modelcommonsupport.utils.MCDialogManager;

import java.util.HashMap;

/**
 * Created by zf on 2018/6/25.
 * 设置页面
 */
public class SettingActivity extends BaseRequestAcitity {

    ACellView phoneNumberCellView;
    // 接单服务
    View view_order_service;
    ImageView img_start;
    boolean isAccept = false;

    @Override
    public int getLayoutId() {
        return R.layout.layout_setting;
    }

    @Override
    public void initView() {
        phoneNumberCellView = findViewById(R.id.phoneNumberCellView);
        view_order_service = findViewById(R.id.view_order_service);
        img_start = findViewById(R.id.img_start);
        txt_tips = findViewById(R.id.txt_tips);
        //        MCUserInfo userInfo = MainApplication.getInstance().getUserInfo(false);
        String phone = MainApplication.getInstance().getValue("sp_key_login_phone");
        if (!TextUtils.isEmpty(phone)) {
            phoneNumberCellView.setRightTitle(phone.replace(phone.substring(3, 7), "****"));
        }

        if (getPackageName().contains("master")) {
            view_order_service.setVisibility(View.VISIBLE);
            String acceptStatus = (String) PreferencesUtil.get("acceptStatus" + phone, "");
            isAccept = acceptStatus.equals("0");
            if (isAccept) {
                img_start.setImageDrawable(getResources().getDrawable(R.drawable.checkbox_switch_on));
            } else {
                img_start.setImageDrawable(getResources().getDrawable(R.drawable.checkbox_switch_off));
            }
            img_start.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    show(isAccept);
                }
            });
        } else {
            view_order_service.setVisibility(View.GONE);
        }

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(MainApplication.LOGOUT_ACTION);
        registerReceiver(receiver, intentFilter);
        showProgress(false);
        initViewData();

    }

    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(MainApplication.LOGOUT_ACTION)) {
                //                    new Handler(Looper.getMainLooper()) {
                //                        @Override
                //                        public void handleMessage(Message msg) {
                //                    Intent intent1 = new Intent(SettingActivity.this, LoginActivity.class);
                //                    intent1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                //                    startActivity(intent1);
                finish();
                //                        }
                //                    }.sendEmptyMessageDelayed(0, 500);
            }
        }
    };

    @Override
    protected void onDestroy() {
        try {
            unregisterReceiver(receiver);
        } catch (Exception e) {
        }
        super.onDestroy();
    }

    @Override
    public void onCellClick(View targetView, int tag) {
        switch (tag) {
            case 4:
                MCDialogManager.showMessage(this, "是否呼叫客服", "0755-2629653", "呼叫客服", "取消呼叫", R.drawable.svg_icon_prompt_big).setListener(new ItemListener() {
                    @Override
                    public void onItemClick(View listView, Object itemData, int tag) {
                        if (tag == MCDialogManager.TAG_RIGHT_BTN) {
                            Intent intent = new Intent();
                            intent.setAction(Intent.ACTION_DIAL);
                            intent.setData(Uri.parse("tel:0755-2629653"));
                            startActivity(intent);
                        }
                    }
                });
                break;
            case 5:
                MCDialogManager.showMessage(this, "清理成功", "缓存已清理完毕", "好的", R.drawable.svg_laugh);
                break;
            case 3:
                Bundle bundle = new Bundle();
                bundle.putString("url", "http://www.baidu.com");
                bundle.putString("title", "关于我们");
                Intent intent = new Intent(this, WebBaseActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
                break;
            case 6:
                bundle = new Bundle();
                bundle.putString("url", "http://www.baidu.com");
                bundle.putString("title", "服务指南");
                intent = new Intent(this, WebBaseActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
                break;
        }
    }

    @Override
    public void onSubmitClick(Button button) {
        MainApplication.getInstance().logOut();
        finish();
    }

    @Override
    public void onDataChanged(Object data, MCBaseRequest request) {
        if (request instanceof AcceptStatusReqeust) {
            isAccept = ((AcceptStatusReqeust) request).acceptStatus.equals("0");
            //            acceptTextView.setText(isAccept ? "暂停接单" : "开始接单");
            if (isAccept) {
                img_start.setImageDrawable(getResources().getDrawable(R.drawable.checkbox_switch_on));
            } else {
                img_start.setImageDrawable(getResources().getDrawable(R.drawable.checkbox_switch_off));
            }
        }
    }

    TextView txt_tips;

    private void initViewData() {
        HashMap<String, String> map = new HashMap<>();
        HttpRequestBiz httpRequestBiz = new HttpRequestBiz();
        HashMap<String, String> params;
        map.put("deviceToken", MainApplication.getInstance().getUserInfo(false).tokenId);

        params = httpRequestBiz.getBaseParams("user/userInfo/pswStrength", map);

        HttpOkBiz.getInstance().sendPost(httpRequestBiz.requestUrl, params, new HttpRequestCallBack() {

            @Override
            public void onSuccess(ResponseBean result) {
                String str = (String) result.getObject();
                String tips = "";
                if (!TextUtils.isEmpty(str)) {
                    // 密码强度0弱1中2强 空为验证码登录
                    if (str.equals("2")) {
                        tips = "强";
                    } else if (str.equals("1")) {
                        tips = "中";
                    } else {
                        tips = "弱";
                    }
                }
                txt_tips.setText(tips);
                dismissProgress();
            }

            @Override
            public void onFail(ResponseBean result) {
                //                String content = result.getInfo();
                //                if (content.contains("=")) {
                //                    try {
                //                        String[] contents = content.split("=");
                //                        content = contents[1].replace("}", "");
                //                    } catch (Exception e) {
                //                        e.printStackTrace();
                //                    }
                //                }
                //                MCDialogManager.error(SettingActivity.this, content);
                dismissProgress();
            }
        });
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        initViewData();
    }

    private void show(boolean isAccept) {
        MCDialogManager.showMessage(this, isAccept ? "是否暂停接单？" : "是否开始接单？", isAccept ? "暂停接单系统将无法给您推送订单，如需开启点击“个人中心-设置-接单服务”打开开关即可" : "开始接单系统将推送订单给您",
                isAccept ? "取消暂停" : "取消开始", isAccept ? "确认暂停" : "确认开始", R.drawable.icon_prompt).setListener(new ItemListener() {
            @Override
            public void onItemClick(View listView, Object itemData, int tag) {
                if (tag == MCDialogManager.TAG_LEFT_BTN) {
                    AcceptStatusReqeust reqeust = new AcceptStatusReqeust();
                    reqeust.acceptStatus = isAccept ? "1" : "0";
                    commitRequest(reqeust);
                }
            }
        });
    }
}

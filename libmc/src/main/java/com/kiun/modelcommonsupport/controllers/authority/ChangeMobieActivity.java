package com.kiun.modelcommonsupport.controllers.authority;

import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.kiun.modelcommonsupport.MainApplication;
import com.kiun.modelcommonsupport.R;
import com.kiun.modelcommonsupport.adapter.ItemListener;
import com.kiun.modelcommonsupport.network.MCBaseRequest;
import com.kiun.modelcommonsupport.network.requests.CodeRequest;
import com.kiun.modelcommonsupport.network.requests.PhoneValidRequest;
import com.kiun.modelcommonsupport.ui.views.UserEditText;
import com.kiun.modelcommonsupport.utils.MCDialogManager;

/**
 * Created by zf on 2018/7/1.
 * 更换手机号码页面
 */
public class ChangeMobieActivity extends PhoneCodeActivity<PhoneValidRequest> {

    TextView txt_phone;
    @Override
    public int getLayoutId() {
        return R.layout.layout_chage_mobile;
    }

    @Override
    public void initView() {
        super.initView();
        txt_phone=findViewById(R.id.txt_phone);

        type = "5";
        View txt_contact = findViewById(R.id.txt_contact);
        fillToView(-1, MainApplication.getInstance().getUserInfo(false));
        String phone = MainApplication.getInstance().getValue("sp_key_login_phone");
        if (!TextUtils.isEmpty(phone)) {
            txt_phone.setText(phone.replace(phone.substring(3, 7), "****"));
        }
        txt_contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MCDialogManager.showMessage(ChangeMobieActivity.this, "是否呼叫客服", "0755-2629653", "呼叫客服", "取消呼叫", R.drawable.svg_icon_prompt_big).setListener(new ItemListener() {
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
            }
        });
    }

    @Override
    protected int getCodeEdit() {
        return R.id.codeSendEdit;
    }

    @Override
    protected Intent getNextIntent() {
        return new Intent(this, PhoneModifyActivity.class);
    }

    @Override
    protected void onRequestFill(PhoneValidRequest request) {
        request.phone = MainApplication.getInstance().getUserInfo(false).getPhone();
    }

    @Override
    protected MCBaseRequest getCurRequest() {
        return null;
    }

    @Override
    protected MCBaseRequest getNextRequest() {
        return null;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onCodeSend(UserEditText editText) {
        CodeRequest request = new CodeRequest();
        request.type = this.type;
        request.phone = MainApplication.getInstance().getUserInfo(false).getPhone();
        if (fillRequest(request, new String[]{"code"})) {
            commitRequest(request);
            return true;
        }
        return false;
    }
}

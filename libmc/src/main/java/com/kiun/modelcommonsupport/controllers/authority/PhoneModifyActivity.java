package com.kiun.modelcommonsupport.controllers.authority;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.kiun.modelcommonsupport.MainApplication;
import com.kiun.modelcommonsupport.R;
import com.kiun.modelcommonsupport.adapter.ItemListener;
import com.kiun.modelcommonsupport.network.MCBaseRequest;
import com.kiun.modelcommonsupport.network.requests.PhoneModifyRequest;
import com.kiun.modelcommonsupport.ui.views.UserEditText;
import com.kiun.modelcommonsupport.utils.MCDialogManager;

import java.util.regex.Pattern;

/**
 * Created by kiun_2007 on 2018/8/20.
 */

public class PhoneModifyActivity extends PhoneCodeActivity {

    @Override
    protected int getCodeEdit() {
        return R.id.codeSendEdit;
    }

    @Override
    protected Intent getNextIntent() {
        return null;
    }

    @Override
    protected MCBaseRequest getCurRequest() {
        //        PhoneModifyRequest modifyRequest = getIntent().getParcelableExtra("request");
        //        return modifyRequest;
        return new PhoneModifyRequest();
    }

    @Override
    protected MCBaseRequest getNextRequest() {
        return null;
    }

    @Override
    public int getLayoutId() {
        type = "5";
        return R.layout.layout_user_chang_phone;
    }

    @Override
    protected void onPhoneValidComplete(Object data) {
        UserEditText phoneEdit = findViewById(R.id.phoneEdit);
        String phone = phoneEdit.getText();
        if (!TextUtils.isEmpty(phone)) {
            phone = (phone.replace(phone.substring(3, 7), "****"));
        }
        MCDialogManager.showMessage(this, "号码修改成功！", "当前手机号是" + phone, "确定", R.drawable.svg_laugh).setListener(new ItemListener() {
            @Override
            public void onItemClick(View listView, Object itemData, int tag) {
                MainApplication.getInstance().logOut();
                //                Intent intent1 = new Intent(PhoneModifyActivity.this, LoginActivity.class);
                //                intent1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                //                startActivity(intent1);
                finish();
            }
        });
    }

    //    phoneEdit
    @Override
    public void onSubmitClick(Button button) {
        UserEditText phoneEdit = findViewById(R.id.phoneEdit);
        String phone = phoneEdit.getText();
        if (TextUtils.isEmpty(phone)) {
            Toast.makeText(this, "请输入手机号", Toast.LENGTH_SHORT).show();
            return;
        } else if (!Pattern.matches("^1[0-9]{10}$", phone)) {
            Toast.makeText(this, "请输入正确的手机号", Toast.LENGTH_SHORT).show();
            return;
        }
        super.onSubmitClick(button);
    }
}

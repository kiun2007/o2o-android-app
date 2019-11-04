package com.kiun.modelcommonsupport.controllers.authority;

import android.content.Intent;
import android.os.Parcelable;
import android.widget.Button;

import com.amos.modulebase.utils.KeyboardUtil;
import com.kiun.modelcommonsupport.ServiceError;
import com.kiun.modelcommonsupport.controllers.BaseRequestAcitity;
import com.kiun.modelcommonsupport.network.MCBaseRequest;
import com.kiun.modelcommonsupport.network.requests.CodeRequest;
import com.kiun.modelcommonsupport.network.requests.PhoneValidRequest;
import com.kiun.modelcommonsupport.ui.views.EditListenter;
import com.kiun.modelcommonsupport.ui.views.UserEditText;

/**
 * Created by zf on 2018/6/29.
 * 用户注册, 修改密码, 修改手机号页面基础类.
 */
public abstract class PhoneCodeActivity<T extends MCBaseRequest> extends BaseRequestAcitity implements EditListenter {

    private UserEditText codeEditText = null;
    protected String type = "2";

    @Override
    public void initView() {
        codeEditText = findViewById(getCodeEdit());
        codeEditText.setListenter(this);
    }

    protected abstract int getCodeEdit();

    protected abstract Intent getNextIntent();

    protected abstract MCBaseRequest getCurRequest();

    protected abstract MCBaseRequest getNextRequest();

    protected boolean fillPhoneValue(CodeRequest request){
        return true;
    }

    protected void onPhoneValidComplete(Object data) {
    }

    protected void onRequestFill(T request) {
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onCodeSend(UserEditText editText) {
        KeyboardUtil.hideKeyBord(codeEditText);
        CodeRequest request = new CodeRequest();
        request.type = this.type;
        if (fillRequest(request, new String[]{"code"}) && fillPhoneValue(request)) {
            commitRequest(request);
            return true;
        }
        return false;
    }

    @Override
    public void onDataChanged(Object data, MCBaseRequest request) {

        if (request instanceof CodeRequest) {
        } else if (request instanceof PhoneValidRequest) {
            Intent intent = getNextIntent();
            MCBaseRequest baseRequest = getNextRequest();

            if (baseRequest != null) {
                fillRequest(baseRequest, null);
                if ((baseRequest instanceof Parcelable)) {
                    if (intent != null) {
                        intent.putExtra("request", (Parcelable) baseRequest);
                    }
                }
            }

            if (intent != null) {
                startActivity(intent);
            }
            if (this instanceof PhoneModifyActivity) {
                onPhoneValidComplete(data);
            } else {
                finish();
            }
        } else {
            onPhoneValidComplete(data);
        }
    }


    @Override
    public void onSubmitClick(Button button) {

        MCBaseRequest request = getCurRequest();
        if (request == null) {
            PhoneValidRequest phoneValidRequest = new PhoneValidRequest();
            phoneValidRequest.codeType = type;
            request = phoneValidRequest;
        }
        if(fillRequest(request, null)){
            onRequestFill((T) request);
            commitRequest(request);
        }
    }

    @Override
    public void onError(Error error) {
        if (error != null) {
            ServiceError serviceError = (ServiceError) error;
            if (serviceError.getErrorCode() == 8003) {//用户未注册
                codeEditText.reSetBtn();
            }
        }
        super.onError(error);
    }

    @Override
    protected void onStop() {
        super.onStop();
        try {
            KeyboardUtil.hideKeyBord(codeEditText);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

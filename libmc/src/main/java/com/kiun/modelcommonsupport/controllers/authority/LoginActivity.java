package com.kiun.modelcommonsupport.controllers.authority;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;

import com.amos.modulebase.utils.KeyboardUtil;
import com.kiun.modelcommonsupport.MainApplication;
import com.kiun.modelcommonsupport.R;
import com.kiun.modelcommonsupport.controllers.BaseRequestAcitity;
import com.kiun.modelcommonsupport.network.MCBaseRequest;
import com.kiun.modelcommonsupport.network.requests.LoginRequest;
import com.kiun.modelcommonsupport.ui.views.UserEditText;

/**
 * Created by zf on 2018/6/27.
 * 登陆页面
 */
public class LoginActivity extends BaseRequestAcitity {

    @Override
    public int getLayoutId() {
        return R.layout.layout_user_login;
    }

    @Override
    public void initView() {
        View main = findViewById(R.id.main);
        View scroll_view = findViewById(R.id.scroll_view);
        addLayoutListener(main, scroll_view);
        ActivityInfo ai = null;
        try {
            ai = this.getPackageManager().getActivityInfo(getComponentName(), PackageManager.GET_META_DATA);
            if (ai != null && ai.metaData != null) {
                int noBackBar = ai.metaData.getInt("noBackBar", 0);
                if (noBackBar == 1) {
                    this.getNavigatorBar().setLeftButtonVisibility(View.GONE);
                }
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onSubmitClick(Button button) {
        LoginRequest request = new LoginRequest();
        if (fillRequest(request, null)) {
            commitRequest(request);
        }
    }

    @Override
    public void onDataChanged(Object data, MCBaseRequest request) {
        if (request instanceof LoginRequest) {
            UserEditText editText = findViewById(R.id.et_phone);
            MainApplication.getInstance().save("sp_key_login_phone", editText.getText());
            try {
                KeyboardUtil.hideKeyBord(editText.editText);
                ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            } catch (Exception e) {
                e.printStackTrace();
            }
            finish();
            MainApplication.getInstance().login(data);
        }
    }

    public void onClick(View view) {

        Intent intent = null;
        if (view.getId() == R.id.id_logit_forget_pwd) {
            intent = new Intent(LoginActivity.this, ForgetPasswordActivity.class);
        } else if (view.getId() == R.id.id_login_register) {
            intent = new Intent(LoginActivity.this, RegisterActivity.class);
        } else if (view.getId() == R.id.id_login_verification) {
            intent = new Intent(LoginActivity.this, CodeLoginActivity.class);
            finish();
        }

        if (intent != null) {
            startActivity(intent);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        try {
            UserEditText editText = findViewById(R.id.et_phone);
            KeyboardUtil.hideKeyBord(editText);
            ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onLeftClick() {
        try {
            UserEditText editText = findViewById(R.id.et_phone);
            KeyboardUtil.hideKeyBord(editText);
            ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onLeftClick();
    }

    public void addLayoutListener(final View main, final View scroll) {
        main.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Rect rect = new Rect();
                //1、获取main在窗体的可视区域
                main.getWindowVisibleDisplayFrame(rect);
                //2、获取main在窗体的不可视区域高度，在键盘没有弹起时，main.getRootView().getHeight()调节度应该和rect.bottom高度一样
                int mainInvisibleHeight = main.getRootView().getHeight() - rect.bottom;
                int screenHeight = main.getRootView().getHeight();//屏幕高度
                //3、不可见区域大于屏幕本身高度的1/4：说明键盘弹起了
                if (mainInvisibleHeight > screenHeight / 4) {
                    //5､让界面整体上移键盘的高度
                    int[] location = new int[2];
                    scroll.getLocationInWindow(location);
                    // 4､获取Scroll的窗体坐标，算出main需要滚动的高度
                    int srollHeight = (location[1] + scroll.getHeight()) - rect.bottom;
                    //5､让界面整体上移键盘的高度
                    main.scrollTo(0, srollHeight);
                } else {
                    //3、不可见区域小于屏幕高度1/4时,说明键盘隐藏了，把界面下移，移回到原有高度
                    main.scrollTo(0, 0);
                }
            }
        });
    }

}
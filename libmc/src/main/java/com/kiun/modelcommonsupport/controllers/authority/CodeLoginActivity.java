package com.kiun.modelcommonsupport.controllers.authority;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.Toast;

import com.kiun.modelcommonsupport.MainApplication;
import com.kiun.modelcommonsupport.R;
import com.kiun.modelcommonsupport.network.MCBaseRequest;
import com.kiun.modelcommonsupport.network.requests.CodeLoginRequest;
import com.kiun.modelcommonsupport.ui.views.UserEditText;

import java.util.regex.Pattern;

/**
 * Created by kiun_2007 on 2018/8/20.
 */

public class CodeLoginActivity extends PhoneCodeActivity<CodeLoginRequest> {

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
        return new CodeLoginRequest();
    }

    @Override
    protected MCBaseRequest getNextRequest() {
        return null;
    }

    @Override
    public void initView() {
        super.initView();
        View main = findViewById(R.id.main);
        View scroll_view = findViewById(R.id.scroll_view);
        addLayoutListener(main, scroll_view);
    }

    @Override
    public int getLayoutId() {
        type = "1";
        return R.layout.layout_user_code_login;
    }

    public void onClick(View v){
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onRequestFill(CodeLoginRequest request) {
        request.account = request.phone;
    }

    @Override
    protected void onPhoneValidComplete(Object data) {
        UserEditText editText = findViewById(R.id.phoneEdit);
        MainApplication.getInstance().save("sp_key_login_phone", editText.getText());
        finish();
        MainApplication.getInstance().login(data);
    }

    //    phoneEdit
    @Override
    public void onSubmitClick(Button button) {
        try {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(button.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        } catch (Exception e) {
            e.printStackTrace();
        }
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

    @Override
    public void onLeftClick() {
        Intent intent = new Intent(CodeLoginActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    // ************************************************************************返回键事件处理
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            if (event.getAction() == KeyEvent.ACTION_DOWN && event.getRepeatCount() == 0) {
                // 要执行的事件
                Intent intent = new Intent(CodeLoginActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
            return true;
        }
        return super.dispatchKeyEvent(event);
    }

    boolean isShow = false;

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
                    if (isShow) {
                        return;
                    }
                    isShow = true;
                    //5､让界面整体上移键盘的高度
                    int[] location = new int[2];
                    scroll.getLocationInWindow(location);
                    // 4､获取Scroll的窗体坐标，算出main需要滚动的高度
                    int srollHeight = (location[1] + scroll.getHeight()) - rect.bottom;
                    //5､让界面整体上移键盘的高度
                    main.scrollTo(0, srollHeight);
                } else {
                    isShow = false;
                    //3、不可见区域小于屏幕高度1/4时,说明键盘隐藏了，把界面下移，移回到原有高度
                    main.scrollTo(0, 0);
                }
            }
        });
    }
}

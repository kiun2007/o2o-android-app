package com.kiun.modelcommonsupport.controllers.authority;

import android.app.Activity;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.amos.modulebase.utils.IntentUtil;
import com.kiun.modelcommonsupport.MainApplication;
import com.kiun.modelcommonsupport.R;
import com.kiun.modelcommonsupport.adapter.ItemListener;
import com.kiun.modelcommonsupport.controllers.BaseRequestAcitity;
import com.kiun.modelcommonsupport.network.MCBaseRequest;
import com.kiun.modelcommonsupport.network.requests.ForgetRequest;
import com.kiun.modelcommonsupport.network.requests.RegisterRequest;
import com.kiun.modelcommonsupport.ui.views.UserEditText;
import com.kiun.modelcommonsupport.utils.MCDialogManager;

import java.util.regex.Pattern;

/**
 * Created by zf on 2018/6/29.
 * 设置登陆密码页面
 */
public class SettingLoginPasswodActivity extends BaseRequestAcitity {

    MCBaseRequest request = null;

    @Override
    public int getLayoutId() {
        return R.layout.layout_setting_login_password;
    }

    @Override
    public void initView() {
        request = getIntent().getParcelableExtra("request");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onSubmitClick(Button button) {
        UserEditText id_setting_login_password = findViewById(R.id.id_setting_login_password);
        String passWord = id_setting_login_password.getText();
        if (TextUtils.isEmpty(passWord) ||
                (!isPassword(passWord))) {
            Toast.makeText(SettingLoginPasswodActivity.this, "请按要求输入密码", Toast.LENGTH_SHORT).show();
            return;
        }
        String phone = "";
        if (request instanceof ForgetRequest) {
            ((ForgetRequest) request).password = passWord;
            phone = ((ForgetRequest) request).phone;
        } else if (request instanceof RegisterRequest) {
            ((RegisterRequest) request).password = passWord;
            phone = ((RegisterRequest) request).phone;
        }
        if (passWord.contains(phone)) {
            Toast.makeText(SettingLoginPasswodActivity.this, "请按要求输入密码", Toast.LENGTH_SHORT).show();
            return;
        }
        if (fillRequest(request, null)) {
            commitRequest(request);
        }
    }

    @Override
    public void onDataChanged(Object data, MCBaseRequest request) {

        if (request instanceof RegisterRequest) {
            MCDialogManager.showMessage(this, "您已注册成功!", "欢迎您来到小装小修", "立即去登录", R.drawable.svg_laugh).setListener(new ItemListener() {
                @Override
                public void onItemClick(View listView, Object itemData, int tag) {
                    SettingLoginPasswodActivity.this.finish();
                }
            });
        }

        if (request instanceof ForgetRequest) {
            MCDialogManager.showMessage(this, "密码修改成功!", "请用新密码登录！", "立即登录", "返回首页", R.drawable.svg_laugh).setListener(new ItemListener() {
                @Override
                public void onItemClick(View listView, Object itemData, int tag) {
                    if (tag == MCDialogManager.TAG_RIGHT_BTN) {
                        MainApplication.getInstance().logOut();
                        SettingLoginPasswodActivity.this.finish();
                    } else if (tag == MCDialogManager.TAG_LEFT_BTN) {
                        // remove();
                        try {
                            if (getPackageName().contains("master")) {
                                IntentUtil.gotoActivityToTopAndFinish(SettingLoginPasswodActivity.this, Class.forName("com.xzxx.decorate.o2o.master.MainActivity"));
                            } else {
                                IntentUtil.gotoActivityToTopAndFinish(SettingLoginPasswodActivity.this, Class.forName("com.xzxx.decorate.o2o.consumer.MainActivity"));
                            }
                        } catch (ClassNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        }
    }

    /** 特殊字符 */
    private static final String SPECIAL_CHARACTER = "~!@#$%^&*()_+,./'\";:{}";
    /** 正则表达式：验证用户名 */
    private static final String REGEX_USERNAME = "^[a-zA-Z]\\w{5,17}$";
    /** 正则表达式：验证密码 */
    //密码为8~20位数字,英文,符号至少两种组合的字符
    private static final String REGEX_PASSWORD = "^(?![0-9]+$)(?![a-z]+$)(?![A-Z]+$)(?![" + SPECIAL_CHARACTER + "]+$)[" + SPECIAL_CHARACTER + "0-9A-Za-z]{8,16}$";

    /**
     * 校验密码
     *
     * @param password
     *         待校验的字符串
     *
     * @return 校验通过返回true，否则返回false
     */
    public static boolean isPassword(String password) {
        return Pattern.matches(REGEX_PASSWORD, password);
    }

    private void remove() {
        SharedPreferences sp = this.getSharedPreferences("AppSave", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.remove(MainApplication.USER_INFO);
        editor.commit();
    }
}

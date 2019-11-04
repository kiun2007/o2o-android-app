package com.kiun.modelcommonsupport.hx;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;

import com.hyphenate.easeui.EaseConstant;
import com.hyphenate.easeui.ui.EaseBaseActivity;
import com.hyphenate.easeui.ui.EaseBaseFragment;
import com.hyphenate.easeui.utils.EasePermissionUtils;
import com.kiun.modelcommonsupport.MainApplication;
import com.kiun.modelcommonsupport.R;
import com.kiun.modelcommonsupport.data.MCUserInfo;


public class ChatActivity extends EaseBaseActivity implements ActivityCompat.OnRequestPermissionsResultCallback {
    public static ChatActivity activityInstance;
    protected EaseBaseFragment chatFragment;
    String toChatUsername;
    String toChatNickName;
    String toChatAvatar;

    public EaseBaseFragment getFragment() {
        return new HXChatFragment();
    }

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.em_activity_chat);
        activityInstance = this;
        //get user id or group id
        toChatUsername = getIntent().getStringExtra(EaseConstant.EXTRA_USER_ID);
        toChatAvatar = getIntent().getStringExtra(EaseConstant.EXTRA_HE_AVATAR);
        toChatNickName = getIntent().getStringExtra(EaseConstant.EXTRA_HE_NICK_NAME);
        //use EaseChatFratFragment
        chatFragment = getFragment();
        //pass parameters to chat fragment

        MCUserInfo userInfo = MainApplication.getInstance().getUserInfo(false);

        if(userInfo != null){
            if(MainApplication.getInstance().getValue(MainApplication.MASTER_INFO) != null){
                getIntent().putExtra(EaseConstant.EXTRA_NICK_NAME, userInfo.name + "|" + MainApplication.getInstance().getValue(MainApplication.MASTER_INFO));
            }else{
                getIntent().putExtra(EaseConstant.EXTRA_NICK_NAME, userInfo.name);
            }

            getIntent().putExtra(EaseConstant.EXTRA_AVATAR, userInfo.headImg);
            if(toChatNickName != null){
                getIntent().putExtra(EaseConstant.EXTRA_HE_NICK_NAME, toChatNickName);
            }
            if(toChatAvatar != null){
                getIntent().putExtra(EaseConstant.EXTRA_HE_AVATAR, toChatAvatar);
            }
        }

        chatFragment.setArguments(getIntent().getExtras());
        getSupportFragmentManager().beginTransaction().add(R.id.container, chatFragment).commit();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        activityInstance = null;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        // make sure only one chat activity is opened
        String username = intent.getStringExtra("userId");
        if (toChatUsername.equals(username))
            super.onNewIntent(intent);
        else {
            finish();
            startActivity(intent);
        }
    }

    @Override
    public void onBackPressed() {
        if (chatFragment instanceof HXChatFragment) {
            ((HXChatFragment) chatFragment).onBackPressed();
        } else {
            finish();
        }
    }

    public String getToChatUsername() {
        return toChatUsername;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        PermissionsManager.getInstance().notifyPermissionsChange(permissions, grantResults);
        if (permissions.length <= 0 || grantResults.length <= 0) {
            return;
        }
        EasePermissionUtils.getInstance().requestPermissionsResult(permissions, grantResults, requestCode);
    }
}

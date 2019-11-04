package com.kiun.modelcommonsupport.hx;

import android.os.Bundle;

import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.easeui.ui.EaseBaseFragment;
import com.hyphenate.easeui.ui.EaseConversationListFragment;

import java.util.List;

/**
 * Created by kiun_2007 on 2018/9/11.
 */

public class ContactListActivity extends ChatActivity implements EMMessageListener {

    protected ConversationListFragment fragment;

    @Override
    public EaseBaseFragment getFragment() {
        return new ConversationListFragment();
    }

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        EMClient.getInstance().chatManager().addMessageListener(this);
    }

    @Override
    public void onMessageReceived(List<EMMessage> list) {
        ((ConversationListFragment)chatFragment).refresh();
    }

    @Override
    public void onCmdMessageReceived(List<EMMessage> list) {

    }

    @Override
    public void onMessageRead(List<EMMessage> list) {

    }

    @Override
    public void onMessageDelivered(List<EMMessage> list) {

    }

    @Override
    public void onMessageRecalled(List<EMMessage> list) {

    }

    @Override
    public void onMessageChanged(EMMessage emMessage, Object o) {

    }
}

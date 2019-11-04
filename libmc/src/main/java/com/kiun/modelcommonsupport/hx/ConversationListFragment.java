package com.kiun.modelcommonsupport.hx;

import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.easeui.EaseConstant;
import com.hyphenate.easeui.ui.EaseConversationListFragment;
import com.kiun.modelcommonsupport.MainApplication;
import com.kiun.modelcommonsupport.R;
import com.kiun.modelcommonsupport.data.MCUserInfo;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by kiun_2007 on 2018/9/11.
 */

public class ConversationListFragment extends EaseConversationListFragment {

    @Override
    protected void setUpView() {
        super.setUpView();
        conversationListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                EMConversation conversation = conversationListView.getItem(position);
                String username = conversation.conversationId();
                String extStr = conversation.getExtField();
                String heAvatar = null;
                String heNickName = null;
                JSONObject extObject = null;
                if(!extStr.isEmpty()){
                    try {
                        extObject = new JSONObject(extStr);
                        heAvatar = extObject.optString("avatar");
                        heNickName = extObject.optString("nick");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                if(extObject == null){
                    EMMessage message = conversation.getLatestMessageFromOthers();
                    if(message != null){
                        heAvatar = (String) message.ext().get("avatar");
                        heNickName = (String) message.ext().get("nick");
                    }
                }

                if (username.equals(EMClient.getInstance().getCurrentUser()))
                    Toast.makeText(getActivity(), R.string.Cant_chat_with_yourself, Toast.LENGTH_SHORT).show();
                else {
                    // start chat acitivity
                    Intent intent = new Intent(getActivity(), ChatActivity.class);
                    if(conversation.isGroup()){
                        if(conversation.getType() == EMConversation.EMConversationType.ChatRoom){
                            // it's group chat
                            intent.putExtra(EaseConstant.EXTRA_CHAT_TYPE, EaseConstant.CHATTYPE_CHATROOM);
                        }else{
                            intent.putExtra(EaseConstant.EXTRA_CHAT_TYPE, EaseConstant.CHATTYPE_GROUP);
                        }
                    }

                    if(heAvatar != null && heNickName != null){
                        intent.putExtra(EaseConstant.EXTRA_HE_AVATAR, heAvatar);
                        intent.putExtra(EaseConstant.EXTRA_HE_NICK_NAME, heNickName);
                    }

                    MCUserInfo userInfo = MainApplication.getInstance().getUserInfo(false);

                    if(userInfo != null) {
                        intent.putExtra(EaseConstant.EXTRA_NICK_NAME, userInfo.name);
                        intent.putExtra(EaseConstant.EXTRA_AVATAR, userInfo.headImg);
                    }

                    // it's single chat
                    intent.putExtra(EaseConstant.EXTRA_USER_ID, username);
                    intent.putExtra("hxAccount", username);
                    startActivity(intent);
                }
            }
        });
        super.setUpView();
    }
}

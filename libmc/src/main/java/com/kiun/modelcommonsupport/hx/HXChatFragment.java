package com.kiun.modelcommonsupport.hx;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.Toast;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.easeui.EaseConstant;
import com.hyphenate.easeui.ui.EaseChatFragment;
import com.hyphenate.easeui.widget.chatrow.EaseCustomChatRowProvider;
import com.kiun.modelcommonsupport.R;
import com.kiun.modelcommonsupport.adapter.ItemListener;
import com.kiun.modelcommonsupport.data.ChatOrderInfoModel;
import com.kiun.modelcommonsupport.network.MCBaseRequest;
import com.kiun.modelcommonsupport.network.MCHttpCGI;
import com.kiun.modelcommonsupport.network.MCResponse;
import com.kiun.modelcommonsupport.network.requests.ChatOrderInfoRequest;
import com.kiun.modelcommonsupport.network.responses.MCUIResponse;
import com.kiun.modelcommonsupport.ui.views.AListView;
import org.json.JSONArray;
/**
 * Created by kiun_2007 on 2018/9/4.
 */

public class HXChatFragment extends EaseChatFragment implements EaseChatFragment.EaseChatFragmentHelper, MCUIResponse, ItemListener<ChatOrderInfoModel> {

    View headerView;
    AListView headListView;
    Class<? extends Activity> orderInfoActvityClz;
    String lastOrderId = null;
    @Override
    protected void setUpView() {
        setChatFragmentHelper(this);
        super.setUpView();
    }

    @Override
    protected void initView() {
        super.initView();
        lastOrderId = getArguments().getString(EaseConstant.EXTRA_LAST_ORDERINFO);
        ChatOrderInfoRequest chatOrderInfoRequest = new ChatOrderInfoRequest();
        chatOrderInfoRequest.hxAccount = toChatUsername;
        chatOrderInfoRequest.setResponse(new MCResponse(this));
        MCHttpCGI.defaultCenter().requestCGI(chatOrderInfoRequest);
        try {
            orderInfoActvityClz = (Class<? extends Activity>) Class.forName(MCBaseRequest.isMaster() ?
                    "com.xzxx.decorate.o2o.master.OrderDetailActivity" : "com.xzxx.decorate.o2o.ui.OrderDetailActivity");
        } catch (ClassNotFoundException e) { e.printStackTrace(); }

        headerView = View.inflate(getContext(), R.layout.layout_list_message_order, null);
        headListView = headerView.findViewById(R.id.mainListView);
        headListView.setItemListener(this);
        listView.addHeaderView(headerView);
    }

    @Override
    public void onItemClick(View listView, ChatOrderInfoModel itemData, int tag) {
        if(lastOrderId != null && lastOrderId.endsWith(itemData.getOrderId())){
            getActivity().finish();
            return;
        }
        if (orderInfoActvityClz != null){
            Intent intent = new Intent(getActivity(), orderInfoActvityClz);
            intent.putExtra("orderId", itemData.getOrderId());
            intent.putExtra("orderStatus", itemData.getOrderStatus());
            if (itemData.getOrderType().endsWith("1")){
                intent.putExtra("salesAfterId", itemData.getSalesAfterId());
                intent.putExtra("salesStatus", itemData.getSalesStatus());
            }
            startActivity(intent);
        }
    }

    @Override
    protected Intent getMapIntent() {
        return new Intent(getActivity(), HXMapActivity.class);
    }

    @Override
    public void onDataChanged(Object data, MCBaseRequest request) {
        if (request instanceof ChatOrderInfoRequest && data instanceof JSONArray){
            headListView.fill(data);
        }
    }

    @Override
    public void onBeginRequest() { }
    @Override
    public void onError(Error error) { }
    @Override
    public boolean isDead() { return false; }
    @Override
    public void onSetMessageAttributes(EMMessage message) { }
    @Override
    public void onEnterToChatDetails() { }
    @Override
    public void onAvatarClick(String username) { }
    @Override
    public void onAvatarLongClick(String username) { }
    @Override
    public boolean onMessageBubbleClick(EMMessage message) {
        return false;
    }
    @Override
    public void onMessageBubbleLongClick(EMMessage message) { }

    @Override
    public boolean onExtendMenuItemClick(int itemId, View view) {
        if(itemId == EaseChatFragment.ITEM_VIDEO){
            startVideoCall();
            return true;
        }
        if (itemId == EaseChatFragment.ITEM_VOICE){
            startVoiceCall();
            return true;
        }
        return false;
    }

    @Override
    public EaseCustomChatRowProvider onSetCustomChatRowProvider() {
        return null;
    }

    protected void startVideoCall() {
        if (!EMClient.getInstance().isConnected())
            Toast.makeText(getActivity(), "未连接到服务器", Toast.LENGTH_SHORT).show();
        else {
            startActivity(new Intent(getActivity(), VideoCallActivity.class).putExtra("username", toChatUsername)
                    .putExtra("isComingCall", false).putExtra(EaseConstant.EXTRA_HE_NICK_NAME, getArguments().getString(EaseConstant.EXTRA_HE_NICK_NAME)));
            // videoCallBtn.setEnabled(false);
            inputMenu.hideExtendMenuContainer();
        }
    }

    protected void startVoiceCall() {
        if (!EMClient.getInstance().isConnected()) {
            Toast.makeText(getActivity(), R.string.not_connect_to_server, Toast.LENGTH_SHORT).show();
        } else {
            startActivity(new Intent(getActivity(), VoiceCallActivity.class).putExtra("username", toChatUsername)
                    .putExtra(EaseConstant.EXTRA_HE_NICK_NAME, getArguments().getString(EaseConstant.EXTRA_HE_NICK_NAME))
                    .putExtra("isComingCall", false));
            // voiceCallBtn.setEnabled(false);
            inputMenu.hideExtendMenuContainer();
        }
    }
}

package com.hyphenate.easeui.utils;

import android.util.Pair;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class EaseLoadUnread {

    public boolean isHaveUnRead() {
        return unReadCount() > 0;
    }

    public int unReadCount() {
        int count = 0;
        List<EMConversation> list = loadConversationList();

        for (int i = 0; i < list.size(); i++) {
            EMConversation conversation = list.get(i);
            int countItem = conversation.getUnreadMsgCount();
            count = count + countItem;
        }
        return count;
    }

    /**
     * load conversation list
     *
     * @return +
     */
    private List<EMConversation> loadConversationList() {
        List<EMConversation> list = new ArrayList<EMConversation>();
        try {
            // get all conversations
            Map<String, EMConversation> conversations = EMClient.getInstance().chatManager().getAllConversations();
            List<Pair<Long, EMConversation>> sortList = new ArrayList<Pair<Long, EMConversation>>();
            /**
             * lastMsgTime will change if there is new message during sorting
             * so use synchronized to make sure timestamp of last message won't change.
             */
            synchronized (conversations) {
                for (EMConversation conversation : conversations.values()) {
                    if (conversation.getAllMessages().size() != 0) {
                        sortList.add(new Pair<Long, EMConversation>(conversation.getLastMessage().getMsgTime(), conversation));
                    }
                }
            }

            for (Pair<Long, EMConversation> sortItem : sortList) {
                list.add(sortItem.second);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
}

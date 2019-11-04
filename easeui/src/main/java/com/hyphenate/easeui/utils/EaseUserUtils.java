package com.hyphenate.easeui.utils;

import android.content.Context;
import android.text.Html;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.hyphenate.easeui.EaseUI;
import com.hyphenate.easeui.EaseUI.EaseUserProfileProvider;
import com.hyphenate.easeui.R;
import com.hyphenate.easeui.domain.EaseUser;

public class EaseUserUtils {

    static EaseUserProfileProvider userProvider;

    static {
        userProvider = EaseUI.getInstance().getUserProfileProvider();
    }

    /**
     * get EaseUser according username
     *
     * @param username
     */
    public static EaseUser getUserInfo(String username) {
        if (userProvider != null)
            return userProvider.getUser(username);

        return null;
    }

    public static String curNickName = null;

    public static String curAvatar = null;

    /**
     * set user avatar
     *
     * @param username
     */
    public static void setUserAvatar(Context context, String username, ImageView imageView) {
        EaseUser user = getUserInfo(username);
        if (user != null && user.getAvatar() != null) {
            try {
                int avatarResId = Integer.parseInt(user.getAvatar());
                Glide.with(context).load(avatarResId).into(imageView);
            } catch (Exception e) {
                //use default avatar
                //                Glide.with(context).load(user.getAvatar()).diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(R.drawable.ease_default_avatar).into(imageView);
                setUserAvatar(context, imageView, user.getAvatar());
            }
        } else {
            Glide.with(context).load(R.drawable.ease_default_avatar).into(imageView);
        }
    }

    public static void setUserAvatar(Context context, ImageView imageView, String iconPath){
        RequestOptions requestOptions = new RequestOptions()
                .placeholder(R.drawable.ease_default_avatar)
                .diskCacheStrategy(DiskCacheStrategy.ALL);

        Glide.with(context).load(iconPath).apply(requestOptions).into(imageView);
    }

    /**
     * set user's nickname
     */
    public static void setUserNick(String username, TextView textView) {
        if (textView != null) {
            EaseUser user = getUserInfo(username);
            if (user != null && user.getNick() != null) {
                textView.setText(user.getNick());
            } else {
                textView.setText(username);
            }
        }
    }

    public static void setUserNick(TextView textView, String nickName) {
        if (textView != null) {
            if(nickName.indexOf("|") < 0){
                textView.setText(nickName);
                return;
            }

            String[] nickNames = nickName.split("\\|");
            String firstName = nickNames[0];
            String scondName = "";
            for (int i = 1; i < nickNames.length; i ++){
                scondName += (" " + nickNames[i]);
            }

            textView.setText(Html.fromHtml(String.format("<font><big>%s</big></font><font color='#999999'><small>%s</small></font>", firstName, scondName)));
        }
    }
}

package com.kiun.modelcommonsupport.data;

import android.text.TextUtils;

/**
 * Created by kiun_2007 on 2018/8/11.
 * 用户信息模型.
 */

public class MCUserInfo extends BeanBase {

    /**
     用户登录标识.
     */
    public String tokenId;

    /**
     姓名.
     */
    public String name;

    /**
     头像.
     */
    public String headImg;

    /**
     用户id.
     */
    public String userId;

    /**
     sysUserId
     */
    public int sysUserId;

    /**
     openId.
     */
    public String openId;

    /**
     审核状态0 待审核 1 审核通过 2 审核不通过
     */
    public String aduitType;

    /**
     * 是否企业用户，0-是，1-不是.
     */
    public String isEnterPrise;

    /**
     * 师傅类型 0-自由师傅，1-平台的师傅，2-企业师傅.
     */
    public String masterType;

    /**
     用户环信密码.
     */
    public String hxPassword;

    /**
     环信账号.
     */
    public String hxAccount;

    /**
     * 用户手机号.
     */
    public String phone;

    public String getPhone(){
        return phone;
    }

    public String getMaskPhone(){
        if (TextUtils.isEmpty(phone)){
            return null;
        }
        return phone.replaceAll("(\\d{3})\\d{4}(\\d{4})", "$1****$2");
    }

    /**
     * 是否审核通过.
     * @return
     */
    public boolean isPassAduit(){
        if ("1".equals(aduitType)){
            return true;
        }
        return false;
    }

    public boolean isAduiting(){
        if ("0".equals(aduitType)){
            return true;
        }
        return false;
    }
}
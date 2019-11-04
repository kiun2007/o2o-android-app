package com.xzxx.decorate.o2o.bean;

import com.amos.modulebase.bean.BaseBean;
import com.amos.modulebase.utils.JsonUtil;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 首页活动item适配器
 * <p>
 * <br> Author: 叶青
 * <br> Version: 1.0.0
 * <br> Date: 2018/9/13
 * <br> Copyright: Copyright © 2018 suiji Technology. All rights reserved.
 */
public class HomeActivityBean extends BaseBean {

    /** */
    private String activityId = "";
    /** */
    private String imgUrl = "";
    /** */
    private String linkUrl = "";
    /** */
    private String orderNum = "";
    /** */
    private String status = "";
    /** */
    private String title = "";
    /** */
    private String type = "";

    @Override
    protected void init(JSONObject jSon) throws JSONException {
        activityId = JsonUtil.optString(jSon, "activityId", "");
        imgUrl = JsonUtil.optString(jSon, "imgUrl", "");
        linkUrl = JsonUtil.optString(jSon, "linkUrl", "");
        orderNum = JsonUtil.optString(jSon, "orderNum", "");
        status = JsonUtil.optString(jSon, "status", "");
        title = JsonUtil.optString(jSon, "title", "");
        type = JsonUtil.optString(jSon, "type", "");
    }

    public String getActivityId() {
        return activityId;
    }

    public void setActivityId(String activityId) {
        this.activityId = activityId;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getLinkUrl() {
        return linkUrl;
    }

    public void setLinkUrl(String linkUrl) {
        this.linkUrl = linkUrl;
    }

    public String getOrderNum() {
        return orderNum;
    }

    public void setOrderNum(String orderNum) {
        this.orderNum = orderNum;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}

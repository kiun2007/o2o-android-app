package com.xzxx.decorate.o2o.bean;

import com.amos.modulebase.bean.BaseBean;
import com.amos.modulebase.utils.JsonUtil;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 用户信息bean
 * <p>
 * <br> Author: 叶青
 * <br> Version: 1.0.0
 * <br> Date: 2016年12月11日
 * <br> Copyright: Copyright © 2016 xTeam Technology. All rights reserved.
 */
public class OrderProgressBean extends BaseBean {

    /** 进度id */
    private String progressId;
    /** 操作时间 */
    private String progressTime;
    /** 进度状态 */
    private String status;

    @Override
    protected void init(JSONObject jSon) throws JSONException {
        progressId = JsonUtil.optString(jSon, "progressId", "");
        progressTime = JsonUtil.optString(jSon, "progressTime", "");
        status = JsonUtil.optString(jSon, "status", "");
    }

    public String getProgressId() {
        return progressId;
    }

    public void setProgressId(String progressId) {
        this.progressId = progressId;
    }

    public String getProgressTime() {
        return progressTime;
    }

    public void setProgressTime(String progressTime) {
        this.progressTime = progressTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}

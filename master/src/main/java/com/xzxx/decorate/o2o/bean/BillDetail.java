package com.xzxx.decorate.o2o.bean;

import com.amos.modulebase.bean.BaseBean;
import com.amos.modulebase.utils.JsonUtil;

import org.json.JSONObject;

/**
 * Created by zf on 2018/7/22.
 */
public class BillDetail extends BaseBean {

    /** */
    private String logo = "";
    /** */
    private String desc = "";
    /** */
    private String tranAmt = "";
    /** */
    private String payType = "";
    /** */
    private String feeName = "";
    /** */
    private String tranType = "";
    /** */
    private String tranDate = "";
    /** */
    private String tranTime = "";
    /** */
    private String busiId = "";

    //"logo": "http:\/\/pawx04z5h.bkt.clouddn.com\/FjB2qQLnM82r2_MsgAif_7ivOISR",
    //"desc": "端木皮怎修电视机",
    //"tranAmt": 0.01,
    //"payType": "0",
    //"feeName": "提现",
    //"tranType": "0",
    //"tranDate": "20180918",
    //"tranTime": "20180918",
    //"busiId": "201809181418670027831395"

    @Override
    protected void init(JSONObject jSon) {
        logo = JsonUtil.optString(jSon, "logo", "");
        desc = JsonUtil.optString(jSon, "desc", "");
        tranAmt = JsonUtil.optString(jSon, "tranAmt", "");
        payType = JsonUtil.optString(jSon, "payType", "");
        feeName = JsonUtil.optString(jSon, "feeName", "");
        tranType = JsonUtil.optString(jSon, "tranType", "");
        tranDate = JsonUtil.optString(jSon, "tranDate", "");
        tranTime = JsonUtil.optString(jSon, "tranTime", "");
        busiId = JsonUtil.optString(jSon, "busiId", "");
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getTranAmt() {
        return tranAmt;
    }

    public void setTranAmt(String tranAmt) {
        this.tranAmt = tranAmt;
    }

    public String getPayType() {
        return payType;
    }

    public void setPayType(String payType) {
        this.payType = payType;
    }

    public String getFeeName() {
        return feeName;
    }

    public void setFeeName(String feeName) {
        this.feeName = feeName;
    }

    public String getTranType() {
        return tranType;
    }

    public void setTranType(String tranType) {
        this.tranType = tranType;
    }

    public String getTranDate() {
        return tranDate;
    }

    public void setTranDate(String tranDate) {
        this.tranDate = tranDate;
    }

    public String getTranTime() {
        return tranTime;
    }

    public void setTranTime(String tranTime) {
        this.tranTime = tranTime;
    }

    public String getBusiId() {
        return busiId;
    }

    public void setBusiId(String busiId) {
        this.busiId = busiId;
    }
}

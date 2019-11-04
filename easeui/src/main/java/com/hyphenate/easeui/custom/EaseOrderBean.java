package com.hyphenate.easeui.custom;

import org.json.JSONObject;

public class EaseOrderBean {

    //    appointmentAddress (string, optional): 预约详细地址 ,
    //    appointmentLocation (string, optional): 预约定位地址 ,
    //    appointmentTime (string, optional): 预约时间 ,
    private String appointmentTime = "";
    //    doorPayMoney (number, optional): 上门费支付金额 ,
    private String doorPayMoney = "";
    //    masterName (string, optional): 师傅姓名 ,
    private String masterName = "";
    //    masterProfession (string, optional): 师傅职业 ,
    private String masterProfession = "";
    //    orderId (string, optional): 订单编号 ,
    //    orderNum (integer, optional): 师傅订单数 ,
    //    orderStatus (string, optional): 订单状态 ,
    private String orderStatus = "";
    //    orderType (string, optional): 订单类型0 正常订单 1 售后订单 ,
    private String orderType = "";
    //    repairMoney (number, optional): 维修费 ,
    private String repairMoney = "";
    //    repairPayMoney (number, optional): 维修费支付金额 ,
    private String repairPayMoney = "";
    //    salesAfterId (string, optional): 售后申请编号 ,
    //    salesAfterOrderId (string, optional): 售后订单编号 ,
    //    salesStatus (string, optional): 售后订单状态 ,
    private String salesStatus = "";
    //    score (number, optional): 师傅评分 ,
    //    secondItemName (string, optional): 维修类目名称 ,
    private String secondItemName = "";
    //    totalPayMoney (number, optional):   订单支付总金额


    public void init(JSONObject jsonObject) {
        masterName = jsonObject.optString("masterName", "");
        masterProfession = jsonObject.optString("masterProfession", "");
        orderStatus = jsonObject.optString("orderStatus", "");
        salesStatus = jsonObject.optString("salesStatus", "");
        secondItemName = jsonObject.optString("secondItemName", "");
        appointmentTime = jsonObject.optString("appointmentTime", "");
        doorPayMoney = jsonObject.optString("doorPayMoney", "");
        repairPayMoney = jsonObject.optString("repairPayMoney", "");
        repairMoney = jsonObject.optString("repairMoney", "");
        orderType = jsonObject.optString("orderType", "");
    }

    public String getAppointmentTime() {
        return appointmentTime;
    }

    public void setAppointmentTime(String appointmentTime) {
        this.appointmentTime = appointmentTime;
    }

    public String getDoorPayMoney() {
        return doorPayMoney;
    }

    public void setDoorPayMoney(String doorPayMoney) {
        this.doorPayMoney = doorPayMoney;
    }

    public String getRepairPayMoney() {
        return repairPayMoney;
    }

    public void setRepairPayMoney(String repairPayMoney) {
        this.repairPayMoney = repairPayMoney;
    }

    public String getMasterName() {
        return masterName;
    }

    public void setMasterName(String masterName) {
        this.masterName = masterName;
    }

    public String getMasterProfession() {
        return masterProfession;
    }

    public void setMasterProfession(String masterProfession) {
        this.masterProfession = masterProfession;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public String getSalesStatus() {
        return salesStatus;
    }

    public void setSalesStatus(String salesStatus) {
        this.salesStatus = salesStatus;
    }

    public String getSecondItemName() {
        return secondItemName;
    }

    public void setSecondItemName(String secondItemName) {
        this.secondItemName = secondItemName;
    }

    public String getRepairMoney() {
        return repairMoney;
    }

    public void setRepairMoney(String repairMoney) {
        this.repairMoney = repairMoney;
    }

    public String getOrderType() {
        return orderType;
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }
}

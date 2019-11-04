package com.kiun.modelcommonsupport.data;

import android.view.View;

import com.kiun.modelcommonsupport.network.MCBaseRequest;

public class ChatOrderInfoModel extends BeanBase {

    private String orderId;
    private String masterName;
    private String masterProfession;
    private int orderNum;
    private float score;
    private String orderStatus;
    private String appointmentTime;
    private String appointmentLocation;
    private String appointmentAddress;
    private String secondItemName;
    private float doorPayMoney;
    private float repairMoney;
    private String repairPayMoney;
    private float totalPayMoney;
    private String orderType;
    private String salesAfterId;
    private String salesAfterOrderId;
    private String salesStatus;
    private String appointmentName;

    public String getAppointmentName() {
        return appointmentName;
    }

    @Override
    public void viewChange(View view) {
    }

    /**
     * 显示金钱格式化.
     * @return
     */
    public String getMoneyFormat(){

        if(orderType.endsWith("0")){
            int orderStatusInt = Integer.parseInt(orderStatus);
            if(orderStatusInt < 4){
                return String.format("%.2f元", doorPayMoney);
            }
            if (orderStatusInt == 5 || orderStatusInt == 4) { //开始服务，输入维修费————显示待支付的费用
                return String.format("%.2f元", repairMoney);
            }
        }
        //支付维修费订单完成————显示支付总金额（上门费+维修费）
        return String.format("%.2f元", totalPayMoney);
    }

    /**
     * 用户类型.
     * @return 师傅端 1 , 个人端 0.
     */
    public int getUserType(){
        return MCBaseRequest.isMaster() ? 1 : 0;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
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

    public int getOrderNum() {
        return orderNum;
    }

    public void setOrderNum(int orderNum) {
        this.orderNum = orderNum;
    }

    public float getScore() {
        return score;
    }

    public void setScore(float score) {
        this.score = score;
    }

    public String getOrderStatus() {
        if(orderType.endsWith("1")){
            return String.format("%d", Integer.parseInt(salesStatus) + 2);
        }
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public String getAppointmentTime() {
        return appointmentTime;
    }

    public void setAppointmentTime(String appointmentTime) {
        this.appointmentTime = appointmentTime;
    }

    public String getAppointmentLocation() {
        return appointmentLocation;
    }

    public void setAppointmentLocation(String appointmentLocation) {
        this.appointmentLocation = appointmentLocation;
    }

    public String getAppointmentAddress() {
        return appointmentAddress;
    }

    public void setAppointmentAddress(String appointmentAddress) {
        this.appointmentAddress = appointmentAddress;
    }

    public String getSecondItemName() {
        return secondItemName;
    }

    public void setSecondItemName(String secondItemName) {
        this.secondItemName = secondItemName;
    }

    public double getDoorPayMoney() {
        return doorPayMoney;
    }

    public void setDoorPayMoney(float doorPayMoney) {
        this.doorPayMoney = doorPayMoney;
    }

    public float getRepairMoney() {
        return repairMoney;
    }

    public void setRepairMoney(float repairMoney) {
        this.repairMoney = repairMoney;
    }

    public String getRepairPayMoney() {
        return repairPayMoney;
    }

    public void setRepairPayMoney(String repairPayMoney) {
        this.repairPayMoney = repairPayMoney;
    }

    public float getTotalPayMoney() {
        return totalPayMoney;
    }

    public void setTotalPayMoney(float totalPayMoney) {
        this.totalPayMoney = totalPayMoney;
    }

    public String getOrderType() {
        return orderType;
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }

    public String getSalesAfterId() {
        return salesAfterId;
    }

    public void setSalesAfterId(String salesAfterId) {
        this.salesAfterId = salesAfterId;
    }

    public String getSalesAfterOrderId() {
        return salesAfterOrderId;
    }

    public void setSalesAfterOrderId(String salesAfterOrderId) {
        this.salesAfterOrderId = salesAfterOrderId;
    }

    public String getSalesStatus() {
        return salesStatus;
    }

    public void setSalesStatus(String salesStatus) {
        this.salesStatus = salesStatus;
    }
}

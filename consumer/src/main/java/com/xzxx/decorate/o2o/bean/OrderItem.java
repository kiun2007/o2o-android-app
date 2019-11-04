package com.xzxx.decorate.o2o.bean;

import com.kiun.modelcommonsupport.data.BeanBase;

import java.io.Serializable;

public class OrderItem extends BeanBase implements Serializable {

    private String orderId;
    private String masterId;
    private String masterName;
    private String masterHxAccount;

    private String masterHeadImg;
    private String masterOrderNum;
    private String masterScore;
    private String masterProfession;
    private String orderStatus;

    private String secondItemId;
    private String secondItemName;
    private String secondItemIcon;
    private String appointmentTime;

    private String appointmentLatitudeLongitude;
    private String appointmentLocation;
    private String createTime;

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getMasterId() {
        return masterId;
    }

    public void setMasterId(String masterId) {
        this.masterId = masterId;
    }

    public String getMasterName() {
        return masterName;
    }

    public void setMasterName(String masterName) {
        this.masterName = masterName;
    }

    public String getMasterHxAccount() {
        return masterHxAccount;
    }

    public void setMasterHxAccount(String masterHxAccount) {
        this.masterHxAccount = masterHxAccount;
    }

    public String getMasterHeadImg() {
        return masterHeadImg;
    }

    public void setMasterHeadImg(String masterHeadImg) {
        this.masterHeadImg = masterHeadImg;
    }

    public String getMasterOrderNum() {
        return masterOrderNum;
    }

    public void setMasterOrderNum(String masterOrderNum) {
        this.masterOrderNum = masterOrderNum;
    }

    public String getMasterScore() {
        return masterScore;
    }

    public void setMasterScore(String masterScore) {
        this.masterScore = masterScore;
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

    public String getSecondItemId() {
        return secondItemId;
    }

    public void setSecondItemId(String secondItemId) {
        this.secondItemId = secondItemId;
    }

    public String getSecondItemName() {
        return secondItemName;
    }

    public void setSecondItemName(String secondItemName) {
        this.secondItemName = secondItemName;
    }

    public String getSecondItemIcon() {
        return secondItemIcon;
    }

    public void setSecondItemIcon(String secondItemIcon) {
        this.secondItemIcon = secondItemIcon;
    }

    public String getAppointmentTime() {
        return appointmentTime;
    }

    public void setAppointmentTime(String appointmentTime) {
        this.appointmentTime = appointmentTime;
    }

    public String getAppointmentLatitudeLongitude() {
        return appointmentLatitudeLongitude;
    }

    public void setAppointmentLatitudeLongitude(String appointmentLatitudeLongitude) {
        this.appointmentLatitudeLongitude = appointmentLatitudeLongitude;
    }

    public String getAppointmentLocation() {
        return appointmentLocation;
    }

    public void setAppointmentLocation(String appointmentLocation) {
        this.appointmentLocation = appointmentLocation;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    @Override
    public String toString() {
        return "OrderItem{" +
                "orderId='" + orderId + '\'' +
                ", masterId='" + masterId + '\'' +
                ", masterName='" + masterName + '\'' +
                ", masterHxAccount='" + masterHxAccount + '\'' +
                ", masterHeadImg='" + masterHeadImg + '\'' +
                ", masterOrderNum='" + masterOrderNum + '\'' +
                ", masterScore='" + masterScore + '\'' +
                ", masterProfession='" + masterProfession + '\'' +
                ", orderStatus='" + orderStatus + '\'' +
                ", secondItemId='" + secondItemId + '\'' +
                ", secondItemName='" + secondItemName + '\'' +
                ", secondItemIcon='" + secondItemIcon + '\'' +
                ", appointmentTime='" + appointmentTime + '\'' +
                ", appointmentLatitudeLongitude='" + appointmentLatitudeLongitude + '\'' +
                ", appointmentLocation='" + appointmentLocation + '\'' +
                ", createTime='" + createTime + '\'' +
                '}';
    }
}

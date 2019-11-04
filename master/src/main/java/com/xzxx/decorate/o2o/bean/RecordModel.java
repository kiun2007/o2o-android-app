package com.xzxx.decorate.o2o.bean;

import com.kiun.modelcommonsupport.data.BeanBase;

public class RecordModel extends BeanBase {


    /**
     * withdrawId : 201809282131670049469530
     * withdrawMoney : 0.01
     * bankIcon : http://pawx04z5h.bkt.clouddn.com/2018/08/6574b695-caae-450a-9a73-334dd4557746.png
     * withdrawBankName : 浦东发展银行
     * bankNo : 6217921102200956
     * applyTime : 20180928 213129
     * auditTime :
     * status : 0
     * failReason :
     */
    private String withdrawId;
    private String withdrawMoney;
    private String bankIcon;
    private String withdrawBankName;
    private String bankNo;
    private String applyTime;
    private String auditTime;
    private String status;
    private String failReason;

    public String getStatusFormat() {
        if (status.endsWith("2")) {
            return "提现失败";
        } else if (status.endsWith("1")) {
            return "提现成功";
        } else if (status.endsWith("0")) {
            return "平台处理中";
        }
        return "";
    }

    public String getWithdrawId() {
        return withdrawId;
    }

    public void setWithdrawId(String withdrawId) {
        this.withdrawId = withdrawId;
    }

    public String getWithdrawMoney() {
        return withdrawMoney;
    }

    public void setWithdrawMoney(String withdrawMoney) {
        this.withdrawMoney = withdrawMoney;
    }

    public String getBankIcon() {
        return bankIcon;
    }

    public void setBankIcon(String bankIcon) {
        this.bankIcon = bankIcon;
    }

    public String getWithdrawBankName() {
        return withdrawBankName;
    }

    public void setWithdrawBankName(String withdrawBankName) {
        this.withdrawBankName = withdrawBankName;
    }

    public String getBankNo() {
        return bankNo;
    }

    public void setBankNo(String bankNo) {
        this.bankNo = bankNo;
    }

    public String getApplyTime() {
        return applyTime;
    }

    public void setApplyTime(String applyTime) {
        this.applyTime = applyTime;
    }

    public String getAuditTime() {
        return auditTime;
    }

    public void setAuditTime(String auditTime) {
        this.auditTime = auditTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getFailReason() {
        return failReason;
    }

    public void setFailReason(String failReason) {
        this.failReason = failReason;
    }
}

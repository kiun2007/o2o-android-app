package com.xzxx.decorate.o2o.bean;

import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.amos.modulebase.utils.DateUtil;
import com.kiun.modelcommonsupport.data.BeanBase;
import com.kiun.modelcommonsupport.utils.MCString;

public class VoucherModel extends BeanBase {

    /**
     * voucherType : 0
     * voucherMoney : 100
     * maxFullSubtractionMoney :
     * minFullSubtractionMoney :
     * expireTime : 20180915000000
     * discount :
     * voucherId : 201809072152670015506392
     * secondItemId :
     * firstItemId :
     * voucherName : 优惠卷
     * voucherDesc :
     * feeType : 1
     * derateType : 1
     * isValid : 1
     * status : 0
     * cityCode :
     * useTime :
     */
    private String voucherType;
    private String voucherMoney;
    private String maxFullSubtractionMoney;
    private String minFullSubtractionMoney;
    private String expireTime;
    private String discount;
    private String voucherId;
    private String secondItemId;
    private String firstItemId;
    private String voucherName;
    private String voucherDesc;
    private String feeType;
    private String derateType;
    private String isValid;
    private String status;
    private String cityCode;
    private String useTime;

    @Override
    public void viewChange(View view) {
        TextView piceTV = view.findViewById(com.kiun.modelcommonsupport.R.id.txt_content);
        TextView uintTextView = view.findViewById(com.kiun.modelcommonsupport.R.id.uintTextView);
        TextView voucherTypeTextView = view.findViewById(com.kiun.modelcommonsupport.R.id.voucherTypeTextView);

        int color = isValidValue() ? 0xFFFF7d00 : 0xFF666666;
        piceTV.setTextColor(color);
        uintTextView.setTextColor(color);
        voucherTypeTextView.setTextColor(color);
    }

    /**
     * 优惠金额显示格式化.
     * @return 格式化之后的字符.
     */
    public String getVoucherValueFormat(){
        if (voucherType.endsWith("0")){
           return MCString.decimalFormat(Float.parseFloat(voucherMoney), 2, "元");
        }else{
            return MCString.decimalFormat(Float.parseFloat(discount)/10, 1, "折");
        }
    }

    public String getDateFormat(){

        if(!TextUtils.isEmpty(useTime)){
            return String.format("<font color='#999999'>使用时间%s</font>", MCString.dateConvert(useTime, "yyyyMMddHHmmss", "yyyy-MM-dd"));
        }
        if (!TextUtils.isEmpty(expireTime)) {
            try {
                long i = DateUtil.compareTime(expireTime, DateUtil.getDate("yyyyMMddHHmmss"), "yyyyMMddHHmmss");
                if (i < 0) {
                    float lest = Math.abs((i / (24f * 60 * 60 * 1000)));
                    if (lest < 5) {
                        if (lest < 1){
                            return "<font color='#6174FF'>今天到期</font>";
                        }
                        return "<font color='#6174FF'>还有" + (int) (lest ) + "天到期</font>";
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return String.format("<font color='#999999'>有效期至%s</font>", MCString.dateConvert(expireTime, "yyyyMMddHHmmss", "yyyy-MM-dd"));
        }
        return null;
    }

    private boolean isValidValue(){
        return isValid.endsWith("0") && status.endsWith("0");
    }

    public String getSuffix(){
        if (isValid.endsWith("1")){
            return "已使用";
        }

        if(status.endsWith("1")){
            return "已过期";
        }
        return "";
    }

    public String getVoucherType() {
        return voucherType;
    }

    public void setVoucherType(String voucherType) {
        this.voucherType = voucherType;
    }

    public String getVoucherMoney() {
        return voucherMoney;
    }

    public void setVoucherMoney(String voucherMoney) {
        this.voucherMoney = voucherMoney;
    }

    public String getMaxFullSubtractionMoney() {
        return maxFullSubtractionMoney;
    }

    public void setMaxFullSubtractionMoney(String maxFullSubtractionMoney) {
        this.maxFullSubtractionMoney = maxFullSubtractionMoney;
    }

    public String getMinFullSubtractionMoney() {
        return minFullSubtractionMoney;
    }

    public void setMinFullSubtractionMoney(String minFullSubtractionMoney) {
        this.minFullSubtractionMoney = minFullSubtractionMoney;
    }

    public String getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(String expireTime) {
        this.expireTime = expireTime;
    }

    public String getDiscount() {
        return discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }

    public String getVoucherId() {
        return voucherId;
    }

    public void setVoucherId(String voucherId) {
        this.voucherId = voucherId;
    }

    public String getSecondItemId() {
        return secondItemId;
    }

    public void setSecondItemId(String secondItemId) {
        this.secondItemId = secondItemId;
    }

    public String getFirstItemId() {
        return firstItemId;
    }

    public void setFirstItemId(String firstItemId) {
        this.firstItemId = firstItemId;
    }

    public String getVoucherName() {
        return voucherName;
    }

    public void setVoucherName(String voucherName) {
        this.voucherName = voucherName;
    }

    public String getVoucherDesc() {
        return voucherDesc;
    }

    public void setVoucherDesc(String voucherDesc) {
        this.voucherDesc = voucherDesc;
    }

    public String getFeeType() {
        return feeType;
    }

    public void setFeeType(String feeType) {
        this.feeType = feeType;
    }

    public String getDerateType() {
        return derateType;
    }

    public void setDerateType(String derateType) {
        this.derateType = derateType;
    }

    public String getIsValid() {
        return isValid;
    }

    public void setIsValid(String isValid) {
        this.isValid = isValid;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCityCode() {
        return cityCode;
    }

    public void setCityCode(String cityCode) {
        this.cityCode = cityCode;
    }

    public String getUseTime() {
        return useTime;
    }

    public void setUseTime(String useTime) {
        this.useTime = useTime;
    }
}

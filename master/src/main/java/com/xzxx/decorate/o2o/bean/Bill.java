package com.xzxx.decorate.o2o.bean;

/**
 * Created by zf on 2018/7/22.
 */
public class Bill {

    private int type; //0：支出 1:收入
    private String billphoto;
    private String billname;
    private String billtext;
    private String billdate;
    private String billvalue;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getBillname() {
        return billname;
    }

    public void setBillname(String billname) {
        this.billname = billname;
    }

    public String getBilltext() {
        return billtext;
    }

    public void setBilltext(String billtext) {
        this.billtext = billtext;
    }

    public String getBilldate() {
        return billdate;
    }

    public void setBilldate(String billdate) {
        this.billdate = billdate;
    }

    public String getBillvalue() {
        return billvalue;
    }

    public void setBillvalue(String billvalue) {
        this.billvalue = billvalue;
    }

    public String getBillphoto() {
        return billphoto;
    }

    public void setBillphoto(String billphoto) {
        this.billphoto = billphoto;
    }
}

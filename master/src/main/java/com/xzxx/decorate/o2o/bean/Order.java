package com.xzxx.decorate.o2o.bean;

/**
 * Created by zf on 2018/7/16.
 */
public class Order {

    private String master_name;
    private String master_photo;
    private int master_order_count;
    private float master_start;
    private String master_title;

    private int order_state; //0 待服务 1 服务中 2 待收款 3:已完成 未评价  4：已完成 已评价 5:售后服务已申请
                             // 6：售后服务处理中 7：售后服务退款中 8：售后服务已完成
    private String order_name;
    private String order_date;
    private String order_address;
    private float order_price;

    public String getMaster_name() {
        return master_name;
    }

    public void setMaster_name(String master_name) {
        this.master_name = master_name;
    }

    public String getMaster_photo() {
        return master_photo;
    }

    public void setMaster_photo(String master_photo) {
        this.master_photo = master_photo;
    }

    public int getMaster_order_count() {
        return master_order_count;
    }

    public void setMaster_order_count(int master_order_count) {
        this.master_order_count = master_order_count;
    }

    public float getMaster_start() {
        return master_start;
    }

    public void setMaster_start(float master_start) {
        this.master_start = master_start;
    }

    public String getMaster_title() {
        return master_title;
    }

    public void setMaster_title(String master_title) {
        this.master_title = master_title;
    }

    public int getOrder_state() {
        return order_state;
    }

    public void setOrder_state(int order_state) {
        this.order_state = order_state;
    }

    public String getOrder_name() {
        return order_name;
    }

    public void setOrder_name(String order_name) {
        this.order_name = order_name;
    }

    public String getOrder_date() {
        return order_date;
    }

    public void setOrder_date(String order_date) {
        this.order_date = order_date;
    }

    public String getOrder_address() {
        return order_address;
    }

    public void setOrder_address(String order_address) {
        this.order_address = order_address;
    }

    public float getOrder_price() {
        return order_price;
    }

    public void setOrder_price(float order_price) {
        this.order_price = order_price;
    }
}

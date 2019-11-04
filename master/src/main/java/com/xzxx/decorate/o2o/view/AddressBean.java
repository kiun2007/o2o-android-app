package com.xzxx.decorate.o2o.view;

import java.io.Serializable;
import java.util.List;

public class AddressBean implements Serializable {

    private List<AddressItemBean> province;
    private List<AddressItemBean> city;
    private List<AddressItemBean> district;

    public List<AddressItemBean> getProvince() {
        return province;
    }

    public void setProvince(List<AddressItemBean> province) {
        this.province = province;
    }

    public List<AddressItemBean> getCity() {
        return city;
    }

    public void setCity(List<AddressItemBean> city) {
        this.city = city;
    }

    public List<AddressItemBean> getDistrict() {
        return district;
    }

    public void setDistrict(List<AddressItemBean> district) {
        this.district = district;
    }

    public static class AddressItemBean implements Serializable {
        private String i;
        private String n;
        private String p;

        public String getI() {
            return i;
        }

        public void setI(String i) {
            this.i = i;
        }

        public String getN() {
            return n;
        }

        public void setN(String n) {
            this.n = n;
        }

        public String getP() {
            return p;
        }

        public void setP(String p) {
            this.p = p;
        }
    }
}

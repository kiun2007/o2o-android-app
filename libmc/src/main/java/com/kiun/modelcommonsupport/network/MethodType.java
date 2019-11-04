package com.kiun.modelcommonsupport.network;

/**
 * Created by kiun_2007 on 2018/4/18.
 */

public enum MethodType {

    POST(0),
    GET(1),
    PUT(2),
    DELETE(3);

    private int mType = 0;
    MethodType(int value) {
        mType = value;
    }

    public int getMethodType() {
        return mType;
    }

    public static MethodType valueOf(int type){
        switch (type){
            case 0:
                return POST;
            case 1:
                return GET;
            case 2:
                return PUT;
            case 3:
                return DELETE;
        }
        return POST;
    }
}

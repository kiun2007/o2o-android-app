package com.kiun.modelcommonsupport;

/**
 * Created by kiun_2007 on 2018/8/11.
 */

public class ServiceError extends Error {

    private int errorCode = -1;

    public ServiceError(String message, int erroCode){
        super(message);
        errorCode = erroCode;
    }

    public int getErrorCode() {
        return errorCode;
    }
}

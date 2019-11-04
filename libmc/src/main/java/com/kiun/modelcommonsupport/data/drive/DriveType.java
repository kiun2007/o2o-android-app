package com.kiun.modelcommonsupport.data.drive;

/**
 * Created by kiun_2007 on 2018/4/14.
 */

public enum DriveType {

    NetOnly(0),
    LocalOnly(1),
    LocalAndNet(2),
    UserDrive(3);

    private int dType = 0;
    DriveType(int value) {
        dType = value;
    }

    public int getDriveType() {
        return dType;
    }

    public static DriveType valueOf(int type){
         switch (type){
             case 0:
                 return NetOnly;
             case 1:
                 return LocalOnly;
             case 2:
                 return LocalAndNet;
             case 3:
                 return UserDrive;
         }
         return NetOnly;
    }
}

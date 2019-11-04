package com.kiun.modelcommonsupport.safe;

import android.os.Build;
import android.support.annotation.RequiresApi;

/**
 * Created by kiun_2007 on 2018/8/25.
 */

public interface OrderPayer {

    void setPayEvent(PayEventer payEvent);

    void payStart(String tradeNo , String total);
}

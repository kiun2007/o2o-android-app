package com.hyphenate.easeui.custom;

import android.text.TextUtils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;


/**
 * 时间处理工具：用于时间相关操作
 * <p>
 * <br> Author: 叶青
 * <br> Version: 1.0.0
 * <br> Date: 2016年12月11日
 * <br> Copyright: Copyright © 2016 xTeam Technology. All rights reserved.
 */
public class DateUtil {

    /**
     * 时间日期格式化到年月日时分秒. HH：24小时制(0-23)  hh：1~12小时制(1-12)
     */
    public static final String YMD_HMS = "yyyy-MM-dd HH:mm:ss";
    /**
     * 时间日期格式化到年月日.
     */
    public static final String YMD = "yyyy-MM-dd";
    /**
     * 时间日期格式化到年月.
     */
    public static final String YM = "yyyy-MM";
    /**
     * 时间日期格式化到年月日时分.
     */
    public static final String YMD_HM = "yyyy-MM-dd HH:mm";
    /**
     * 时间日期格式化到月日.
     */
    public static final String MD = "MM-dd";
    /**
     * 时分秒.
     */
    public static final String HMS = "HH:mm:ss";
    /**
     * 时分.
     */
    public static final String HM = "HH:mm";
    /**
     * 上午.
     */
    public static final String DATE_FORMAT_AM = "AM";
    /**
     * 下午.
     */
    public static final String DATE_FORMAT_PM = "PM";

    // ************************************* 获取当前时间

    /**
     * 获取系统当前时间-yyyy-MM-dd HH:mm:ss
     * <p>
     * <br> Version: 1.0.0
     * <br> CreateTime: 2016-11-24,下午2:52:36
     * <br> UpdateTime: 2016-11-24,下午2:52:36
     * <br> CreateAuthor: 叶青
     * <br> UpdateAuthor: 叶青
     * <br> UpdateInfo: (此处输入修改内容, 若无修改可不写.)
     *
     * @return 时间字符串
     */
    public static String getDate() {
        Date currentTime = new Date();
        return getSimpleDateFormat(YMD_HMS).format(currentTime);
    }

    /**
     * 获取系统当前时间
     * <br> Version: 1.0.0
     * <br> CreateTime: 2016-11-24,下午2:52:28
     * <br> UpdateTime: 2016-11-24,下午2:52:28
     * <br> CreateAuthor: 叶青
     * <br> UpdateAuthor: 叶青
     * <br> UpdateInfo: (此处输入修改内容, 若无修改可不写.)
     *
     * @param str
     *         yyyy-MM-dd HH:mm:ss
     *
     * @return 时间字符串
     */
    public static String getDate(String str) {
        Date currentTime = new Date();
        return getSimpleDateFormat(str).format(currentTime);
    }

    /**
     * 获取当前时间戳
     * <p>
     * <br> Version: 1.0.0
     * <br> CreateTime: 2016-11-24,下午2:52:06
     * <br> UpdateTime: 2016-11-24,下午2:52:06
     * <br> CreateAuthor: 叶青
     * <br> UpdateAuthor: 叶青
     * <br> UpdateInfo: (此处输入修改内容, 若无修改可不写.)
     */
    public static long getDateMillis() {
        return System.currentTimeMillis();
    }

    // ************************************* 时间戳和时间格式转换

    /**
     * 格式化时间(时间戳)
     * <br> Version: 1.0.0
     * <br> CreateTime: 2016-11-15,下午11:23:18
     * <br> UpdateTime: 2016-11-15,下午11:23:18
     * <br> CreateAuthor: 叶青
     * <br> UpdateAuthor: 叶青
     * <br> UpdateInfo: (此处输入修改内容, 若无修改可不写.)
     *
     * @param timeMillis
     *         时间戳
     * @param pattern
     *         时间正则
     *
     * @return 返回格式后的时间
     */
    public static String millis2Time(long timeMillis, String pattern) {

        SimpleDateFormat sdf = getSimpleDateFormat(pattern);
        // 1451382700000/1000
        if (String.valueOf(timeMillis).length() > 10) {
            return sdf.format(new Date(timeMillis));
        } else {
            return sdf.format(new Date(timeMillis * 1000L));
        }
    }

    /**
     * 获取指定时间格式的时间戳
     * <br> Version: 1.0.0
     * <br> CreateTime: 2016年1月3日, 下午1:55:49
     * <br> UpdateTime: 2016年1月3日, 下午1:55:49
     * <br> CreateAuthor: 叶青
     * <br> UpdateAuthor: 叶青
     * <br> UpdateInfo: (此处输入修改内容, 若无修改可不写.)
     *
     * @param time
     *         时间
     * @param pattern
     *         格式
     *
     * @return 指定时间格式的时间戳
     */
    public static long time2Millis(String time, String pattern) {
        try {
            Date date = getSimpleDateFormat(pattern).parse(time);
            // 1451382700000
            return date.getTime();
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    // ************************************* 2016年1月5日 10:44:55 时间格式化

    /**
     * 更改时间格式
     * <br> Version: 1.0.0
     * <br> CreateTime: 2016年1月3日, 下午2:08:33
     * <br> UpdateTime: 2016年1月3日, 下午2:08:33
     * <br> CreateAuthor: 叶青
     * <br> UpdateAuthor: 叶青
     * <br> UpdateInfo: (此处输入修改内容, 若无修改可不写.)
     *
     * @param time
     *         原时间
     * @param fromPattern
     *         原始格式 yyyy-MM-dd hh:mm:ss
     * @param toPattern
     *         目标格式 yyyy-MM-dd hh:mm:ss
     *
     * @return 格式化之后的时间
     */
    public static String formatTime(String time, String fromPattern, String toPattern) {

        if (TextUtils.isEmpty(fromPattern)) {
            fromPattern = YMD_HMS;
        }
        if (TextUtils.isEmpty(toPattern)) {
            toPattern = YMD_HMS;
        }

        try {
            Date date = getSimpleDateFormat(fromPattern).parse(time);
            return getSimpleDateFormat(toPattern).format(date);
        } catch (Exception e) {
            e.printStackTrace();
            return time;
        }
    }

    // ************************************* 2016年1月5日 10:44:55 时间比较

    /**
     * 比较时间  (两个时间相差毫秒数)
     * <br> Version: 1.0.0
     * <br> CreateTime: 2016-11-22,下午5:23:41
     * <br> UpdateTime: 2016-11-22,下午5:23:41
     * <br> CreateAuthor: 叶青
     * <br> UpdateAuthor: 叶青
     * <br> UpdateInfo: (此处输入修改内容, 若无修改可不写.)
     *
     * @param time1
     *         时间1（格式 yyyy-MM-dd HH:mm:ss）
     * @param time2
     *         时间2（格式 yyyy-MM-dd HH:mm:ss）
     *
     * @return 0=(time1相等time2) <0=(time1小于time2) >0=(time1大于time2) (两个时间相差毫秒数)
     */
    public static long compareTime(String time1, String time2, String format) throws Exception {

        if (TextUtils.isEmpty(format)) {
            format = YMD_HMS;
        }

        Date date1 = getSimpleDateFormat(format).parse(time1);
        Date date2 = getSimpleDateFormat(format).parse(time2);
        long timeMillis1 = date1.getTime();
        long timeMillis2 = date2.getTime();

        return (timeMillis2 - timeMillis1);
    }

    // ************************************* 时间工具

    /**
     * 计算时间差：获取两个日期之间的间隔天数
     * <p>
     * <br> Version: 1.0.0
     * <br> CreateTime: 2016-11-22,下午5:23:41
     * <br> UpdateTime: 2016-11-22,下午5:23:41
     * <br> CreateAuthor: 叶青
     * <br> UpdateAuthor: 叶青
     * <br> UpdateInfo: (此处输入修改内容, 若无修改可不写.)
     *
     * @param time1
     *         时间1（格式 yyyy-MM-dd HH:mm:ss）
     * @param time2
     *         时间2（格式 yyyy-MM-dd HH:mm:ss）
     *
     * @return * @return 间隔天数
     */
    public static float getTimeInterval(String time1, String time2, String format) throws Exception {
        return compareTime(time1, time2, format) / (1000 * 60 * 60 * 24f);
    }

    /**
     * 获取某个月份的天数
     * <br> Version: 1.0.0
     * <br> CreateTime: 2016-10-31,下午2:48:20
     * <br> UpdateTime: 2016-10-31,下午2:48:20
     * <br> CreateAuthor: 叶青
     * <br> UpdateAuthor: 叶青
     * <br> UpdateInfo: (此处输入修改内容, 若无修改可不写.)
     *
     * @param strDate
     *         yyyy-MM
     */
    public static int getDaysForMonth(String strDate) {
        int days = 0;
        try {
            Calendar calendar = new GregorianCalendar();
            Date date1 = getSimpleDateFormat(YM).parse(strDate);
            calendar.setTime(date1); // 放入你的日期
            days = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
            System.out.println("天数为=" + days);
            //         方法2
            System.out.println("天数为=" + date1.getDate());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return days;
    }



    /**
     * 获取某个日期是周几
     * <p>
     * <br> Version: 1.0.0
     * <br> CreateTime: 2016/3/28 18:54
     * <br> UpdateTime: 2016/3/28 18:54
     * <br> CreateAuthor: 叶青
     * <br> UpdateAuthor: 叶青
     * <br> UpdateInfo: (此处输入修改内容,若无修改可不写.)
     *
     * @param date
     *         yyyy-MM-dd HH:mm:ss
     *
     * @return 1-7，-1则为格式化失败
     */
    public static int getNumForWeekInt(String date) {
        try {
            Date sourceDate = getSimpleDateFormat(YMD).parse(date);
            int weekPosition = sourceDate.getDay();
            return weekPosition + 1;
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    // ************************************* 获得指定日期的前/后几天

    /**
     * 获得指定日期的前一天
     * <p>
     * <br> Version: 1.0.0
     * <br> CreateTime: 2016/3/28 18:54
     * <br> UpdateTime: 2016/3/28 18:54
     * <br> CreateAuthor: 叶青
     * <br> UpdateAuthor: 叶青
     * <br> UpdateInfo: (此处输入修改内容,若无修改可不写.)
     *
     * @param specifiedDay
     *         指定日期
     * @param days
     *         天数
     *
     * @return 指定日期的前一天
     */
    public static String getDateBefore(String specifiedDay, int days) {

        try {
            Date date = getSimpleDateFormat(YMD).parse(specifiedDay);
            Calendar c = Calendar.getInstance();
            c.setTime(date);
            int day = c.get(Calendar.DATE);
            c.set(Calendar.DATE, day - days);
            return getSimpleDateFormat(YMD).format(c.getTime());
        } catch (Exception e) {
            e.printStackTrace();
            return specifiedDay;
        }
    }

    /**
     * 获得指定日期的后一天
     * <p>
     * <br> Version: 1.0.0
     * <br> CreateTime: 2016/3/28 18:54
     * <br> UpdateTime: 2016/3/28 18:54
     * <br> CreateAuthor: 叶青
     * <br> UpdateAuthor: 叶青
     * <br> UpdateInfo: (此处输入修改内容,若无修改可不写.)
     *
     * @param specifiedDay
     *         指定日期
     * @param days
     *         天数
     *
     * @return 指定日期的后一天
     */
    public static String getDateAfter(String specifiedDay, int days) {
        try {
            Calendar c = Calendar.getInstance();
            Date date = getSimpleDateFormat(YMD).parse(specifiedDay);
            c.setTime(date);
            int day = c.get(Calendar.DATE);
            c.set(Calendar.DATE, day + days);

            return getSimpleDateFormat(YMD).format(c.getTime());
        } catch (Exception e) {
            e.printStackTrace();
            return specifiedDay;
        }
    }


    //    private static SimpleDateFormat getSimpleDateFormat(String time) {
    //
    //    }

    /**
     * 获取指定格式的 SimpleDateFormat
     * <p>
     * <br> Version: 1.0.0
     * <br> CreateAuthor: 叶青
     * <br> CreateTime: 2018/4/15 0:20
     * <br> UpdateAuthor: 叶青
     * <br> UpdateTime: 2018/4/15 0:20
     * <br> UpdateInfo: (此处输入修改内容,若无修改可不写.)
     *
     * @param pattern
     *         时间格式化的样式
     *
     * @return SimpleDateFormat
     */
    public static SimpleDateFormat getSimpleDateFormat(String pattern) {
        return new SimpleDateFormat(pattern, Locale.getDefault());
    }
}
package com.wmz.campusplatform.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtil {
    public static boolean isThisTime(Date date, String pattern) {
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        String param = sdf.format(date);//参数时间
        String now = sdf.format(new Date());//当前时间
        if (param.equals(now)) {
            return true;
        }
        return false;
    }

    public static boolean isToday(Date date) {
        return isThisTime(date, "yyyy-MM-dd");
    }

    //判断选择的日期是否是本年
    public static boolean isThisYear(Date time) {
        return isThisTime(time, "yyyy");
    }

}

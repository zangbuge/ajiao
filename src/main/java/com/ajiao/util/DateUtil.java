package com.ajiao.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @Author Li Huiming
 * @Date 2019/8/22
 */


public class DateUtil {
    public static final String FORMAT_YMDHMS_STR = "yyyy-MM-dd HH:mm:ss";
    public static final String FORMAT_YMD_STR = "yyyy-MM-dd";
    //时区
    public static final String TIME_ZONE = "GMT+8";

    public static Date getDate(String dateStr, String formart) {
        DateFormat dateFormat = getDateFormat(formart);
        try {
            Date parse = dateFormat.parse(dateStr);
            return parse;
        } catch (Exception e) {
            throw new RuntimeException("字符串转Date异常", e);
        }
    }

    public static String getDateStr(Date date, String formart) {
        DateFormat dateFormat = getDateFormat(formart);
        return dateFormat.format(getDate(date));
    }

    public static String getFormatYmdhmsStr(Date date) {
        DateFormat dateFormat = getDateFormat(FORMAT_YMDHMS_STR);
        return dateFormat.format(getDate(date));
    }

    public static String getFormatYmdStr(Date date) {
        DateFormat dateFormat = getDateFormat(FORMAT_YMD_STR);
        return dateFormat.format(getDate(date));
    }

    public static Date getDate(Date date){
        if (null == date) {
            return new Date();
        }
        return date;
    }

    public static Date getCurDate(){
        return new Date();
    }

    static DateFormat getDateFormat(String formatStr){
        if (null == formatStr || "" == formatStr)
            formatStr = FORMAT_YMDHMS_STR;
        return new SimpleDateFormat(formatStr);
    }

}

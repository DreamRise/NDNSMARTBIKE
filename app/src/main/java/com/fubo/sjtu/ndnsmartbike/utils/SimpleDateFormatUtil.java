package com.fubo.sjtu.ndnsmartbike.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by sjtu on 2015/11/17.
 */
public class SimpleDateFormatUtil {

    public static final String NORMAL_UTIL_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public static final String UTIL_DATE_WITHOUT_SECOND="yyyy-MM-dd HH:mm";

    public static Date getUtilDateFromString(String date){
        Date date1=null;
        try {
            date1 = new SimpleDateFormat(NORMAL_UTIL_DATE_FORMAT).parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date1;
    }
    public static String formatUtilDate(Date date,String pattern) {
        return new SimpleDateFormat(pattern).format(date);
    }
}

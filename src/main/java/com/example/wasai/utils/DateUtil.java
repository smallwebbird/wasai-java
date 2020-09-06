package com.example.wasai.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtil {
    public static final SimpleDateFormat TIMEFORMAT = new SimpleDateFormat("YYYY-MM-DD HH:mm:ss");

    public static String getTime() {
        return TIMEFORMAT.format(new Date());
    }

    public static String getJWTValidTime(Long i) {
        Date date = new Date(System.currentTimeMillis() + i * 1000);
        return TIMEFORMAT.format(date);
    }

    public static String getJWTExpiration(Long i) {
        Date date = new Date(System.currentTimeMillis() + i * 1000);
        return TIMEFORMAT.format(date);
    }


    public static Date formatTime(String date) {
        try {
            return TIMEFORMAT.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static boolean equalTime(String date1, String date2) {
        if (formatTime(date1) == null || formatTime(date2) == null) {
            return false;
        }
        return date1.compareTo(date2) > 0;
    }
}

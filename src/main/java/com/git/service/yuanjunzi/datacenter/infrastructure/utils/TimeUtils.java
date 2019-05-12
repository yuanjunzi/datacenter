package com.git.service.yuanjunzi.datacenter.infrastructure.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.time.DateFormatUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by wangshenxiang on 16/9/28.
 */

@Slf4j
public class TimeUtils {

    public static final String DEFAULT_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

    public static long currentTimeSec() {
        return currentTimeMillis() / 1000;
    }

    public static int currentUnixTimeSec() {
        return Math.toIntExact(currentTimeSec());
    }

    public static long currentTimeMillis() {
        return System.currentTimeMillis();
    }

    public static String sec2Format(long sec) {
        return mills2Format("yyyy-MM-dd HH:mm", sec * 1000);
    }

    public static String mills2Format(String format, long mills) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(new Date(mills));
    }

    private static long dateToLong(Date date) {
        return (date.getTime() / 1000);
    }

    private static Date stringToDate(String strTime, String formatType)
            throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat(formatType);
        Date date;
        date = formatter.parse(strTime);
        return date;
    }

    public static long stringToLong(String strTime, String formatType) {
        Date date;
        try {
            date = stringToDate(strTime, formatType);
        } catch (Exception e) {
            log.error("time stringToLong error, error={}", e.getMessage(), e);
            return 0;
        }
        if (date == null) {
            return 0;
        }
        return dateToLong(date);
    }

    public static void main(String[] args) {
        System.out.println();
    }

    public static String getStartTime() {
        return DateFormatUtils.format(new Date(), "yyyy-MM-dd 00:00:00");
    }

    public static String getTimeBefore(int field, int amount) {
        Calendar date = Calendar.getInstance();
        date.add(field, amount);
        return DateFormatUtils.format(date.getTime(), DEFAULT_TIME_FORMAT);
    }

    public static String getCurrentMoment() {
        return DateFormatUtils.format(new Date(), DEFAULT_TIME_FORMAT);
    }

    public static String reformatDateStrDefault(String strTime, String formaType) {
        return reformatDateStr(strTime, formaType, DEFAULT_TIME_FORMAT);
    }

    public static String reformatDateStr(String strTime, String formatType, String reformatType) {
        long longTime = stringToLong(strTime, formatType);
        return mills2Format(reformatType, longTime * 1000);
    }
}

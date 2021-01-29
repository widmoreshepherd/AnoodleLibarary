package com.anoodle.webapi.utils;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by sun on 18/1/3.
 */

public class DateUtil {
    //获取日历样式
    public static final String STYLE1 = "yyyy-MM-dd HH:mm:ss";
    public static final String STYLE2 = "yyyy年MM月dd日 HH:mm:ss";
    public static final String STYLE3 = "yyyy-MM-dd";
    public static final String STYLE4 = "yyyy年MM月dd日";
    public static final String STYLE5 = "yyyy-MM";
    public static final String STYLE6 = "yyyy年MM月";
    public static final String STYLE7 = "HH:mm:ss";
    public static final String STYLE8 = "HH:mm";
    public static final String STYLE9 = "yyyy-MM-dd HH:mm";
    public static final String STYLE10 = "MM月dd日";
    public static final String STYLE11 = "yyyy-MM-dd HH:mm:ss SSS";
    //getSingleDate(String type)用
    public static final String SINGLE_YEAR = "single_year";
    public static final String SINGLE_MONTH = "single_month";
    public static final String SINGLE_DAY = "single_day";
    public static final String SINGLE_HOUR = "single_hour";
    public static final String SINGLE_MINUTE = "single_minute";
    public static final String SINGLE_SECOND = "single_second";

    //getSingleDate(long time, String type)用
    public static final String SINGLE_YEAR1 = "yyyy";
    public static final String SINGLE_MONTH1 = "MM";
    public static final String SINGLE_DAY1 = "dd";
    public static final String SINGLE_HOUR1 = "HH";
    public static final String SINGLE_MINUTE1 = "mm";
    public static final String SINGLE_SECOND1 = "ss";


    /**
     * @param style 格式   yyyy-MM-dd   或      yyyy年MM月dd日
     * @return 2018-01-01 星期一       或      2018年1月1日 星期一
     */
    public static String getDateAndWeek(String style) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(style);
        String date = simpleDateFormat.format(new Date());
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
        String week = String.valueOf(calendar.get(Calendar.DAY_OF_WEEK));
        switch (week) {
            case "1":
                date = date + " 星期日";
                break;
            case "2":
                date = date + " 星期一";
                break;
            case "3":
                date = date + " 星期二";
                break;
            case "4":
                date = date + " 星期三";
                break;
            case "5":
                date = date + " 星期四";
                break;
            case "6":
                date = date + " 星期五";
                break;
            case "7":
                date = date + " 星期六";
                break;
        }
        return date;
    }

    /**
     * @param style 格式  yyyy-MM-dd HH:mm:ss 或 yyyy-MM-dd   或      yyyy年MM月dd日  或  HH:mm:ss  或   HH:mm
     * @return 2018-01-01   或      2018年01月01日
     */
    public static String getDateAndTime(String style) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(style);
        String date = simpleDateFormat.format(new Date());
        return date;
    }


    public static long date2TimeStamp(String date_str, String style) {
        SimpleDateFormat sdf = new SimpleDateFormat(style);
        try {
            return sdf.parse(date_str).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * @param style 格式  yyyy-MM-dd HH:mm:ss 或 yyyy-MM-dd   或      yyyy年MM月dd日  或  HH:mm:ss  或   HH:mm
     * @return 2018-01-01   或      2018年01月01日
     */
    public static String getDateAndTime(String style, long time) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(style);
        String str = simpleDateFormat.format(time);
        return str;
    }

    public static long getTime(String style, String timeString) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(style);
        long time = 0;
        try {
            time = simpleDateFormat.parse(timeString).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return time;
    }

    /**
     * 获取当日星期
     *
     * @return
     */
    public static String getWeek() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
        String week = String.valueOf(calendar.get(Calendar.DAY_OF_WEEK));
        switch (week) {
            case "1":
                return "星期日";
            case "2":
                return "星期一";
            case "3":
                return "星期二";
            case "4":
                return "星期三";
            case "5":
                return "星期四";
            case "6":
                return "星期五";
            case "7":
                return "星期六";
        }
        return "";
    }

    /**
     * 获取某一天星期几
     *
     * @param date 如：2018-01-02
     * @return
     */
    public static String getWeekOfOneDay(String date) {
        SimpleDateFormat format = new SimpleDateFormat(STYLE3);
        Calendar calendar = Calendar.getInstance();
        try {
            calendar.setTime(format.parse(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String week = String.valueOf(calendar.get(Calendar.DAY_OF_WEEK));
        switch (week) {
            case "1":
                return "星期日";
            case "2":
                return "星期一";
            case "3":
                return "星期二";
            case "4":
                return "星期三";
            case "5":
                return "星期四";
            case "6":
                return "星期五";
            case "7":
                return "星期六";
        }
        return "";
    }

    /**
     * @param type 年月日时分秒 SINGLE_YEAR，SINGLE_MONTH，SINGLE_DAY，SINGLE_HOUR，SINGLE_MINUTE，SINGLE_SECOND
     * @return
     */
    public static int getSingleDate(String type) {
        Calendar calendar = Calendar.getInstance();
        switch (type) {
            case SINGLE_YEAR:
                return calendar.get(Calendar.YEAR);
            case SINGLE_MONTH:
                return calendar.get(Calendar.MONTH) + 1;
            case SINGLE_DAY:
                return calendar.get(Calendar.DAY_OF_MONTH);
            case SINGLE_HOUR:
                return calendar.get(Calendar.HOUR_OF_DAY);
            case SINGLE_MINUTE:
                return calendar.get(Calendar.MINUTE);
            case SINGLE_SECOND:
                return calendar.get(Calendar.SECOND);
            default:
                return 0;
        }

    }

    /**
     * @param time 时间戳
     * @param type yyyy,MM,dd,HH,mm,ss
     * @return 2018, 04.15, 12, 12, 12
     */

    public static int getSingleDate(long time, String type) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(type);//设置日期格式
        String t = simpleDateFormat.format(new Date(time));// new Date()为获取当前系统时间，也可使用当前时间戳
        return Integer.parseInt(t);
    }


    // date类型转换为String类型
    // formatType格式为yyyy-MM-dd HH:mm:ss//yyyy年MM月dd日 HH时mm分ss秒
    // data Date类型的时间
    public static String dateToString(Date data, String formatType) {
        return new SimpleDateFormat(formatType).format(data);
    }

    // long类型转换为String类型
    // currentTime要转换的long类型的时间
    // formatType要转换的string类型的时间格式
    public static String longToString(long currentTime, String formatType)
            throws ParseException {
        Date date = longToDate(currentTime, formatType); // long类型转成Date类型
        String strTime = dateToString(date, formatType); // date类型转成String
        return strTime;
    }

    // string类型转换为date类型
    // strTime要转换的string类型的时间，formatType要转换的格式yyyy-MM-dd HH:mm:ss//yyyy年MM月dd日
    // HH时mm分ss秒，
    // strTime的时间格式必须要与formatType的时间格式相同
    public static Date stringToDate(String strTime, String formatType)
            throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat(formatType);
        Date date = null;
        date = formatter.parse(strTime);
        return date;
    }

    // long转换为Date类型
    // currentTime要转换的long类型的时间
    // formatType要转换的时间格式yyyy-MM-dd HH:mm:ss//yyyy年MM月dd日 HH时mm分ss秒
    public static Date longToDate(long currentTime, String formatType)
            throws ParseException {
        Date dateOld = new Date(currentTime); // 根据long类型的毫秒数生命一个date类型的时间
        String sDateTime = dateToString(dateOld, formatType); // 把date类型的时间转换为string
        Date date = stringToDate(sDateTime, formatType); // 把String类型转换为Date类型
        return date;
    }

    // string类型转换为long类型
    // strTime要转换的String类型的时间
    // formatType时间格式
    // strTime的时间格式和formatType的时间格式必须相同
    public static long stringToLong(String strTime, String formatType)
            throws ParseException {
        Date date = stringToDate(strTime, formatType); // String类型转成date类型
        if (date == null) {
            return 0;
        } else {
            long currentTime = dateToLong(date); // date类型转成long类型
            return currentTime;
        }
    }

    // date类型转换为long类型
    // date要转换的date类型的时间
    public static long dateToLong(Date date) {
        return date.getTime();
    }

    //返回连个日期的间隔月数
    public static int getMonthSpace(long date1, long date2) {
        int result;
        Calendar c1 = Calendar.getInstance();
        Calendar c2 = Calendar.getInstance();
        c1.setTimeInMillis(date1);
        c2.setTimeInMillis(date2);
        int year1 = c1.get(Calendar.YEAR);
        int year2 = c2.get(Calendar.YEAR);
        result = c2.get(Calendar.MONTH) - c1.get(Calendar.MONTH);
        if (year1 == year2) {
            return result;
        } else {
            return c2.get(Calendar.MONTH) + 12 * (year2 - year1) - c1.get(Calendar.MONTH);
        }
    }

    // 获得某天最大时间 2018-03-20 23:59:59
    public static long getMax(Date date) {
        Calendar calendarEnd = Calendar.getInstance();
        calendarEnd.setTime(date);
        calendarEnd.set(Calendar.HOUR_OF_DAY, 23);
        calendarEnd.set(Calendar.MINUTE, 59);
        calendarEnd.set(Calendar.SECOND, 59);
        //防止mysql自动加一秒,毫秒设为0
        calendarEnd.set(Calendar.MILLISECOND, 0);
        return calendarEnd.getTime().getTime();
    }

    // 获得某天最小时间 2018-03-20 00:00:00
    @RequiresApi(api = Build.VERSION_CODES.O)
    public static long getMin(Date date) {
        LocalDateTime localDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(date.getTime()), ZoneId.systemDefault());
        LocalDateTime startOfDay = localDateTime.with(LocalTime.MIN);
        return Date.from(startOfDay.atZone(ZoneId.systemDefault()).toInstant()).getTime();
    }

    public static String getDates(long staTime, long endTime) {
        long solvetimes = endTime - staTime;
        long time = solvetimes / 1000;
        if (time < 60) {
            return time + "秒";
        }else if(60<time&&time<3600){
            return time/60+"分钟";
        }else{
            return time/3600+"小时";
        }
    }
    public static Long getMonthBegin(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        //设置为1号,当前日期既为本月第一天
        c.set(Calendar.DAY_OF_MONTH, 1);
        //将小时至0
        c.set(Calendar.HOUR_OF_DAY, 0);
        //将分钟至0
        c.set(Calendar.MINUTE, 0);
        //将秒至0
        c.set(Calendar.SECOND,0);
        //将毫秒至0
        c.set(Calendar.MILLISECOND, 0);
        // 获取本月第一天的时间戳
        return c.getTimeInMillis();
    }
    public static Long getMonthEnd(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        //设置为当月最后一天
        c.set(Calendar.DAY_OF_MONTH, c.getActualMaximum(Calendar.DAY_OF_MONTH));
        //将小时至23
        c.set(Calendar.HOUR_OF_DAY, 23);
        //将分钟至59
        c.set(Calendar.MINUTE, 59);
        //将秒至59
        c.set(Calendar.SECOND,59);
        //将毫秒至999
        c.set(Calendar.MILLISECOND, 999);
        // 获取本月最后一天的时间戳
        return c.getTimeInMillis();
    }

    public static Long getYearBegin(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        //设置为1号,当前日期既为本月第一天
        c.set(Calendar.MONTH, 1);
        c.set(Calendar.DAY_OF_MONTH, 1);
        //将小时至0
        c.set(Calendar.HOUR_OF_DAY, 0);
        //将分钟至0
        c.set(Calendar.MINUTE, 0);
        //将秒至0
        c.set(Calendar.SECOND,0);
        //将毫秒至0
        c.set(Calendar.MILLISECOND, 0);
        // 获取本月第一天的时间戳
        return c.getTimeInMillis();
    }
    public static Long getYearEnd(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        //设置为当月最后一天
        c.set(Calendar.MONTH, 12);
        c.set(Calendar.DAY_OF_MONTH, c.getActualMaximum(Calendar.DAY_OF_MONTH));
        //将小时至23
        c.set(Calendar.HOUR_OF_DAY, 23);
        //将分钟至59
        c.set(Calendar.MINUTE, 59);
        //将秒至59
        c.set(Calendar.SECOND,59);
        //将毫秒至999
        c.set(Calendar.MILLISECOND, 999);
        // 获取本月最后一天的时间戳
        return c.getTimeInMillis();
    }
}

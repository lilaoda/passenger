package lhy.lhylibrary.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by lilaoda on 2016/10/24.
 */
public class DateUtils {
    public static final String dateFormat = "yyyy-MM-dd HH:mm:ss";

    /**
     * 得到系统当前时间：时间格式 2016-08-15 12：00：00
     *
     * @return
     */
    public static String getCurrentTime() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat,Locale.CHINA);
        return simpleDateFormat.format(new Date());
    }


    /**
     * 得到系统当前时间：时间格式 2016-08-15 12：00：00
     *
     */
    public static String getCurrentTime(Date date) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat,Locale.CHINA);
        return simpleDateFormat.format(date);
    }

    public static long getCurrenTimestemp() {
        return new Date().getTime();
    }

    /**
     * 时间格式 2016-08-15 12：00：00
     *
     * @return
     */
    public static long getCurrenTimestemp(String time) {
        SimpleDateFormat format = new SimpleDateFormat(dateFormat, Locale.CHINA);
        Date date = new Date();
        try {
            date = format.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date.getTime();
    }

    /**
     * 根据得到系统当前时间：时间格式 2016-08-15 12：00：00
     *
     * @return
     */
    public static String getCurrentTime(Long timestemp) {
        Date date = new Date(timestemp);
        SimpleDateFormat format = new SimpleDateFormat(dateFormat, Locale.CHINA);
        return format.format(date);
    }

}

package com.glsx.plat.common.utils;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateFormatUtils;
import org.joda.time.*;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * 日期处理
 *
 * @author liuyufeng@outlook.com
 */
public class DateUtils extends org.apache.commons.lang3.time.DateUtils {

    /**
     * 时间格式(yyyy-MM-dd HH:mm:ss)
     */
    public final static String NORMAL_DATE_PATTERN = DateFormatUtils.ISO_DATE_FORMAT.getPattern();

    /**
     * 时间格式(yyyy-MM-dd HH:mm:ss)
     */
    public final static String NORMAL_DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";

    /**
     * 时间格式(yyyyMMddHHmmss)
     */
    public final static String SERIAL_DATE_TIME_PATTERN = "yyyyMMddHHmmss";

    /**
     * 获取当前时间字符串
     *
     * @return
     */
    public static String currentDateTime() {
        return format(new Date(), NORMAL_DATE_TIME_PATTERN);
    }

    /**
     * 日期格式化 日期格式为：yyyy-MM-dd
     *
     * @param date 日期
     * @return 返回yyyy-MM-dd格式日期
     */
    public static String format(Date date) {
        return DateFormatUtils.ISO_DATE_FORMAT.format(date);
    }

    /**
     * 日期格式化 日期格式为：yyyy-MM-dd HH:mm:ss
     *
     * @param date 日期时间
     * @return 返回yyyy-MM-dd HH:mm:ss格式日期
     */
    public static String formatNormal(Date date) {
        return format(date, NORMAL_DATE_TIME_PATTERN);
    }

    /**
     * 日期格式化 日期格式为：yyyy-MM-dd HH:mm:ss
     *
     * @param date 日期时间
     * @return 返回yyyy-MM-dd HH:mm:ss格式日期
     */
    public static String formatSerial(Date date) {
        return format(date, SERIAL_DATE_TIME_PATTERN);
    }

    /**
     * 日期格式化 日期格式为：yyyy-MM-dd
     *
     * @param date    日期
     * @param pattern 格式，如：DateUtils.DATE_TIME_PATTERN
     * @return 返回yyyy-MM-dd格式日期
     */
    public static String format(Date date, String pattern) {
        if (date != null) {
            SimpleDateFormat df = new SimpleDateFormat(pattern);
            return df.format(date);
        }
        return null;
    }

    /**
     * 字符串转换成日期
     *
     * @param strDate 日期字符串
     * @param pattern 日期的格式，如：DateUtils.DATE_TIME_PATTERN
     */
    public static Date stringToDate(String strDate, String pattern) {
        if (StringUtils.isBlank(strDate)) {
            return null;
        }
        DateTimeFormatter fmt = DateTimeFormat.forPattern(pattern);
        return fmt.parseLocalDateTime(strDate).toDate();
    }

    public static Date parse(String strDate) {
        Date date = null;
        try {
            date = parseDate(strDate, NORMAL_DATE_PATTERN, NORMAL_DATE_TIME_PATTERN);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    /**
     * 根据周数，获取开始日期、结束日期
     *
     * @param week 周期  0本周，-1上周，-2上上周，1下周，2下下周
     * @return 返回date[0]开始日期、date[1]结束日期
     */
    public static Date[] getWeekStartAndEnd(int week) {
        DateTime dateTime = new DateTime();
        LocalDate date = new LocalDate(dateTime.plusWeeks(week));

        date = date.dayOfWeek().withMinimumValue();
        Date beginDate = date.toDate();
        Date endDate = date.plusDays(6).toDate();
        return new Date[]{beginDate, endDate};
    }

    /**
     * 对日期的【秒】进行加/减
     *
     * @param date    日期
     * @param seconds 秒数，负数为减
     * @return 加/减几秒后的日期
     */
    public static Date addDateSeconds(Date date, int seconds) {
        DateTime dateTime = new DateTime(date);
        return dateTime.plusSeconds(seconds).toDate();
    }

    /**
     * 对日期的【分钟】进行加/减
     *
     * @param date    日期
     * @param minutes 分钟数，负数为减
     * @return 加/减几分钟后的日期
     */
    public static Date addDateMinutes(Date date, int minutes) {
        DateTime dateTime = new DateTime(date);
        return dateTime.plusMinutes(minutes).toDate();
    }

    /**
     * 对日期的【小时】进行加/减
     *
     * @param date  日期
     * @param hours 小时数，负数为减
     * @return 加/减几小时后的日期
     */
    public static Date addDateHours(Date date, int hours) {
        DateTime dateTime = new DateTime(date);
        return dateTime.plusHours(hours).toDate();
    }

    /**
     * 对日期的【天】进行加/减
     *
     * @param date 日期
     * @param days 天数，负数为减
     * @return 加/减几天后的日期
     */
    public static Date addDateDays(Date date, int days) {
        DateTime dateTime = new DateTime(date);
        return dateTime.plusDays(days).toDate();
    }

    /**
     * 对日期的【周】进行加/减
     *
     * @param date  日期
     * @param weeks 周数，负数为减
     * @return 加/减几周后的日期
     */
    public static Date addDateWeeks(Date date, int weeks) {
        DateTime dateTime = new DateTime(date);
        return dateTime.plusWeeks(weeks).toDate();
    }

    /**
     * 对日期的【月】进行加/减
     *
     * @param date   日期
     * @param months 月数，负数为减
     * @return 加/减几月后的日期
     */
    public static Date addDateMonths(Date date, int months) {
        DateTime dateTime = new DateTime(date);
        return dateTime.plusMonths(months).toDate();
    }

    /**
     * 对日期的【年】进行加/减
     *
     * @param date  日期
     * @param years 年数，负数为减
     * @return 加/减几年后的日期
     */
    public static Date addDateYears(Date date, int years) {
        DateTime dateTime = new DateTime(date);
        return dateTime.plusYears(years).toDate();
    }

    /**
     * @param birthday
     * @return
     */
    public static int getAgeByBirth(Date birthday) {
        int age = 0;
        if (birthday == null) {
            return age;
        }
        Calendar cal = Calendar.getInstance();
        if (cal.before(birthday)) { //出生日期晚于当前时间，无法计算
            throw new IllegalArgumentException(
                    "The birthDay is before Now.It's unbelievable!");
        }
        int yearNow = cal.get(Calendar.YEAR);  //当前年份
        int monthNow = cal.get(Calendar.MONTH);  //当前月份
        int dayOfMonthNow = cal.get(Calendar.DAY_OF_MONTH); //当前日期
        cal.setTime(birthday);
        int yearBirth = cal.get(Calendar.YEAR);
        int monthBirth = cal.get(Calendar.MONTH);
        int dayOfMonthBirth = cal.get(Calendar.DAY_OF_MONTH);
        age = yearNow - yearBirth;   //计算整岁数
        if (monthNow <= monthBirth) {
            if (monthNow == monthBirth) {
                if (dayOfMonthNow < dayOfMonthBirth) {
                    age--;//当前日期在生日之前，年龄减一
                }
            } else {
                age--;//当前月份在生日之前，年龄减一
            }
        }
        return age;
    }

    /**
     * 获取今天剩余的秒数
     *
     * @return 秒数
     */
    public static int oddSecondOfDay() {
        DateTime start = new DateTime();
        DateTime end = new DateTime().withHourOfDay(23).withMinuteOfHour(59).withSecondOfMinute(59);
        return Seconds.secondsBetween(start, end).getSeconds();
    }

    /**
     * 获取本周剩余的秒数
     *
     * @return 秒数
     */
    public static int oddSecondOfWeek() {
        DateTime start = new DateTime();
        DateTime end = new DateTime().dayOfWeek().withMaximumValue().withHourOfDay(23).withMinuteOfHour(59).withSecondOfMinute(59);
        return Seconds.secondsBetween(start, end).getSeconds();
    }

    /**
     * 获取本月剩余的秒数
     *
     * @return 秒数
     */
    public static int oddSecondOfMonth() {
        DateTime start = new DateTime();
        DateTime end = new DateTime().dayOfMonth().withMaximumValue().withHourOfDay(23).withMinuteOfHour(59).withSecondOfMinute(59);
        return Seconds.secondsBetween(start, end).getSeconds();
    }

    /**
     * 获取今年剩余的秒数
     *
     * @return 秒数
     */
    public static int oddSecondOfYear() {
        DateTime start = new DateTime();
        DateTime end = new DateTime().dayOfYear().withMaximumValue().withHourOfDay(23).withMinuteOfHour(59).withSecondOfMinute(59);
        return Seconds.secondsBetween(start, end).getSeconds();
    }

    /**
     * 格式化传的时间为 X天X小时X分X秒
     *
     * @param durationTime 秒
     * @return 时间
     */
    public static String formatTimeInSec(long durationTime) {
        if (durationTime > 0) {
            int s = (int) durationTime;
            int day = s / (3600 * 24);
            s = s % (3600 * 24);
            int hour = s / 3600;
            s = s % 3600;
            int min = s / 60;
            s = s % 60;
            int sec = s;
            return day + "天" + hour + "小时" + min + "分" + sec + "秒";
        } else {
            return "0";
        }
    }

    /**
     * 获取一段时间内的星期集合
     *
     * @param startDate
     * @param endDate
     * @return
     */
    public List<String[]> getWeekListByDates(String startDate, String endDate) {
        return convertWeekList(new LocalDate(startDate), new LocalDate(endDate));
    }

    /**
     * 获取本月星期集合
     *
     * @return
     */
    public List<String[]> getLocalMonthWeekList() {
        LocalDate now = LocalDate.now();
        LocalDate endDate = now.dayOfMonth().withMaximumValue();
        LocalDate startDate = now.dayOfMonth().withMinimumValue();
        return this.convertWeekList(startDate, endDate);
    }

    /**
     * 转换weeklist
     *
     * @param startDate
     * @param endDate
     * @return
     */
    public static List<String[]> convertWeekList(LocalDate startDate, LocalDate endDate) {
        List<String[]> weekList = new ArrayList<>();
        //转换成joda-time的对象
        LocalDate firstDay = startDate.dayOfWeek().withMinimumValue().withDayOfWeek(1);
        LocalDate lastDay = endDate.dayOfWeek().withMaximumValue().withDayOfWeek(7);
        //计算两日期间的区间天数
        Period p = new Period(firstDay, lastDay, PeriodType.days());
        int days = p.getDays();
        if (days > 0) {
            int weekLength = 7;
            for (int i = 0; i < days; i = i + weekLength) {
                String monDay = firstDay.plusDays(i).toString("yyyy-MM-dd");
                String sunDay = firstDay.plusDays(i + 6).toString("yyyy-MM-dd");
                String[] week = {monDay, sunDay};
                weekList.add(week);
            }
        }
        return weekList;
    }

}

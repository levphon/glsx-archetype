package cn.com.glsx.canal.modules.convert;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author zhouhaibao
 * @version 1.0
 * @description: 类型转化
 * @date 2021/6/23 14:36
 */
public class TypeConvertHandler {
    public static final Map<Class, String> BEAN_FIELD_TYPE;

    static {
        BEAN_FIELD_TYPE = new HashMap<>(8);
        BEAN_FIELD_TYPE.put(Integer.class, "Integer");
        BEAN_FIELD_TYPE.put(Long.class, "Long");
        BEAN_FIELD_TYPE.put(Double.class, "Double");
        BEAN_FIELD_TYPE.put(String.class, "String");
        BEAN_FIELD_TYPE.put(Date.class, "java.util.Date");
        BEAN_FIELD_TYPE.put(java.sql.Date.class, "java.sql.Date");
        BEAN_FIELD_TYPE.put(java.sql.Timestamp.class, "java.sql.Timestamp");
        BEAN_FIELD_TYPE.put(java.sql.Time.class, "java.sql.Time");
        BEAN_FIELD_TYPE.put(LocalDateTime.class, "java.time.LocalDateTime");
        BEAN_FIELD_TYPE.put(BigDecimal.class, "java.math.BigDecimal");
    }

    protected static final Integer parseToInteger(String source) {
        if (isSourceNull(source)) {
            return null;
        }
        return Integer.valueOf(source);

    }

    protected static final Long parseToLong(String source) {
        if (isSourceNull(source)) {
            return null;
        }
        return Long.valueOf(source);

    }

    protected static final Double parseToDouble(String source) {
        if (isSourceNull(source)) {
            return null;
        }
        return Double.valueOf(source);

    }

    protected static final Date parseToDate(String source) {
        if (isSourceNull(source)) {
            return null;
        }
        if (source.length() == 10) {
            source = source + "00:00:00";
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = null;
        try {
            date = sdf.parse(source);
        } catch (ParseException e) {
            return null;
        }
        return date;

    }

    protected static final java.sql.Date parseToSqlDate(String source) {
        if (isSourceNull(source)) {
            return null;
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        java.sql.Date sqlDate;
        Date date;
        try {
            date = sdf.parse(source);
        } catch (ParseException e) {
            return null;
        }
        sqlDate = new java.sql.Date(date.getTime());
        return sqlDate;
    }

    protected static final java.sql.Timestamp parseToTimestamp(String source) {
        if (isSourceNull(source)) {
            return null;
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date;
        java.sql.Timestamp timestamp;
        try {
            date = sdf.parse(source);
        } catch (ParseException e) {
            return null;
        }
        timestamp = new java.sql.Timestamp(date.getTime());
        return timestamp;
    }

    protected static final java.sql.Time parseToTime(String source) {
        if (isSourceNull(source)) {
            return null;
        }

        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        Date date;
        java.sql.Time time;
        try {
            date = sdf.parse(source);
        } catch (ParseException e) {
            return null;
        }
        time = new java.sql.Time(date.getTime());
        return time;
    }


    public static LocalDateTime parseStringToDateTime(String time) {

        if (StringUtils.isEmpty(time)) {
            return null;
        }

        return LocalDateTime.parse(time, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    public static BigDecimal parseStringToBigDecimal(String source) {

        if (StringUtils.isBlank(source)) {

            return null;
        }

        return NumberUtils.createBigDecimal(source);
    }


    private static boolean isSourceNull(String source) {
        if (source == "" || source == null) {
            return true;
        }
        return false;
    }
}

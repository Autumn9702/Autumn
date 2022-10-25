package cn.autumn.util;

import java.text.ParseException;
import java.util.Date;
import java.util.Objects;

/**
 * @author: autumn
 * created in 2022/9/10 Class DateUtil
 */
public final class DateUtil extends Util{

    /* ===================   constant   ====================== */

    public static final String DEFAULT_DATE_CLOSE_FORMAT = "yyyyMMdd";
    public static final String DEFAULT_DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public static final String DEFAULT_CRON_FORMAT = "ss mm HH dd MM ?";



    public static String getCron(Long timestamp) {
        boolean second = timestamp.toString().length() < 11;
        if (second) {
            timestamp *= NumberUtil.THOUSAND;
        }
        return format(timestamp, DEFAULT_CRON_FORMAT);
    }

    public static String format(Long t, String format) {
        return Util.format(format).format(new Date(t));
    }

    public static Long formatTime(String time) {
        String format = Util.formatDateTime().format(new Date());
        Long dateTime = null;
        try {
            dateTime = Util.formatDateTime().parse(format.substring(0, 11) + time).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return dateTime;
    }

    public static Long timeStrToTimestamp(String timeStr, String format) throws ParseException {
        parameterNullNotify(timeStr);
        parameterNullNotify(format);
        try {
            return format(format).parse(timeStr).getTime();
        } catch (ParseException pe) {
            log().error("The incoming parameters timeStr: " + timeStr + " and " + "format: " + format + " do not match!");
            throw pe;
        }
    }

    public static Long dateToTimestamp(String dateStr) throws ParseException {
        parameterNullNotify(dateStr);
        try {
            return formatDate().parse(dateStr).getTime();
        } catch (ParseException pe) {
            log().error("The incoming parameter dateStr: " + dateStr + " wrong format.");
            throw pe;
        }
    }

    public static Long dateTimeToTimestamp(String dateTimeStr) throws ParseException {
        parameterNullNotify(dateTimeStr);
        try {
            return formatDateTime().parse(dateTimeStr).getTime();
        } catch (ParseException pe) {
            log().error("The incoming parameter dateTimeStr: " + dateTimeStr + " wrong format.");
            throw pe;
        }
    }

    public static String timestampToDateStr(Long timestamp) {
        parameterNullNotify(timestamp);
        return formatDate().format(new Date(timestamp));
    }

    public static String timestampToDateTimeStr(Long timestamp) {
        parameterNullNotify(timestamp);
        return formatDateTime().format(new Date(timestamp));
    }

    private static void parameterNullNotify(Number value) {
        if (Objects.isNull(value)) {
            log().error("The incoming parameter " + value + ": " + value + " is null.");
            throw new NullPointerException("Parameter " + value + ": "+ value +" is null");
        }
    }

    private static void parameterNullNotify(String value) {
        if (Objects.isNull(value)) {
            log().error("The incoming parameter " + value + ": " + value + " is null.");
            throw new NullPointerException("Parameter " + value + ": "+ value +" is null");
        }
    }

}

package cn.autumn.util;

import java.text.ParseException;
import java.util.Date;

/**
 * @author cf
 * Created in 17:25 2022/8/29
 */
public class Calc {

    public static int calculate(String birthday) throws ParseException {
        long d1 = System.currentTimeMillis() / NumberUtil.DAY_MILLIS;
        long d2 = Util.formatDate().parse(birthday).getTime() / NumberUtil.DAY_MILLIS;
        return (int) (d1 - d2);
    }

    public static int getBirthDay(String birthday) {
        int days;
        int year = Integer.parseInt(Util.formatDate().format(new Date()).substring(0, 4));
        birthday = year + birthday.substring(4);
        long birth = 0, time = 0;
        try {
            birth = Util.formatDate().parse(birthday).getTime();
            time = new Date().getTime();
            if (birth < time) {
                birthday = (year + 1) + birthday.substring(4);
                birth = Util.formatDate().parse(birthday).getTime();
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        days = (int) ((birth - time) / (NumberUtil.DAY_MILLIS));

        return days;
    }

    public static void main(String[] args) throws ParseException {
        int birthDay = getBirthDay("1997-10-02");
        System.out.println("s");
    }

    public static int judgeNowTime(){
        int a = Integer.parseInt(Util.formatHour().format(new Date()));
        if (a >= 0 && a <= 6) {
            return 3;
        }
        if (a > 6 && a <= 12) {
            return 1;
        }
        if (a == 13) {
            return 2;
        }
        if (a > 13 && a <= 18) {
            return 2;
        }
        if (a > 18 && a <= 24) {
            return 3;
        }
        return 0;
    }
}

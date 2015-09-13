package base.test.base.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.log4j.Logger;

/**
 * 
 * @ClassName: DateUtils
 * @Description: 时间工具类
 * @author chechangqun@jd.com
 * @date 2015年7月20日 下午4:29:41
 * 
 */
public class DateUtils {

    static Logger logger = Logger.getLogger("DateUtils");

    public static Date getDate(String dateString) {
        Date date = null;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            date = sdf.parse(dateString);
        } catch (ParseException e) {
            date = null;
        }
        return date;
    }

    public static String getDateStr(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(date);
    }

    /**
     * 获取date和当前时间的差距，按照月或天或分为单位超过12个月的为很久远
     * 
     * @param date
     * @return
     */
    public static String getTimeFromNow(Date date) {

        if (date == null) {
            return "火星时间";
        }
        String timeHavePast = "冰河时代";
        Calendar oldCalendar = Calendar.getInstance();
        oldCalendar.setTime(date);
        int oldYear = oldCalendar.get(Calendar.YEAR);
        int oldMonth = oldCalendar.get(Calendar.MONTH);
        int oldDay = oldCalendar.get(Calendar.DAY_OF_MONTH);
        int oldHour = oldCalendar.get(Calendar.HOUR_OF_DAY);
        int oldMinute = oldCalendar.get(Calendar.MINUTE);

        Calendar nowCalendar = Calendar.getInstance();
        nowCalendar.setTime(new Date());
        int nowYear = nowCalendar.get(Calendar.YEAR);
        int nowMonth = nowCalendar.get(Calendar.MONTH);
        int nowDay = nowCalendar.get(Calendar.DAY_OF_MONTH);
        int nowHour = nowCalendar.get(Calendar.HOUR_OF_DAY);
        int nowMinute = nowCalendar.get(Calendar.MINUTE);
        System.out.println("topicDate=" + JsonUtil.toJson(oldCalendar) + "nowdate = " + JsonUtil.toJson(nowCalendar));
        logger.error("topicDate=" + JsonUtil.toJson(oldCalendar) + "nowdate = " + JsonUtil.toJson(nowCalendar));
        if ((nowYear - oldYear) > 1) {
            timeHavePast = "很久很久以前";
        } else if ((nowMonth - oldMonth) >= 1) {
            timeHavePast = (nowMonth - oldMonth) + "月以前";
        } else if ((nowDay - oldDay) >= 1) {
            timeHavePast = (nowDay - oldDay) + "天以前";
        } else if ((nowHour - oldHour) >= 1) {
            timeHavePast = (nowHour - oldHour) + "小时以前";
        } else if ((nowMinute - oldMinute) >= 1) {
            timeHavePast = (nowMinute - oldMinute) + "分钟以前";
        } else {
            timeHavePast = "刚刚";
        }
        logger.error("计算出的时间差距为：" + timeHavePast);
        return timeHavePast;
    }

    public static void main(String[] args) {
        try {
            SimpleDateFormat myFmt1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date oldDate1 = myFmt1.parse("2015-08-20 19:08:59");
            getTimeFromNow(oldDate1);
            SimpleDateFormat myFmt2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date oldDate2 = myFmt2.parse("2015-08-25 20:26:16");
            getTimeFromNow(oldDate2);

        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}

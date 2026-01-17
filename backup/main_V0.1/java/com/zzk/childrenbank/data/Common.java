package com.zzk.childrenbank.data;

import android.graphics.Color;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class Common {
    public static final String CHILD_ID = "child_id";
    public static final String ACCOUNT_ID = "account_id";
    public static final String TRANSACTION_ID = "transaction_id";
    public static final String CATEGORY_ID = "category_id";
    public static final String FROM_TO = "from_to";
    public static final String FROMTO_ID = "fromto_id";

    public static int COLOR_INCREASE = Color.RED;
    public static int COLOR_DECREASE = Color.GREEN;

    public static String balanceToString(int balance){
        StringBuilder strBuilder = new StringBuilder(Integer.toString(balance));
        int length = strBuilder.length();
        switch (length) {
            case 0: strBuilder.insert(0, "000");
                break;
            case 1: strBuilder.insert(0, "00");
                break;
            case 2: strBuilder.insert(0, "0");
                break;
        }
        length = strBuilder.length();       //  处理完位数不够的情况后再重新计算字符个数
        strBuilder.insert(length-2, '.');
        return strBuilder.toString();
    }

    public static int stringToBalance(String str) {
        float f = Float.parseFloat(str);
        int val = Math.round(f * 100);
        return val;
    }

    public static String signedAmountToString(int sign, int amount) {
        if(sign==1) return "+" + balanceToString(amount);
        else return "-" + balanceToString(amount);
    }

    static String[] weekString ={"日", "一", "二", "三", "四", "五", "六"};
    public static String DateTimeToString(Date datetime, boolean showDate, boolean showWeek, boolean showTime){
        Calendar calendar = GregorianCalendar.getInstance();
        calendar.setTime(datetime);
        String dataStr = String.format("%d年%d月%d日", calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH)+1,  calendar.get(Calendar.DAY_OF_MONTH));
        String timeStr = String.format("%02d:%02d", calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE));
        int weekDay = calendar.get(Calendar.DAY_OF_WEEK)-1;
        String weekStr = "星期" + weekString[weekDay];
        StringBuilder result = new StringBuilder();
        if(showDate) result.append(dataStr);
        if(showWeek) {
            if(result.length()>0) result.append(" ");
            result.append(weekStr);
        }
        if(showTime) {
            if(result.length()>0) result.append(" ");
            result.append(timeStr);
        }
        return result.toString();
    }

    public static String DateTimeToStringShort(Date datetime, boolean showDate, boolean showWeek, boolean showTime){
        Calendar calendar = GregorianCalendar.getInstance();
        calendar.setTime(datetime);
        String dataStr = String.format("%d-%d-%d", calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH)+1,  calendar.get(Calendar.DAY_OF_MONTH));
        String timeStr = String.format("%02d:%02d", calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE));
        int weekDay = calendar.get(Calendar.DAY_OF_WEEK)-1;
        String weekStr = "(" + weekString[weekDay] + ")";
        StringBuilder result = new StringBuilder();
        if(showDate) result.append(dataStr);
        if(showWeek) {
            //if(result.length()>0) result.append(" ");
            result.append(weekStr);
        }
        if(showTime) {
            if(result.length()>0) result.append(" ");
            result.append(timeStr);
        }
        return result.toString();
    }

    public static int getSignColor(int sign) {
        if(sign==1) return COLOR_INCREASE;
        else return COLOR_DECREASE;
    }
}

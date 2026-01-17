package com.zzk.familybank.data;

/**
 * 用整数表示的时间格式
 * 时间格式：630 = 6:30
 * 时长格式：630 = 6小时30分
 */
public class IntTime {
    public static int HMtoInt(int hour, int minute){
        return hour*100+minute;
    }

    public static int getHour(int time) {return time/100;}
    public static int getMinute(int time) {return time%100;}

    public static int addMinutes(int time, int minute) {
        int h = time/100 + minute/60;   //  小时
        int m = time%100 + minute%60;   //  分
        h += m/60;
        return h*100 + m%60;
    }

    public static int subMinutes(int time, int minute) {
        int h = time/100 - minute/60;   //  小时
        int m = time%100 - minute%60;   //  分
        while(m<0) {
            m += 60;
            h --;
        }
        while(h<0) h+=24;
        return h*100 + m;
    }

    public static String IntTimeToString(int time){
        int h = time/100;   //  小时
        int m = time%100;   //  分
        return String.format("%d:%02d", h, m);
    }
}

package com.zzk.familybank.data;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * 用整数表示的日期格式：
 * 日期格式：20210121
 * 月格式：202001
 */
public class IntDate {
     /**
     * 年+月+日转化为IntDate，例如2021，1，21转化为20210121
     * @param year
     * @param month
     * @param day
     * @return
     */
    public static int YMDToInt(int year, int month, int day) {
        return year*10000 + month*100 + day;
    }

    /**
     * 年月+日转化为IntDate，例如202101，21转化为20210121
     * @param yearMonth
     * @param day
     * @return
     */
    public static int YMDToInt(int yearMonth, int day) {
        return yearMonth*100 + day;
    }

    public static int DateToInt(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int intDate = IntDate.YMDToInt(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH)+1, calendar.get(Calendar.DAY_OF_MONTH));
        return intDate;
    }

    public static Date getDate(int intDate) {
        Calendar cal = new GregorianCalendar(getYear(intDate), getMonth(intDate)-1, getDay(intDate),0,0,0);
        return cal.getTime();
    }

    /**
     * 获取年月，例如20210121的年月为202101
     * @param date
     * @return
     */
    public static int getYearMonth(int date){
        return date / 100;
    }

    /**
     * 获取日，例如20210121的日为21
     * @param date
     * @return
     */
    public static int getDay(int date) {
        return date % 100;
    }

    public static int getYear(int date) { return date/10000;}

    public static int getMonth(int date) { return date / 100 % 100;}

    /**
     * 获取今天日期，例如20210121
     * @return
     */
    public static int getToday(){
        Calendar todayDate = Calendar.getInstance();
        int today = IntDate.YMDToInt(todayDate.get(Calendar.YEAR), todayDate.get(Calendar.MONTH)+1, todayDate.get(Calendar.DAY_OF_MONTH));
        return today;
    }

    /**
     * 获取今天年月，例如202101
     * @return
     */
    public static int getYearMonthOfToday(){
        Calendar todayDate = Calendar.getInstance();
        int yearMonth = IntDate.YMToInt(todayDate.get(Calendar.YEAR), todayDate.get(Calendar.MONTH)+1);
        return yearMonth;
    }

    /**
     * 年+月转化为年月IntDate，例如2021，01转化为202101
     * @param year
     * @param month
     * @return
     */
    public static int YMToInt(int year, int month){
        return year*100 + month;
    }

    /**
     * 从年月IntDate中获取年，例如202101的年为2021
     * @param yearMonth
     * @return
     */
    public static int getYearOfYearMonth(int yearMonth) {return yearMonth/100;}

    /**
     * 从年月IntDate中获取月，例如202101的月为1
     * @param yearMonth
     * @return
     */
    public static int getMonthOfYearMonth(int yearMonth) { return yearMonth%100;}

    /**
     * 求两个年月IntDate差几个月，first-second。例如MonthSub(202101,202012)结果为1
     * @param first
     * @param second
     * @return
     */
    public static int MonthDiffer(int first, int second) {
        int sign =  1;
        if(first<second) {
            sign = -1;
            int tmp = first;
            first = second;
            second = tmp;
        }

        int firstYear = first / 100;
        int firstMonth = first % 100;

        int secondYear = second / 100;
        int secondMonth = second % 100;

        int result = (firstYear - secondYear) * 12 + firstMonth - secondMonth;

        return sign * result;
    }

    /**
     * 年月IntDate加上几个月，yearMonth+differMonth，例如MonthAdd(202012,1)结果为202101
     * @param yearMonth
     * @param differMonth
     * @return
     */
    public static int MonthAdd(int yearMonth, int differMonth) {
        int year = yearMonth / 100;
        int month = yearMonth % 100;
        int resultMonth = month + differMonth;
        if(resultMonth>12) {
            year += resultMonth / 12;
            resultMonth = (resultMonth-1) % 12 + 1;
        }
        return year*100+resultMonth;
    }

    /**
     * 年月IntDate减去几个月，yearMonth-differMonth，例如MonthSub(202101,2)结果为202011
     * @param yearMonth
     * @param differMonth
     * @return
     */
    public static int MonthSub(int yearMonth, int differMonth) {
        int year = yearMonth / 100;
        int month = yearMonth % 100;
        int resultMonth = month - differMonth;
        if(resultMonth<1) {
            //  202001 - 1 = 201912
            //  resultMonth = 0;    year -= 1;  resultMonth = 12 - 0;
            //  202002 - 12 = 201902
            //  resultMonth = -10;  year -= 1;  resultMonth = 12 - 10;
            year -= (0 - resultMonth) / 12 + 1;
            resultMonth = 12 - (0 - resultMonth) % 12;
        }
        return year*100+resultMonth;
    }

    /**
     * 年月日IntDate加上几天，date + days，例如DayAdd(20210121, 11)结果为20210201
     * days为负数表示减去几天
     * @param date
     * @param days
     * @return
     */
    public static int dayAdd(int date, int days) {
        Calendar cal = new GregorianCalendar(getYear(date), getMonth(date)-1, getDay(date),0,0,0);
        cal.add(Calendar.DATE, days);
        return YMDToInt(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH)+1, cal.get(Calendar.DAY_OF_MONTH));
    }

    public static int dayDiffer(int startDate, int endDate){
        Calendar startCal = new GregorianCalendar(getYear(startDate), getMonth(startDate)-1, getDay(startDate),0,0,0);
        Calendar endCal = new GregorianCalendar(getYear(endDate), getMonth(endDate)-1, getDay(endDate),0,0,0);

        long days = (endCal.getTime().getTime() - startCal.getTime().getTime())/(1000*3600*24);
        return (int)days;
    }
}

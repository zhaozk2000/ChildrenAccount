package com.zzk.familybank.data;

import android.content.ContentValues;

public class Automation {
    public int id;
    public int type;
    public int period;
    public int date;
    public int account1;
    public int account2;
    public int account1Category;
    public int account1FromTo;
    public int account2Category;
    public int account2FromTo;
    public int int_data1;
    public float float_data1;

    public static String[] typeString = {"未知", "结息", "取零花钱"};
    public String getTypeString() {
        if(type>=1 && type<=2) return typeString[type];
        else return typeString[0];
    }

    public static String[] periodString = {"年", "月"};
    public String getPeriodString(){
        return periodString[period];
    }
    public String getDescribeString(DataModel dataModel) {
        if(type==1) {   //  结息
            String s = "账户" + dataModel.getAccountString(account1) +  "每" + periodString[period] + Common.intDateToString(date) + "计算利息，" +
                    "年化利率" + float_data1 + "%，利息存入账户" + dataModel.getAccountString(account2);
            return s;
        } else if(type==2) {    //  取零花钱
            String s = "从账户" + dataModel.getAccountString(account1) + "每" + periodString[period] + Common.intDateToString(date) +
                    "取零花钱" + Common.balanceToString(int_data1) + "元存入账户" + dataModel.getAccountString(account2);
            return s;
        }
        return typeString[0];
    }

    @Override
    public boolean equals(Object object) {
        Automation other = (Automation) object;
        if(type!=other.type) return false;
        if(period !=other.period) return false;
        if(date !=other.date) return false;
        if(account1!=other.account1) return false;
        if(account2!=other.account2) return false;
        if(account1Category!=other.account1Category) return false;
        if(account1FromTo!=other.account1FromTo) return false;
        if(account2Category!=other.account2Category) return false;
        if(account2FromTo!=other.account2FromTo) return false;
        if(int_data1 !=other.int_data1) return false;
        if(float_data1!=other.float_data1) return false;
        return true;
    }

    public void copyFrom(Automation automation) {
        id = automation.id;
        type = automation.type;
        period = automation.period;
        date = automation.date;
        account1 = automation.account1;
        account2 = automation.account2;
        account1Category = automation.account1Category;
        account1FromTo = automation.account1FromTo;
        account2Category = automation.account2Category;
        account2FromTo = automation.account2FromTo;
        int_data1 = automation.int_data1;
        float_data1 = automation.float_data1;
    }

    public ContentValues toContentValues(){
        ContentValues values = new ContentValues();
        values.put(DBHelper.AutomationTable.type, type);
        values.put(DBHelper.AutomationTable.period, period);
        values.put(DBHelper.AutomationTable.date, date);
        values.put(DBHelper.AutomationTable.account1, account1);
        values.put(DBHelper.AutomationTable.account2, account2);
        values.put(DBHelper.AutomationTable.category1, account1Category);
        values.put(DBHelper.AutomationTable.fromto1, account1FromTo);
        values.put(DBHelper.AutomationTable.category2, account2Category);
        values.put(DBHelper.AutomationTable.fromto2, account2FromTo);
        values.put(DBHelper.AutomationTable.int_data1, int_data1);
        values.put(DBHelper.AutomationTable.float_data1, float_data1);
        return values;
    }
}

package com.zzk.childrenbank.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "children_account.db";
    private static final int DB_VERSION = 1;
    public static final String tableNameChildren = "children_table";        //  儿童表，每个儿童一条记录
    public static final String tableNameAccount = "account_table";          //  账户表，每个账户一条记录，一个儿童可以有多个账户
    public static final String tableNameTransaction = "transaction_table";  //  交易表，一个存取操作一条记录
    public static final String tableNameCategory = "category_table";        //  条目表，一个交易类型一条记录，如：考试奖励、扔垃圾、等等
    public static final String tableNameFromTo = "fromto_table";            //  来源去向表，一个来源/去向一条记录，如：爷爷奶奶、爸爸、喜登门、淘宝、游戏等等
    public static final String tableNameAutomated = "automated_table";      //  自动规则表，一条规则一条记录，如：每月结算利息、每月取零花钱、等等

    public DBHelper(Context context){
        super(context, DB_NAME, null, DB_VERSION);
    }

    public void onCreate(SQLiteDatabase db){
        db.execSQL("CREATE TABLE IF NOT EXISTS " + tableNameChildren +
                "(_id INTEGER PRIMARY KEY AUTOINCREMENT, name VERCHAR(255), create_time LONG)");
        db.execSQL("CREATE TABLE IF NOT EXISTS " + tableNameAccount +
                "(_id INTEGER PRIMARY KEY AUTOINCREMENT, child TINYINT, name VERCHAR(255), balance INT)");   //  余额balance单位为分
        db.execSQL("CREATE TABLE IF NOT EXISTS " + tableNameTransaction +
                "(_id INTEGER PRIMARY KEY AUTOINCREMENT, child TINYINT, account TINYINT, amount INT, category SMALLINT, fromto SMALLINT, date LONG, notes VERCHAR(255))");
        db.execSQL("CREATE TABLE IF NOT EXISTS " + tableNameCategory +
                "(_id INTEGER PRIMARY KEY AUTOINCREMENT, child TINYINT, account TINYINT, name VERCHAR(64), sign TINYINT, default_amount INT, default_fromto INT, notes VERCHAR(255))");
        db.execSQL("CREATE TABLE IF NOT EXISTS " + tableNameFromTo +
                "(_id INTEGER PRIMARY KEY AUTOINCREMENT, child TINYINT, name VERCHAR(16), fromto TINYINT)");
        db.execSQL("CREATE TABLE IF NOT EXISTS " + tableNameAutomated +
                "(_id INTEGER PRIMARY KEY AUTOINCREMENT, name VERCHAR(64), account1 TINYINT, account2 TINYINT, " +
                "int_data1 INT, int_data2 INT, float_data1 FLOAT, notes VERCHAR(255))");
        //每月自动结息规则：<name=自动结息, account1=1,      account2=2,     int_data1=1,    int_data2=20,   int_data3=5>
        //                              根据账户1余额结息，记入账户2，        按月结息，       每月20号结息     年化利率5%
        //每月自动取零花钱：<name=自动取零花钱，account1=1,   account2=2,     int_data1=1,    int_data2=20,   int_data3=100>
        //                              从存款账户取，     加到零花账户，     按月结息，       每月20号取       取100元
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
    }

    public static class ChildrenTable {
        public static String _id = "_id";
        public static String name = "name";
        public static String create_time = "create_time";
    }

    public static class AccountTable {
        public static String _id = "_id";
        public static String child = "child";
        public static String name = "name";
        public static String balance = "balance";
    }

    public static class TransactionTable {
        public static String _id = "_id";
        public static String child = "child";
        public static String account = "account";
        public static String amount = "amount";
        public static String category = "category";
        public static String fromto = "fromto";
        public static String date = "date";
        public static String notes = "notes";
    }

    public static class CategoryTable {
        public static String _id = "_id";
        public static String child = "child";
        public static String account = "account";
        public static String name = "name";
        public static String sign = "sign";
        public static String default_amount = "default_amount";
        public static String default_fromto = "default_fromto";
        public static String notes = "notes";
    }

    public static class FromToTable {
        public static String _id = "_id";
        public static String child = "child";
        public static String name = "name";
        public static String fromto = "fromto";
    }

}

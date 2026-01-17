package com.zzk.familybank.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "family_bank.db";
    private static final int DB_VERSION = 1;
    public static final String tableNameHolder = "holder_table";        //  持有人表，每个家庭成员一条记录
    public static final String tableNameAccount = "account_table";          //  账户表，每个账户一条记录，一个儿童可以有多个账户
    public static final String tableNameTransaction = "transaction_table";  //  交易表，一个存取操作一条记录
    public static final String tableNameCategory = "category_table";        //  条目表，一个交易类型一条记录，如：考试奖励、扔垃圾、等等
    public static final String tableNameFromTo = "fromto_table";            //  来源去向表，一个来源/去向一条记录，如：爷爷奶奶、爸爸、喜登门、淘宝、游戏等等
    public static final String tableNameAutomation = "automation_table";      //  自动规则表，一条规则一条记录，如：每月结算利息、每月取零花钱、等等

    public DBHelper(Context context){
        super(context, DB_NAME, null, DB_VERSION);
    }

    public void onCreate(SQLiteDatabase db){
        db.execSQL("CREATE TABLE IF NOT EXISTS " + tableNameHolder +
                "(_id INTEGER PRIMARY KEY AUTOINCREMENT, name VERCHAR(255), create_time LONG)");
        db.execSQL("CREATE TABLE IF NOT EXISTS " + tableNameAccount +
                "(_id INTEGER PRIMARY KEY AUTOINCREMENT, holder TINYINT, name VERCHAR(255), balance INT)");   //  余额balance单位为分
        db.execSQL("CREATE TABLE IF NOT EXISTS " + tableNameTransaction +
                "(_id INTEGER PRIMARY KEY AUTOINCREMENT, holder TINYINT, account TINYINT, amount INT, after_balance INT, category SMALLINT, fromto SMALLINT, date LONG, notes VERCHAR(255))");
        db.execSQL("CREATE TABLE IF NOT EXISTS " + tableNameCategory +
                "(_id INTEGER PRIMARY KEY AUTOINCREMENT, holder TINYINT, account TINYINT, name VERCHAR(64), sign TINYINT, default_amount INT, default_fromto INT, notes VERCHAR(255))");
        db.execSQL("CREATE TABLE IF NOT EXISTS " + tableNameFromTo +
                "(_id INTEGER PRIMARY KEY AUTOINCREMENT, name VERCHAR(16), direction TINYINT)");
        db.execSQL("CREATE TABLE IF NOT EXISTS " + tableNameAutomation +
                "(_id INTEGER PRIMARY KEY AUTOINCREMENT, type TINYINT, account1 TINYINT, account2 TINYINT, " +
                "period INT, execute_date INT, category1 INT, fromto1 INT, category2 INT, fromto2 INT, " +
                "int_data1 INT, float_data1 FLOAT, notes VERCHAR(255))");
        //每月自动结息规则：< type=1,   account1=1,      account2=2,  int_data1=1,    int_data2=20,   int_data3=0,    float_data1=0.05>
        //                 结息      根据账户1余额结息，记入账户2，     按月结息，       每月20号结息                     年化利率5%
        //每月自动取零花钱：<type=2,    account1=1,   account2=2,     int_data1=1,    int_data2=20,   int_data3=100>
        //                 零花钱    从存款账户取，     加到零花账户，   按月取零花，     每月20号取       取100元
        // category1, fromto1 用于取款transaction；category2, fromto2 用于存款transaction
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
    }

    public static class HolderTable {
        public static String _id = "_id";
        public static String name = "name";
        public static String create_time = "create_time";
    }

    public static class AccountTable {
        public static String _id = "_id";
        public static String holder = "holder";
        public static String name = "name";
        public static String balance = "balance";
    }

    public static class TransactionTable {
        public static String _id = "_id";
        public static String holder = "holder";
        public static String account = "account";
        public static String amount = "amount";
        public static String after_balance = "after_balance";
        public static String category = "category";
        public static String fromto = "fromto";
        public static String date = "date";
        public static String notes = "notes";
    }

    public static class CategoryTable {
        public static String _id = "_id";
        public static String holder = "holder";
        public static String account = "account";
        public static String name = "name";
        public static String sign = "sign";
        public static String default_amount = "default_amount";
        public static String default_fromto = "default_fromto";
        public static String notes = "notes";
    }

    public static class FromToTable {
        public static String _id = "_id";
        public static String name = "name";
        public static String direction = "direction";
    }

    public static class AutomationTable {
        public static String _id = "_id";
        public static String type = "type";
        public static String period = "period";
        public static String date = "execute_date";
        public static String account1 = "account1";
        public static String account2 = "account2";
        public static String category1 = "category1";
        public static String fromto1 = "fromto1";
        public static String category2 = "category2";
        public static String fromto2 = "fromto2";
        public static String int_data1 = "int_data1";
        public static String float_data1 = "float_data1";
    }
}

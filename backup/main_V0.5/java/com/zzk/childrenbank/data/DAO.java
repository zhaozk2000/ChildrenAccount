package com.zzk.childrenbank.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;

public class DAO {
    static DBHelper dbHelper;

    public static void init(Context context){
        dbHelper = new DBHelper(context);
    }

    public static void loadDataFromDB(DataModel model){
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        // 读取Children表
        model.childList.clear();
        String sql = "select * from " + DBHelper.tableNameChildren;
        Cursor cursor = db.rawQuery(sql, null);
        int idIndex = cursor.getColumnIndex(DBHelper.ChildrenTable._id);
        int nameIndex = cursor.getColumnIndex(DBHelper.ChildrenTable.name);
        int createTimeIndex = cursor.getColumnIndex(DBHelper.ChildrenTable.create_time);
        while(cursor.moveToNext()) {
            Child child = new Child();
            child.id = cursor.getInt(idIndex);
            child.name = cursor.getString(nameIndex);
            child.createTime = new Date(cursor.getLong(createTimeIndex));
            model.childList.add(child);
        }
        cursor.close();

        // 读取Account表
        sql = "select * from " + DBHelper.tableNameAccount;
        cursor = db.rawQuery(sql, null);
        idIndex = cursor.getColumnIndex(DBHelper.AccountTable._id);
        int childIndex = cursor.getColumnIndex(DBHelper.AccountTable.child);
        nameIndex = cursor.getColumnIndex(DBHelper.AccountTable.name);
        int balanceIndex = cursor.getColumnIndex(DBHelper.AccountTable.balance);
        while(cursor.moveToNext()) {
            Account account = new Account();
            account.id = cursor.getInt(idIndex);
            account.child = cursor.getInt(childIndex);
            account.name = cursor.getString(nameIndex);
            account.balance = cursor.getInt(balanceIndex);
            Child child = model.findChildById(account.child);
            if(child!=null) child.accountList.add(account);
        }
        cursor.close();

        // 读取Category表
        sql = "select * from " + DBHelper.tableNameCategory;
        cursor = db.rawQuery(sql, null);
        idIndex = cursor.getColumnIndex(DBHelper.CategoryTable._id);
        childIndex = cursor.getColumnIndex(DBHelper.CategoryTable.child);
        int accountIndex = cursor.getColumnIndex(DBHelper.CategoryTable.account);
        nameIndex = cursor.getColumnIndex(DBHelper.CategoryTable.name);
        int signIndex = cursor.getColumnIndex(DBHelper.CategoryTable.sign);
        int defaultAmountIndex = cursor.getColumnIndex(DBHelper.CategoryTable.default_amount);
        int defaultFromToIndex = cursor.getColumnIndex(DBHelper.CategoryTable.default_fromto);
        int notesIndex = cursor.getColumnIndex(DBHelper.CategoryTable.notes);
        while(cursor.moveToNext()) {
            Category category = new Category();
            category.id = cursor.getInt(idIndex);
            category.child = cursor.getInt(childIndex);
            category.account = cursor.getInt(accountIndex);
            category.name = cursor.getString(nameIndex);
            category.sign = cursor.getInt(signIndex);
            category.default_amount = cursor.getInt(defaultAmountIndex);
            category.default_fromto = cursor.getInt(defaultFromToIndex);
            category.notes = cursor.getString(notesIndex);

            Child child = model.findChildById(category.child);
            if(child==null) continue;
            Account account = child.findAccountById(category.account);
            if(account!=null) {
                account.categoryList.add(category);
            }
        }
        cursor.close();

        // 读取FromTo表
        sql = "select * from " + DBHelper.tableNameFromTo + " order by _id desc";
        cursor = db.rawQuery(sql, null);
        idIndex = cursor.getColumnIndex(DBHelper.FromToTable._id);
        //childIndex = cursor.getColumnIndex(DBHelper.FromToTable.child);
        nameIndex = cursor.getColumnIndex(DBHelper.FromToTable.name);
        int directionIndex = cursor.getColumnIndex(DBHelper.FromToTable.direction);
        while(cursor.moveToNext()) {
            FromTo fromTo = new FromTo();
            fromTo.id = cursor.getInt(idIndex);
            //fromTo.child = cursor.getInt(childIndex);
            fromTo.name = cursor.getString(nameIndex);
            fromTo.direction = (byte)cursor.getInt(directionIndex);

            if(fromTo.direction==1 || fromTo.direction==3) model.fromList.add(fromTo);
            if(fromTo.direction==2 || fromTo.direction==3) model.toList.add(fromTo);
            model.fromToList.add(fromTo);
        }
        cursor.close();

        //  计算每个Child的总余额，载入最近交易列表
        Iterator<Child> iter = model.childList.iterator();
        while(iter.hasNext()) {
            Child child = iter.next();
            child.calculateTotalBalance();

            // 读取Transaction表
            // 因为要针对每个child限制读取到内存缓存的transaction个数，所以不能一次读取全部transaction再分配给child
            // 而是要对每个child分别读取一定个数的transaction
            sql = "select * from " + DBHelper.tableNameTransaction + " where " + DBHelper.TransactionTable.child + "=" + child.id
                    + " order by " + DBHelper.TransactionTable.date + " desc limit " + Child.TRANSACTION_BUFFER_SIZE;
            cursor = db.rawQuery(sql, null);
            idIndex = cursor.getColumnIndex(DBHelper.TransactionTable._id);
            childIndex = cursor.getColumnIndex(DBHelper.TransactionTable.child);
            accountIndex = cursor.getColumnIndex(DBHelper.TransactionTable.account);
            int categoryIndex = cursor.getColumnIndex(DBHelper.TransactionTable.category);
            int amountIndex = cursor.getColumnIndex(DBHelper.TransactionTable.amount);
            int afterBalanceIndex = cursor.getColumnIndex(DBHelper.TransactionTable.after_balance);
            int fromtoIndex = cursor.getColumnIndex(DBHelper.TransactionTable.fromto);
            int dateIndex = cursor.getColumnIndex(DBHelper.TransactionTable.date);
            notesIndex = cursor.getColumnIndex(DBHelper.TransactionTable.notes);
            while(cursor.moveToNext()) {
                Transaction transaction = new Transaction();
                transaction.id = cursor.getInt(idIndex);
                transaction.child = cursor.getInt(childIndex);
                transaction.account = cursor.getInt(accountIndex);
                transaction.category = cursor.getInt(categoryIndex);
                transaction.amount = cursor.getInt(amountIndex);
                transaction.afterBalance = cursor.getInt(afterBalanceIndex);
                transaction.fromto = cursor.getInt(fromtoIndex);
                transaction.date = new Date(cursor.getLong(dateIndex));
                transaction.notes = cursor.getString(notesIndex);
                child.transactionList.add(transaction);
            }
            cursor.close();
        }

        // 读取Automation表
        loadAutomationFromTable(db, model.automationList);

        db.close();
    }

    static void loadAutomationFromTable(SQLiteDatabase db, List<Automation> automationList) {
        String sql = "select * from " + DBHelper.tableNameAutomation + " order by _id asc";
        Cursor cursor = db.rawQuery(sql, null);
        int idIndex = cursor.getColumnIndex(DBHelper.AutomationTable._id);
        int typeIndex = cursor.getColumnIndex(DBHelper.AutomationTable.type);
        int periodIndex = cursor.getColumnIndex(DBHelper.AutomationTable.period);
        int dateIndex = cursor.getColumnIndex(DBHelper.AutomationTable.date);
        int account1Index = cursor.getColumnIndex(DBHelper.AutomationTable.account1);
        int account2Index = cursor.getColumnIndex(DBHelper.AutomationTable.account2);
        int category1Index = cursor.getColumnIndex(DBHelper.AutomationTable.category1);
        int fromto1Index = cursor.getColumnIndex(DBHelper.AutomationTable.fromto1);
        int category2Index = cursor.getColumnIndex(DBHelper.AutomationTable.category2);
        int fromto2Index = cursor.getColumnIndex(DBHelper.AutomationTable.fromto2);
        int int_data1Index = cursor.getColumnIndex(DBHelper.AutomationTable.int_data1);
        int float_data1Index = cursor.getColumnIndex(DBHelper.AutomationTable.float_data1);
        while(cursor.moveToNext()) {
            Automation automation = new Automation();
            automation.id = cursor.getInt(idIndex);
            automation.type = cursor.getInt(typeIndex);
            automation.period = cursor.getInt(periodIndex);
            automation.date = cursor.getInt(dateIndex);
            automation.account1 = cursor.getInt(account1Index);
            automation.account2 = cursor.getInt(account2Index);
            automation.account1Category = cursor.getInt(category1Index);
            automation.account1FromTo = cursor.getInt(fromto1Index);
            automation.account2Category = cursor.getInt(category2Index);
            automation.account2FromTo = cursor.getInt(fromto2Index);
            automation.int_data1 = cursor.getInt(int_data1Index);
            automation.float_data1 = cursor.getInt(float_data1Index);

            automationList.add(automation);
        }
        cursor.close();
    }

    public static boolean insertChild(Child child) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DBHelper.ChildrenTable.name, child.name);
        values.put(DBHelper.ChildrenTable.create_time, child.createTime.getTime());
        long id = db.insert(DBHelper.tableNameChildren, null, values);
        db.close();

        if(id>=0) {
            child.id = (int)id;
            return true;
        } else {
            return false;
        }
    }

    public static boolean deleteChild(String name) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String whereClause = DBHelper.ChildrenTable.name + "='" + name + "'";
        int result = db.delete(DBHelper.tableNameChildren, whereClause, null);
        db.close();
        if(result==1) return true;
        return false;
    }

    public static boolean updateChild(int id, String name) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DBHelper.ChildrenTable.name, name);
        String whereClause = DBHelper.ChildrenTable._id + "=" + id;
        int result = db.update(DBHelper.tableNameChildren, values, whereClause, null);
        db.close();
        if(result==1) return true;
        return false;
    }

    public static boolean deleteAllChild() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int result = db.delete(DBHelper.tableNameChildren, null, null);
        db.delete(DBHelper.tableNameAccount, null, null);
        db.close();
        return true;
    }

    public static boolean insertAccount(Account account) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DBHelper.AccountTable.child, account.child);
        values.put(DBHelper.AccountTable.name, account.name);
        values.put(DBHelper.AccountTable.balance, account.balance);
        long id = db.insert(DBHelper.tableNameAccount, null, values);
        db.close();

        if(id>=0) {
            account.id = (int)id;
            return true;
        } else {
            return false;
        }
    }

    public static boolean deleteAccount(int childId, String name) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String whereClause = DBHelper.AccountTable.child + "=" + childId + " AND " + DBHelper.AccountTable.name + "='" + name + "'";
        int result = db.delete(DBHelper.tableNameAccount, whereClause, null);
        db.close();
        if(result==1) return true;
        return false;
    }

    public static boolean updateAccount(int childId, int accountId, String name) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DBHelper.AccountTable.name, name);
        String whereClause = DBHelper.AccountTable._id + "=" + accountId;
        int result = db.update(DBHelper.tableNameAccount, values, whereClause, null);
        db.close();
        if(result==1) return true;
        return false;
    }

    public static boolean deleteAllAccounts(int childId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String whereClause = DBHelper.AccountTable.child + "=" + childId;
        int result = db.delete(DBHelper.tableNameAccount, whereClause, null);
        db.close();
        return true;
    }

    public static boolean insertCategory(Category category) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DBHelper.CategoryTable.child, category.child);
        values.put(DBHelper.CategoryTable.account, category.account);
        values.put(DBHelper.CategoryTable.name, category.name);
        values.put(DBHelper.CategoryTable.sign, category.sign);
        values.put(DBHelper.CategoryTable.default_amount, category.default_amount);
        values.put(DBHelper.CategoryTable.default_fromto, category.default_fromto);
        values.put(DBHelper.CategoryTable.notes, category.notes);
        long id = db.insert(DBHelper.tableNameCategory, null, values);
        db.close();

        if(id>=0) {
            category.id = (int)id;
            return true;
        } else {
            return false;
        }
    }

    public static boolean updateCategory(Category category) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DBHelper.CategoryTable.child, category.child);
        values.put(DBHelper.CategoryTable.account, category.account);
        values.put(DBHelper.CategoryTable.name, category.name);
        values.put(DBHelper.CategoryTable.sign, category.sign);
        values.put(DBHelper.CategoryTable.default_amount, category.default_amount);
        values.put(DBHelper.CategoryTable.default_fromto, category.default_fromto);
        values.put(DBHelper.CategoryTable.notes, category.notes);
        String whereClause = DBHelper.CategoryTable._id + "=" + category.id;
        int result = db.update(DBHelper.tableNameCategory, values, whereClause, null);
        db.close();
        if(result==1) return true;
        return false;
    }

    /**
     * 插入一条交易，
     * 1、根据时间获取前一次交易，获得其交易后的余额；
     * 2、计算本次交易后余额，并将本次交易插入交易表；
     * 3、更新交易表中时间上在本次交易之后的交易（儿童、账户一致）的余额；
     * 4、更新账户表中账户余额
     * @param transaction
     * @return
     */
//    public static int insertTransaction(Transaction transaction, int sign) {
    public static int insertTransaction(Transaction transaction) {
        int result = 0;
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        try{
            db.beginTransaction();

            //result = insertTransaction(db, transaction, sign);
            result = insertTransaction(db, transaction);

            if(result==0) db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
            db.close();
        }
        return result;
    }

//    public static int insertTransaction(SQLiteDatabase db, Transaction transaction, int sign) {
    public static int insertTransaction(SQLiteDatabase db, Transaction transaction) {
        //  计算交易后余额
        int baseBalance = getPrevBalance(db, transaction);
        //transaction.afterBalance = baseBalance + transaction.amount * sign;
        transaction.afterBalance = baseBalance + transaction.amount;

        int result = 1;
        //  交易表中插入交易记录
        ContentValues values = new ContentValues();
        values.put(DBHelper.TransactionTable.child, transaction.child);
        values.put(DBHelper.TransactionTable.account, transaction.account);
        values.put(DBHelper.TransactionTable.category, transaction.category);
        values.put(DBHelper.TransactionTable.amount, transaction.amount);
        values.put(DBHelper.TransactionTable.after_balance, transaction.afterBalance);
        values.put(DBHelper.TransactionTable.fromto, transaction.fromto);
        values.put(DBHelper.TransactionTable.date, transaction.date.getTime());
        values.put(DBHelper.TransactionTable.notes, transaction.notes);
        long id = db.insert(DBHelper.tableNameTransaction, null, values);
        if(id>=0) {     //  insert transaction correct
            transaction.id = (int) id;
            result = 2;

            //  更新交易表中发生在交易时间之后的交易余额
            updateFollowTransactions(db, transaction, 1);

            //  更新账户表中账户余额
            updateAccountBalance(db, transaction, 1);

            result = 0;
        }

        return  result;
    }

    /**
     * 根据交易时间获取本交易的前一次交易，再获取其交易后的账户余额。用于计算本交易后的账户余额。
     * 也适用于在已有交易之前再插入交易
     * @param db
     * @param transaction
     * @return  前一次交易后的账户余额
     */
    public static int getPrevBalance(SQLiteDatabase db, Transaction transaction){
        int balance = 0;
        String sql = "select * from " + DBHelper.tableNameTransaction +
                " where " + DBHelper.TransactionTable.child + "=" + transaction.child +
                " and " + DBHelper.TransactionTable.account + "=" + transaction.account +
                " and " + DBHelper.TransactionTable.date + "<" + transaction.date.getTime() +
                " and " + DBHelper.TransactionTable._id + "!=" + transaction.id +
                " order by " + DBHelper.TransactionTable.date + " desc limit 1";
        Cursor cursor = db.rawQuery(sql, null);
        if(cursor.moveToNext()) {
            int index = cursor.getColumnIndex(DBHelper.TransactionTable.after_balance);
            balance = cursor.getInt(index);
        }
        cursor.close();
        return  balance;
    }

    /**
     * 插入/删除/更新一条交易后，更新所有交易时间在该交易之后的交易的余额。
     * @param db
     * @param transaction  amount可以有正负
     * @param sign  决定balance是加/减transaction.amount，当insertTransaction时sign=1；当deleteTransaction时sign=-1
     */
    public static void updateFollowTransactions(SQLiteDatabase db, Transaction transaction, int sign) {
        if(transaction.amount==0) return;
        int amount = transaction.amount * sign;
        String sqlOp = "+";
        if(amount<0) {
            sqlOp = "-";
            amount = 0-amount;
        }
        String sql = "update " + DBHelper.tableNameTransaction +
                " set " + DBHelper.TransactionTable.after_balance + "=" + DBHelper.TransactionTable.after_balance +
//                (sign>0 ? "+" : "-") + transaction.amount +
                sqlOp + amount +
                " where " + DBHelper.TransactionTable.child + "=" + transaction.child +
                " and " + DBHelper.TransactionTable.account + "=" + transaction.account +
                " and " + DBHelper.TransactionTable._id + "!=" + transaction.id +
                " and " + DBHelper.TransactionTable.date + ">" + transaction.date.getTime();
        db.execSQL(sql);
    }

    /**
     * 插入/删除/更新一条交易后，更新所交易账户的余额。
     * @param db
     * @param transaction
     * @param sign  决定balance是加/减transaction.amount，当insertTransaction时sign=1；当deleteTransaction时sign=-1
     */
    public static void updateAccountBalance(SQLiteDatabase db, Transaction transaction, int sign) {
        if(transaction.amount==0) return;
        int amount = transaction.amount * sign;
        String sqlOp = "+";
        if(amount<0) {
            sqlOp = "-";
            amount = 0-amount;
        }
        String sql = "update " + DBHelper.tableNameAccount +
                " set " + DBHelper.AccountTable.balance + "=" + DBHelper.AccountTable.balance +
//                (sign>0 ? "+" : "-") + transaction.amount +
                sqlOp + amount +
                " where " + DBHelper.AccountTable._id + "=" + transaction.account;
        db.execSQL(sql);
    }

    /**
     * 更新一条交易记录，相当于删除原记录+插入新纪录两个操作。但是不是真正删除记录，而是更新记录，因为_id保持不变
     * 1、按删除原记录更新后续交易和账户余额
     * 2、计算新交易的交易后余额；
     * 3、从交易表中更新本次交易记录；
     * 4、按插入新记录更新后续交易和账户余额
     * @param originalTransaction
     * @param transaction
     * @return
     */
//    public static int updateTransaction(Transaction originalTransaction, int originalSign, Transaction transaction, int sign) {
    public static int updateTransaction(Transaction originalTransaction, Transaction transaction) {
        int result = 0;
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        try{
            db.beginTransaction();
            result = 1;

            // 原交易账户下原交易时间之后的交易都按删除原交易更新
            updateFollowTransactions(db, originalTransaction,-1);
            // 更新账户表中原账户余额
            updateAccountBalance(db, originalTransaction, -1);

            result = 2;
            // 计算新的交易后余额
            int baseBalance = getPrevBalance(db, transaction);
            //transaction.afterBalance = baseBalance + transaction.amount * sign;
            transaction.afterBalance = baseBalance + transaction.amount;

            // 更新交易表中本条交易记录
            ContentValues values = new ContentValues();
            values.put(DBHelper.TransactionTable.account, transaction.account);
            values.put(DBHelper.TransactionTable.category, transaction.category);
            values.put(DBHelper.TransactionTable.amount, transaction.amount);
            values.put(DBHelper.TransactionTable.after_balance, transaction.afterBalance);
            values.put(DBHelper.TransactionTable.fromto, transaction.fromto);
            values.put(DBHelper.TransactionTable.date, transaction.date.getTime());
            values.put(DBHelper.TransactionTable.notes, transaction.notes);
            String whereClause = DBHelper.TransactionTable._id + "=" + transaction.id;
            db.update(DBHelper.tableNameTransaction, values, whereClause, null);

            result = 3;
            // 新交易账户下新交易时间之后的交易都按插入交易更新
            updateFollowTransactions(db, transaction, 1);
            // 更新账户表中新账户余额
            updateAccountBalance(db, transaction, 1);

            db.setTransactionSuccessful();
            result = 0;
        } finally {
            db.endTransaction();
            db.close();
        }
        return result;
    }

    /**
     * 删除一条交易，
     * 1、从交易表中删除本次交易；
     * 2、更新交易表中时间上在本次交易之后的交易（儿童、账户一致）的余额；
     * 3、更新账户表中账户余额
     * @param transaction
     * @return
     */
//    public static int deleteTransaction(Transaction transaction, int sign) {
    public static int deleteTransaction(Transaction transaction) {
        int result = 0;
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        try{
            db.beginTransaction();

            result = 1;
            //  从交易表中删除交易
            String whereClause = DBHelper.TransactionTable._id + "=" + transaction.id;
            int r = db.delete(DBHelper.tableNameTransaction, whereClause, null);
            if(r==1) {     //  delete transaction correct
                result = 2;
                //  更新交易表中发生在交易时间之后的交易余额
                //updateFollowTransactions(db, transaction, 0-sign);
                updateFollowTransactions(db, transaction, -1);

                //  更新账户表中账户余额
                //updateAccountBalance(db, transaction, 0-sign);
                updateAccountBalance(db, transaction, -1);

                db.setTransactionSuccessful();
                result = 0;
            }
        } finally {
            db.endTransaction();
            db.close();
        }
        return result;
    }

    public static boolean insertFromTo(FromTo fromTo) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        //values.put(DBHelper.FromToTable.child, fromTo.child);
        values.put(DBHelper.FromToTable.name, fromTo.name);
        values.put(DBHelper.FromToTable.direction, fromTo.direction);
        long id = db.insert(DBHelper.tableNameFromTo, null, values);
        db.close();

        if(id>=0) {
            fromTo.id = (int)id;
            return true;
        } else {
            return false;
        }
    }

    public static boolean updateFromTo(FromTo fromTo) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DBHelper.FromToTable.name, fromTo.name);
        values.put(DBHelper.FromToTable.direction, fromTo.direction);
        String whereClause = DBHelper.FromToTable._id + "=" + fromTo.id;
        int result = db.update(DBHelper.tableNameFromTo, values, whereClause, null);
        db.close();
        if(result==1) return true;
        return false;
    }

    public static List<Transaction> queryTransaction(int childId, int accountId, Date begin, Date end, String orderBy, boolean asc) {
        StringBuffer sql = new StringBuffer();
        sql.append("select * from " + DBHelper.tableNameTransaction + " where " + DBHelper.TransactionTable.child + "=" + childId);
        if(accountId>=0)
            sql.append(" and " + DBHelper.TransactionTable.account + "=" + accountId);
        if(begin!=null)
            sql.append(" and " + DBHelper.TransactionTable.date + ">=" + begin.getTime());
        if(end!=null)
            sql.append(" and " + DBHelper.TransactionTable.date + "<=" + end.getTime());
        if(orderBy!=null) {
            sql.append(" order by " + orderBy);
            if(!asc) sql.append(" desc");
        }

        List<Transaction> result = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(sql.toString(), null);
        int idIndex = cursor.getColumnIndex(DBHelper.TransactionTable._id);
        int childIndex = cursor.getColumnIndex(DBHelper.TransactionTable.child);
        int accountIndex = cursor.getColumnIndex(DBHelper.TransactionTable.account);
        int categoryIndex = cursor.getColumnIndex(DBHelper.TransactionTable.category);
        int amountIndex = cursor.getColumnIndex(DBHelper.TransactionTable.amount);
        int fromtoIndex = cursor.getColumnIndex(DBHelper.TransactionTable.fromto);
        int dateIndex = cursor.getColumnIndex(DBHelper.TransactionTable.date);
        int notesIndex = cursor.getColumnIndex(DBHelper.TransactionTable.notes);
        while(cursor.moveToNext()) {
            Transaction transaction = new Transaction();
            transaction.id = cursor.getInt(idIndex);
            transaction.child = cursor.getInt(childIndex);
            transaction.account = cursor.getInt(accountIndex);
            transaction.category = cursor.getInt(categoryIndex);
            transaction.amount = cursor.getInt(amountIndex);
            transaction.fromto = cursor.getInt(fromtoIndex);
            transaction.date = new Date(cursor.getLong(dateIndex));
            transaction.notes = cursor.getString(notesIndex);
            result.add(transaction);
        }
        cursor.close();

        return  result;
    }

    public static boolean insertAutomation(Automation automation) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = automation.toContentValues();
        long id = db.insert(DBHelper.tableNameAutomation, null, values);
        db.close();

        if(id>=0) {
            automation.id = (int)id;
            return true;
        } else {
            return false;
        }
    }

    public static boolean updateAutomation(Automation automation) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = automation.toContentValues();
        String whereClause = DBHelper.AutomationTable._id + "=" + automation.id;
        int result = db.update(DBHelper.tableNameAutomation, values, whereClause, null);
        db.close();
        if(result==1) return true;
        return false;
    }

    public static boolean deleteAutomation(int automationId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String whereClause = DBHelper.AutomationTable._id + "=" + automationId;
        int result = db.delete(DBHelper.tableNameAutomation, whereClause, null);
        db.close();
        if(result==1) return true;
        return false;
    }

    public static boolean deleteAllAutomation() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int result = db.delete(DBHelper.tableNameAutomation, null, null);
        db.close();
        return true;
    }

    public static void executeAutomations(int lastExecuteDate, int today) {
        if(today<=lastExecuteDate) return;

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        List<Automation> automationList = new ArrayList<>();
        loadAutomationFromTable(db, automationList);

        Calendar cal = new GregorianCalendar(IntDate.getYear(lastExecuteDate), IntDate.getMonth(lastExecuteDate)-1, IntDate.getDay(lastExecuteDate),0,0,0);
        cal.add(Calendar.DATE, 1);
        int date = IntDate.YMDToInt(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH)+1, cal.get(Calendar.DAY_OF_MONTH));
        while(date<=today) {
            Iterator<Automation> iter = automationList.iterator();
            while(iter.hasNext()) {
                Automation automation = iter.next();
                executeAutomation(automation, date, db);
            }

            cal.add(Calendar.DATE, 1);
            date = IntDate.YMDToInt(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH)+1, cal.get(Calendar.DAY_OF_MONTH));
        }

        db.close();
    }

    private static void executeAutomation(Automation automation, int date, SQLiteDatabase db) {
        //  若不符合执行日期，则返回
        if(automation.period ==0) {   //  按年
            if(date % 10000 != automation.date) return;
        } else if(automation.period ==1)  {   //  按月
            if(date % 100 != automation.date) return;
        } else {
            return;
        }

        Account account1 = null;
        Account account2 = null;
        // 读取账户信息
        String sql = "select * from " + DBHelper.tableNameAccount;
        Cursor cursor = db.rawQuery(sql, null);
        int idIndex = cursor.getColumnIndex(DBHelper.AccountTable._id);
        int childIndex = cursor.getColumnIndex(DBHelper.AccountTable.child);
        int nameIndex = cursor.getColumnIndex(DBHelper.AccountTable.name);
        int balanceIndex = cursor.getColumnIndex(DBHelper.AccountTable.balance);
        while(cursor.moveToNext()) {
            Account account = new Account();
            account.id = cursor.getInt(idIndex);
            account.child = cursor.getInt(childIndex);
            account.name = cursor.getString(nameIndex);
            account.balance = cursor.getInt(balanceIndex);
            if(account.id == automation.account1) account1 = account;
            if(account.id == automation.account2) account2 = account;
        }
        cursor.close();

        if(account1==null || account2==null) return;

        Transaction transaction = new Transaction();
        transaction.date = IntDate.getDate(date);
        // 符合执行日期，则执行
        if(automation.type == 1) {  //  结息，只在利息存入账户生成一条Transaction
            int balance = account1.balance;
            int interest = Math.round(balance * automation.float_data1 / 100);  //  年利息
            if(automation.period ==1) {   //  按月计息需除以12
                interest = interest / 12;
            }
            transaction.child = account2.child;
            transaction.account = automation.account2;
            transaction.amount = interest;      //  amount可以为正数或负数
            transaction.category = automation.account2Category;
            transaction.fromto = automation.account2FromTo;
            transaction.notes = "";
            insertTransaction(db, transaction);   //  transaction的afterbalance以及account2的balance都会一起更新
        } else if(automation.type == 2) {   //  取零花钱，在存入账户生成一条Transaction；如果有取出账户，在取出账户生成一条Transaction
            //  取款交易
            transaction.child = account1.child;
            transaction.account = automation.account1;
            transaction.amount = 0 - automation.int_data1;  //  取款时amount为负数
            transaction.category = automation.account1Category;
            transaction.fromto = automation.account1FromTo;
            transaction.notes = "";
            insertTransaction(db, transaction);   //  transaction的afterbalance以及account1的balance都会一起更新

            //  存款交易
            transaction.child = account2.child;
            transaction.account = automation.account2;
            transaction.amount = automation.int_data1;      //  存款时amount为正数
            transaction.category = automation.account2Category;
            transaction.fromto = automation.account2FromTo;
            transaction.notes = "";
            insertTransaction(db, transaction);   //  transaction的afterbalance以及account2的balance都会一起更新
        }
    }

}

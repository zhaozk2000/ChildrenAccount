package com.zzk.childrenbank.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.Date;
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

        //  计算每个Child的总余额，载入最近交易列表
        Iterator<Child> iter = model.childList.iterator();
        while(iter.hasNext()) {
            Child child = iter.next();
            child.calculateTotalBalance();

            // 读取Transaction表
            sql = "select * from " + DBHelper.tableNameTransaction + " where " + DBHelper.TransactionTable.child + "=" + child.id
                    + " order by _id desc limit " + Child.TRANSACTION_BUFFER_SIZE;
            cursor = db.rawQuery(sql, null);
            idIndex = cursor.getColumnIndex(DBHelper.TransactionTable._id);
            childIndex = cursor.getColumnIndex(DBHelper.TransactionTable.child);
            accountIndex = cursor.getColumnIndex(DBHelper.TransactionTable.account);
            int categoryIndex = cursor.getColumnIndex(DBHelper.TransactionTable.category);
            int amountIndex = cursor.getColumnIndex(DBHelper.TransactionTable.amount);
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
                transaction.fromto = cursor.getInt(fromtoIndex);
                transaction.date = new Date(cursor.getLong(dateIndex));
                transaction.notes = cursor.getString(notesIndex);
                child.transactionList.add(transaction);
            }
            cursor.close();

            // 读取FromTo表
            sql = "select * from " + DBHelper.tableNameFromTo + " where " + DBHelper.FromToTable.child + "=" + child.id
                    + " order by _id desc";
            cursor = db.rawQuery(sql, null);
            idIndex = cursor.getColumnIndex(DBHelper.FromToTable._id);
            childIndex = cursor.getColumnIndex(DBHelper.FromToTable.child);
            nameIndex = cursor.getColumnIndex(DBHelper.FromToTable.name);
            fromtoIndex = cursor.getColumnIndex(DBHelper.FromToTable.fromto);
            while(cursor.moveToNext()) {
                FromTo fromTo = new FromTo();
                fromTo.id = cursor.getInt(idIndex);
                fromTo.child = cursor.getInt(childIndex);
                fromTo.name = cursor.getString(nameIndex);
                fromTo.fromto = (byte)cursor.getInt(fromtoIndex);
                if(fromTo.fromto==1) child.fromList.add(fromTo);
                else child.toList.add(fromTo);
            }
            cursor.close();

        }

        db.close();
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

    public static int insertTransaction(Transaction transaction, int afterBalance) {
        int result = 0;
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        try{
            db.beginTransaction();

            result = 1;
            ContentValues values = new ContentValues();
            values.put(DBHelper.TransactionTable.child, transaction.child);
            values.put(DBHelper.TransactionTable.account, transaction.account);
            values.put(DBHelper.TransactionTable.category, transaction.category);
            values.put(DBHelper.TransactionTable.amount, transaction.amount);
            values.put(DBHelper.TransactionTable.fromto, transaction.fromto);
            values.put(DBHelper.TransactionTable.date, transaction.date.getTime());
            values.put(DBHelper.TransactionTable.notes, transaction.notes);
            long id = db.insert(DBHelper.tableNameTransaction, null, values);
            if(id>=0) {     //  insert transaction correct
                transaction.id = (int) id;

                result = 2;
                ContentValues valuesAccount = new ContentValues();
                valuesAccount.put(DBHelper.AccountTable.balance, afterBalance);
                String whereClause = DBHelper.AccountTable._id + "=" + transaction.account;
                db.update(DBHelper.tableNameAccount, valuesAccount, whereClause, null);

                db.setTransactionSuccessful();
                result = 0;
            }
        } finally {
            db.endTransaction();
            db.close();
        }
        return result;
/*
        ContentValues values = new ContentValues();
        values.put(DBHelper.TransactionTable.child, transaction.child);
        values.put(DBHelper.TransactionTable.account, transaction.account);
        values.put(DBHelper.TransactionTable.category, transaction.category);
        values.put(DBHelper.TransactionTable.amount, transaction.amount);
        values.put(DBHelper.TransactionTable.date, transaction.date.getTime());
        values.put(DBHelper.TransactionTable.notes, transaction.notes);
        long id = db.insert(DBHelper.tableNameTransaction, null, values);
        if(id>=0) {     //  insert transaction correct
            transaction.id = (int) id;
            ContentValues valuesAccount = new ContentValues();
            valuesAccount.put(DBHelper.AccountTable.balance, afterBalance);
            String whereClause = DBHelper.AccountTable._id + "=" + transaction.account;
            int result = db.update(DBHelper.tableNameAccount, valuesAccount, whereClause, null);
            db.close();
            if(result==1) return 0;     // update account balance correct

            whereClause = DBHelper.TransactionTable._id + "=" + transaction.id;     // delete the transaction
            db.delete(DBHelper.tableNameTransaction, whereClause, null);
            return 2;   // update account balance error
        } else {        //  insert transaction error
            db.close();
            return 1;
        }
 */
    }

    public static int updateTransaction(Transaction transaction, int originAccountId, int originBalance, int accountId, int balance) {
        int result = 0;
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        try{
            db.beginTransaction();

            result = 1;
            ContentValues values = new ContentValues();
            //values.put(DBHelper.TransactionTable.child, transaction.child);
            values.put(DBHelper.TransactionTable.account, transaction.account);
            values.put(DBHelper.TransactionTable.category, transaction.category);
            values.put(DBHelper.TransactionTable.amount, transaction.amount);
            values.put(DBHelper.TransactionTable.fromto, transaction.fromto);
            values.put(DBHelper.TransactionTable.date, transaction.date.getTime());
            values.put(DBHelper.TransactionTable.notes, transaction.notes);
            String whereClause = DBHelper.TransactionTable._id + "=" + transaction.id;
            db.update(DBHelper.tableNameTransaction, values, whereClause, null);

            result = 2;
            ContentValues valuesAccount = new ContentValues();
            valuesAccount.put(DBHelper.AccountTable.balance, balance);
            whereClause = DBHelper.AccountTable._id + "=" + accountId;
            db.update(DBHelper.tableNameAccount, valuesAccount, whereClause, null);

            result = 3;
            if(originAccountId!=accountId) {
                valuesAccount.clear();
                valuesAccount.put(DBHelper.AccountTable.balance, originBalance);
                whereClause = DBHelper.AccountTable._id + "=" + originAccountId;
                db.update(DBHelper.tableNameAccount, valuesAccount, whereClause, null);
            }

            db.setTransactionSuccessful();
            result = 0;
        } finally {
            db.endTransaction();
            db.close();
        }
        return result;
    }

    public static int deleteTransaction(Transaction transaction, int balance) {
        int result = 0;
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        try{
            db.beginTransaction();

            result = 1;
            String whereClause = DBHelper.TransactionTable._id + "=" + transaction.id;
            int r = db.delete(DBHelper.tableNameTransaction, whereClause, null);
            if(r==1) {     //  insert transaction correct
                result = 2;
                ContentValues valuesAccount = new ContentValues();
                valuesAccount.put(DBHelper.AccountTable.balance, balance);
                whereClause = DBHelper.AccountTable._id + "=" + transaction.account;
                db.update(DBHelper.tableNameAccount, valuesAccount, whereClause, null);

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
        values.put(DBHelper.FromToTable.child, fromTo.child);
        values.put(DBHelper.FromToTable.name, fromTo.name);
        values.put(DBHelper.FromToTable.fromto, fromTo.fromto);
        long id = db.insert(DBHelper.tableNameFromTo, null, values);
        db.close();

        if(id>=0) {
            fromTo.id = (int)id;
            return true;
        } else {
            return false;
        }
    }

    public static boolean updateFromTo(int fromtoId, String name) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DBHelper.FromToTable.name, name);
        String whereClause = DBHelper.FromToTable._id + "=" + fromtoId;
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
}

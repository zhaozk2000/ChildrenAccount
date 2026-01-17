package com.zzk.childrenbank.data;

import android.os.Bundle;

import java.util.Date;

public class Transaction {
    public int id;
    public int child;
    public int account;
    public int amount;      //  余额balance单位为分，不管支出收入都取正值
    public int afterBalance;    //  交易后账户余额
    public int category;
    public int fromto;      //  来源 / 去向
    public Date date;
    public String notes;

    public Transaction(){
        id = -1;
        child = -1;
        account = -1;
        amount = 0;
        afterBalance = 0;
        category = -1;
        fromto = 1;
        date = new Date();
        notes = "";
    }

    public Bundle toBundle() {
        Bundle bundle = new Bundle();
        bundle.putInt("id", id);
        bundle.putInt("child", child);
        bundle.putInt("account", account);
        bundle.putInt("amount", amount);
        bundle.putInt("afterBalance", afterBalance);
        bundle.putInt("category", category);
        bundle.putInt("fromto", fromto);
        bundle.putLong("date", date.getTime());
        bundle.putString("notes", notes);
        return bundle;
    }

    public static Transaction fromBundle(Bundle bundle){
        Transaction transaction = new Transaction();
        transaction.id = bundle.getInt("id");
        transaction.child = bundle.getInt("child");
        transaction.account = bundle.getInt("account");
        transaction.amount = bundle.getInt("amount");
        transaction.afterBalance = bundle.getInt("afterBalance");
        transaction.category = bundle.getInt("category");
        transaction.fromto = bundle.getInt("fromto");
        transaction.date = new Date(bundle.getLong("date"));
        transaction.notes = bundle.getString("notes");
        return transaction;
    }

    public void copyFrom(Transaction transaction) {
        id = transaction.id;
        child = transaction.child;
        account = transaction.account;
        amount = transaction.amount;
        afterBalance = transaction.afterBalance;
        category = transaction.category;
        fromto = transaction.fromto;
        date = transaction.date;
        notes = transaction.notes;
    }
}

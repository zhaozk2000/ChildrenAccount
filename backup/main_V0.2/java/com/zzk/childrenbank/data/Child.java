package com.zzk.childrenbank.data;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

public class Child {
    public int id;
    public String name;
    public Date createTime;
    public List<Account> accountList;
    public int totalBalance;
    public List<Transaction> transactionList;
    public List<FromTo> fromList;
    public List<FromTo> toList;

    public static final int TRANSACTION_BUFFER_SIZE = 128;

    public Child(){
        accountList = new ArrayList();
        transactionList = new ArrayList<>(TRANSACTION_BUFFER_SIZE);
        fromList = new ArrayList<>();
        toList = new ArrayList<>();
    }

    public void calculateTotalBalance(){
        totalBalance = 0;
        Iterator<Account> iter = accountList.iterator();
        while(iter.hasNext()) {
            Account account = iter.next();
            totalBalance += account.balance;
        }
    }

    public Account findAccountById(int accountId) {
        Iterator<Account> iter = accountList.iterator();
        while(iter.hasNext()) {
            Account account = iter.next();
            if(account.id == accountId) return account;
        }
        return null;
    }

    public Account findAccountByName(String accountName) {
        Iterator<Account> iter = accountList.iterator();
        while(iter.hasNext()) {
            Account account = iter.next();
            if(account.name.equals(accountName)) return account;
        }
        return null;
    }

    public Category findCategoryById(int categoryId) {
        Iterator<Account> iter = accountList.iterator();
        while(iter.hasNext()) {
            Account account = iter.next();
            Category category = account.findCategoryById(categoryId);
            if(category!=null) return category;
        }
        return null;
    }

    public List<Category> getCategoryList(){
        List<Category> result = new ArrayList<>();
        Iterator<Account> iter = accountList.iterator();
        while(iter.hasNext()) {
            Account account = iter.next();
            result.addAll(account.categoryList);
        }
        return result;
    }

    public Transaction findTransactionById(int transactionId) {
        Iterator<Transaction> iter = transactionList.iterator();
        while(iter.hasNext()) {
            Transaction transaction = iter.next();
            if(transaction.id==transactionId) return transaction;
        }
        return null;
    }

    public void deleteTransaction(Transaction transaction) {
        Iterator<Transaction> iter = transactionList.iterator();
        while(iter.hasNext()) {
            Transaction t = iter.next();
            if(t.id==transaction.id) {
                iter.remove();
                return;
            };
        }
    }

    public FromTo findFromToById(int fromtoId) {
        Iterator<FromTo> iter = fromList.iterator();
        while (iter.hasNext()) {
            FromTo fromTo = iter.next();
            if(fromTo.id == fromtoId) return fromTo;
        }
        iter = toList.iterator();
        while(iter.hasNext()) {
            FromTo fromTo = iter.next();
            if(fromTo.id == fromtoId) return  fromTo;
        }
        return null;
    }

    public List<FromTo> getFromToList(int fromToFlag) {
        if(fromToFlag==1) return fromList;
        else if(fromToFlag==2) return toList;
        else return null;
    }

    public FromTo findFromToByName(String fromToName, int fromToFlag) {
        Iterator<FromTo> iter = getFromToList(fromToFlag).iterator();
        while (iter.hasNext()) {
            FromTo fromTo = iter.next();
            if(fromTo.name.equals(fromToName)) return fromTo;
        }
        return null;
    }

    public List<FromTo> getFromToList() {
        List<FromTo> result = new ArrayList<>();
        result.addAll(fromList);
        result.addAll(toList);
        return result;
    }

    public List<Category> findRecentCategories(int sign) {
        List<Category> result = new ArrayList<>();
        Iterator<Transaction> iter = transactionList.iterator();
        while(iter.hasNext()) {
            Transaction transaction = iter.next();
            Category category = findCategoryById(transaction.category);
            if(sign==0 || category.sign == sign) { //  +1, -1 表示增减
                if(!result.contains(category)) {
                    result.add(category);
                }
            }
        }
        return  result;
    }
}

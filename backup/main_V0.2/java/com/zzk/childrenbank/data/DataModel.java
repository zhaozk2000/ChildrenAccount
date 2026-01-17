package com.zzk.childrenbank.data;

import android.content.Context;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

public class DataModel {
    static DataModel dataModel = null;
    public static DataModel getDataModel(Context context){
        if(dataModel == null) {
            dataModel = new DataModel(context);
            DAO.loadDataFromDB(dataModel);
        }
        return dataModel;
    }


    public List<Child> childList;
    DBHelper dbHelper;

    public DataModel(Context context){
        childList = new ArrayList<>();
        dbHelper = new DBHelper(context);
    }

    public Child findChildById(int id) {
        Iterator<Child> iter = childList.iterator();
        while(iter.hasNext()) {
            Child child = iter.next();
            if(child.id == id) return child;
        }
        return null;
    }

    public Child findChildByName(String name) {
        Iterator<Child> iter = childList.iterator();
        while(iter.hasNext()) {
            Child child = iter.next();
            if(child.name.equals(name)) return child;
        }
        return null;
    }

    public int createChild(String name) {
        Child child = findChildByName(name);
        if(child!=null) return 1;

        child = new Child();
        child.name = name;
        child.createTime = new Date();
        child.totalBalance = 0;

        boolean result = DAO.insertChild(child);
        if(result) {
            childList.add(child);
        } else {
            return 2;
        }

        return 0;
    }

    public int deleteChild(String name) {
        boolean result = DAO.deleteChild(name);
        if(result) {
            Iterator<Child> iter = childList.iterator();
            while(iter.hasNext()) {
                Child child = iter.next();
                if(child.name.equals(name)) {
                    iter.remove();
                    break;
                }
            }
            return 0;
        } else {
            return 1;
        }
    }

    public int updateChild(int id, String name) {
        Child child = findChildById(id);
        if(child.name.equals(name)) return 0;   // 新姓名与旧姓名相同
        boolean result = DAO.updateChild(id, name);
        if (result) {
            child.name = name;
            return 0;
        } else {
            return 1;
        }
    }

    public int deleteAllChild() {
        boolean result = DAO.deleteAllChild();
        if (result) {
            childList.clear();
            return 0;
        } else {
            return 1;
        }
    }

    public Account findAccountById(int childId, int acccountId) {
        Child child = findChildById(childId);
        if(child==null) return null;
        Account account = child.findAccountById(acccountId);
        return account;
    }

    public Account findAccountByName(int childId, String acccountName) {
        Child child = findChildById(childId);
        if(child==null) return null;
        Account account = child.findAccountByName(acccountName);
        return account;
    }

    public int createAccount(int childId, String accountName) {
        Child child = findChildById(childId);
        if(child==null) return 2;   //  儿童不存在

        Account account = child.findAccountByName(accountName);
        if(account!=null) return 1; //  账户名称已经存在

        account = new Account();
        account.child = childId;
        account.name = accountName;
        account.balance = 0;

        boolean result = DAO.insertAccount(account);
        if(result) {
            child.accountList.add(account);
        } else {
            return 3;
        }

        return 0;
    }

    public int deleteAccount(int childId, String name) {
        boolean result = DAO.deleteAccount(childId, name);
        if(result) {
            Child child = findChildById(childId);
            Iterator<Account> iter = child.accountList.iterator();
            while(iter.hasNext()) {
                Account account = iter.next();
                if(account.name.equals(name)) {
                    iter.remove();
                    break;
                }
            }
            return 0;
        } else {
            return 1;
        }
    }

    public int updateAccount(int childId, int accountId, String name) {
        Account account = findAccountById(childId, accountId);
        if(account==null) return 2; //  账户不存在

        if(account.name.equals(name)) return 0;   // 新账户名与旧账户名相同
        boolean result = DAO.updateAccount(childId, accountId, name);
        if (result) {
            account.name = name;
            return 0;
        } else {
            return 1;
        }
    }

    public int deleteAllAccounts(int childId) {
        boolean result = DAO.deleteAllAccounts(childId);
        if (result) {
            Child child = findChildById(childId);
            if(child!=null) {
                child.accountList.clear();
            }
            return 0;
        } else {
            return 1;
        }
    }

    public Category findCategoryById(int categoryId) {
        Iterator<Child> iter = childList.iterator();
        while(iter.hasNext()) {
            Child child = iter.next();
            Category category = child.findCategoryById(categoryId);
            if(category!=null) return category;
        }
        return null;
    }

    public Category findCategoryById(int childId, int accountId, int categoryId) {
        Child child = findChildById(childId);
        if(child==null) return null;
        Account account = child.findAccountById(accountId);
        if(account==null) return null;
        return account.findCategoryById(categoryId);
    }

    public int createCategory(Category category) {
        Account account = findAccountById(category.child, category.account);
        if(account==null) return 1; //  账户不存在

        boolean result = DAO.insertCategory(category);
        if(result) {
            account.categoryList.add(category);
        } else {
            return 2;
        }

        return 0;
    }

    public int updateCategory(Category category) {
        Account account = findAccountById(category.child, category.account);
        if(account==null) return 1; //  账户不存在

        boolean result = DAO.updateCategory(category);
        if (result) {
            Category origin = account.findCategoryById(category.id);
            origin.copyFrom(category);
            return 0;
        } else {
            return 1;
        }
    }

    public int createTransaction(Transaction transaction) {
        Child child = findChildById(transaction.child);
        Account account = child.findAccountById(transaction.account);
        Category category = account.findCategoryById(transaction.category);
        int balance = account.balance + transaction.amount * category.sign;
        int result = DAO.insertTransaction(transaction, balance);

        if(result != 0) return result;

        account.balance = balance;
        child.calculateTotalBalance();
        child.transactionList.add(0, transaction);
        return 0;
    }

    public int updateTransaction(Transaction transaction) {
        Child child = findChildById(transaction.child);

        Transaction originTransaction = child.findTransactionById(transaction.id);
        Account originAccount = child.findAccountById(originTransaction.account);
        Category originCategory = child.findCategoryById(originTransaction.category);
        int originBalance = originAccount.balance - originTransaction.amount * originCategory.sign;

        Account account = child.findAccountById(transaction.account);
        Category category = account.findCategoryById(transaction.category);
        int balance = account.balance;
        if(account.id==originAccount.id) balance = originBalance;
        balance += transaction.amount * category.sign;

        int result = DAO.updateTransaction(transaction, originAccount.id, originBalance, account.id, balance);

        if(result != 0) return result;

        originTransaction.copyFrom(transaction);
        account.balance = balance;
        if(originAccount.id!=account.id) {
            originAccount.balance = originBalance;
        }
        child.calculateTotalBalance();
        return 0;
    }

    public int deleteTransaction(int childId, int transactionId) {
        Child child = findChildById(childId);
        Transaction transaction = child.findTransactionById(transactionId);
        Account account = child.findAccountById(transaction.account);
        Category category = account.findCategoryById(transaction.category);
        int balance = account.balance - transaction.amount * category.sign;
        int result = DAO.deleteTransaction(transaction, balance);

        if(result != 0) return result;

        child.deleteTransaction(transaction);
        account.balance = balance;
        child.calculateTotalBalance();

        return 0;
    }

    public int createFromTo(int childId, String fromToName, int fromToFlag) {
        Child child = findChildById(childId);
        if(child==null) return 2;   //  儿童不存在

        FromTo fromTo = child.findFromToByName(fromToName, fromToFlag);
        if(fromTo!=null) return 1; //  账户名称已经存在

        fromTo = new FromTo();
        fromTo.child = childId;
        fromTo.name = fromToName;
        fromTo.fromto = fromToFlag;

        boolean result = DAO.insertFromTo(fromTo);
        if(result) {
            child.getFromToList(fromToFlag).add(fromTo);
        } else {
            return 3;
        }

        return 0;
    }

    public int updateFromTo(int childId, int fromtoId, String name) {
        Child child = findChildById(childId);
        if(child==null) return 2;   //  儿童不存在

        FromTo fromTo = child.findFromToById(fromtoId);
        if(fromTo==null) return 3;  //  来源去向不存在

        if(fromTo.name.equals(name)) return 4;  //  名字没有变化

        boolean result = DAO.updateFromTo(fromtoId, name);
        if (result) {
            fromTo.name = name;
            return 0;
        } else {
            return 1;
        }
    }
}

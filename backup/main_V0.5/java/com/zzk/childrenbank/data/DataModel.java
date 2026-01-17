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
    public List<Automation> automationList;
    public List<FromTo> fromToList;
    public List<FromTo> fromList;
    public List<FromTo> toList;
    DBHelper dbHelper;

    public DataModel(Context context){
        childList = new ArrayList<>();
        automationList = new ArrayList<>();
        fromToList = new ArrayList<>();
        fromList = new ArrayList<>();
        toList = new ArrayList<>();
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

    /**
     * 插入一条交易，先操作数据库数据，成功后再操作内存中数据
     * 操作包括：计算交易后余额、插入交易、更新时间顺序之后的所有交易的余额、更新账户余额
     * @param transaction
     * @return
     */
    public int createTransaction(Transaction transaction) {
        Child child = findChildById(transaction.child);
        Account account = child.findAccountById(transaction.account);
//        Category category = account.findCategoryById(transaction.category);

        //  数据库操作
//        int result = DAO.insertTransaction(transaction, category.sign);
        int result = DAO.insertTransaction(transaction);
        if(result != 0) return result;

        //  更新内存中交易列表，和数据库一致
//        child.insertTransaction(transaction, category.sign);
        child.insertTransaction(transaction);

        //  更新内存中账户余额
//        account.balance = account.balance + transaction.amount * category.sign;
        account.balance = account.balance + transaction.amount;
        child.calculateTotalBalance();
        return 0;
    }

    /**
     * 更新一条交易，先操作数据库数据，成功后再操作内存中数据
     * 操作包括：相当于先删除就交易记录，再插入新交易记录。
     * 但为了保持交易_id不变，不实际删除数据表中记录，而是更新。其他相关交易记录和账户表余额都按删除就交易+插入新交易操作
     * @param transaction
     * @return
     */
    public int updateTransaction(Transaction transaction) {
        Child child = findChildById(transaction.child);

        Transaction originTransaction = child.findTransactionById(transaction.id);
        Account originAccount = child.findAccountById(originTransaction.account);
//        Category originCategory = child.findCategoryById(originTransaction.category);
        //int originBalance = originAccount.balance - originTransaction.amount * originCategory.sign;

        Account account = child.findAccountById(transaction.account);
//        Category category = account.findCategoryById(transaction.category);

//        int result = DAO.updateTransaction(originTransaction, originCategory.sign, transaction, category.sign);
        int result = DAO.updateTransaction(originTransaction, transaction);
        if(result != 0) return result;

        //  更新内存中交易列表
//        child.deleteTransaction(originTransaction, originCategory.sign);
//        child.insertTransaction(transaction, category.sign);
        child.deleteTransaction(originTransaction);
        child.insertTransaction(transaction);

        //  更新账户余额
//        originAccount.balance = originAccount.balance - originTransaction.amount * originCategory.sign;
//        account.balance = account.balance + transaction.amount * category.sign;
        originAccount.balance = originAccount.balance - originTransaction.amount;
        account.balance = account.balance + transaction.amount;

        child.calculateTotalBalance();
        return 0;
    }

    /**
     * 删除一条交易，先操作数据库数据，成功后再操作内存中数据
     * 操作包括：删除交易、更新时间顺序之后的所有交易的余额、更新账户余额
     * @param childId
     * @param transactionId
     * @return
     */
    public int deleteTransaction(int childId, int transactionId) {
        Child child = findChildById(childId);
        Transaction transaction = child.findTransactionById(transactionId);
        Account account = child.findAccountById(transaction.account);
        Category category = account.findCategoryById(transaction.category);

        //  进行数据库操作
//        int result = DAO.deleteTransaction(transaction, category.sign);
        int result = DAO.deleteTransaction(transaction);
        if(result != 0) return result;

        //  更新内存中交易列表
//        child.deleteTransaction(transaction, category.sign);
        child.deleteTransaction(transaction);

        //  更新内存中账户余额
//        account.balance = account.balance - transaction.amount * category.sign;
        account.balance = account.balance - transaction.amount;
        child.calculateTotalBalance();

        return 0;
    }

    public int createFromTo(FromTo fromTo) {
        FromTo exist = findFromToByName(fromTo.name);
        if(exist!=null) return 1; //  往来名称已经存在

        boolean result = DAO.insertFromTo(fromTo);
        if(result) {
            fromToList.add(fromTo);
            if(fromTo.direction==1 || fromTo.direction==3) fromList.add(fromTo);
            if(fromTo.direction==2 || fromTo.direction==3) toList.add(fromTo);
        } else {
            return 3;
        }

        return 0;
    }

    public int updateFromTo(FromTo fromTo) {
/*
        Child child = findChildById(childId);
        if(child==null) return 2;   //  儿童不存在
*/
        FromTo exist = findFromToById(fromTo.id);
        if(exist==null) return 3;  //  往来对象不存在

        if(fromTo.equals(exist)) return 4;  //  数据没有变化

        boolean result = DAO.updateFromTo(fromTo);
        if (result) {
            if(exist.direction==1) {
                if(fromTo.direction==2) {
                    removeFromTo(exist);
                    toList.add(exist);
                } else if(fromTo.direction==3) {
                    toList.add(exist);
                }
            } else if(exist.direction==2) {
                if(fromTo.direction==1) {
                    removeFromTo(exist);
                    fromList.add(exist);
                } else if(fromTo.direction==3) {
                    fromList.add(exist);
                }
            } else if(exist.direction==3) {
                if(fromTo.direction==1) {
                    exist.direction=2;
                    removeFromTo(exist);
                } else if(fromTo.direction==2) {
                    exist.direction=1;
                    removeFromTo(exist);
                }
            }
            exist.copyFrom(fromTo);
            return 0;
        } else {
            return 1;
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

    public void removeFromTo(FromTo fromTo) {
        List<FromTo> list = getFromToList(fromTo.direction);
        Iterator<FromTo> iter = list.iterator();
        while(iter.hasNext()) {
            FromTo item = iter.next();
            if(item.equals(fromTo)) {
                iter.remove();
                break;
            }
        }
    }

    public List<FromTo> getFromToList(int direction) {
        if(direction==1) return fromList;
        else if(direction==2) return toList;
        else if(direction==3) return fromToList;
        else return null;
    }

    public FromTo findFromToByName(String fromToName) {
        Iterator<FromTo> iter = fromToList.iterator();
        while (iter.hasNext()) {
            FromTo fromTo = iter.next();
            if(fromTo.name.equals(fromToName)) return fromTo;
        }
        return null;
    }
/*
    public List<FromTo> getFromToList() {
        List<FromTo> result = new ArrayList<>();
        result.addAll(fromList);
        result.addAll(toList);
        return result;
    }
*/


    public String getAccountString(int accountId) {
        Child child = findChildByAccount(accountId);
        Account account = child.findAccountById(accountId);
        return "<" + child.name + ">的<" + account.name + ">";
    }

    public Child findChildByAccount(int accountId) {
        Iterator<Child> iter = childList.iterator();
        while(iter.hasNext()) {
            Child child = iter.next();
            Account account = child.findAccountById(accountId);
            if(account!=null) return child;
        }
        return null;
    }

    public int createAutomation(Automation automation) {
        boolean result = DAO.insertAutomation(automation);
        if(result) {
            automationList.add(automation);
        } else {
            return 1;
        }
        return 0;
    }

    public Automation findAutomationById(int automationId) {
        Iterator<Automation> iter = automationList.iterator();
        while(iter.hasNext()) {
            Automation automation = iter.next();
            if(automation.id == automationId) {
                return automation;
            }
        }
        return null;
    }

    public int updateAutomation(Automation automation) {
        boolean result = DAO.updateAutomation(automation);
        if (result) {
            Automation origin = findAutomationById(automation.id);
            origin.copyFrom(automation);
            return 0;
        } else {
            return 1;
        }
    }

    public int deleteAutomation(int automationId) {
        boolean result = DAO.deleteAutomation(automationId);
        if(result) {
            Iterator<Automation> iter = automationList.iterator();
            while(iter.hasNext()) {
                Automation automation = iter.next();
                if(automation.id == automationId) {
                    iter.remove();
                    break;
                }
            }
            return 0;
        } else {
            return 1;
        }
    }

    public int deleteAllAutomation() {
        boolean result = DAO.deleteAllAutomation();
        if (result) {
            automationList.clear();
            return 0;
        } else {
            return 1;
        }
    }
}

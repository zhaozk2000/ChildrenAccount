package com.zzk.familybank.data;

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

    public List<Holder> holderList;
    public List<Automation> automationList;
    public List<FromTo> fromToList;
    public List<FromTo> fromList;
    public List<FromTo> toList;
    public int lastExecuteDate;
    DBHelper dbHelper;

    public DataModel(Context context){
        holderList = new ArrayList<>();
        automationList = new ArrayList<>();
        fromToList = new ArrayList<>();
        fromList = new ArrayList<>();
        toList = new ArrayList<>();
        dbHelper = new DBHelper(context);
    }

    public Holder findHolderById(int id) {
        Iterator<Holder> iter = holderList.iterator();
        while(iter.hasNext()) {
            Holder holder = iter.next();
            if(holder.id == id) return holder;
        }
        return null;
    }

    public Holder findHolderByName(String name) {
        Iterator<Holder> iter = holderList.iterator();
        while(iter.hasNext()) {
            Holder holder = iter.next();
            if(holder.name.equals(name)) return holder;
        }
        return null;
    }

    public int createHolder(String name) {
        Holder holder = findHolderByName(name);
        if(holder !=null) return 1;

        holder = new Holder();
        holder.name = name;
        holder.createTime = new Date();
        holder.totalBalance = 0;

        boolean result = DAO.insertHolder(holder);
        if(result) {
            holderList.add(holder);
        } else {
            return 2;
        }

        return 0;
    }

    public int deleteHolder(String name) {
        boolean result = DAO.deleteHolder(name);
        if(result) {
            Iterator<Holder> iter = holderList.iterator();
            while(iter.hasNext()) {
                Holder holder = iter.next();
                if(holder.name.equals(name)) {
                    iter.remove();
                    break;
                }
            }
            return 0;
        } else {
            return 1;
        }
    }

    public int updateHolder(int id, String name) {
        Holder holder = findHolderById(id);
        if(holder.name.equals(name)) return 0;   // 新姓名与旧姓名相同
        boolean result = DAO.updateHolder(id, name);
        if (result) {
            holder.name = name;
            return 0;
        } else {
            return 1;
        }
    }

    public int deleteAllHolder() {
        boolean result = DAO.deleteAllHolder();
        if (result) {
            holderList.clear();
            return 0;
        } else {
            return 1;
        }
    }

    public Account findAccountById(int childId, int acccountId) {
        Holder holder = findHolderById(childId);
        if(holder ==null) return null;
        Account account = holder.findAccountById(acccountId);
        return account;
    }

    public Account findAccountByName(int childId, String acccountName) {
        Holder holder = findHolderById(childId);
        if(holder ==null) return null;
        Account account = holder.findAccountByName(acccountName);
        return account;
    }

    public int createAccount(int childId, String accountName) {
        Holder holder = findHolderById(childId);
        if(holder ==null) return 2;   //  儿童不存在

        Account account = holder.findAccountByName(accountName);
        if(account!=null) return 1; //  账户名称已经存在

        account = new Account();
        account.holder = childId;
        account.name = accountName;
        account.balance = 0;

        boolean result = DAO.insertAccount(account);
        if(result) {
            holder.accountList.add(account);
        } else {
            return 3;
        }

        return 0;
    }

    public int deleteAccount(int childId, String name) {
        boolean result = DAO.deleteAccount(childId, name);
        if(result) {
            Holder holder = findHolderById(childId);
            Iterator<Account> iter = holder.accountList.iterator();
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
            Holder holder = findHolderById(childId);
            if(holder !=null) {
                holder.accountList.clear();
            }
            return 0;
        } else {
            return 1;
        }
    }

    public Category findCategoryById(int categoryId) {
        Iterator<Holder> iter = holderList.iterator();
        while(iter.hasNext()) {
            Holder holder = iter.next();
            Category category = holder.findCategoryById(categoryId);
            if(category!=null) return category;
        }
        return null;
    }

    public Category findCategoryById(int holderId, int accountId, int categoryId) {
        Holder holder = findHolderById(holderId);
        if(holder ==null) return null;
        Account account = holder.findAccountById(accountId);
        if(account==null) return null;
        return account.findCategoryById(categoryId);
    }

    public int createCategory(Category category) {
        Account account = findAccountById(category.holder, category.account);
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
        Account account = findAccountById(category.holder, category.account);
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
        Holder holder = findHolderById(transaction.holder);
        Account account = holder.findAccountById(transaction.account);
//        Category category = account.findCategoryById(transaction.category);

        //  数据库操作
//        int result = DAO.insertTransaction(transaction, category.sign);
        int result = DAO.insertTransaction(transaction);
        if(result != 0) return result;

        //  更新内存中交易列表，和数据库一致
//        child.insertTransaction(transaction, category.sign);
        holder.insertTransaction(transaction);

        //  更新内存中账户余额
//        account.balance = account.balance + transaction.amount * category.sign;
        account.balance = account.balance + transaction.amount;
        holder.calculateTotalBalance();
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
        Holder holder = findHolderById(transaction.holder);

        Transaction originTransaction = holder.findTransactionById(transaction.id);
        Account originAccount = holder.findAccountById(originTransaction.account);

        Account account = holder.findAccountById(transaction.account);

        int result = DAO.updateTransaction(originTransaction, transaction);
        if(result != 0) return result;

        //  更新内存中交易列表
        holder.deleteTransaction(originTransaction);
        holder.insertTransaction(transaction);

        //  更新账户余额
        originAccount.balance = originAccount.balance - originTransaction.amount;
        account.balance = account.balance + transaction.amount;

        holder.calculateTotalBalance();
        return 0;
    }

    /**
     * 删除一条交易，先操作数据库数据，成功后再操作内存中数据
     * 操作包括：删除交易、更新时间顺序之后的所有交易的余额、更新账户余额
     * @param holderId
     * @param transactionId
     * @return
     */
    public int deleteTransaction(int holderId, int transactionId) {
        Holder holder = findHolderById(holderId);
        Transaction transaction = holder.findTransactionById(transactionId);
        Account account = holder.findAccountById(transaction.account);
        Category category = account.findCategoryById(transaction.category);

        //  进行数据库操作
//        int result = DAO.deleteTransaction(transaction, category.sign);
        int result = DAO.deleteTransaction(transaction);
        if(result != 0) return result;

        //  更新内存中交易列表
//        child.deleteTransaction(transaction, category.sign);
        holder.deleteTransaction(transaction);

        //  更新内存中账户余额
//        account.balance = account.balance - transaction.amount * category.sign;
        account.balance = account.balance - transaction.amount;
        holder.calculateTotalBalance();

        return 0;
    }

    public int modifyTransactionData(Transaction transaction) {
        Holder holder = findHolderById(transaction.holder);

        //Transaction originTransaction = holder.findTransactionById(transaction.id);
        //Account originAccount = holder.findAccountById(originTransaction.account);

        //Account account = holder.findAccountById(transaction.account);

        int result = DAO.modifyTransactionData(transaction);
        if(result != 0) return result;

        //  更新内存中交易列表
        //holder.deleteTransaction(originTransaction);
        //holder.insertTransaction(transaction);
        holder.modifyTransactionData(transaction);

        //  更新账户余额
        //originAccount.balance = originAccount.balance - originTransaction.amount;
        //account.balance = account.balance + transaction.amount;
        //holder.calculateTotalBalance();

        return 0;
    }

    public int createFromTo(FromTo fromTo) {
        FromTo exist = findFromToByName(fromTo.name);
        if(exist!=null) return 1; //  往来名称已经存在

        boolean result = DAO.insertFromTo(fromTo);
        if(result) {
            fromToList.add(fromTo);
            if(fromTo.direction==1 || fromTo.direction>=3) fromList.add(fromTo);
            if(fromTo.direction==2 || fromTo.direction>=3) toList.add(fromTo);
        } else {
            return 3;
        }

        return 0;
    }

    public int updateFromTo(FromTo fromTo) {
        FromTo exist = findFromToById(fromTo.id);
        if(exist==null) return 3;  //  往来对象不存在

        if(fromTo.equals(exist)) return 4;  //  数据没有变化

        boolean result = DAO.updateFromTo(fromTo);
        if (result) {
            if(exist.direction==1) {
                if(fromTo.direction==2) {
                    removeFromTo(exist);
                    toList.add(exist);
                } else if(fromTo.direction>=3) {
                    toList.add(exist);
                }
            } else if(exist.direction==2) {
                if(fromTo.direction==1) {
                    removeFromTo(exist);
                    fromList.add(exist);
                } else if(fromTo.direction>=3) {
                    fromList.add(exist);
                }
            } else if(exist.direction>=3) {
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
        Holder holder = findHolderByAccount(accountId);
        Account account = holder.findAccountById(accountId);
        return "<" + holder.name + ">的<" + account.name + ">";
    }

    public Holder findHolderByAccount(int accountId) {
        Iterator<Holder> iter = holderList.iterator();
        while(iter.hasNext()) {
            Holder holder = iter.next();
            Account account = holder.findAccountById(accountId);
            if(account!=null) return holder;
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

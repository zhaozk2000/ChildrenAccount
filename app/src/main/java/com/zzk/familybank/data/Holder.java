package com.zzk.familybank.data;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

/**
 * 表示一个儿童的类，包含儿童的基本信息、账户列表、交易列表等，
 * 并提供了对账户、交易、类别等数据的操作方法。
 */
public class Holder {
    /**
     * 儿童的唯一标识符
     */
    public int id;
    /**
     * 儿童的姓名
     */
    public String name;
    /**
     * 儿童信息创建的时间
     */
    public Date createTime;
    /**
     * 儿童拥有的账户列表
     */
    public List<Account> accountList;
    /**
     * 儿童所有账户的总余额
     */
    public int totalBalance;
    /**
     * 儿童的交易记录列表
     */
    public List<Transaction> transactionList;
    //public List<FromTo> fromList;
    //public List<FromTo> toList;

    /**
     * 交易列表的缓冲区大小
     */
    public static final int TRANSACTION_BUFFER_SIZE = 128;

    /**
     * 构造函数，初始化儿童对象的账户列表和交易列表
     */
    public Holder() {
        accountList = new ArrayList();
        transactionList = new ArrayList<>(TRANSACTION_BUFFER_SIZE);
    }

    /**
     * 计算儿童所有账户的总余额
     */
    public void calculateTotalBalance() {
        totalBalance = 0;
        Iterator<Account> iter = accountList.iterator();
        while (iter.hasNext()) {
            Account account = iter.next();
            totalBalance += account.balance;
        }
    }

    /**
     * 根据账户 ID 查找账户
     * @param accountId 要查找的账户的 ID
     * @return 如果找到则返回该账户对象，否则返回 null
     */
    public Account findAccountById(int accountId) {
        Iterator<Account> iter = accountList.iterator();
        while (iter.hasNext()) {
            Account account = iter.next();
            if (account.id == accountId) return account;
        }
        return null;
    }

    /**
     * 根据账户名称查找账户
     * @param accountName 要查找的账户的名称
     * @return 如果找到则返回该账户对象，否则返回 null
     */
    public Account findAccountByName(String accountName) {
        Iterator<Account> iter = accountList.iterator();
        while (iter.hasNext()) {
            Account account = iter.next();
            if (account.name.equals(accountName)) return account;
        }
        return null;
    }

    /**
     * 根据类别 ID 查找类别
     * @param categoryId 要查找的类别的 ID
     * @return 如果找到则返回该类别对象，否则返回 null
     */
    public Category findCategoryById(int categoryId) {
        Iterator<Account> iter = accountList.iterator();
        while (iter.hasNext()) {
            Account account = iter.next();
            Category category = account.findCategoryById(categoryId);
            if (category != null) return category;
        }
        return null;
    }

    /**
     * 获取儿童所有账户的类别列表
     * @return 包含所有类别对象的列表
     */
    public List<Category> getCategoryList() {
        List<Category> result = new ArrayList<>();
        Iterator<Account> iter = accountList.iterator();
        while (iter.hasNext()) {
            Account account = iter.next();
            result.addAll(account.categoryList);
        }
        return result;
    }

    /**
     * 根据交易 ID 查找交易记录
     * @param transactionId 要查找的交易的 ID
     * @return 如果找到则返回该交易记录对象，否则返回 null
     */
    public Transaction findTransactionById(int transactionId) {
        Iterator<Transaction> iter = transactionList.iterator();
        while (iter.hasNext()) {
            Transaction transaction = iter.next();
            if (transaction.id == transactionId) return transaction;
        }
        return null;
    }

    /**
     * 插入一条交易的内存数据操作，在数据库操作成功后执行
     * 内存中的交易列表按时间降序排列。
     * 依次遍历列表，如果列表中交易时间在插入的交易之后，则更新交易后余额；
     * 直到列表中交易的时间在插入交易之前，则把新的交易插入到该位置。
     * @param transaction 要插入的交易记录
     */
//    public void insertTransaction(Transaction transaction, int sign) {
    public void insertTransaction(Transaction transaction) {
        int pos = 0;
        Iterator<Transaction> iter = transactionList.iterator();
        while (iter.hasNext()) {
            Transaction t = iter.next();
            if (t.date.getTime() > transaction.date.getTime()) {
                if (t.account == transaction.account)
//                    t.afterBalance += transaction.amount * sign;
                    t.afterBalance += transaction.amount;
            } else {
                break;
            }
            pos++;
        }
        transactionList.add(pos, transaction);
    }

    /**
     * 删除一条交易的内存数据操作，在数据库操作成功后执行
     * 内存中的交易列表按时间降序排列。
     * 依次遍历列表，如果列表中交易时间在插入的交易之后，则更新交易后余额；
     * 直到列表中找到该交易，则删除该交易。
     * @param transaction 要删除的交易记录
     */
//    public void deleteTransaction(Transaction transaction, int sign) {
    public void deleteTransaction(Transaction transaction) {
        Iterator<Transaction> iter = transactionList.iterator();
        while (iter.hasNext()) {
            Transaction t = iter.next();
            // 如果当前交易记录的时间在要删除的交易之后
            if (t.date.getTime() > transaction.date.getTime()) {
                // 如果当前交易记录的账户和要删除的交易账户相同
                if (t.account == transaction.account)
//                    t.afterBalance -= transaction.amount * sign;
                    t.afterBalance -= transaction.amount;
            }
            if (t.id == transaction.id) {
                // 删除该交易记录
                iter.remove();
                return;
            }
        }
    }

    public void modifyTransactionData(Transaction transaction) {
        Transaction buffered = findTransactionById(transaction.id);
        if(buffered!=null) {
            buffered.copyFrom(transaction);
        }
    }

    /**
     * 查找最近使用的类别
     * @param sign 交易类型的符号，0 表示所有类型，+1 表示增加，-1 表示减少
     * @return 包含最近使用类别的列表
     */
    public List<Category> findRecentCategories(int sign) {
        List<Category> result = new ArrayList<>();
        Iterator<Transaction> iter = transactionList.iterator();
        while (iter.hasNext()) {
            Transaction transaction = iter.next();
            Category category = findCategoryById(transaction.category);
            if (sign == 0 || category.sign == sign) { //  +1, -1 表示增减
                if (!result.contains(category)) {
                    result.add(category);
                }
            }
        }
        return result;
    }
}

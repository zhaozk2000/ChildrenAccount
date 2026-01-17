package com.zzk.childrenbank.data;

public class Category {
    public int id;
    public int child;
    public int account;
    public String name;
    public int sign = 1;        //  +1, -1 表示增减
    public int default_amount;      //  单位为分
    public int default_fromto;
    public String notes;

    public Category cloneCategory() {
        Category category = new Category();
        category.id = id;
        category.child = child;
        category.account = account;
        category.name = name;
        category.sign = sign;
        category.default_amount = default_amount;
        category.default_fromto = default_fromto;
        category.notes = notes;
        return category;
    }

    public void copyFrom(Category category) {
        id = category.id;
        child = category.child;
        account = category.account;
        name = category.name;
        sign = category.sign;
        default_amount = category.default_amount;
        default_fromto = category.default_fromto;
        notes = category.notes;
    }
}

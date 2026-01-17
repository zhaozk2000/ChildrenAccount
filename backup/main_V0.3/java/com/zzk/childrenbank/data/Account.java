package com.zzk.childrenbank.data;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class Account {
    public int id;
    public int child;
    public String name;
    public int balance;        //  余额balance单位为分

    public List<Category> categoryList;

    public Account(){
        categoryList = new LinkedList<>();
    }

    public Category findCategoryById(int categoryId) {
        Iterator<Category> iter = categoryList.iterator();
        while(iter.hasNext()) {
            Category category = iter.next();
            if(category.id == categoryId) return category;
        }
        return null;
    }
}

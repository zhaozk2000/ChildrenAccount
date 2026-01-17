package com.zzk.childrenbank;

import android.content.Context;

public class TransactionShortcut extends androidx.appcompat.widget.AppCompatButton {
    int categoryId;

    public TransactionShortcut(Context context) {
        super(context);
    }

    public void setCategoryId(int id) {
        categoryId = id;
    }

    public int getCategoryId(){
        return categoryId;
    }
}

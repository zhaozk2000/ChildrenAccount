package com.zzk.familybank.query;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ListTextView extends androidx.appcompat.widget.AppCompatTextView {
    public static final String separator = ";";

    String[] items;
    boolean[] selected;
    boolean[] selectedCopy;

    ListView listView;
    OnChangeListener onChangeListener;

    public ListTextView(@NonNull Context context) {
        super(context);
        init();
    }

    public ListTextView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }
    void init(){
        setDataItems(new String[]{});
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                System.arraycopy(selected, 0, selectedCopy, 0, selected.length);
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("请选择")
                        .setMultiChoiceItems(items, selectedCopy, new DialogInterface.OnMultiChoiceClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i, boolean b) {
                                selectedCopy[i] = b;
                            }
                        })
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                System.arraycopy(selectedCopy, 0, selected, 0, selected.length);
                                setText(selectionToString());
                                if(onChangeListener!=null) onChangeListener.onChanged();
                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                            }
                        });
                listView = builder.show().getListView();
            }
        });
    }

    public void setDataItems(String[] dataItems) {
        this.items = dataItems;
        selected = new boolean[dataItems.length];
        selectedCopy = new boolean[dataItems.length];
    }

    public void setDataItems(List<String> dataItems) {
        this.items = new String[dataItems.size()];
        for(int i=0; i<items.length; i++)
            this.items[i] = dataItems.get(i);
        selected = new boolean[dataItems.size()];
        selectedCopy = new boolean[dataItems.size()];
    }

    String selectionToString() {
        StringBuffer buffer = new StringBuffer();
        for(int i=0; i<selected.length; i++) {
            if(selected[i]) {
                if(buffer.length()>0) buffer.append(separator);
                buffer.append(items[i]);
            }
        }
        return buffer.toString();
    }

    public int getSelectedCount() {
        int count = 0;
        if(selected==null) return count;
        for(int i=0; i<selected.length; i++) {
            if(selected[i]) count ++;
        }
        return count;
    }

    public String[] getSelectedItems() {
        int count = getSelectedCount();
        String[] result = new String[count];
        if(count==0) return result;
        int j = 0;
        for(int i=0; i<selected.length; i++) {
            if(selected[i]) {
                result[j] = items[i];
                j++;
            }
        }
        return  result;
    }

    public int[] getSelectedPositions(){
        int count = getSelectedCount();
        int[] selectedPositions = new int[count];
        if(count==0) return selectedPositions;
        int j = 0;
        for(int i=0; i<selected.length; i++) {
            if(selected[i]) {
                selectedPositions[j] = i;
                j++;
            }
        }
        return  selectedPositions;
    }

    public void setOnChangeListener(OnChangeListener listener) {
        onChangeListener = listener;
    }

    public static interface OnChangeListener {
        void onChanged();
    }
}

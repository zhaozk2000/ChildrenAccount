package com.zzk.familybank.statistic;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class DropdownList extends androidx.appcompat.widget.AppCompatSpinner {
    DropdownItemAdapter itemAdapter;

    void init(){
        itemAdapter = new DropdownItemAdapter();
        setAdapter(itemAdapter);
    }

    public DropdownList(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public DropdownList(@NonNull Context context) {
        super(context);
        init();
    }

    public void clearItem(){
        itemAdapter.itemList.clear();
    }

    public void addItem(String itemText){
        itemAdapter.itemList.add(itemText);
    }

    public void updateData(){
        itemAdapter.notifyDataSetChanged();
    }

    public void setSelectedItem(String itemText) {
        int pos = 0;
        Iterator<String> iter = itemAdapter.itemList.iterator();
        while(iter.hasNext()) {
            String text = iter.next();
            if(text.equals(itemText)) {
                break;
            }
            pos ++;
        }
        setSelection(pos);
    }

    static class DropdownItemAdapter extends BaseAdapter {
        List<String> itemList;

        public DropdownItemAdapter(){
            itemList = new ArrayList<>();
        }

        public void setItems(List<String> list) {
            itemList = list;
        }

        @Override
        public int getCount() {
            if(itemList!=null) return itemList.size();
            else return 0;
        }

        @Override
        public Object getItem(int i) {
            return itemList.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_spinner_item, parent, false);
            }

            TextView textView = convertView.findViewById(android.R.id.text1);
            textView.setText(itemList.get(i));

            return convertView;
        }
    }
}

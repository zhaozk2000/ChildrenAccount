package com.zzk.familybank.statistic;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckedTextView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class MultiDropdownList extends androidx.appcompat.widget.AppCompatSpinner {
    MultiDropdownList.MultiDropdownItemAdapter itemAdapter;

    void init(){
        itemAdapter = new MultiDropdownList.MultiDropdownItemAdapter();
        setAdapter(itemAdapter);
    }

    public MultiDropdownList(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MultiDropdownList(@NonNull Context context) {
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

    static class MultiDropdownItemAdapter extends BaseAdapter {
        List<String> itemList;

        public MultiDropdownItemAdapter(){
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
                convertView = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_multiple_choice, parent, false);
            }

            CheckedTextView textView = convertView.findViewById(android.R.id.text1);
            textView.setText(itemList.get(i));
            textView.setChecked(true);

            return convertView;
        }
    }
}

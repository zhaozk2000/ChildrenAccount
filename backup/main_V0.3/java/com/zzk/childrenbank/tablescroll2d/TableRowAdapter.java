package com.zzk.childrenbank.tablescroll2d;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import com.zzk.childrenbank.R;

class TableRowAdapter extends RecyclerView.Adapter<TableRowAdapter.RowCellViewHolder> {
    List<String> rowCells;
    int cellWidth, cellHeight;

    public TableRowAdapter() {
        rowCells = new ArrayList<>();
    }

    public void setCellSize(int cellWidth, int cellHeight){
        this.cellWidth = cellWidth;
        this.cellHeight = cellHeight;
    }

    @NonNull
    @Override
    public RowCellViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.table_row_cell, parent, false);
        RowCellViewHolder tableLineViewHolder = new RowCellViewHolder(view, this);
        return tableLineViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RowCellViewHolder holder, int position) {
        String txt = rowCells.get(position);
        holder.textViewContent.setText(txt);
    }
    @Override
    public int getItemCount() {
        return rowCells.size();
    }

    public static class RowCellViewHolder extends RecyclerView.ViewHolder {
        TextView textViewContent;
        TableRowAdapter rowAdapter;
        public RowCellViewHolder(@NonNull View itemView, TableRowAdapter rowAdapter) {
            super(itemView);
            this.rowAdapter = rowAdapter;
            textViewContent = itemView.findViewById(R.id.textview_table_cell);
            //textViewContent.setWidth(40);
            ViewGroup.LayoutParams params = textViewContent.getLayoutParams();
            //params.width = 160;
            params.width = rowAdapter.cellWidth;
            textViewContent.setLayoutParams(params);
        }
    }

    public void setTitles(String[] titles) {
        rowCells.clear();
        for(int i=1; i<titles.length; i++) {
            rowCells.add(titles[i]);
        }
    }
}
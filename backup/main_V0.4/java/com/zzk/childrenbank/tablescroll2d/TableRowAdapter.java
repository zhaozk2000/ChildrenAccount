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
    int[] columnWidth;
    TableContentAdapter.TableRowViewHolder tableRowViewHolder;  //  用于获取行的序号
    TableCellOnClickListener cellOnClickListener;

    public TableRowAdapter() {
        rowCells = new ArrayList<>();
    }

    public void setCellSize(int cellWidth, int cellHeight){
        this.cellWidth = cellWidth;
        this.cellHeight = cellHeight;
    }

    public void setColumnWidth(int[] columnWidth) {
        this.columnWidth = columnWidth;
    }

    public void setRowHolder(TableContentAdapter.TableRowViewHolder tableRowViewHolder) {
        this.tableRowViewHolder = tableRowViewHolder;
    }

    public void setCellOnClickListener(TableCellOnClickListener listener) {
        cellOnClickListener = listener;
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
        if(columnWidth!=null) {
            if(position<columnWidth.length) {
                holder.setWidth(columnWidth[position]);
            }
        }
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
            setWidth(rowAdapter.cellWidth);
            textViewContent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int x = getAdapterPosition() + 1;
                    int y = 0;
                    if(rowAdapter.tableRowViewHolder!=null)
                        y = rowAdapter.tableRowViewHolder.getAdapterPosition() + 1;
                    if(rowAdapter.cellOnClickListener!=null) {
                        rowAdapter.cellOnClickListener.onClick(x,y,textViewContent.getText().toString());
                    }
                    //Toast.makeText(itemView.getContext(), "Cell onClick " + x + ", " + y, Toast.LENGTH_SHORT).show();
                }
            });
        }

        public void setWidth(int width) {
            ViewGroup.LayoutParams params = textViewContent.getLayoutParams();
            //params.width = 160;
            if(width>0)
                params.width = width;
            else
                params.width = ViewGroup.LayoutParams.WRAP_CONTENT;
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
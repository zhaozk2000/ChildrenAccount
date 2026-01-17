package com.zzk.childrenbank.tablescroll2d;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.zzk.childrenbank.R;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class TableContentAdapter extends RecyclerView.Adapter<TableContentAdapter.TableRowViewHolder> {
    List<RowData> tableData;
    int cellWidth, cellHeight;
    int firstColumnWidth;
    int[] columnWidth;

    //int textSize;
    int firstPos = -1;
    int firstOffset = -1;
    HashSet<RecyclerView> rowRVSet = new HashSet<>();
    View.OnTouchListener rvOnTouchListener;
    RecyclerView.OnScrollListener rvOnScrollListener;
    TableCellOnClickListener cellOnClickListener;

    public TableContentAdapter() {
        tableData = new ArrayList<>();
        rvOnTouchListener = new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(motionEvent.getAction()==MotionEvent.ACTION_DOWN || motionEvent.getAction()==MotionEvent.ACTION_POINTER_DOWN) {
                    for (RecyclerView rv: rowRVSet) {
                        rv.stopScroll();
                    }
                }
                return false;
            }
        };
        rvOnScrollListener = new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
           }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager layoutManager = (LinearLayoutManager)recyclerView.getLayoutManager();
                int currentFirstPos = layoutManager.findFirstVisibleItemPosition();
                View firstVisibleItem = layoutManager.getChildAt(0);
                if(firstVisibleItem!=null) {
                    int firstRight = layoutManager.getDecoratedRight(firstVisibleItem);
                    for(RecyclerView rv : rowRVSet){
                        if(rv==recyclerView) continue;
                        LinearLayoutManager rowLayoutManager = null;
                        if(rv.getLayoutManager() instanceof LinearLayoutManager)
                            rowLayoutManager = (LinearLayoutManager)rv.getLayoutManager();
                        if(rowLayoutManager!=null) {
                            firstPos = currentFirstPos;
                            firstOffset = firstRight;
                            rowLayoutManager.scrollToPositionWithOffset(firstPos+1, firstRight);
                        }
                    }
                }
            }
        };
    }

    public void setCellSize(int cellWidth, int cellHeight){
        this.cellWidth = cellWidth;
        this.cellHeight = cellHeight;
    }

    public void setColumnWidth(int[] columnWidth) {
        this.columnWidth = columnWidth;
    }

    public void setFirstColumnWidth(int firstColumnWidth){
        this.firstColumnWidth = firstColumnWidth;
    }

    public void setCellOnClickListener(TableCellOnClickListener listener) {
        cellOnClickListener = listener;
    }

    @NonNull
    @Override
    public TableRowViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.table_row, parent, false);
        TableRowViewHolder tableRowViewHolder = new TableRowViewHolder(view, this);
        return tableRowViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull TableRowViewHolder holder, int position) {
        RowData rowData = tableData.get(position);
        holder.textViewRowName.setText(rowData.rowName);
        holder.rowAdapter.setTitles(rowData.values);
        holder.rowAdapter.notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return tableData.size();
    }

    public void initRowRecyclerView(RecyclerView rv, Context context) {
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        layoutManager.setOrientation(RecyclerView.HORIZONTAL);
        rv.setLayoutManager(layoutManager);
        rv.setHasFixedSize(true);
        if(firstPos>=0 && firstOffset>=0){
            layoutManager.scrollToPositionWithOffset(firstPos+1, firstOffset);
        }
        rowRVSet.add(rv);
        rv.setOnTouchListener(rvOnTouchListener);
        rv.addOnScrollListener(rvOnScrollListener);
    }

    public static class TableRowViewHolder extends RecyclerView.ViewHolder {
        TextView textViewRowName;
        RecyclerView recyclerViewRowContent;
        TableRowAdapter rowAdapter;

        TableContentAdapter contentAdapter;

        public TableRowViewHolder(@NonNull View itemView, TableContentAdapter contentAdapter) {
            super(itemView);
            this.contentAdapter = contentAdapter;

            textViewRowName = itemView.findViewById(R.id.tv_left_title);
            recyclerViewRowContent = itemView.findViewById(R.id.rv_item_right);

            // 设置第一列宽度
            ViewGroup.LayoutParams params = textViewRowName.getLayoutParams();
            params.width = contentAdapter.firstColumnWidth;
            textViewRowName.setLayoutParams(params);

            //  设置本行高度
            LinearLayout linearLayoutRowRect = itemView.findViewById(R.id.linearlayout_row_rect);
            params = linearLayoutRowRect.getLayoutParams();
            params.height = contentAdapter.cellHeight;
            linearLayoutRowRect.setLayoutParams(params);

            //  设置本行单元格宽度
            rowAdapter = new TableRowAdapter();
            rowAdapter.setCellSize(contentAdapter.cellWidth, contentAdapter.cellHeight);
            rowAdapter.setColumnWidth(contentAdapter.columnWidth);
            recyclerViewRowContent.setAdapter(rowAdapter);

            contentAdapter.initRowRecyclerView(recyclerViewRowContent, itemView.getContext());

            rowAdapter.setRowHolder(this);  //  为了在单元格中能通过RowViewHoler得到行号
            rowAdapter.setCellOnClickListener(contentAdapter.cellOnClickListener);
            textViewRowName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //Toast.makeText(itemView.getContext(), "Row name onClick " + getAdapterPosition(), Toast.LENGTH_SHORT).show();
                    if(contentAdapter.cellOnClickListener!=null) {
                        int y = getAdapterPosition();
                        contentAdapter.cellOnClickListener.onClick(0, y+1, textViewRowName.getText().toString());
                    }
                }
            });
        }
    }

    public void setData(String[][] data) {
        tableData.clear();
        for(int i=1; i<data.length; i++) {
            RowData row = new RowData();
            row.rowName = data[i][0];
            row.values = data[i];
            tableData.add(row);
        }
    }
}

class RowData {
    String rowName;
    String[] values;
}
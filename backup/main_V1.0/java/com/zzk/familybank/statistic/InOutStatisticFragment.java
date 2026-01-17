package com.zzk.familybank.statistic;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zzk.familybank.R;
import com.zzk.familybank.data.Category;
import com.zzk.familybank.data.DataModel;
import com.zzk.familybank.data.FromTo;
import com.zzk.familybank.data.Holder;
import com.zzk.familybank.data.Common;
import com.zzk.familybank.data.Transaction;

import java.util.Date;
import java.util.Iterator;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link InOutStatisticFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class InOutStatisticFragment extends PeriodFragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    RecyclerView recyclerViewTable;
    InOutTableAdapter inOutTableAdapter;

    public InOutStatisticFragment(Holder holder) {
        super(holder);
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment InOutStatisticFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static InOutStatisticFragment newInstance(Holder holder) {
        InOutStatisticFragment fragment = new InOutStatisticFragment(holder);
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View fragment = inflater.inflate(R.layout.fragment_in_out_statistic, container, false);
        recyclerViewTable = fragment.findViewById(R.id.recyclerview_table);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        recyclerViewTable.setLayoutManager(layoutManager);
        inOutTableAdapter = new InOutTableAdapter(tableData);
        recyclerViewTable.setAdapter(inOutTableAdapter);
        recyclerViewTable.addItemDecoration(new RecyclerView.ItemDecoration() {  //  添加分隔线
            int dividerWidth = 3;
            private Paint mPaint = new Paint();
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);
                outRect.right = dividerWidth;
            }
            @Override
            public void onDraw(@NonNull Canvas c, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                super.onDraw(c, parent, state);
                mPaint.setColor(Color.BLACK);
                mPaint.setStyle(Paint.Style.FILL);
                int left = parent.getPaddingLeft();
                int right = parent.getWidth() - parent.getPaddingRight();
                for(int i=0; i<parent.getChildCount(); i++) {
                    View view = parent.getChildAt(i);
                    float top = view.getBottom();
                    float bottom = view.getBottom() + dividerWidth;
                    c.drawRect(left, top, right, bottom, mPaint);
                    if(i==0) {
                        top = view.getTop();
                        bottom = view.getTop() + dividerWidth;
                        c.drawRect(left, top, right, bottom, mPaint);
                    }
                }
            }
        });

        //textViewTitle = fragment.findViewById(R.id.textView_Title);
        //fillTableTitle();

        if(periodSelectFragment!=null) {    //  执行到此处时，periodSelectFragment已经设置好
            beginDate = 0;
            setPeriod(periodSelectFragment.getStartDate(), periodSelectFragment.getEndDate(), periodSelectFragment.getAccountIndex());
        }
        return fragment;
    }

    @Override
    public boolean setPeriod(Date begin, Date end, int accountIndex) {
        if(recyclerViewTable==null) return false;
        if(!super.setPeriod(begin, end, accountIndex)) return false;
        queryData(begin, end, accountIndex);
        prepareData();
        inOutTableAdapter.notifyDataSetChanged();
        return true;
    }

    @Override
    public boolean setYearMonth(int yearMonth){
        if(recyclerViewTable==null) return false;
        if(!super.setYearMonth(yearMonth)) return false;
        prepareData();
        inOutTableAdapter.notifyDataSetChanged();
        return true;
    }

    @Override
    protected DataItem newDataItem(){
        return new DataItemInOut();
    }

    void prepareData(){
        DataModel dataModel = DataModel.getDataModel(this.getActivity());

        generateDataFrame();

        //  计算每行的数据
        Iterator<Transaction> iter = transactions.iterator();
        while(iter.hasNext()) {
            Transaction transaction = iter.next();
            DataItemInOut dataItem = (DataItemInOut) findDataItemByDate(tableData, transaction.date.getTime());
            if(dataItem!=null) {
                Category category = holder.findCategoryById(transaction.category);
                FromTo fromTo = dataModel.findFromToById(transaction.fromto);
                if(category.sign>0 && fromTo.direction!=4) {        //  非自己内部转账
                    dataItem.income += transaction.amount;
                } else {
                    dataItem.payout -= transaction.amount;
                }
            }
        }

        //  计算并生成最后的 合计 行
        DataItemInOut total = new DataItemInOut();
        total.name = "合计";
        Iterator<DataItem> itemIter = tableData.iterator();
        while(itemIter.hasNext()) {
            DataItemInOut item = (DataItemInOut) itemIter.next();
            total.income += item.income;
            total.payout += item.payout;
        }
        tableData.add(total);
    }

}

class InOutTableAdapter extends RecyclerView.Adapter<InOutTableAdapter.ColumnHolder>{
    List<PeriodFragment.DataItem> tableData;
    int textSize;

    public InOutTableAdapter(List<PeriodFragment.DataItem> tableData) {
        this.tableData = tableData;
        textSize = 20;
    }

    @NonNull
    @Override
    public ColumnHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LinearLayout column = new LinearLayout(parent.getContext());
        column.setOrientation(LinearLayout.HORIZONTAL);
        Drawable divider = AppCompatResources.getDrawable(parent.getContext(), R.drawable.table_divider);
        column.setDividerDrawable(divider);
        column.setShowDividers(LinearLayout.SHOW_DIVIDER_BEGINNING|LinearLayout.SHOW_DIVIDER_MIDDLE|LinearLayout.SHOW_DIVIDER_END);
        int cellWidth = parent.getWidth()/3;
        TextView[] textViews = new TextView[3];
        for(int i=0; i<3; i++) {
            TextView tv = new TextView(parent.getContext());
            tv.setTextSize(textSize);
            column.addView(tv);
            textViews[i] = tv;
            ViewGroup.LayoutParams layoutParams = tv.getLayoutParams();
            layoutParams.width = cellWidth;
            tv.setLayoutParams(layoutParams);
        }
        return new ColumnHolder(column, textViews);
    }

    @Override
    public void onBindViewHolder(@NonNull ColumnHolder holder, int position) {
        DataItemInOut columnData = (DataItemInOut) tableData.get(position);
        holder.textViews[0].setText(columnData.name);
        holder.textViews[1].setText(Common.balanceToString(columnData.income));
        holder.textViews[2].setText(Common.balanceToString(columnData.payout));
    }

    @Override
    public int getItemCount() {
        return tableData.size();
    }

    static class ColumnHolder extends RecyclerView.ViewHolder{
        TextView[] textViews;
        public ColumnHolder(@NonNull View itemView, TextView[] textViews) {
            super(itemView);
            this.textViews = textViews;
        }
    }
}

class DataItemInOut extends PeriodFragment.DataItem{
/*
    String name;
    long beginTime;     // >=
    long endTime;       // <
 */
    int income = 0;
    int payout = 0;
}
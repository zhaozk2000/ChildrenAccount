package com.zzk.childrenbank.statistic;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zzk.childrenbank.R;
import com.zzk.childrenbank.data.Category;
import com.zzk.childrenbank.data.Child;
import com.zzk.childrenbank.data.Common;
import com.zzk.childrenbank.data.IntObject;
import com.zzk.childrenbank.data.Transaction;
import com.zzk.childrenbank.tablescroll2d.TableScroll2dFragment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CategoryStatisticFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CategoryStatisticFragment extends PeriodFragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    TableScroll2dFragment tableFragment;

    public CategoryStatisticFragment(Child child) {
        super(child);
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment CategoryStatisticFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CategoryStatisticFragment newInstance(Child child) {
        CategoryStatisticFragment fragment = new CategoryStatisticFragment(child);
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
        View view = inflater.inflate(R.layout.fragment_category_statistic, container, false);

        tableFragment = (TableScroll2dFragment) getChildFragmentManager().findFragmentById(R.id.fragment_table_category_statistic);

        tableFragment.setLeftTopTitle("日期", "分类");
        tableFragment.setCellSize(250, 100);
        tableFragment.setFirstCellSize(400, 200);

        if(periodSelectFragment!=null) {    //  执行到此处时，periodSelectFragment已经设置好
            beginDate = 0;
            setPeriod(periodSelectFragment.getStartDate(), periodSelectFragment.getEndDate(), periodSelectFragment.getAccountIndex());
        }

        return view;
    }

    @Override
    public boolean setPeriod(Date begin, Date end, int accountIndex) {
        if(tableFragment==null) return false;
        if(!super.setPeriod(begin, end, accountIndex)) return false;
        queryData(begin, end, accountIndex);
        prepareData();
        return true;
    }

    @Override
    public boolean setYearMonth(int yearMonth){
        if(tableFragment==null) return false;
        if(!super.setYearMonth(yearMonth)) return false;
        prepareData();
        return true;
    }

    @Override
    protected DataItem newDataItem(){
        return new DataItemCategory();
    }

    int findCategoryPosition(List<Category> categories, int categoryId) {
        int pos = 0;
        Iterator<Category> iterator = categories.iterator();
        while(iterator.hasNext()) {
            Category category = iterator.next();
            if(category.id == categoryId) return pos;
            pos ++;
        }
        return -1;
    }

    void prepareData(){
        List<Category> categories = child.getCategoryList();
        boolean[] hasData = new boolean[categories.size()];
        Arrays.fill(hasData, false);

        //  按时间区间生成数据项
        generateDataFrame();
        //  每个数据项生成分类列表
        Iterator<DataItem> dataIter = tableData.iterator();
        while(dataIter.hasNext()) {
            DataItemCategory dataItem = (DataItemCategory)dataIter.next();
            dataItem.init(categories.size());
        }

        //  Transaction数据累加到数据项
        Iterator<Transaction> iter = transactions.iterator();
        while (iter.hasNext()) {
            Transaction transaction = iter.next();
            DataItemCategory dataItem = (DataItemCategory) findDataItemByDate(tableData, transaction.date.getTime());
            if(dataItem!=null) {
                Category category = child.findCategoryById(transaction.category);
                int pos = findCategoryPosition(categories, category.id);
                if(pos>=0) {
                    hasData[pos] = true;
                    IntObject dataObj = dataItem.categoryTotal.get(pos);
                    if(category.sign>0) {
                        dataObj.value += transaction.amount;
                    } else {
                        dataObj.value -= transaction.amount;
                    }
                }
            }
        }

        //  移除未使用分类，包括标题列categories和后面tableData的所有categoryTotal列
        for(int i=hasData.length-1; i>=0; i--) {
            if(hasData[i]) continue;
            categories.remove(i);
            dataIter = tableData.iterator();
            while(dataIter.hasNext()) {
                DataItemCategory dataItem = (DataItemCategory)dataIter.next();
                dataItem.categoryTotal.remove(i);
            }
        }

        //  计算并添加合计列
        DataItemCategory total = new DataItemCategory();
        total.name = "合计";
        total.init(categories.size());
        dataIter = tableData.iterator();
        while(dataIter.hasNext()) {
            DataItemCategory dataItem = (DataItemCategory)dataIter.next();
            for(int i=0; i<categories.size(); i++) {
                IntObject totalObj = total.categoryTotal.get(i);
                totalObj.value = totalObj.value + dataItem.categoryTotal.get(i).value;
            }
        }
        tableData.add(0, total);

        //  数据项填充到表格
        String[][] cells = new String[categories.size()+1][tableData.size()+1];
        for (int i=0; i<categories.size(); i++) cells[i+1][0] = categories.get(i).name;
        for (int x=0; x<tableData.size(); x++) {
            DataItemCategory dataItem = (DataItemCategory)tableData.get(x);
            cells[0][x+1] = dataItem.name;
            for(int y=0; y<categories.size(); y++) {
                IntObject dataObj = dataItem.categoryTotal.get(y);
                cells[y+1][x+1] = Common.balanceToString(dataObj.value);
            }
        }

        tableFragment.setData(cells);
    }
}

class DataItemCategory extends PeriodFragment.DataItem{
    List<IntObject> categoryTotal = new ArrayList<>();

    public void init(int size) {
        for(int i=0; i<size; i++){
            categoryTotal.add(new IntObject(0));
        }
    }
}
package com.zzk.familybank.statistic;

import androidx.fragment.app.Fragment;

import com.zzk.familybank.data.Holder;
import com.zzk.familybank.data.Common;
import com.zzk.familybank.data.DBHelper;
import com.zzk.familybank.data.Transaction;
import com.zzk.familybank.data.DAO;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

public abstract class PeriodFragment extends Fragment {
    protected PeriodSelectFragment periodSelectFragment;
    protected long beginDate=-1, endDate=-1;
    protected int yearMonth;        //  0: 按年统计；    1:按月统计
    protected int accountIndex;     //  0: 全部；      >0: 减1就是在child.accountList里的位置

    protected Holder holder;
    protected List<Transaction> transactions = new ArrayList<>();
    protected List<DataItem> tableData = new ArrayList<>();

    public PeriodFragment(Holder holder) {
        this.holder = holder;
    }

    public boolean setPeriod(Date begin, Date end, int accountIndex){
        long tmpBegin = (begin==null ? -1 : begin.getTime());
        long tmpEnd = (end==null ? -1 : end.getTime());
        if( tmpBegin==beginDate && tmpEnd==endDate && this.accountIndex==accountIndex) return false;
        beginDate = tmpBegin;
        endDate = tmpEnd;
        this.accountIndex = accountIndex;
        return true;
    }
    public boolean setYearMonth(int yearMonth){
        if(this.yearMonth==yearMonth) return false;
        this.yearMonth = yearMonth;
        return true;
    }

    public void setPeriodSelectFragment(PeriodSelectFragment periodSelectFragment) {
        this.periodSelectFragment = periodSelectFragment;
        setPeriod(periodSelectFragment.getStartDate(), periodSelectFragment.getEndDate(), periodSelectFragment.getAccountIndex());
    }

    void queryData(Date begin, Date end, int accountIndex) {
        int accountId = -1;
        if(accountIndex>0) {
            accountId = holder.accountList.get(accountIndex-1).id;
        }
        transactions = DAO.queryTransaction(holder.id, accountId, begin, end, DBHelper.TransactionTable.date, false);
    }

    protected void generateDataFrame(){
        tableData.clear();
        //  生成条目，即表格行
        int[] startYearMonth = null;
        if(beginDate>0) {
            startYearMonth = Common.getYearMonth(new Date(beginDate));
        } else {
            int size = transactions.size();
            if(size>0)
                startYearMonth = Common.getYearMonth(transactions.get(size-1).date);
        }
        if(startYearMonth!=null) {
            int[] endYearMonth = null;
            if(endDate>0) {
                endYearMonth = Common.getYearMonth(new Date(endDate));
            } else {
                if(transactions.size()>0)
                    endYearMonth = Common.getYearMonth(transactions.get(0).date);
            }
            if(endYearMonth!=null) {
                if(yearMonth==0){   //  按年
                    for(int year=endYearMonth[0]; year>=startYearMonth[0]; year--) {
                        DataItem dataItem = newDataItem();
                        dataItem.name = Integer.toString(year) + "年";
                        dataItem.beginTime = Common.getBeginTimeOfYear(year);
                        dataItem.endTime = Common.getBeginTimeOfYear(year+1);
                        tableData.add(dataItem);
                    }
                } else {    //  按月
                    if(startYearMonth[0]==endYearMonth[0]) {  //    开始时间 和 结束时间 同一年内
                        generateDataItems(tableData, startYearMonth[0], endYearMonth[1], startYearMonth[1]);
                    } else {
                        generateDataItems(tableData, endYearMonth[0], endYearMonth[1], 0);
                        for(int year=endYearMonth[0]-1; year>startYearMonth[0]; year--) {
                            generateDataItems(tableData, year, 11, 0);
                        }
                        generateDataItems(tableData, startYearMonth[0], 11, startYearMonth[1]);
                    }
                }
            }
        }
    }

    void generateDataItems(List<DataItem> tableData, int year, int endMonth, int beginMonth){
        for(int month = endMonth; month>=beginMonth; month--){
            DataItem dataItem = newDataItem();
            dataItem.name = Integer.toString(year) + "年" + (month+1) +"月";
            dataItem.beginTime = Common.getBeginTimeOfYearMonth(year, month);
            dataItem.endTime = Common.getBeginTimeOfYearMonth(year, month+1);
            tableData.add(dataItem);
        }
    }

    //  must be override
    protected DataItem newDataItem(){
        return new DataItem();
    }

    DataItem findDataItemByDate(List<DataItem> tableData, long datetime) {
        Iterator<DataItem> iter = tableData.iterator();
        while(iter.hasNext()) {
            DataItem dataItem = iter.next();
            if(datetime>=dataItem.beginTime && datetime<dataItem.endTime) return dataItem;
        }
        return null;
    }

    public static class DataItem {
        public String name;
        public long beginTime;     // >=
        public long endTime;       // <
    }
}

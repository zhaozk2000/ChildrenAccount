package com.zzk.familybank.query;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.zzk.familybank.R;
import com.zzk.familybank.data.Account;
import com.zzk.familybank.data.Category;
import com.zzk.familybank.data.Common;
import com.zzk.familybank.data.DAO;
import com.zzk.familybank.data.DBHelper;
import com.zzk.familybank.data.DataModel;
import com.zzk.familybank.data.Holder;
import com.zzk.familybank.data.IntDate;
import com.zzk.familybank.data.IntObject;
import com.zzk.familybank.data.Transaction;
import com.zzk.familybank.tablescroll2d.TableScroll2dFragment;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

public class QueryActivity extends AppCompatActivity {
    ListTextView listTextViewHolders;
    ListTextView listTextViewAccounts;
    ListTextView listTextViewCategories;
    ListTextView listTextViewFromTo;
    TextView textViewStartDate;
    TextView textViewEndDate;
    TextView textViewNotes;
    Button buttonQuery;
    TextView textViewResult;
    TableScroll2dFragment tableFragment;

    DataModel dataModel;
    // holder和fromto列表是固定的，直接用dataModel中的
    //  account和category列表是随holder变化的，要动态生成
    List<Account> accounts = new ArrayList<>();
    List<Category> categories = new ArrayList<>();
    private Date startDate;
    private Date endDate;

    private final String resultString = "共%d条交易，累计金额%s元";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_query);

        listTextViewHolders = findViewById(R.id.listtextview_query_holders);
        listTextViewAccounts = findViewById(R.id.listtextview_query_accounts);
        listTextViewCategories = findViewById(R.id.listtextview_query_categories);
        listTextViewFromTo = findViewById(R.id.listtextview_query_fromto);
        textViewStartDate = findViewById(R.id.textView_query_startdate);
        textViewEndDate = findViewById(R.id.textView_query_enddate);
        textViewNotes = findViewById(R.id.textView_query_notes);
        buttonQuery = findViewById(R.id.button_query_query);
        textViewResult = findViewById(R.id.textView_query_result);
        tableFragment = (TableScroll2dFragment)getSupportFragmentManager().findFragmentById(R.id.fragment_query_result);

        dataModel = DataModel.getDataModel(this);
        startDate = IntDate.getDate(20250101);
        endDate = IntDate.getDate(IntDate.getToday());

        listTextViewHolders.setText("");
        listTextViewAccounts.setText("");
        listTextViewCategories.setText("");
        listTextViewFromTo.setText("");
        textViewStartDate.setText(Common.DateTimeToStringShort(startDate, true, false, false));
        textViewEndDate.setText(Common.DateTimeToStringShort(endDate, true, false, false));
        textViewNotes.setText("");
        textViewResult.setText(String.format(resultString, 0, "0.00"));
        tableFragment.setLeftTopTitle("数据", "日期");
        tableFragment.setCellSize(250, 100);
        tableFragment.setFirstCellSize(400, 200);

        String[] items = new String[dataModel.holderList.size()];
        for(int i=0; i<items.length; i++) {
            items[i] = dataModel.holderList.get(i).name;
        }
        listTextViewHolders.setDataItems(items);
        listTextViewHolders.setOnChangeListener(new ListTextView.OnChangeListener() {
            @Override
            public void onChanged() {
                String[] holders = listTextViewHolders.getSelectedItems();
                accounts.clear();
                categories.clear();
                List<String> accountNames = new ArrayList<>();
                List<String> categoryNames = new ArrayList<>();
                for(String s : holders) {
                    Holder holder = dataModel.findHolderByName(s);
                    for(Account account : holder.accountList) {
                        accounts.add(account);
                        accountNames.add(account.name);
                    }
                    for(Category category : holder.getCategoryList()) {
                        categories.add(category);
                        categoryNames.add(category.name);
                    }
                }
                listTextViewAccounts.setDataItems(accountNames);
                listTextViewCategories.setDataItems(categoryNames);
            }
        });


        items = new String[dataModel.fromToList.size()];
        for(int i=0; i<items.length; i++) {
            items[i] = dataModel.fromToList.get(i).name;
        }
        listTextViewFromTo.setDataItems(items);

        textViewStartDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar calendar = GregorianCalendar.getInstance();
                calendar.setTime(startDate);
                DatePickerDialog datePickerDialog = new DatePickerDialog(QueryActivity.this,  new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
                        Calendar cal = GregorianCalendar.getInstance();
                        //cal.setTime(startDate);
                        cal.set(Calendar.YEAR, year);
                        cal.set(Calendar.MONTH, month);
                        cal.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        cal.set(Calendar.HOUR_OF_DAY, 0);
                        cal.set(Calendar.MINUTE, 0);
                        cal.set(Calendar.SECOND, 0);
                        Date datetime = cal.getTime();
                        //setStartDate(datetime);
                        startDate = datetime;
                        textViewStartDate.setText(Common.DateTimeToStringShort(startDate, true, false, false));
                    }
                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.show();
            }
        });

        textViewEndDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar calendar = GregorianCalendar.getInstance();
                calendar.setTime(endDate);
                DatePickerDialog datePickerDialog = new DatePickerDialog(QueryActivity.this,  new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
                        Calendar cal = GregorianCalendar.getInstance();
                        //cal.setTime(endDate);
                        cal.set(Calendar.YEAR, year);
                        cal.set(Calendar.MONTH, month);
                        cal.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        cal.set(Calendar.HOUR_OF_DAY, 23);
                        cal.set(Calendar.MINUTE, 59);
                        cal.set(Calendar.SECOND, 59);
                        Date datetime = cal.getTime();
                        //setEndDate(datetime);
                        endDate = datetime;
                        textViewEndDate.setText(Common.DateTimeToStringShort(endDate, true, false, false));
                    }
                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.show();
            }
        });

        textViewNotes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(QueryActivity.this);
                builder.setIcon(R.drawable.ic_launcher_foreground);
                builder.setTitle("交易备注");
                builder.setMessage("请输入交易备注中包含的字符串：");
                EditText input = new EditText(QueryActivity.this);
                String s = textViewNotes.getText().toString();
                input.setText(s);
                builder.setView(input);
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        String s = input.getText().toString();
                        textViewNotes.setText(s);
                    }
                });
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        //Toast.makeText(getBaseContext(), "操作取消", Toast.LENGTH_SHORT).show();
                    }
                });
                builder.show();
            }
        });

        buttonQuery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 构造查询条件语句
                StringBuffer where = new StringBuffer();

                int[] holderPos = listTextViewHolders.getSelectedPositions();
                if(holderPos.length>0) {
                    StringBuffer subStr = new StringBuffer();
                    for(int i=0; i<holderPos.length; i++) {
                        int pos = holderPos[i];
                        subStr.append(dataModel.holderList.get(pos).id);
                        if(i<holderPos.length-1) subStr.append(", ");
                    }
                    where.append(DBHelper.TransactionTable.holder).append(" IN (").append(subStr).append(")");
                }

                int[] accountPos = listTextViewAccounts.getSelectedPositions();
                if(accountPos.length>0) {
                    StringBuffer subStr = new StringBuffer();
                    for(int i=0; i<accountPos.length; i++) {
                        int pos = accountPos[i];
                        subStr.append(accounts.get(pos).id);
                        if(i<accountPos.length-1) subStr.append(", ");
                    }
                    if(where.length()>0) where.append(" AND ");
                    where.append(DBHelper.TransactionTable.account).append(" IN (").append(subStr).append(")");
                }

                int[] categoryPos = listTextViewCategories.getSelectedPositions();
                if(categoryPos.length>0) {
                    StringBuffer subStr = new StringBuffer();
                    for(int i=0; i<categoryPos.length; i++) {
                        int pos = categoryPos[i];
                        subStr.append(categories.get(pos).id);
                        if(i<categoryPos.length-1) subStr.append(", ");
                    }
                    if(where.length()>0) where.append(" AND ");
                    where.append(DBHelper.TransactionTable.category).append(" IN (").append(subStr).append(")");
                }

                int[] fromtoPos = listTextViewFromTo.getSelectedPositions();
                if(fromtoPos.length>0) {
                    StringBuffer subStr = new StringBuffer();
                    for(int i=0; i<fromtoPos.length; i++) {
                        int pos = fromtoPos[i];
                        subStr.append(dataModel.fromToList.get(pos).id);
                        if(i<fromtoPos.length-1) subStr.append(", ");
                    }
                    if(where.length()>0) where.append(" AND ");
                    where.append(DBHelper.TransactionTable.fromto).append(" IN (").append(subStr).append(")");
                }

                int intStartDate = IntDate.DateToInt(startDate);
                Date startDate000 = IntDate.getDate(intStartDate);  //  对齐到0点0分
                int intEndDate = IntDate.DateToInt(endDate);
                intEndDate = IntDate.dayAdd(intEndDate, 1); //  对齐到下一天0点0分
                Date endDate000 = IntDate.getDate(intEndDate);
                if(where.length()>0) where.append(" AND ");
                where.append(DBHelper.TransactionTable.date).append(">=").append(startDate000.getTime());
                where.append(" AND ").append(DBHelper.TransactionTable.date).append("<").append(endDate000.getTime());

                String str = textViewNotes.getText().toString().trim();
                if(str.length()>0) {
                    if(where.length()>0) where.append(" AND ");
                    where.append(DBHelper.TransactionTable.notes).append(" LIKE '%").append(str).append("%'");
                }

                //  查询
                if(where.length()>0) where.insert(0, "where ");
                List<Transaction> transactions = DAO.queryTransaction(where.toString());

                //  数据项填充到表格
                String[] fieldName = {"日期", "持有人", "账户", "类型", "金额", "余额", "往来方", "备注"};

                String[][] cells = new String[transactions.size()+1][fieldName.length];

                for(int j=1; j<fieldName.length; j++) cells[0][j] = fieldName[j];   //  日期属于特殊表头，不在这里设置

                int sum = 0;
                for(int i=0; i<transactions.size(); i++) {
                    Transaction transaction = transactions.get(i);
                    cells[i+1][0] = Common.DateTimeToStringShort(transaction.date, true, false, false);
                    Holder holder = dataModel.findHolderById(transaction.holder);
                    cells[i+1][1] = holder.name;
                    cells[i+1][2] = holder.findAccountById(transaction.account).name;
                    cells[i+1][3] = holder.findCategoryById(transaction.category).name;
                    cells[i+1][4] = Common.balanceToString(transaction.amount);
                    cells[i+1][5] = Common.balanceToString(transaction.afterBalance);
                    cells[i+1][6] = dataModel.findFromToById(transaction.fromto).name;
                    cells[i+1][7] = transaction.notes;
                    sum += transaction.amount;
                }
                tableFragment.setData(cells);

                str = String.format(resultString, transactions.size(), Common.balanceToString(sum));
                textViewResult.setText(str);
            }
        });
    }
}
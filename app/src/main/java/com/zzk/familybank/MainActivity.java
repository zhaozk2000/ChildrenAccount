package com.zzk.familybank;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.zzk.familybank.accounts.AccountsManagementActivity;
import com.zzk.familybank.automation.AutomationManagementActivity;
import com.zzk.familybank.holder.HolderManagementActivity;
import com.zzk.familybank.data.Category;
import com.zzk.familybank.data.Holder;
import com.zzk.familybank.data.Common;
import com.zzk.familybank.data.DAO;
import com.zzk.familybank.data.DataModel;
import com.zzk.familybank.data.IntDate;
import com.zzk.familybank.data.Transaction;
import com.zzk.familybank.query.QueryActivity;
import com.zzk.familybank.statistic.StatisticActivity;
import com.zzk.familybank.transactions.TransactionActivity;

import java.util.Date;
import java.util.Iterator;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    Spinner spinnerHolder;
    TextView textViewLine1;
    RecyclerView recyclerViewAccounts;
    Button buttonTransaction;
    RecyclerView recyclerViewTransactions;
    LinearLayout linearLayoutShortcutsOutgo;
    LinearLayout linearLayoutShortcutsIncome;

    DataModel dataModel;
    SpinnerHolderAdapter spinnerHolderAdapter;
    AccountsDetailAdapter accountsDetailAdapter;
    TransactionsDetailAdapter transactionsDetailAdapter;

    Holder currentHolder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        spinnerHolder = findViewById(R.id.spinner_main_holder);
        textViewLine1 = findViewById(R.id.textView_line1);
        recyclerViewAccounts = findViewById(R.id.recyclerView_accounts);
        buttonTransaction = findViewById(R.id.button_transaction);
        recyclerViewTransactions = findViewById(R.id.recyclerView_transactions);
        linearLayoutShortcutsOutgo = findViewById(R.id.linear_layout_shortcuts_outgo);
        linearLayoutShortcutsIncome = findViewById(R.id.linear_layout_shortcuts_income);

        DAO.init(this);

        //  执行自动命令，只修改数据库中数据，所以必须在DataModel.getDataModel之前执行
        SharedPreferences preferences = getSharedPreferences("config", Context.MODE_PRIVATE);
        int lastExecuteDate = preferences.getInt("lastExecuteDate", -1);  //  格式为IntDate, 例如：20250324
        int today = IntDate.getToday();
        if(today > lastExecuteDate) {
            if(lastExecuteDate>0) {     //  不是第一次执行app
                DAO.executeAutomations(lastExecuteDate, today);
            }
            lastExecuteDate = today;
            SharedPreferences.Editor editor = preferences.edit();
            editor.putInt("lastExecuteDate", today);
            editor.commit();
        }

        dataModel = DataModel.getDataModel(this);
        dataModel.lastExecuteDate = lastExecuteDate;

        spinnerHolderAdapter = new SpinnerHolderAdapter();
        spinnerHolderAdapter.setChildList(dataModel.holderList);
        spinnerHolder.setAdapter(spinnerHolderAdapter);

        accountsDetailAdapter = new AccountsDetailAdapter(this);
        //accountsDetailAdapter.setChild(currentChild);
        recyclerViewAccounts.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewAccounts.setAdapter(accountsDetailAdapter);

        transactionsDetailAdapter = new TransactionsDetailAdapter(this, new MyRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent = new Intent(MainActivity.this, TransactionActivity.class);
                Transaction transaction = transactionsDetailAdapter.accountHolder.transactionList.get(position);
                intent.putExtra("transaction", transaction.toBundle());
                startActivityForResult(intent, 3);
            }
        });
        //transactionsDetailAdapter.setChild(currentChild);
        recyclerViewTransactions.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewTransactions.setAdapter(transactionsDetailAdapter);

        spinnerHolder.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Holder newHolder = spinnerHolderAdapter.holderList.get(i);
                setChild(newHolder);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        if(dataModel.holderList.size()>0) {
            Holder holder = dataModel.holderList.get(0);
            setChild(holder);
        }

        buttonTransaction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, TransactionActivity.class);
                Transaction transaction = new Transaction();
                transaction.id = -1;
                transaction.holder = currentHolder.id;
                transaction.account = currentHolder.accountList.get(0).id;
                transaction.category = 0;
                transaction.amount = 0;
                transaction.fromto = 1;
                transaction.date = new Date();
                transaction.notes = "";
                intent.putExtra("transaction", transaction.toBundle());
                startActivityForResult(intent, 3);
            }
        });
    }

    public void setChild(Holder holder){
        if(currentHolder !=null && currentHolder.id== holder.id) return;
        currentHolder = holder;
        updateHolderInfo();
        accountsDetailAdapter.setChild(holder);
        accountsDetailAdapter.notifyDataSetChanged();
        transactionsDetailAdapter.setAccountHolder(holder);
        transactionsDetailAdapter.notifyDataSetChanged();
        updateShortcuts();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = new MenuInflater(this);
        inflater.inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.menuitem_holderManagement:
                Intent intent = new Intent(this, HolderManagementActivity.class);
                startActivityForResult(intent, 1);
                break;
            case R.id.menuitem_accountManagement:
                if(currentHolder ==null) {
                    Toast.makeText(this, "请先创建儿童，才能管理该儿童的账户", Toast.LENGTH_SHORT).show();
                } else {
                    intent = new Intent(this, AccountsManagementActivity.class);
                    intent.putExtra(Common.HOLDER_ID, currentHolder.id);
                    startActivityForResult(intent, 2);
                }
                break;
            case R.id.menuitem_statistic:
                if(currentHolder ==null) {
                    Toast.makeText(this, "请先创建儿童，才能管理该儿童的账户", Toast.LENGTH_SHORT).show();
                } else {
                    intent = new Intent(this, StatisticActivity.class);
                    intent.putExtra(Common.HOLDER_ID, currentHolder.id);
                    startActivity(intent);
                }
                break;
            case R.id.menuitem_query:
                intent = new Intent(this, QueryActivity.class);
                startActivityForResult(intent, 4);
                break;
            case R.id.menuitem_automation:
                intent = new Intent(this, AutomationManagementActivity.class);
                startActivity(intent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent){
        if(resultCode==1) {   // Operation success
            if(requestCode==1) {    // Child Management
                updateSpinnerHolders();
            } else if(requestCode==2) { // Account Management,
                accountsDetailAdapter.notifyDataSetChanged();
            } else if(requestCode==3) { //  Transaction create, update
                accountsDetailAdapter.notifyDataSetChanged();
                transactionsDetailAdapter.notifyDataSetChanged();
                updateHolderInfo();
            } else if(requestCode==4) { // Query with transaction modified
                accountsDetailAdapter.notifyDataSetChanged();
                transactionsDetailAdapter.notifyDataSetChanged();
                updateHolderInfo();
            }
            updateShortcuts();
        }
        super.onActivityResult(requestCode, resultCode, intent);
    }

    void updateHolderInfo(){
        String line1 = "总余额： " + Common.balanceToString(currentHolder.totalBalance) + " 元";
        textViewLine1.setText(line1);
    }

    void updateSpinnerHolders(){
        spinnerHolderAdapter.notifyDataSetChanged();
        if(dataModel.holderList.size()>0) {
            Holder holder = dataModel.holderList.get(0);
            setChild(holder);
        }
    }

    View.OnClickListener shortcutsListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            TransactionShortcut shortcut = (TransactionShortcut) view;
            Category category = currentHolder.findCategoryById(shortcut.getCategoryId());
            Intent intent = new Intent(MainActivity.this, TransactionActivity.class);
            Transaction transaction = new Transaction();
            //transaction.id = -1;
            transaction.holder = currentHolder.id;
            transaction.account = category.account;
            transaction.category = category.id;
            //transaction.amount = 0;
            //transaction.fromto = 1;
            //transaction.date = new Date();
            //transaction.notes = "";
            intent.putExtra("transaction", transaction.toBundle());
            startActivityForResult(intent, 3);
        }
    };

    void updateShortcuts(){
        linearLayoutShortcutsOutgo.removeAllViews();
        linearLayoutShortcutsIncome.removeAllViews();
        if(currentHolder ==null) return;
        fillOneShortcutsSlot(linearLayoutShortcutsOutgo, -1, 3);
        fillOneShortcutsSlot(linearLayoutShortcutsIncome, 1, 4);
    }

    void fillOneShortcutsSlot(LinearLayout slot, int sign, int maxCount) {
        List<Category> recentCategories = currentHolder.findRecentCategories(sign);
        int count = maxCount;
        Iterator<Category> iter = recentCategories.iterator();
        while (iter.hasNext()) {
            Category category = iter.next();
            TransactionShortcut shortcut = new TransactionShortcut(this);
            shortcut.setText(category.name);
            shortcut.setTextColor(Common.getSignColor(sign));
            shortcut.setCategoryId(category.id);
            slot.addView(shortcut);
            count --;
            if(count==0) break;
            shortcut.setOnClickListener(shortcutsListener);
        }
    }
}

class SpinnerHolderAdapter extends BaseAdapter {
    List<Holder> holderList;

    public void setChildList(List<Holder> list) {
        holderList = list;
    }

    @Override
    public int getCount() {
        if(holderList !=null) return holderList.size();
        else return 0;
    }

    @Override
    public Object getItem(int i) {
        return holderList.get(i).name;
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
        textView.setText(holderList.get(i).name);

        return convertView;
    }

}
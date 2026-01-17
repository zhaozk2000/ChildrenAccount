package com.zzk.childrenbank;

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

import com.zzk.childrenbank.accounts.AccountsManagementActivity;
import com.zzk.childrenbank.automation.AutomationManagementActivity;
import com.zzk.childrenbank.children.ChildrenManagementActivity;
import com.zzk.childrenbank.data.Category;
import com.zzk.childrenbank.data.Child;
import com.zzk.childrenbank.data.Common;
import com.zzk.childrenbank.data.DAO;
import com.zzk.childrenbank.data.DataModel;
import com.zzk.childrenbank.data.IntDate;
import com.zzk.childrenbank.data.Transaction;
import com.zzk.childrenbank.statistic.StatisticActivity;
import com.zzk.childrenbank.transactions.TransactionActivity;

import java.util.Date;
import java.util.Iterator;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    Spinner spinnerChildren;
    TextView textViewLine1;
    RecyclerView recyclerViewAccounts;
    Button buttonTransaction;
    RecyclerView recyclerViewTransactions;
    LinearLayout linearLayoutShortcutsOutgo;
    LinearLayout linearLayoutShortcutsIncome;

    DataModel dataModel;
    SpinnerChildrenAdapter spinnerChildrenAdapter;
    AccountsDetailAdapter accountsDetailAdapter;
    TransactionsDetailAdapter transactionsDetailAdapter;

    Child currentChild;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        spinnerChildren = findViewById(R.id.spinner_children);
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
            SharedPreferences.Editor editor = preferences.edit();
            editor.putInt("lastExecuteDate", today);
            editor.commit();
        }

        dataModel = DataModel.getDataModel(this);

        spinnerChildrenAdapter = new SpinnerChildrenAdapter();
        spinnerChildrenAdapter.setChildList(dataModel.childList);
        spinnerChildren.setAdapter(spinnerChildrenAdapter);

        accountsDetailAdapter = new AccountsDetailAdapter(this);
        //accountsDetailAdapter.setChild(currentChild);
        recyclerViewAccounts.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewAccounts.setAdapter(accountsDetailAdapter);

        transactionsDetailAdapter = new TransactionsDetailAdapter(this, new MyRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent = new Intent(MainActivity.this, TransactionActivity.class);
                Transaction transaction = transactionsDetailAdapter.child.transactionList.get(position);
                intent.putExtra("transaction", transaction.toBundle());
                startActivityForResult(intent, 3);
            }
        });
        //transactionsDetailAdapter.setChild(currentChild);
        recyclerViewTransactions.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewTransactions.setAdapter(transactionsDetailAdapter);

        spinnerChildren.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Child newChild = spinnerChildrenAdapter.childList.get(i);
                setChild(newChild);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        if(dataModel.childList.size()>0) {
            Child child = dataModel.childList.get(0);
            setChild(child);
        }

        buttonTransaction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, TransactionActivity.class);
                Transaction transaction = new Transaction();
                transaction.id = -1;
                transaction.child = currentChild.id;
                transaction.account = currentChild.accountList.get(0).id;
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

    public void setChild(Child child){
        if(currentChild!=null && currentChild.id==child.id) return;
        currentChild = child;
        updateChildInfo();
        accountsDetailAdapter.setChild(child);
        accountsDetailAdapter.notifyDataSetChanged();
        transactionsDetailAdapter.setChild(child);
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
            case R.id.menuitem_childrenManagement:
                Intent intent = new Intent(this, ChildrenManagementActivity.class);
                startActivityForResult(intent, 1);
                break;
            case R.id.menuitem_accountManagement:
                if(currentChild==null) {
                    Toast.makeText(this, "请先创建儿童，才能管理该儿童的账户", Toast.LENGTH_SHORT).show();
                } else {
                    intent = new Intent(this, AccountsManagementActivity.class);
                    intent.putExtra(Common.CHILD_ID, currentChild.id);
                    startActivityForResult(intent, 2);
                }
                break;
            case R.id.menuitem_statistic:
                if(currentChild==null) {
                    Toast.makeText(this, "请先创建儿童，才能管理该儿童的账户", Toast.LENGTH_SHORT).show();
                } else {
                    intent = new Intent(this, StatisticActivity.class);
                    intent.putExtra(Common.CHILD_ID, currentChild.id);
                    startActivity(intent);
                }
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
                updateSpinnerChildren();
            } else if(requestCode==2) { // Account Management,
                accountsDetailAdapter.notifyDataSetChanged();
            } else if(requestCode==3) { //  Transaction create, update
                accountsDetailAdapter.notifyDataSetChanged();
                transactionsDetailAdapter.notifyDataSetChanged();
                updateChildInfo();
            }
            updateShortcuts();
        }
        super.onActivityResult(requestCode, resultCode, intent);
    }

    void updateChildInfo(){
        String line1 = "总余额： " + Common.balanceToString(currentChild.totalBalance) + " 元";
        textViewLine1.setText(line1);
    }

    void updateSpinnerChildren(){
        spinnerChildrenAdapter.notifyDataSetChanged();
        if(dataModel.childList.size()>0) {
            Child child = dataModel.childList.get(0);
            setChild(child);
        }
    }

    View.OnClickListener shortcutsListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            TransactionShortcut shortcut = (TransactionShortcut) view;
            Category category = currentChild.findCategoryById(shortcut.getCategoryId());
            Intent intent = new Intent(MainActivity.this, TransactionActivity.class);
            Transaction transaction = new Transaction();
            //transaction.id = -1;
            transaction.child = currentChild.id;
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
        if(currentChild==null) return;
        fillOneShortcutsSlot(linearLayoutShortcutsOutgo, -1, 3);
        fillOneShortcutsSlot(linearLayoutShortcutsIncome, 1, 4);
    }

    void fillOneShortcutsSlot(LinearLayout slot, int sign, int maxCount) {
        List<Category> recentCategories = currentChild.findRecentCategories(sign);
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

class SpinnerChildrenAdapter extends BaseAdapter {
    List<Child> childList;

    public void setChildList(List<Child> list) {
        childList = list;
    }

    @Override
    public int getCount() {
        if(childList!=null) return childList.size();
        else return 0;
    }

    @Override
    public Object getItem(int i) {
        return childList.get(i).name;
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
        textView.setText(childList.get(i).name);

        return convertView;
    }

}
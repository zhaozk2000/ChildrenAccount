package com.zzk.childrenbank.transactions;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.zzk.childrenbank.R;
import com.zzk.childrenbank.data.Account;
import com.zzk.childrenbank.data.Category;
import com.zzk.childrenbank.data.Child;
import com.zzk.childrenbank.data.Common;
import com.zzk.childrenbank.data.DataModel;
import com.zzk.childrenbank.data.FromTo;
import com.zzk.childrenbank.data.Transaction;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;

public class TransactionActivity extends AppCompatActivity {
    TextView textViewChild;
    Spinner spinnerAccount;
    Spinner spinnerCategory;
    Button buttonCategoryManagement;
    TextView textViewSign;
    EditText editTextAmount;
    TextView textViewFromTo;
    Spinner spinnerFromTo;
    Button buttonFromToManagement;
    TextView textViewDate;
    TextView textViewTime;
    EditText editTextNotes;
    Button buttonCreate;
    Button buttonDelete;
    Button buttonUpdate;

    DataModel dataModel;
    Transaction transaction;
    Child child;
    Account account;
    Category category;
    //boolean fromToInitinalize = true;
    int op;

    AccountsAdapter spinnerAccountsAdapter;
    CategoryAdapter spinnerCategoryAdapter;
    FromToAdapter spinnerFromToAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction);

        textViewChild = findViewById(R.id.textView_transaction_child);
        spinnerAccount = findViewById(R.id.spinner_transaction_account);
        spinnerCategory = findViewById(R.id.spinner_transaction_category);
        buttonCategoryManagement = findViewById(R.id.button_category_management);
        textViewSign = findViewById(R.id.textView_transaction_sign);
        editTextAmount = findViewById(R.id.editTextNumberDecimal_transaction_amount);
        textViewFromTo = findViewById(R.id.textView_transaction_fromto);
        spinnerFromTo = findViewById(R.id.spinner_transaction_fromto);
        buttonFromToManagement = findViewById(R.id.button_transaction_fromto_manage);
        textViewDate = findViewById(R.id.textView_transaction_date);
        textViewTime = findViewById(R.id.textView_transaction_time);
        editTextNotes = findViewById(R.id.editText_transaction_notes);
        buttonCreate = findViewById(R.id.button_transaction_create);
        buttonDelete = findViewById(R.id.button_transaction_delete);
        buttonUpdate = findViewById(R.id.button_transaction_update);

        dataModel = DataModel.getDataModel(this);

        Intent intent = getIntent();
        transaction = Transaction.fromBundle(intent.getBundleExtra("transaction"));
        if(transaction.id<0) {
            op = 1;     //  新建交易
            buttonCreate.setVisibility(View.VISIBLE);
            buttonDelete.setVisibility(View.INVISIBLE);
            buttonUpdate.setVisibility(View.INVISIBLE);
        } else {
            op = 2;     //  修改交易
            buttonCreate.setVisibility(View.INVISIBLE);
            buttonDelete.setVisibility(View.VISIBLE);
            buttonUpdate.setVisibility(View.VISIBLE);
        }

        child = dataModel.findChildById(transaction.child);

        textViewChild.setText(child.name);
        setDatetime(transaction.date);
        editTextAmount.setText(Common.balanceToString(transaction.amount));
        editTextNotes.setText(transaction.notes);

        spinnerAccountsAdapter = new AccountsAdapter();
        spinnerAccountsAdapter.setAccountList(child.accountList);
        spinnerAccount.setAdapter(spinnerAccountsAdapter);

        spinnerFromToAdapter = new FromToAdapter();         //  设置fromToList在Category更新时
        spinnerFromTo.setAdapter(spinnerFromToAdapter);
        buttonFromToManagement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TransactionActivity.this, FromToManagementActivity.class);
                intent.putExtra(Common.CHILD_ID, child.id);
                int fromTo = 1;
                if(category.sign==-1) fromTo = 2;
                intent.putExtra(Common.FROM_TO, fromTo);
                startActivityForResult(intent, 2);
            }
        });

        textViewDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar calendar = GregorianCalendar.getInstance();
                calendar.setTime(transaction.date);
                DatePickerDialog datePickerDialog = new DatePickerDialog(TransactionActivity.this,  new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
                        Calendar cal = GregorianCalendar.getInstance();
                        cal.setTime(transaction.date);
                        cal.set(Calendar.YEAR, year);
                        cal.set(Calendar.MONTH, month);
                        cal.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        Date datetime = cal.getTime();
                        setDatetime(datetime);
                    }
                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.show();
            }
        });

        textViewTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar calendar = GregorianCalendar.getInstance();
                calendar.setTime(transaction.date);
                TimePickerDialog timePickerDialog = new TimePickerDialog(TransactionActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute) {
                        Calendar cal = GregorianCalendar.getInstance();
                        cal.setTime(transaction.date);
                        cal.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        cal.set(Calendar.MINUTE, minute);
                        setDatetime(cal.getTime());
                    }
                }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true);
                timePickerDialog.show();
            }
        });

        spinnerCategoryAdapter = new CategoryAdapter();
        spinnerCategory.setAdapter(spinnerCategoryAdapter);
        buttonCategoryManagement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TransactionActivity.this, CategoryManagementActivity.class);
                intent.putExtra(Common.CHILD_ID, child.id);
                intent.putExtra(Common.ACCOUNT_ID, account.id);
                startActivityForResult(intent, 1);
            }
        });

        spinnerAccount.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                updateAccount(i);
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        spinnerCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                updateCategory(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        int accountIndex = spinnerAccountsAdapter.findAccountIndex(transaction.account);
        spinnerAccount.setSelection(accountIndex);
        updateAccount(accountIndex);


        buttonCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!validate()) return;
                int result = dataModel.createTransaction(transaction);
                if(result==0) {
                    Toast.makeText(getBaseContext(), "创建交易成功", Toast.LENGTH_SHORT).show();
                    setResult(1);
                    finish();
                } else {
                    Toast.makeText(getBaseContext(), "创建交易失败，错误代码：" + result, Toast.LENGTH_SHORT).show();
                }
            }
        });

        buttonUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!validate()) return;
                int result = dataModel.updateTransaction(transaction);
                if(result==0) {
                    Toast.makeText(getBaseContext(), "修改交易成功", Toast.LENGTH_SHORT).show();
                    setResult(1);
                    finish();
                } else {
                    Toast.makeText(getBaseContext(), "修改交易失败，错误代码：" + result, Toast.LENGTH_SHORT).show();
                }
            }
        });

        buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(TransactionActivity.this);
                builder.setIcon(R.drawable.ic_launcher_foreground);
                builder.setTitle("操作确认");
                builder.setMessage("确定要删除本条交易？");
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        int result = dataModel.deleteTransaction(child.id, transaction.id);
                        if(result==0) {
                            Toast.makeText(getBaseContext(), "删除交易成功", Toast.LENGTH_SHORT).show();
                            setResult(1);
                            finish();
                        } else {
                            Toast.makeText(getBaseContext(), "删除交易失败，错误代码：" + result, Toast.LENGTH_SHORT).show();
                        }
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
    }

    boolean validate(){
        //int accountIndex = spinnerAccount.getSelectedItemPosition();
        transaction.account = account.id;
        int categoryIndex = spinnerCategory.getSelectedItemPosition();
        transaction.category = account.categoryList.get(categoryIndex).id;

        int fromToIndex = spinnerFromTo.getSelectedItemPosition();
        transaction.fromto = spinnerFromToAdapter.fromToList.get(fromToIndex).id;

        //int amount = 0;
        String amountStr = editTextAmount.getText().toString().trim();
        if(amountStr.length()==0) {
            Toast.makeText(TransactionActivity.this, "<金额>不能为空", Toast.LENGTH_SHORT).show();
            return false;
        }
        try{
            transaction.amount = Common.stringToBalance(amountStr);
        } catch (NumberFormatException e) {
            Toast.makeText(TransactionActivity.this, "<金额>必须是数字格式", Toast.LENGTH_SHORT).show();
            return false;
        }

        transaction.notes = editTextNotes.getText().toString();

        return true;
    }

    void setDatetime(Date date){
        transaction.date = date;
        Calendar calendar = GregorianCalendar.getInstance();
        calendar.setTime(transaction.date);
        textViewDate.setText(Common.DateTimeToString(transaction.date, true, true, false));
        textViewTime.setText(String.format("%02d:%02d", calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE)));
    }

    void updateAccount(int index){
        Account newAccount = spinnerAccountsAdapter.accountList.get(index);
        if(account!=null && newAccount.id == account.id) return;
        account = newAccount;
        transaction.account = account.id;
        spinnerCategoryAdapter.setCategoryList(account.categoryList);
        spinnerCategoryAdapter.notifyDataSetChanged();
        int categoryIndex = spinnerCategoryAdapter.findCategoryIndex(transaction.category);
        if(categoryIndex>=0) {
            spinnerCategory.setSelection(categoryIndex);
            updateCategory(categoryIndex);
        } else {
            if(account.categoryList.size()>0) {
                spinnerCategory.setSelection(0);
                updateCategory(0);
            }
        }
    }

    void updateCategory(int index){
        Category newCategory = spinnerCategoryAdapter.categoryList.get(index);
        if(category!=null && category.id == newCategory.id) return;
        category = newCategory;
        transaction.category = category.id;
        transaction.fromto = category.default_fromto;
        editTextAmount.setText(Common.balanceToString(category.default_amount));
        editTextAmount.setTextColor(Common.getSignColor(category.sign));
        if(category.sign==1) {
            textViewSign.setText("+");
            textViewFromTo.setText("来源");
            spinnerFromToAdapter.setFromToList(child.fromList);
        } else {
            textViewSign.setText("-");
            textViewFromTo.setText("去向");
            spinnerFromToAdapter.setFromToList(child.toList);
        }
        int fromToIndex = 0;
        fromToIndex = spinnerFromToAdapter.findFromToIndex(transaction.fromto);
        spinnerFromTo.setSelection(fromToIndex);
        spinnerFromToAdapter.notifyDataSetChanged();

        textViewSign.setTextColor(Common.getSignColor(category.sign));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent){
        if(resultCode==1) {     // success return
            if(requestCode==1) { // category manage
                spinnerCategoryAdapter.notifyDataSetChanged();
            } else if(requestCode==2) { //  FromTo manage
                spinnerFromToAdapter.notifyDataSetChanged();
            }
        }
        super.onActivityResult(requestCode, resultCode, intent);
    }

}

class CategoryAdapter extends BaseAdapter {
    List<Category> categoryList;
    public void setCategoryList(List<Category> list) {
        categoryList = list;
    }

    @Override
    public int getCount() {
        if(categoryList!=null)  return categoryList.size();
        else return 0;
    }

    @Override
    public Object getItem(int i) {
        return categoryList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if(view==null) {
            view = View.inflate(viewGroup.getContext(), android.R.layout.simple_list_item_1, null);
        }
        Category category = categoryList.get(i);
        TextView textView1 = view.findViewById(android.R.id.text1);
        textView1.setText(category.name);
        textView1.setTextColor(Common.getSignColor(category.sign));
        return view;
    }

    public int findCategoryIndex(int categoryId) {
        int index = 0;
        Iterator<Category> iter = categoryList.iterator();
        while(iter.hasNext()) {
            Category category = iter.next();
            if(category.id == categoryId) return index;
            index ++;
        }
        return -1;
    }
}

class AccountsAdapter extends BaseAdapter {
    List<Account> accountList;

    public void setAccountList(List<Account> list) {
        accountList = list;
    }

    @Override
    public int getCount() {
        if(accountList!=null) return accountList.size();
        else return 0;
    }

    @Override
    public Object getItem(int i) {
        return accountList.get(i).name;
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
        textView.setText(accountList.get(i).name);

        return convertView;
    }

    public int findAccountIndex(int accountId) {
        int index = 0;
        Iterator<Account> iter = accountList.iterator();
        while(iter.hasNext()) {
            Account account = iter.next();
            if(account.id == accountId) return index;
            index ++;
        }
        return -1;
    }
}

class FromToAdapter extends BaseAdapter {
    List<FromTo> fromToList;

    public void setFromToList(List<FromTo> list) {
        fromToList = list;
    }

    @Override
    public int getCount() {
        if(fromToList!=null) return fromToList.size();
        else return 0;
    }

    @Override
    public Object getItem(int i) {
        return fromToList.get(i).name;
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
        textView.setText(fromToList.get(i).name);

        return convertView;
    }

    public int findFromToIndex(int fromToId) {
        int index = 0;
        Iterator<FromTo> iter = fromToList.iterator();
        while(iter.hasNext()) {
            FromTo fromTo = iter.next();
            if(fromTo.id == fromToId) return index;
            index ++;
        }
        return -1;
    }
}
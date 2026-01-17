package com.zzk.childrenbank.accounts;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.zzk.childrenbank.R;
import com.zzk.childrenbank.data.Account;
import com.zzk.childrenbank.data.Child;
import com.zzk.childrenbank.data.Common;
import com.zzk.childrenbank.data.DataModel;

import java.util.List;

public class AccountsManagementActivity extends AppCompatActivity {
    TextView textViewChild;
    Button buttonNewAccount, buttonDeleteAll;
    ListView listViewAccountList;

    AccountsAdapter accountsAdapter;
    int childId;
    Child child;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accounts_management);

        textViewChild = findViewById(R.id.textViewChild);
        buttonNewAccount = findViewById(R.id.button_newAccount);
        buttonDeleteAll = findViewById(R.id.button_deleteAllAccounts);
        listViewAccountList = findViewById(R.id.listview_accountList);

        setResult(0);

        childId = getIntent().getIntExtra(Common.CHILD_ID, -1);
        if(childId>=0) {
            child = DataModel.getDataModel(this).findChildById(childId);
            textViewChild.setText(child.name);
        } else {
            child = new Child();
        }

        accountsAdapter = new AccountsAdapter();
        accountsAdapter.setAccountList(child.accountList);
        listViewAccountList.setAdapter(accountsAdapter);

        buttonNewAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AccountsManagementActivity.this, AccountActivity.class);
                intent.putExtra(Common.CHILD_ID, child.id);
                startActivityForResult(intent, 1);
            }
        });

        listViewAccountList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Account account = accountsAdapter.accountList.get(i);
                Intent intent = new Intent(AccountsManagementActivity.this, AccountActivity.class);
                intent.putExtra(Common.CHILD_ID, account.child);
                intent.putExtra(Common.ACCOUNT_ID, account.id);
                startActivityForResult(intent, 1);
            }
        });

        listViewAccountList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                AlertDialog.Builder builder = new AlertDialog.Builder(AccountsManagementActivity.this);
                builder.setIcon(R.drawable.ic_launcher_foreground);
                builder.setTitle("操作确认");
                builder.setMessage("确定要删除账户：" + accountsAdapter.accountList.get(i).name + "?");
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        String name = accountsAdapter.accountList.get(i).name;
                        int result = DataModel.getDataModel(getBaseContext()).deleteAccount(child.id, name);
                        if(result==0) {
                            Toast.makeText(getBaseContext(), "删除账户<" + name + ">成功", Toast.LENGTH_SHORT).show();
                            accountsAdapter.notifyDataSetChanged();
                            setResult(1);
                        } else {
                            Toast.makeText(getBaseContext(), "删除失败，错误代码：" + result, Toast.LENGTH_SHORT).show();
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
                return true;
            }
        });

        buttonDeleteAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(AccountsManagementActivity.this);
                builder.setIcon(R.drawable.ic_launcher_foreground);
                builder.setTitle("操作确认");
                builder.setMessage("确定要删除<" + child.name + ">的所有账户？");
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        int result = DataModel.getDataModel(getBaseContext()).deleteAllAccounts(child.id);
                        if(result==0) {
                            Toast.makeText(getBaseContext(), "删除<" + child.name + ">的所有账户成功", Toast.LENGTH_SHORT).show();
                            accountsAdapter.notifyDataSetChanged();
                            setResult(1);
                        } else {
                            Toast.makeText(getBaseContext(), "删除失败，错误代码：" + result, Toast.LENGTH_SHORT).show();
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent){
        if(requestCode==1 && resultCode==1) {   // create Account, success
            accountsAdapter.notifyDataSetChanged();
            setResult(1);
        }
        super.onActivityResult(requestCode, resultCode, intent);
    }

}

class AccountsAdapter extends BaseAdapter {
    List<Account> accountList;
    public void setAccountList(List<Account> list) {
        accountList = list;
    }


    @Override
    public int getCount() {
        return accountList.size();
    }

    @Override
    public Object getItem(int i) {
        return accountList.get(i);
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
        TextView textView1 = view.findViewById(android.R.id.text1);
        textView1.setText(accountList.get(i).name);

        return view;
    }
}
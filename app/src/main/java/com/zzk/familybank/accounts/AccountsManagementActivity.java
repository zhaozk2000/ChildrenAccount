package com.zzk.familybank.accounts;

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

import com.zzk.familybank.R;
import com.zzk.familybank.data.Account;
import com.zzk.familybank.data.Holder;
import com.zzk.familybank.data.Common;
import com.zzk.familybank.data.DataModel;

import java.util.List;

public class AccountsManagementActivity extends AppCompatActivity {
    TextView textViewHolder;
    Button buttonNewAccount, buttonDeleteAll;
    ListView listViewAccountList;

    AccountsAdapter accountsAdapter;
    int holderId;
    Holder holder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accounts_management);

        textViewHolder = findViewById(R.id.textViewHolder);
        buttonNewAccount = findViewById(R.id.button_newAccount);
        buttonDeleteAll = findViewById(R.id.button_deleteAllAccounts);
        listViewAccountList = findViewById(R.id.listview_accountList);

        setResult(0);

        holderId = getIntent().getIntExtra(Common.HOLDER_ID, -1);
        if(holderId >=0) {
            holder = DataModel.getDataModel(this).findHolderById(holderId);
            textViewHolder.setText(holder.name);
        } else {
            holder = new Holder();
        }

        accountsAdapter = new AccountsAdapter();
        accountsAdapter.setAccountList(holder.accountList);
        listViewAccountList.setAdapter(accountsAdapter);

        buttonNewAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AccountsManagementActivity.this, AccountActivity.class);
                intent.putExtra(Common.HOLDER_ID, holder.id);
                startActivityForResult(intent, 1);
            }
        });

        listViewAccountList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Account account = accountsAdapter.accountList.get(i);
                Intent intent = new Intent(AccountsManagementActivity.this, AccountActivity.class);
                intent.putExtra(Common.HOLDER_ID, account.holder);
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
                        int result = DataModel.getDataModel(getBaseContext()).deleteAccount(holder.id, name);
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
                builder.setMessage("确定要删除<" + holder.name + ">的所有账户？");
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        int result = DataModel.getDataModel(getBaseContext()).deleteAllAccounts(holder.id);
                        if(result==0) {
                            Toast.makeText(getBaseContext(), "删除<" + holder.name + ">的所有账户成功", Toast.LENGTH_SHORT).show();
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
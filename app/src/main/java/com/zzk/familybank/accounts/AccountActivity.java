package com.zzk.familybank.accounts;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.zzk.familybank.R;
import com.zzk.familybank.data.Account;
import com.zzk.familybank.data.Holder;
import com.zzk.familybank.data.Common;
import com.zzk.familybank.data.DataModel;

public class AccountActivity extends AppCompatActivity {
    TextView textViewHolder;
    EditText editTextAccountName;
    Button buttonOk;

    DataModel dataModel;
    Holder holder;
    Account account;
    int op;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        textViewHolder = findViewById(R.id.textViewAccount_holder);
        editTextAccountName = findViewById(R.id.editText_accountName);
        buttonOk = findViewById(R.id.button_account_ok);

        dataModel = DataModel.getDataModel(AccountActivity.this);

        setResult(0);

        Intent intent = getIntent();
        int childId = intent.getIntExtra(Common.HOLDER_ID, -1);
        if(childId>=0) {
            holder = dataModel.findHolderById(childId);
            textViewHolder.setText(holder.name);
        }
        int accountId = intent.getIntExtra(Common.ACCOUNT_ID, -1);
        if(accountId==-1) {
            op = 1;
            buttonOk.setText("创建账户");
            account = null;
        } else {
            op = 2;
            buttonOk.setText("确定修改");
            account = holder.findAccountById(accountId);
            editTextAccountName.setText(account.name);
        }

        buttonOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = editTextAccountName.getText().toString().trim();
                if(name.length()>0) {
                    if(op==1) {
                        int result = dataModel.createAccount(holder.id, name);
                        if(result==0) {
                            Toast.makeText(getBaseContext(), "创建成功", Toast.LENGTH_SHORT).show();
                            setResult(1);
                            finish();
                        } else {
                            Toast.makeText(getBaseContext(), "创建失败，错误代码：" + result, Toast.LENGTH_SHORT).show();
                        }
                    } else if(op==2) {
                        int result = DataModel.getDataModel(getBaseContext()).updateAccount(holder.id, account.id, name);
                        if(result==0) {
                            Toast.makeText(getBaseContext(), "修改成功", Toast.LENGTH_SHORT).show();
                            setResult(1);
                            finish();
                        } else {
                            Toast.makeText(getBaseContext(), "修改失败，错误代码：" + result, Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    Toast.makeText(getBaseContext(), "账户名称不能为空", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
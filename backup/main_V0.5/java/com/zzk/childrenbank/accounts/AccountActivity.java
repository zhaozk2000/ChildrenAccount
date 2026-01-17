package com.zzk.childrenbank.accounts;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.zzk.childrenbank.R;
import com.zzk.childrenbank.data.Account;
import com.zzk.childrenbank.data.Child;
import com.zzk.childrenbank.data.Common;
import com.zzk.childrenbank.data.DataModel;

public class AccountActivity extends AppCompatActivity {
    TextView textViewChild;
    EditText editTextAccountName;
    Button buttonOk;

    DataModel dataModel;
    Child child;
    Account account;
    int op;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        textViewChild = findViewById(R.id.textViewAccount_child);
        editTextAccountName = findViewById(R.id.editText_accountName);
        buttonOk = findViewById(R.id.button_account_ok);

        dataModel = DataModel.getDataModel(AccountActivity.this);

        setResult(0);

        Intent intent = getIntent();
        int childId = intent.getIntExtra(Common.CHILD_ID, -1);
        if(childId>=0) {
            child = dataModel.findChildById(childId);
            textViewChild.setText(child.name);
        }
        int accountId = intent.getIntExtra(Common.ACCOUNT_ID, -1);
        if(accountId==-1) {
            op = 1;
            buttonOk.setText("创建账户");
            account = null;
        } else {
            op = 2;
            buttonOk.setText("确定修改");
            account = child.findAccountById(accountId);
            editTextAccountName.setText(account.name);
        }

        buttonOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = editTextAccountName.getText().toString().trim();
                if(name.length()>0) {
                    if(op==1) {
                        int result = dataModel.createAccount(child.id, name);
                        if(result==0) {
                            Toast.makeText(getBaseContext(), "创建成功", Toast.LENGTH_SHORT).show();
                            setResult(1);
                            finish();
                        } else {
                            Toast.makeText(getBaseContext(), "创建失败，错误代码：" + result, Toast.LENGTH_SHORT).show();
                        }
                    } else if(op==2) {
                        int result = DataModel.getDataModel(getBaseContext()).updateAccount(child.id, account.id, name);
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
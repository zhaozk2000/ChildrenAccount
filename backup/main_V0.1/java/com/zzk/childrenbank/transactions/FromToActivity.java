package com.zzk.childrenbank.transactions;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.zzk.childrenbank.R;
import com.zzk.childrenbank.accounts.AccountActivity;
import com.zzk.childrenbank.data.Account;
import com.zzk.childrenbank.data.Child;
import com.zzk.childrenbank.data.Common;
import com.zzk.childrenbank.data.DataModel;
import com.zzk.childrenbank.data.FromTo;

public class FromToActivity extends AppCompatActivity {
    TextView textViewChild;
    TextView textViewFromToName;
    EditText editTextFromToName;
    Button buttonOk;

    DataModel dataModel;
    Child child;
    //Account account;
    FromTo fromTo;
    int fromToFlag;     //  1=来源；2=去向
    String title;
    int op;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_from_to);
        textViewChild = findViewById(R.id.textView_fromto_child);
        textViewFromToName = findViewById(R.id.textView_fromto_name);
        editTextFromToName = findViewById(R.id.editText_fromto_name);
        buttonOk = findViewById(R.id.button_fromto_ok);

        dataModel = DataModel.getDataModel(FromToActivity.this);

        setResult(0);

        Intent intent = getIntent();
        int childId = intent.getIntExtra(Common.CHILD_ID, -1);
        if(childId>=0) {
            child = dataModel.findChildById(childId);
            textViewChild.setText(child.name);
        }
        int fromtoId = intent.getIntExtra(Common.FROMTO_ID, -1);
        if(fromtoId<0) {
            op = 1;
            buttonOk.setText("创建");
            fromTo = null;
            if(fromtoId==-1) {  //  创建来源
                fromToFlag = 1;
            } else {            //  创建去向
                fromToFlag = 2;
            }
        } else {
            op = 2;
            buttonOk.setText("确定修改");
            fromTo = child.findFromToById(fromtoId);
            editTextFromToName.setText(fromTo.name);
            fromToFlag = fromTo.fromto;
        }
        if(fromToFlag==1) {
            title = "资金来源：";
        } else {
            title = "资金去向：";
        }
        textViewFromToName.setText(title);

        buttonOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = editTextFromToName.getText().toString().trim();
                if(name.length()>0) {
                    if(op==1) {
                        int result = dataModel.createFromTo(child.id, name, fromToFlag);
                        if(result==0) {
                            Toast.makeText(getBaseContext(), "创建成功", Toast.LENGTH_SHORT).show();
                            setResult(1);
                            finish();
                        } else {
                            Toast.makeText(getBaseContext(), "创建失败，错误代码：" + result, Toast.LENGTH_SHORT).show();
                        }
                    } else if(op==2) {
                        int result = dataModel.updateFromTo(child.id, fromTo.id, name);
                        if(result==0) {
                            Toast.makeText(getBaseContext(), "修改成功", Toast.LENGTH_SHORT).show();
                            setResult(1);
                            finish();
                        } else {
                            Toast.makeText(getBaseContext(), "修改失败，错误代码：" + result, Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    Toast.makeText(getBaseContext(), title + "不能为空", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
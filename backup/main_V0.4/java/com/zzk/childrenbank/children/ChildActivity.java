package com.zzk.childrenbank.children;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.zzk.childrenbank.R;
import com.zzk.childrenbank.data.Child;
import com.zzk.childrenbank.data.Common;
import com.zzk.childrenbank.data.DataModel;

public class ChildActivity extends AppCompatActivity {
    EditText editTextName;
    Button buttonOk;

    int op;
    Child child;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_child);
        editTextName = findViewById(R.id.editTextName);
        buttonOk = findViewById(R.id.button_child_ok);

        setResult(0);

        Intent intent = getIntent();
        int childId = intent.getIntExtra(Common.CHILD_ID, -1);
        if(childId==-1) {
            op = 1;
            buttonOk.setText("创建");
            child = null;
        } else {
            op = 2;
            buttonOk.setText("修改");
            child = DataModel.getDataModel(getBaseContext()).findChildById(childId);
            editTextName.setText(child.name);
        }

        buttonOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = editTextName.getText().toString().trim();
                if(name.length()>0) {
                    if(op==1) {
                        int result = DataModel.getDataModel(getBaseContext()).createChild(name);
                        if(result==0) {
                            Toast.makeText(getBaseContext(), "创建成功", Toast.LENGTH_SHORT).show();
                            setResult(1);
                            finish();
                        } else {
                            Toast.makeText(getBaseContext(), "创建失败，错误代码：" + result, Toast.LENGTH_SHORT).show();
                        }
                    } else if(op==2) {
                        int result = DataModel.getDataModel(getBaseContext()).updateChild(childId, name);
                        if(result==0) {
                            Toast.makeText(getBaseContext(), "修改成功", Toast.LENGTH_SHORT).show();
                            setResult(1);
                            finish();
                        } else {
                            Toast.makeText(getBaseContext(), "修改失败，错误代码：" + result, Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    Toast.makeText(getBaseContext(), "姓名不能为空", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
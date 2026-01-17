package com.zzk.familybank.holder;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.zzk.familybank.R;
import com.zzk.familybank.data.Holder;
import com.zzk.familybank.data.Common;
import com.zzk.familybank.data.DataModel;

public class HolderActivity extends AppCompatActivity {
    EditText editTextName;
    Button buttonOk;

    int op;
    Holder holder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_holder);
        editTextName = findViewById(R.id.editTextName);
        buttonOk = findViewById(R.id.button_holder_ok);

        setResult(0);

        Intent intent = getIntent();
        int holderId = intent.getIntExtra(Common.HOLDER_ID, -1);
        if(holderId==-1) {
            op = 1;
            buttonOk.setText("创建");
            holder = null;
        } else {
            op = 2;
            buttonOk.setText("修改");
            holder = DataModel.getDataModel(getBaseContext()).findHolderById(holderId);
            editTextName.setText(holder.name);
        }

        buttonOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = editTextName.getText().toString().trim();
                if(name.length()>0) {
                    if(op==1) {
                        int result = DataModel.getDataModel(getBaseContext()).createHolder(name);
                        if(result==0) {
                            Toast.makeText(getBaseContext(), "创建成功", Toast.LENGTH_SHORT).show();
                            setResult(1);
                            finish();
                        } else {
                            Toast.makeText(getBaseContext(), "创建失败，错误代码：" + result, Toast.LENGTH_SHORT).show();
                        }
                    } else if(op==2) {
                        int result = DataModel.getDataModel(getBaseContext()).updateHolder(holderId, name);
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
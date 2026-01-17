package com.zzk.familybank.transactions;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.zzk.familybank.R;
import com.zzk.familybank.data.Common;
import com.zzk.familybank.data.DataModel;
import com.zzk.familybank.data.FromTo;
import com.zzk.familybank.statistic.DropdownList;

public class FromToActivity extends AppCompatActivity {
    //TextView textViewChild;
    TextView textViewName;
    EditText editTextName;
    TextView textViewDirection;
    DropdownList dropdownListDirection;
    Button buttonOk;

    DataModel dataModel;
    //Child child;
    //Account account;
    FromTo fromTo;
    FromTo exist;
    //int fromToFlag;     //  1=来源；2=去向
    String title;
    int op;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_from_to);
        //textViewChild = findViewById(R.id.textView_fromto_child);
        textViewName = findViewById(R.id.textView_fromto_name);
        editTextName = findViewById(R.id.editText_fromto_name);
        textViewDirection = findViewById(R.id.textView_fromto_direction);
        dropdownListDirection = findViewById(R.id.dropdownList_fromto_direction);
        buttonOk = findViewById(R.id.button_fromto_ok);

        dataModel = DataModel.getDataModel(FromToActivity.this);

        dropdownListDirection.addItem(FromTo.FROMTOSTRING[0]);
        dropdownListDirection.addItem(FromTo.FROMTOSTRING[1]);
        dropdownListDirection.addItem(FromTo.FROMTOSTRING[2]);
        dropdownListDirection.updateData();
        dropdownListDirection.setSelection(2);

        setResult(0);

        Intent intent = getIntent();
        fromTo = new FromTo();
/*
        int childId = intent.getIntExtra(Common.CHILD_ID, -1);
        if(childId>=0) {
            child = dataModel.findChildById(childId);
            textViewChild.setText(child.name);
        }
 */
        int fromtoId = intent.getIntExtra(Common.FROMTO_ID, -1);
        if(fromtoId<0) {
            op = 1;
            buttonOk.setText("创建");
/*
            if(fromtoId==-1) {  //  创建来源
                fromToFlag = 1;
            } else {            //  创建去向
                fromToFlag = 2;
            }
 */
        } else {
            op = 2;
            fromTo.id = fromtoId;
            buttonOk.setText("确定修改");
            exist = dataModel.findFromToById(fromtoId);
            editTextName.setText(exist.name);
            dropdownListDirection.setSelectedItem(exist.getFromToString());
            //fromToFlag = fromTo.fromto;
        }
/*
        if(fromToFlag==1) {
            title = "资金来源：";
        } else {
            title = "资金去向：";
        }
        textViewFromToName.setText(title);
 */

        buttonOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!validate()) return;
                if(op==1) {
                    int result = dataModel.createFromTo(fromTo);
                    if(result==0) {
                        Toast.makeText(getBaseContext(), "创建成功", Toast.LENGTH_SHORT).show();
                        setResult(1);
                        finish();
                    } else {
                        Toast.makeText(getBaseContext(), "创建失败，错误代码：" + result, Toast.LENGTH_SHORT).show();
                    }
                } else if(op==2) {
                    int result = dataModel.updateFromTo(fromTo);
                    if(result==0) {
                        Toast.makeText(getBaseContext(), "修改成功", Toast.LENGTH_SHORT).show();
                        setResult(1);
                        finish();
                    } else {
                        Toast.makeText(getBaseContext(), "修改失败，错误代码：" + result, Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    boolean validate() {
        fromTo.name = editTextName.getText().toString().trim();
        if(fromTo.name.length()==0) {
            Toast.makeText(this, "<对方名称>不能为空", Toast.LENGTH_SHORT).show();
            return false;
        }
        FromTo sameName = dataModel.findFromToByName(fromTo.name);
        if(sameName!=null) {
            if(op==1 || (op==2 && sameName.id!=exist.id)) {
                Toast.makeText(this, "名称<"+fromTo.name+">已经存在", Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        fromTo.direction = dropdownListDirection.getSelectedItemPosition()+1;
        return true;
    }
}
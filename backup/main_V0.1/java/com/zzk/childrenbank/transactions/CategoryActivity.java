package com.zzk.childrenbank.transactions;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.zzk.childrenbank.R;
import com.zzk.childrenbank.data.Account;
import com.zzk.childrenbank.data.Category;
import com.zzk.childrenbank.data.Child;
import com.zzk.childrenbank.data.Common;
import com.zzk.childrenbank.data.DataModel;
import com.zzk.childrenbank.data.FromTo;

public class CategoryActivity extends AppCompatActivity {
    EditText editTextName;
    RadioButton radioButtonIncrease;
    RadioButton radioButtonDecrease;
    EditText editTextDefaultAmount;
    TextView textViewDefaultFromto;
    Spinner spinnerDefaultFromto;
    Button buttonFromtoManagement;
    EditText editTextNotes;
    Button buttonOk;

    DataModel dataModel;
    Child child;
    Category category;
    Account account;
    int op;
    FromToAdapter spinnerFromToAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        editTextName = findViewById(R.id.editText_category_name);
        radioButtonIncrease = findViewById(R.id.radioButton_increase);
        radioButtonDecrease = findViewById(R.id.radioButton_decrease);
        editTextDefaultAmount = findViewById(R.id.editText_category_defaultAmount);
        textViewDefaultFromto = findViewById(R.id.textView_category_defaultfromto);
        spinnerDefaultFromto = findViewById(R.id.spinner_category_defaultfromto);
        buttonFromtoManagement = findViewById(R.id.button_category_fromtomanagement);
        editTextNotes = findViewById(R.id.editText_category_notes);
        buttonOk = findViewById(R.id.button_category_ok);

        radioButtonIncrease.setTextColor(Common.COLOR_INCREASE);
        radioButtonDecrease.setTextColor(Common.COLOR_DECREASE);
        radioButtonIncrease.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                updateSign();
            }
        });
        radioButtonDecrease.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                updateSign();
            }
        });

        dataModel = DataModel.getDataModel(this);
        Intent intent = getIntent();
        int childId = intent.getIntExtra(Common.CHILD_ID, -1);
        int accountId = intent.getIntExtra(Common.ACCOUNT_ID, -1);
        int categoryId = intent.getIntExtra(Common.CATEGORY_ID, -1);

        child = dataModel.findChildById(childId);

        if(categoryId==-1) {
            category = new Category();
            category.child = childId;
            category.account = accountId;
            radioButtonIncrease.setChecked(true);
            op = 1;
        } else {
            category = dataModel.findCategoryById(childId, accountId, categoryId).cloneCategory();  //  clone防止修改失败
            account = dataModel.findAccountById(category.child, category.account);
            op = 2;
            editTextName.setText(category.name);
            if(category.sign==1) radioButtonIncrease.setChecked(true);
            else radioButtonDecrease.setChecked(true);
            editTextDefaultAmount.setText(Common.balanceToString(category.default_amount));
            editTextNotes.setText(category.notes);
        }

        spinnerFromToAdapter = new FromToAdapter();         //  设置fromToList在Category更新时
        spinnerDefaultFromto.setAdapter(spinnerFromToAdapter);
        buttonFromtoManagement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CategoryActivity.this, FromToManagementActivity.class);
                intent.putExtra(Common.CHILD_ID, childId);
                int fromTo = 1;
                if(radioButtonDecrease.isChecked()) fromTo = 2;
                intent.putExtra(Common.FROM_TO, fromTo);
                startActivityForResult(intent, 2);
            }
        });

        updateSign();

        buttonOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = editTextName.getText().toString().trim();
                if(name.length()==0) {
                    Toast.makeText(CategoryActivity.this, "<条目名称>不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }

                int defaultAmount = 0;
                String defaultAmountStr = editTextDefaultAmount.getText().toString().trim();
                if(defaultAmountStr.length()==0) {
                    defaultAmountStr = "0.00";
                    editTextDefaultAmount.setText(defaultAmountStr);
                }
                try{
                    defaultAmount = Common.stringToBalance(defaultAmountStr);
                } catch (NumberFormatException e) {
                    Toast.makeText(CategoryActivity.this, "<缺省金额>必须是数字格式", Toast.LENGTH_SHORT).show();
                    return;
                }

                int defaultFromtoIndex = spinnerDefaultFromto.getSelectedItemPosition();
                if(defaultFromtoIndex<0) {
                    Toast.makeText(CategoryActivity.this, "<缺省来源/去向>不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                FromTo fromTo = spinnerFromToAdapter.fromToList.get(defaultFromtoIndex);
                category.default_fromto = fromTo.id;

                String notes = editTextNotes.getText().toString().trim();

                category.name = name;
                category.default_amount = defaultAmount;
                category.notes = notes;
                if(radioButtonIncrease.isChecked()) category.sign = 1;
                else category.sign = -1;

                if(op==1) {
                    int result = dataModel.createCategory(category);
                    if(result==0) {
                        Toast.makeText(getBaseContext(), "创建成功", Toast.LENGTH_SHORT).show();
                        setResult(1);
                        finish();
                    } else {
                        Toast.makeText(getBaseContext(), "创建失败，错误代码：" + result, Toast.LENGTH_SHORT).show();
                    }
                } else if(op==2) {
                    int result = dataModel.updateCategory(category);
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

    private void updateSign() {
        if(radioButtonIncrease.isChecked()) {
            editTextDefaultAmount.setTextColor(Common.getSignColor(1));
            spinnerFromToAdapter.setFromToList(child.fromList);
            textViewDefaultFromto.setText("缺省来源：");
        }
        else {
            editTextDefaultAmount.setTextColor(Common.getSignColor(-1));
            spinnerFromToAdapter.setFromToList(child.toList);
            textViewDefaultFromto.setText("缺省去向：");
        }
        spinnerFromToAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent){
        if(resultCode==1) {     // success return
            if(requestCode==2) { //  FromTo manage
                spinnerFromToAdapter.notifyDataSetChanged();
            }
        }
        super.onActivityResult(requestCode, resultCode, intent);
    }

}
package com.zzk.familybank.automation;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.zzk.familybank.R;
import com.zzk.familybank.data.Account;
import com.zzk.familybank.data.Automation;
import com.zzk.familybank.data.Category;
import com.zzk.familybank.data.Holder;
import com.zzk.familybank.data.Common;
import com.zzk.familybank.data.DAO;
import com.zzk.familybank.data.DataModel;
import com.zzk.familybank.data.FromTo;
import com.zzk.familybank.data.IntDate;
import com.zzk.familybank.statistic.DropdownList;
import com.zzk.familybank.transactions.CategoryManagementActivity;
import com.zzk.familybank.transactions.FromToManagementActivity;

import java.util.Iterator;
import java.util.List;

public class AutomationActivity extends AppCompatActivity {
    DropdownList dropdownListType;

    TextView textViewAccount1;
    DropdownList dropdownListAccount1Holder;
    DropdownList dropdownListAccount1;
    DropdownList dropdownListAccount1Category;
    DropdownList dropdownListAccount1FromTo;

    DropdownList dropdownListAccount2Holder;
    DropdownList dropdownListAccount2;
    DropdownList dropdownListAccount2Category;
    DropdownList dropdownListAccount2FromTo;

    DropdownList dropdownListPeriod;
    EditText editTextDate;

    EditText editTextAmount;
    EditText editTextRate;

    Button buttonCreate;
    Button buttonDelete;
    Button buttonUpdate;
    Button buttonExecute;

    LinearLayout linearLayoutAmount;
    LinearLayout linearLayoutRate;

    Button buttonAccount1Category;
    Button buttonAccount2Category;
    Button buttonFromTo;

    DataModel dataModel;
    Automation automation;
    Automation originAutomation;
    int op;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_automation);

        dropdownListType = findViewById(R.id.dropdownList_automation_type);
        textViewAccount1 = findViewById(R.id.textView_automation_account1);
        dropdownListAccount1Holder = findViewById(R.id.dropdownList_automation_account1_holder);
        dropdownListAccount1 = findViewById(R.id.dropdownList_automation_account1);
        dropdownListAccount1Category = findViewById(R.id.dropdownList_automation_account1_category);
        dropdownListAccount1FromTo = findViewById(R.id.dropdownList_automation_account1_fromto);
        dropdownListAccount2Holder = findViewById(R.id.dropdownList_automation_account2_holder);
        dropdownListAccount2 = findViewById(R.id.dropdownList_automation_account2);
        dropdownListAccount2Category = findViewById(R.id.dropdownList_automation_account2_category);
        dropdownListAccount2FromTo = findViewById(R.id.dropdownList_automation_account2_fromto);
        dropdownListPeriod = findViewById(R.id.dropdownList_automation_period);
        editTextDate = findViewById(R.id.editTextNumberDecimal_automation_date);
        editTextAmount = findViewById(R.id.editTextNumberDecimal_automation_amount);
        editTextRate = findViewById(R.id.editTextNumber_automation_rate);
        buttonCreate = findViewById(R.id.button_automation_create);
        buttonDelete = findViewById(R.id.button_automation_delete);
        buttonUpdate = findViewById(R.id.button_automation_update);
        buttonExecute = findViewById(R.id.button_automation_execute);
        linearLayoutAmount = findViewById(R.id.linearlayout_automation_amount);
        linearLayoutRate = findViewById(R.id.linearlayout_automation_rate);
        buttonAccount1Category = findViewById(R.id.button_automation_account1_category);
        buttonAccount2Category = findViewById(R.id.button_automation_account2_category);
        buttonFromTo = findViewById(R.id.button_automation_fromto);

        buttonExecute.setVisibility(View.GONE);

        dataModel = DataModel.getDataModel(this);
        automation = new Automation();

        dropdownListType.addItem(Automation.typeString[1]);
        dropdownListType.addItem(Automation.typeString[2]);
        dropdownListType.updateData();

        dropdownListPeriod.addItem(Automation.periodString[0]);
        dropdownListPeriod.addItem(Automation.periodString[1]);
        dropdownListPeriod.updateData();

        dropdownListType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                int pos = dropdownListType.getSelectedItemPosition();
                if(pos==0) {
                    textViewAccount1.setText("结息账户");
                    linearLayoutRate.setVisibility(View.VISIBLE);
                    linearLayoutAmount.setVisibility(View.GONE);
                    dropdownListAccount1Category.setVisibility(View.GONE);
                    dropdownListAccount1FromTo.setVisibility(View.GONE);
                    buttonAccount1Category.setVisibility(View.GONE);
                } else if(pos==1) {
                    textViewAccount1.setText("取出账户");
                    linearLayoutRate.setVisibility(View.GONE);
                    linearLayoutAmount.setVisibility(View.VISIBLE);
                    dropdownListAccount1Category.setVisibility(View.VISIBLE);
                    dropdownListAccount1FromTo.setVisibility(View.VISIBLE);
                    buttonAccount1Category.setVisibility(View.VISIBLE);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {  }
        });

        fillFromToItems(dropdownListAccount1FromTo, 2);
        fillFromToItems(dropdownListAccount2FromTo, 1);

        fillHolderItems(dropdownListAccount1Holder);
        dropdownListAccount1Holder.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                int pos = dropdownListAccount1Holder.getSelectedItemPosition();
                Holder holder = dataModel.holderList.get(pos);
                fillAccountItems(dropdownListAccount1, holder);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) { }
        });
        dropdownListAccount1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                int pos = dropdownListAccount1Holder.getSelectedItemPosition();
                Holder holder = dataModel.holderList.get(pos);
                pos = dropdownListAccount1.getSelectedItemPosition();
                Account account = holder.accountList.get(pos);
                fillCategoryItems(dropdownListAccount1Category, account);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {  }
        });

        fillHolderItems(dropdownListAccount2Holder);
        dropdownListAccount2Holder.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                int pos = dropdownListAccount2Holder.getSelectedItemPosition();
                Holder holder = dataModel.holderList.get(pos);
                fillAccountItems(dropdownListAccount2, holder);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) { }
        });
        dropdownListAccount2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                int pos = dropdownListAccount2Holder.getSelectedItemPosition();
                Holder holder = dataModel.holderList.get(pos);
                pos = dropdownListAccount2.getSelectedItemPosition();
                Account account = holder.accountList.get(pos);
                fillCategoryItems(dropdownListAccount2Category, account);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {  }
        });


        int automationId = getIntent().getIntExtra(Common.AUTOMATION_ID, -1);
        if(automationId>=0) {       //  修改
            op = 2;
            originAutomation = dataModel.findAutomationById(automationId);
            automation.id = originAutomation.id;
            buttonCreate.setVisibility(View.INVISIBLE);
            buttonDelete.setVisibility(View.VISIBLE);
            buttonUpdate.setVisibility(View.VISIBLE);

            dropdownListType.setSelectedItem(originAutomation.getTypeString());
            dropdownListPeriod.setSelectedItem(originAutomation.getPeriodString());

            Holder holder = dataModel.findHolderByAccount(originAutomation.account1);
            dropdownListAccount1Holder.setSelectedItem(holder.name);
            fillAccountItems(dropdownListAccount1, holder);
            Account account = holder.findAccountById(originAutomation.account1);
            dropdownListAccount1.setSelectedItem(account.name);
            fillCategoryItems(dropdownListAccount1Category, account);
            Category category = account.findCategoryById(originAutomation.account1Category);
            if(category!=null) dropdownListAccount1Category.setSelectedItem(category.name);
            FromTo fromTo = dataModel.findFromToById(originAutomation.account1FromTo);
            if(fromTo!=null) dropdownListAccount1FromTo.setSelectedItem(fromTo.name);

            holder = dataModel.findHolderByAccount(originAutomation.account2);
            dropdownListAccount2Holder.setSelectedItem(holder.name);
            fillAccountItems(dropdownListAccount2, holder);
            account = holder.findAccountById(originAutomation.account2);
            dropdownListAccount2.setSelectedItem(account.name);
            fillCategoryItems(dropdownListAccount2Category, account);
            category = account.findCategoryById(originAutomation.account2Category);
            if(category!=null) dropdownListAccount2Category.setSelectedItem(category.name);
            fromTo = dataModel.findFromToById(originAutomation.account2FromTo);
            if(fromTo!=null) dropdownListAccount2FromTo.setSelectedItem(fromTo.name);

            editTextDate.setText(Integer.toString(originAutomation.date));
            editTextAmount.setText(Common.balanceToString(originAutomation.int_data1));
            editTextRate.setText(Float.toString(originAutomation.float_data1));
        } else {        //  新建
            op = 1;
            originAutomation = null;
            buttonCreate.setVisibility(View.VISIBLE);
            buttonDelete.setVisibility(View.INVISIBLE);
            buttonUpdate.setVisibility(View.INVISIBLE);
            dropdownListPeriod.setSelectedItem("月");
            editTextDate.setText("1");
            editTextAmount.setText("100.00");
            editTextRate.setText("5.0");
        }

        buttonFromTo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AutomationActivity.this, FromToManagementActivity.class);
                startActivityForResult(intent, 1);
            }
        });

        buttonAccount1Category.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int pos = dropdownListAccount1Holder.getSelectedItemPosition();
                Holder holder = dataModel.holderList.get(pos);
                pos = dropdownListAccount1.getSelectedItemPosition();
                Account account = holder.accountList.get(pos);
                Intent intent = new Intent(AutomationActivity.this, CategoryManagementActivity.class);
                intent.putExtra(Common.HOLDER_ID, holder.id);
                intent.putExtra(Common.ACCOUNT_ID, account.id);
                startActivityForResult(intent, 2);
            }
        });

        buttonAccount2Category.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int pos = dropdownListAccount2Holder.getSelectedItemPosition();
                Holder holder = dataModel.holderList.get(pos);
                pos = dropdownListAccount2.getSelectedItemPosition();
                Account account = holder.accountList.get(pos);
                Intent intent = new Intent(AutomationActivity.this, CategoryManagementActivity.class);
                intent.putExtra(Common.HOLDER_ID, holder.id);
                intent.putExtra(Common.ACCOUNT_ID, account.id);
                startActivityForResult(intent, 3);
            }
        });

        buttonCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!validate()) return;
                String description = automation.getDescribeString(dataModel);
                AlertDialog.Builder builder = new AlertDialog.Builder(AutomationActivity.this);
                builder.setIcon(R.drawable.ic_launcher_foreground);
                builder.setTitle("操作确认");
                builder.setMessage("确定要创建自动指令：" + description);
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        int result = dataModel.createAutomation(automation);
                        if(result==0) {
                            Toast.makeText(getBaseContext(), "创建自动指令成功", Toast.LENGTH_SHORT).show();
                            setResult(1);
                            finish();
                        } else {
                            Toast.makeText(getBaseContext(), "创建自动指令失败，错误代码：" + result, Toast.LENGTH_SHORT).show();
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

        buttonUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!validate()) return;
                if(automation.equals(originAutomation)) {
                    Toast.makeText(getBaseContext(), "设置参数与原指令完全相同，不需要修改", Toast.LENGTH_SHORT).show();
                    return;
                }
                String description = automation.getDescribeString(dataModel);
                AlertDialog.Builder builder = new AlertDialog.Builder(AutomationActivity.this);
                builder.setIcon(R.drawable.ic_launcher_foreground);
                builder.setTitle("操作确认");
                builder.setMessage("确定将原指令：("+ originAutomation.getDescribeString(dataModel) + ")修改为：(" + description + ")");
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        int result = dataModel.updateAutomation(automation);
                        if(result==0) {
                            Toast.makeText(getBaseContext(), "修改自动指令成功", Toast.LENGTH_SHORT).show();
                            setResult(1);
                            finish();
                        } else {
                            Toast.makeText(getBaseContext(), "修改自动指令失败，错误代码：" + result, Toast.LENGTH_SHORT).show();
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

        buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(AutomationActivity.this);
                builder.setIcon(R.drawable.ic_launcher_foreground);
                builder.setTitle("操作确认");
                builder.setMessage("确定要删除本条指令？"+originAutomation.getDescribeString(dataModel));
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        int result = dataModel.deleteAutomation(originAutomation.id);
                        if(result==0) {
                            Toast.makeText(getBaseContext(), "删除指令成功", Toast.LENGTH_SHORT).show();
                            setResult(1);
                            finish();
                        } else {
                            Toast.makeText(getBaseContext(), "删除指令失败，错误代码：" + result, Toast.LENGTH_SHORT).show();
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

        buttonExecute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!validate()) return;
                AlertDialog.Builder builder = new AlertDialog.Builder(AutomationActivity.this);
                builder.setIcon(R.drawable.ic_launcher_foreground);
                builder.setTitle("操作确认");
                builder.setMessage("确定要手动执行本条指令？"+originAutomation.getDescribeString(dataModel));
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        DAO.executeAutomation(automation, IntDate.getToday());
                        Toast.makeText(getBaseContext(), "手动执行指令成功", Toast.LENGTH_SHORT).show();
                        setResult(1);
                        finish();
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

    void fillHolderItems(DropdownList dropdownList) {
        dropdownList.clearItem();
        Iterator<Holder> iter = dataModel.holderList.iterator();
        while(iter.hasNext()) {
            Holder holder = iter.next();
            dropdownList.addItem(holder.name);
        }
        dropdownList.updateData();
    }

    void fillAccountItems(DropdownList dropdownList, Holder holder) {
        dropdownList.clearItem();
        if(holder ==null) {
            dropdownList.updateData();
            return;
        }
        Iterator<Account> iter = holder.accountList.iterator();
        while(iter.hasNext()) {
            Account account = iter.next();
            dropdownList.addItem(account.name);
        }
        dropdownList.updateData();
    }

    void fillCategoryItems(DropdownList dropdownList, Account account) {
        dropdownList.clearItem();
        if(account==null) {
            dropdownList.updateData();
            return;
        }
        Iterator<Category> iter = account.categoryList.iterator();
        while(iter.hasNext()) {
            Category category = iter.next();
            dropdownList.addItem(category.name);
        }
        dropdownList.updateData();
    }

    void fillFromToItems(DropdownList dropdownList, int direction) {
        dropdownList.clearItem();
        List<FromTo> fromToList = dataModel.fromToList;
        if(direction==1) fromToList = dataModel.fromList;
        else if(direction==2) fromToList = dataModel.toList;
        Iterator<FromTo> iter = fromToList.iterator();
        while(iter.hasNext()) {
            FromTo fromTo = iter.next();
            dropdownList.addItem(fromTo.name);
        }
        dropdownList.updateData();
    }

    boolean validate() {
        automation.type = dropdownListType.getSelectedItemPosition() + 1;

        int holderPos = dropdownListAccount1Holder.getSelectedItemPosition();
        Holder holder = dataModel.holderList.get(holderPos);
        int accountPos = dropdownListAccount1.getSelectedItemPosition();
        Account account = holder.accountList.get(accountPos);
        automation.account1 = account.id;
        if(automation.type==1) {    //  结息
            automation.account1Category = 0;
            automation.account1FromTo = 0;
        } else if(automation.type==2) {     //  取零花
            int categoryPos = dropdownListAccount1Category.getSelectedItemPosition();
            Category category = account.categoryList.get(categoryPos);
            automation.account1Category = category.id;
            int fromtoPos = dropdownListAccount1FromTo.getSelectedItemPosition();
            FromTo fromTo = dataModel.toList.get(fromtoPos);
            automation.account1FromTo = fromTo.id;
        }

        holderPos = dropdownListAccount2Holder.getSelectedItemPosition();
        holder = dataModel.holderList.get(holderPos);
        accountPos = dropdownListAccount2.getSelectedItemPosition();
        account = holder.accountList.get(accountPos);
        automation.account2 = account.id;
        int categoryPos = dropdownListAccount2Category.getSelectedItemPosition();
        Category category = account.categoryList.get(categoryPos);
        automation.account2Category = category.id;
        int fromtoPos = dropdownListAccount2FromTo.getSelectedItemPosition();
        FromTo fromTo = dataModel.fromList.get(fromtoPos);
        automation.account2FromTo = fromTo.id;

        automation.period = dropdownListPeriod.getSelectedItemPosition();
        try{
            automation.date = Integer.parseInt(editTextDate.getText().toString());
        } catch (NumberFormatException e) {
            Toast.makeText(this, "<执行日期>格式错误", Toast.LENGTH_SHORT).show();
        }
        int month = automation.date / 100 % 100;
        int day = automation.date % 100;
        if(day<1 || day>28) {
            Toast.makeText(this, "<执行日期>必须在1~28之间", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(automation.period ==0) {       //  每年执行
            if(month<1 || month>12) {
                Toast.makeText(this, "<执行月份>必须在1~12之间", Toast.LENGTH_SHORT).show();
                return false;
            }
        }

        if(automation.type==1) {    //  结息
            automation.float_data1 = Float.parseFloat(editTextRate.getText().toString());
        } else if(automation.type==2){    //  取零花钱
            try{
                automation.int_data1 = Math.round(Float.parseFloat(editTextAmount.getText().toString())*100);
            } catch (NumberFormatException e) {
                Toast.makeText(this, "<执行金额>格式错误", Toast.LENGTH_SHORT).show();
            }
        }

        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent){
        if(resultCode==1) {     // success return
            if(requestCode==1) {    //  FromTo manage
                fillFromToItems(dropdownListAccount1FromTo, 2);
                fillFromToItems(dropdownListAccount2FromTo, 1);
            } else if(requestCode==2) { //  Account1 category manage
                int pos = dropdownListAccount1Holder.getSelectedItemPosition();
                Holder holder = dataModel.holderList.get(pos);
                pos = dropdownListAccount1.getSelectedItemPosition();
                Account account = holder.accountList.get(pos);
                fillCategoryItems(dropdownListAccount1Category, account);
                fillFromToItems(dropdownListAccount1FromTo, 2);
                fillFromToItems(dropdownListAccount2FromTo, 1);
            } else if(requestCode==3) {
                int pos = dropdownListAccount2Holder.getSelectedItemPosition();
                Holder holder = dataModel.holderList.get(pos);
                pos = dropdownListAccount2.getSelectedItemPosition();
                Account account = holder.accountList.get(pos);
                fillCategoryItems(dropdownListAccount2Category, account);
                fillFromToItems(dropdownListAccount1FromTo, 2);
                fillFromToItems(dropdownListAccount2FromTo, 1);
            }
        }
        super.onActivityResult(requestCode, resultCode, intent);
    }


}
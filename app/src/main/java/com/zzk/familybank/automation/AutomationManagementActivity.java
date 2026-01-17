package com.zzk.familybank.automation;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.zzk.familybank.R;
import com.zzk.familybank.data.Automation;
import com.zzk.familybank.data.Common;
import com.zzk.familybank.data.DAO;
import com.zzk.familybank.data.DataModel;
import com.zzk.familybank.tablescroll2d.TableCellOnClickListener;
import com.zzk.familybank.tablescroll2d.TableScroll2dFragment;

public class AutomationManagementActivity extends AppCompatActivity {
    Button buttonNewAutomation;
    Button buttonClearAll;
    Button buttonTestAll;
    TextView textViewTips;
    TableScroll2dFragment tableFragment;

    DataModel dataModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_automation_management);

        buttonNewAutomation = findViewById(R.id.button_newAutomation);
        buttonClearAll = findViewById(R.id.button_clearAllAutomation);
        textViewTips = findViewById(R.id.textView_automation_management_tips);
        tableFragment = (TableScroll2dFragment)getSupportFragmentManager().findFragmentById(R.id.fragment_table_automation_list);
        buttonTestAll = findViewById(R.id.button_testAllAutomation);

        dataModel = DataModel.getDataModel(getBaseContext());
        String s = "点击条目编辑，自动命令目前执行到" + dataModel.lastExecuteDate;
        textViewTips.setText(s);

        tableFragment.setLeftTopTitle("", "名称");
        tableFragment.setCellSize(400, 100);
        tableFragment.setFirstCellSize(300, 200);
        int[] columnWidth = {0};
        tableFragment.setColumnWidth(columnWidth);
        tableFragment.setCellOnClickListener(new TableCellOnClickListener() {
            @Override
            public void onClick(int x, int y, String content) {
                //String s = "Cell onClick " + x + "," + y + ", " + content;
                //Toast.makeText(AutomationManagementActivity.this, s, Toast.LENGTH_SHORT).show();
                if(y<1) return;
                Automation automation = dataModel.automationList.get(y-1);
                Intent intent = new Intent(AutomationManagementActivity.this, AutomationActivity.class);
                intent.putExtra(Common.AUTOMATION_ID, automation.id);
                startActivityForResult(intent, 1);
            }
        });
        fillTableData();

        buttonNewAutomation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AutomationManagementActivity.this, AutomationActivity.class);
                startActivityForResult(intent, 1);
            }
        });

        buttonClearAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(AutomationManagementActivity.this);
                builder.setIcon(R.drawable.ic_launcher_foreground);
                builder.setTitle("操作确认");
                builder.setMessage("确定要删除所有自动指令？");
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        int result = DataModel.getDataModel(getBaseContext()).deleteAllAutomation();
                        if(result==0) {
                            Toast.makeText(getBaseContext(), "删除所有指令成功", Toast.LENGTH_SHORT).show();
                            fillTableData();
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

        buttonTestAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DAO.testAllAutomations();
            }
        });
    }

    void fillTableData(){
        //  数据项填充到表格
        String[][] cells = new String[dataModel.automationList.size()+1][2];
        cells[0][0] = "";
        cells[0][1] = "命令内容";

        for(int i=0; i<dataModel.automationList.size(); i++) {
            Automation automation = dataModel.automationList.get(i);
            cells[i+1][0] = automation.getTypeString();
            cells[i+1][1] = automation.getDescribeString(dataModel);
        }

        tableFragment.setData(cells);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent){
        if(requestCode==1 && resultCode==1) {   // create Child, success
            fillTableData();
        }
        super.onActivityResult(requestCode, resultCode, intent);
    }

}
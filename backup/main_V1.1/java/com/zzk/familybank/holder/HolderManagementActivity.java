package com.zzk.familybank.holder;

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
import com.zzk.familybank.data.Holder;
import com.zzk.familybank.data.Common;
import com.zzk.familybank.data.DataModel;

import java.util.List;

public class HolderManagementActivity extends AppCompatActivity {
    Button buttonNewHolder, buttonDeleteAll;
    ListView listViewHolderList;

    HolderAdapter holderAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_holder_management);

        buttonNewHolder = findViewById(R.id.button_newHolder);
        buttonDeleteAll = findViewById(R.id.button_deleteAllHolders);
        listViewHolderList = findViewById(R.id.listview_holderList);

        holderAdapter = new HolderAdapter();
        holderAdapter.setChildList(DataModel.getDataModel(this).holderList);
        listViewHolderList.setAdapter(holderAdapter);

        buttonNewHolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HolderManagementActivity.this, HolderActivity.class);
                startActivityForResult(intent, 1);
            }
        });

        //  点击条目进入编辑
        listViewHolderList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                int id = holderAdapter.holderList.get(i).id;
                Intent intent = new Intent(HolderManagementActivity.this, HolderActivity.class);
                intent.putExtra(Common.HOLDER_ID, id);
                startActivityForResult(intent, 1);
            }
        });

        //  长按条目进行删除
        listViewHolderList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                AlertDialog.Builder builder = new AlertDialog.Builder(HolderManagementActivity.this);
                builder.setIcon(R.drawable.ic_launcher_foreground);
                builder.setTitle("操作确认");
                builder.setMessage("确定要删除" + Common.HOLDER_STRING + "<" + holderAdapter.holderList.get(i).name + ">?");
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        String name = holderAdapter.holderList.get(i).name;
                        int result = DataModel.getDataModel(getBaseContext()).deleteHolder(name);
                        if(result==0) {
                            Toast.makeText(getBaseContext(), "删除" + Common.HOLDER_STRING+ "<" + name + ">成功", Toast.LENGTH_SHORT).show();
                            holderAdapter.notifyDataSetChanged();
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
                AlertDialog.Builder builder = new AlertDialog.Builder(HolderManagementActivity.this);
                builder.setIcon(R.drawable.ic_launcher_foreground);
                builder.setTitle("操作确认");
                builder.setMessage("确定要删除所有"+ Common.HOLDER_STRING + "？");
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        int result = DataModel.getDataModel(getBaseContext()).deleteAllHolder();
                        if(result==0) {
                            Toast.makeText(getBaseContext(), "删除所有"+ Common.HOLDER_STRING + "成功", Toast.LENGTH_SHORT).show();
                            holderAdapter.notifyDataSetChanged();
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
        if(requestCode==1 && resultCode==1) {   // create Child, success
            holderAdapter.notifyDataSetChanged();
            setResult(1);
        }
        super.onActivityResult(requestCode, resultCode, intent);
    }

}

class HolderAdapter extends BaseAdapter {
    List<Holder> holderList;
    public void setChildList(List<Holder> list) {
        holderList = list;
    }

    @Override
    public int getCount() {
        return holderList.size();
    }

    @Override
    public Object getItem(int i) {
        return holderList.get(i).name;
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
        textView1.setText(holderList.get(i).name);

        return view;
    }
}
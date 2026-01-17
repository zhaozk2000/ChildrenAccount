package com.zzk.childrenbank.children;

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

import com.zzk.childrenbank.R;
import com.zzk.childrenbank.data.Child;
import com.zzk.childrenbank.data.Common;
import com.zzk.childrenbank.data.DataModel;

import java.util.List;

public class ChildrenManagementActivity extends AppCompatActivity {
    Button buttonNewChild, buttonDeleteAll;
    ListView listViewChildList;

    ChildrenAdapter childrenAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_children_management);

        buttonNewChild = findViewById(R.id.button_newChild);
        buttonDeleteAll = findViewById(R.id.button_deleteAllChildren);
        listViewChildList = findViewById(R.id.listview_childList);

        childrenAdapter = new ChildrenAdapter();
        childrenAdapter.setChildList(DataModel.getDataModel(this).childList);
        listViewChildList.setAdapter(childrenAdapter);

        buttonNewChild.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ChildrenManagementActivity.this, ChildActivity.class);
                startActivityForResult(intent, 1);
            }
        });

        //  点击条目进入编辑
        listViewChildList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                int id = childrenAdapter.childList.get(i).id;
                Intent intent = new Intent(ChildrenManagementActivity.this, ChildActivity.class);
                intent.putExtra(Common.CHILD_ID, id);
                startActivityForResult(intent, 1);
            }
        });

        //  长按条目进行删除
        listViewChildList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ChildrenManagementActivity.this);
                builder.setIcon(R.drawable.ic_launcher_foreground);
                builder.setTitle("操作确认");
                builder.setMessage("确定要删除儿童：" + childrenAdapter.childList.get(i).name + "?");
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        String name = childrenAdapter.childList.get(i).name;
                        int result = DataModel.getDataModel(getBaseContext()).deleteChild(name);
                        if(result==0) {
                            Toast.makeText(getBaseContext(), "删除儿童<" + name + ">成功", Toast.LENGTH_SHORT).show();
                            childrenAdapter.notifyDataSetChanged();
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
                AlertDialog.Builder builder = new AlertDialog.Builder(ChildrenManagementActivity.this);
                builder.setIcon(R.drawable.ic_launcher_foreground);
                builder.setTitle("操作确认");
                builder.setMessage("确定要删除所有儿童？");
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        int result = DataModel.getDataModel(getBaseContext()).deleteAllChild();
                        if(result==0) {
                            Toast.makeText(getBaseContext(), "删除所有儿童成功", Toast.LENGTH_SHORT).show();
                            childrenAdapter.notifyDataSetChanged();
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
            childrenAdapter.notifyDataSetChanged();
        }
        super.onActivityResult(requestCode, resultCode, intent);
    }

}

class ChildrenAdapter extends BaseAdapter {
    List<Child> childList;
    public void setChildList(List<Child> list) {
        childList = list;
    }

    @Override
    public int getCount() {
        return childList.size();
    }

    @Override
    public Object getItem(int i) {
        return childList.get(i).name;
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
        textView1.setText(childList.get(i).name);

        return view;
    }
}
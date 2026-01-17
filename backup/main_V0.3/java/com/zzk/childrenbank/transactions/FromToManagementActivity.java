package com.zzk.childrenbank.transactions;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.zzk.childrenbank.R;
import com.zzk.childrenbank.data.Child;
import com.zzk.childrenbank.data.Common;
import com.zzk.childrenbank.data.DataModel;
import com.zzk.childrenbank.data.FromTo;


public class FromToManagementActivity extends AppCompatActivity {
    TextView textViewChild;
    TextView textViewFromTo;
    Button buttonAdd;
    ListView listViewFromTo;

    Child child;
    int fromToFlag;  //  1=来源；2=去向
    FromToAdapter fromToAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_from_to_management);

        textViewChild = findViewById(R.id.textView_fromto_child);
        textViewFromTo = findViewById(R.id.textView_fromto_fromto);
        buttonAdd = findViewById(R.id.button_fromto_add);
        listViewFromTo = findViewById(R.id.listView_fromto_list);

        Intent intent = getIntent();
        int childId = intent.getIntExtra(Common.CHILD_ID, -1);
        child = DataModel.getDataModel(this).findChildById(childId);
        textViewChild.setText(child.name);

        fromToAdapter = new FromToAdapter();
        fromToFlag = intent.getIntExtra(Common.FROM_TO, 1);
        if(fromToFlag ==1) {
            textViewFromTo.setText("来源");
            fromToAdapter.setFromToList(child.fromList);
        } else {
            textViewFromTo.setText("去向");
            fromToAdapter.setFromToList(child.toList);
        }
        listViewFromTo.setAdapter(fromToAdapter);

        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(FromToManagementActivity.this, FromToActivity.class);
                intent.putExtra(Common.CHILD_ID, child.id);
                intent.putExtra(Common.FROMTO_ID, 0 - fromToFlag);
                startActivityForResult(intent, 1);
            }
        });

        listViewFromTo.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                FromTo fromTo = fromToAdapter.fromToList.get(i);
                Intent intent = new Intent(FromToManagementActivity.this, FromToActivity.class);
                intent.putExtra(Common.CHILD_ID, child.id);
                intent.putExtra(Common.FROMTO_ID, fromTo.id);
                startActivityForResult(intent, 1);
            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent){
        if(requestCode==1 && resultCode==1) {   // category add, success
            fromToAdapter.notifyDataSetChanged();
            setResult(1);
        }
        super.onActivityResult(requestCode, resultCode, intent);
    }
}
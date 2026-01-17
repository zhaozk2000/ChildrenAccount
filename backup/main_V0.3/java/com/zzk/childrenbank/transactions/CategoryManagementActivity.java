package com.zzk.childrenbank.transactions;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.zzk.childrenbank.R;
import com.zzk.childrenbank.data.Account;
import com.zzk.childrenbank.data.Category;
import com.zzk.childrenbank.data.Child;
import com.zzk.childrenbank.data.Common;
import com.zzk.childrenbank.data.DataModel;
import com.zzk.childrenbank.data.FromTo;

import java.util.List;

public class CategoryManagementActivity extends AppCompatActivity {
    TextView textViewChild;
    TextView textViewAccount;
    Button buttonAdd;
    ListView listViewCategories;

    Child child;
    Account account;
    CategoryDetailAdapter categoryDetailAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_management);

        textViewChild = findViewById(R.id.textView_category_child);
        textViewAccount = findViewById(R.id.textView_category_account);
        buttonAdd = findViewById(R.id.button_category_add);
        listViewCategories = findViewById(R.id.listView_category_list);

        Intent intent = getIntent();
        int childId = intent.getIntExtra(Common.CHILD_ID, -1);
        child = DataModel.getDataModel(this).findChildById(childId);
        int accountId = intent.getIntExtra(Common.ACCOUNT_ID, -1);
        account = child.findAccountById(accountId);

        textViewChild.setText(child.name);
        textViewAccount.setText(account.name);

        categoryDetailAdapter = new CategoryDetailAdapter();
        categoryDetailAdapter.setCategoryList(child, account.categoryList);
        listViewCategories.setAdapter(categoryDetailAdapter);

        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CategoryManagementActivity.this, CategoryActivity.class);
                intent.putExtra(Common.CHILD_ID, child.id);
                intent.putExtra(Common.ACCOUNT_ID, account.id);
                startActivityForResult(intent, 1);
            }
        });

        listViewCategories.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Category category = categoryDetailAdapter.categoryList.get(i);
                Intent intent = new Intent(CategoryManagementActivity.this, CategoryActivity.class);
                intent.putExtra(Common.CHILD_ID, child.id);
                intent.putExtra(Common.ACCOUNT_ID, account.id);
                intent.putExtra(Common.CATEGORY_ID, category.id);
                startActivityForResult(intent, 1);
            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent){
        if(requestCode==1 && resultCode==1) {   // category add, success
            categoryDetailAdapter.notifyDataSetChanged();
            setResult(1);
        }
        super.onActivityResult(requestCode, resultCode, intent);
    }
}

class CategoryDetailAdapter extends BaseAdapter {
    static String[] signString = {"增加余额", "减少余额"};

    Child child;
    List<Category> categoryList;
    public void setCategoryList(Child child, List<Category> list) {
        this.child = child;
        categoryList = list;
    }

    @Override
    public int getCount() {
        return categoryList.size();
    }

    @Override
    public Object getItem(int i) {
        return categoryList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if(view==null) {
            view = View.inflate(viewGroup.getContext(), android.R.layout.simple_list_item_2, null);
        }
        Category category = categoryList.get(i);
        TextView textView1 = view.findViewById(android.R.id.text1);
        textView1.setText(category.name);
        TextView textView2 = view.findViewById(android.R.id.text2);
        String s = category.sign==1 ? signString[0] : signString[1];
        String txt = s + ",\t\t缺省金额：" + Common.balanceToString(category.default_amount) + "元,\t\t";
        if(category.sign==1) txt += "缺省来源：";
        else txt += "缺省去向：";
        FromTo fromTo = child.findFromToById(category.default_fromto);
        txt += fromTo.name;

        textView2.setText(txt);

        textView1.setTextColor(Common.getSignColor(category.sign));
        textView2.setTextColor(Common.getSignColor(category.sign));

        return view;
    }
}
package com.zzk.familybank;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.zzk.familybank.data.Account;
import com.zzk.familybank.data.Holder;
import com.zzk.familybank.data.Common;

public class AccountsDetailAdapter extends RecyclerView.Adapter<AccountsDetailAdapter.RecordHolder> {
    Activity activity;

    //List<Account> accountList;
    Holder holder;

    public AccountsDetailAdapter(Activity activity){
        this.activity = activity;
    }

    public void setChild(Holder holder) {
        this.holder = holder;
    }

    @NonNull
    @Override
    public AccountsDetailAdapter.RecordHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(activity).inflate(R.layout.account_list_item, parent, false);
        RecordHolder recordHolder = new RecordHolder(view);
        return recordHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull AccountsDetailAdapter.RecordHolder holder, int position) {
        if(this.holder !=null) {
            Account account = this.holder.accountList.get(position);
            if(holder.accountName!=null) holder.accountName.setText(account.name);
            if(holder.accountBalance!=null) holder.accountBalance.setText(Common.balanceToString(account.balance));
        }
    }

    @Override
    public int getItemCount() {
        if(holder !=null) {
            return holder.accountList.size();
        } else {
            return 0;
        }
    }

    public static class RecordHolder extends RecyclerView.ViewHolder {
        TextView accountName;
        TextView accountBalance;

        public RecordHolder(@NonNull View itemView) {
            super(itemView);
            accountName = itemView.findViewById(R.id.textView_accountName);
            accountBalance = itemView.findViewById(R.id.textView_accountBalance);
        }
    }
}

package com.zzk.childrenbank;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.zzk.childrenbank.data.Account;
import com.zzk.childrenbank.data.Category;
import com.zzk.childrenbank.data.Child;
import com.zzk.childrenbank.data.Common;
import com.zzk.childrenbank.data.DataModel;
import com.zzk.childrenbank.data.FromTo;
import com.zzk.childrenbank.data.Transaction;

public class TransactionsDetailAdapter extends RecyclerView.Adapter<TransactionsDetailAdapter.RecordHolder>{
    Activity activity;

    Child child;
    DataModel dataModel;

    MyRecyclerViewItemClickListener itemClickListener;

    public TransactionsDetailAdapter(Activity activity, MyRecyclerViewItemClickListener itemClickListener) {
        this.activity = activity;
        dataModel = DataModel.getDataModel(activity);
        this.itemClickListener = itemClickListener;
    }

    public void setChild(Child child) {
        this.child = child;
    }

    @NonNull
    @Override
    public RecordHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(activity).inflate(R.layout.transaction_list_item, parent, false);
        TransactionsDetailAdapter.RecordHolder recordHolder = new TransactionsDetailAdapter.RecordHolder(view);
        recordHolder.itemClickListener = itemClickListener;
        view.setOnClickListener(recordHolder);
        return recordHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecordHolder holder, int position) {
        if(child!=null) {
            Transaction transaction = child.transactionList.get(position);
            Account account = child.findAccountById(transaction.account);
            Category category = account.findCategoryById(transaction.category);
            holder.category.setText(category.name);
//            holder.amount.setText(Common.signedAmountToString(category.sign, transaction.amount));
            holder.amount.setText(Common.balanceToString(transaction.amount));
            holder.account.setText(account.name);
            holder.date.setText(Common.DateTimeToStringShort(transaction.date, true, false, false));
            //holder.notes.setText(transaction.notes);
            FromTo fromTo = dataModel.findFromToById(transaction.fromto);
            //holder.notes.setText(fromTo.name);
            holder.notes.setText(Common.balanceToString(transaction.afterBalance));

            int sign = transaction.amount>=0 ? 1 : -1;
            holder.amount.setTextColor(Common.getSignColor(sign));
        }
    }

    @Override
    public int getItemCount() {
        if(child!=null) return child.transactionList.size();
        else return 0;
    }

    public static class RecordHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView category;
        TextView amount;
        TextView account;
        TextView date;
        TextView notes;

        MyRecyclerViewItemClickListener itemClickListener;

        public RecordHolder(@NonNull View itemView) {
            super(itemView);
            category = itemView.findViewById(R.id.textView_transaction_item_category);
            amount = itemView.findViewById(R.id.textView_transaction_item_amount);
            account = itemView.findViewById(R.id.textView_transaction_item_account);
            date = itemView.findViewById(R.id.textView_transaction_item_date);
            notes = itemView.findViewById(R.id.textView_transaction_item_notes);
        }

        @Override
        public void onClick(View view) {
            if(itemClickListener!=null) {
                itemClickListener.onItemClick(view, getAdapterPosition());
            }
        }
    }
}

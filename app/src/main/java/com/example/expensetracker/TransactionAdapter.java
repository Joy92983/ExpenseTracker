package com.example.expensetracker;

import android.content.Context;
import android.view.*;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.ViewHolder> {
    private List<Transaction> transactions;
    private Context context;

    // ✅ Fix: Accept and assign context properly
    public TransactionAdapter(Context context, List<Transaction> transactions) {
        this.context = context;
        this.transactions = transactions;
    }

    public void setTransactions(List<Transaction> transactions) {
        this.transactions = transactions;
        notifyDataSetChanged();
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // ✅ Safer context from parent
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_transaction, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Transaction t = transactions.get(position);
        holder.category.setText(t.getCategory());
        holder.note.setText(t.getNote());
        holder.date.setText(t.getDate());
        holder.amount.setText("KES " + t.getAmount());
        holder.type.setText(t.getType());

        // Color: green for income, red for expense
        holder.amount.setTextColor(t.getType().equals("Income") ? 0xFF4CAF50 : 0xFFF44336);
    }

    @Override
    public int getItemCount() {
        return transactions != null ? transactions.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView category, note, date, amount , type;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            category = itemView.findViewById(R.id.textViewCategory);
            note = itemView.findViewById(R.id.textViewNote);
            date = itemView.findViewById(R.id.textViewDate);
            amount = itemView.findViewById(R.id.textViewAmount);
            type = itemView.findViewById(R.id.textViewType);

        }
    }
}

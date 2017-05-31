package com.example.zeeshan.expensecalculator;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import java.util.List;

class Adapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Object> items;

    private final int LEDGER = 0;
    private final int TYPE = 1;
    private final int EXPENSE = 2;
    Context context;

    Adapter(Context context, List<Object> items) {
        this.context = context;
        this.items = items;
    }

    @Override
    public int getItemCount() {
        return this.items.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (items.get(position) instanceof Ledger) {
            return LEDGER;
        } else if (items.get(position) instanceof Type) {
            return TYPE;
        } else if (items.get(position) instanceof Expense) {
            return EXPENSE;
        }
        return -1;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());

        switch (viewType) {
            case EXPENSE:
                View v1 = inflater.inflate(R.layout.adapter_view_items, viewGroup, false);
                viewHolder = new ViewHolderExpense(v1);
                break;
            case LEDGER:
                View v2 = inflater.inflate(R.layout.adapter_view_items, viewGroup, false);
                viewHolder = new ViewHolderLedger(v2);
                break;
            case TYPE:
                View v3 = inflater.inflate(R.layout.adapter_view_items, viewGroup, false);
                viewHolder = new ViewHolderType(v3);
                break;
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        switch (viewHolder.getItemViewType()) {
            case EXPENSE:
                ViewHolderExpense view1 = (ViewHolderExpense) viewHolder;
                configureViewHolderExpense(view1, position);
                break;
            case LEDGER:
                ViewHolderLedger view2 = (ViewHolderLedger) viewHolder;
                configureViewHolderLedger(view2, position);
                break;
            case TYPE:
                ViewHolderType view3 = (ViewHolderType) viewHolder;
                configureViewHolderType(view3, position);
                break;
        }
    }

    private void configureViewHolderExpense(ViewHolderExpense viewHolder, int position) {
        Expense expense = (Expense) items.get(position);
        if (expense != null) {
            viewHolder.getName().setText(expense.getTitle());
            viewHolder.getDate().setText(expense.getDate());
            viewHolder.getType().setText(expense.getType());
            String value = context.getString(R.string.sum, expense.getValue());
            viewHolder.getValue().setText(value);
        }
    }

    private void configureViewHolderLedger(ViewHolderLedger viewHolder, int position) {
        Ledger ledger = (Ledger) items.get(position);
        if (ledger != null) {
            viewHolder.getName().setText(ledger.getTitle());
            viewHolder.getValue().setText(ledger.getValue());
        }
    }

    private void configureViewHolderType(ViewHolderType viewHolder, int position) {
        Type type = (Type) items.get(position);
        if (type != null) {
            viewHolder.getName().setText(type.getTitle());
        }
    }
}

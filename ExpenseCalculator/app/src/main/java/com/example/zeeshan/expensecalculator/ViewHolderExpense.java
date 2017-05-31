package com.example.zeeshan.expensecalculator;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

class ViewHolderExpense extends RecyclerView.ViewHolder {

    private TextView name, value, date, type;

    ViewHolderExpense(View v) {
        super(v);
        name = (TextView) itemView.findViewById(R.id.name);
        value = (TextView) itemView.findViewById(R.id.value);
        date = (TextView) itemView.findViewById(R.id.date);
        type = (TextView) itemView.findViewById(R.id.type);
    }

    TextView getName() {
        return name;
    }

    TextView getValue() {
        return value;
    }

    TextView getDate() {
        return date;
    }

    TextView getType() {
        return type;
    }
}

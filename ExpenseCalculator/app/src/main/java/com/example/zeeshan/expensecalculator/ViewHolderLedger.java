package com.example.zeeshan.expensecalculator;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

class ViewHolderLedger extends RecyclerView.ViewHolder {

    private TextView name, value;

    ViewHolderLedger(View v) {
        super(v);
        name = (TextView) itemView.findViewById(R.id.name);
        value = (TextView) itemView.findViewById(R.id.value);
    }

    TextView getName() {
        return name;
    }
    TextView getValue() {
        return value;
    }
}

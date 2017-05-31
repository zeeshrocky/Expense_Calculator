package com.example.zeeshan.expensecalculator;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

class ViewHolderType extends RecyclerView.ViewHolder {

    private TextView name;

    ViewHolderType(View v) {
        super(v);
        name = (TextView) itemView.findViewById(R.id.name);
    }

    TextView getName() {
        return name;
    }
}

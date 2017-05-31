package com.example.zeeshan.expensecalculator;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import java.util.List;

class AdapterDrawerItems extends ArrayAdapter<Ledger> {
    private final LayoutInflater inflater;
    private TextView name;
    private List<Ledger> list;
    private int selectedItemPosition = -1;

    AdapterDrawerItems(Context context, List<Ledger> list) {
        super(context, R.layout.adapter_drawer_items, list);
        inflater = LayoutInflater.from(context);
        this.list = list;
    }

//    @Override
//    public int getItemViewType(int position) {
//        if (items.get(position) instanceof Expense) {
//            return EXPENSE;
//        }
//        return -1;
//    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        if (view == null) {
            view = inflater.inflate(R.layout.adapter_drawer_items, parent, false);
            name = (TextView) view.findViewById(R.id.name_ledger);
        }
        name.setText(list.get(position).getTitle());

        if(selectedItemPosition != -1) {
            if (selectedItemPosition == position) {
                setTypeFace(view, Typeface.BOLD);

            } else {
                setTypeFace(view, Typeface.NORMAL);
            }
        }

        return view;
    }

    void setSelectedItemPosition(int position) {
        selectedItemPosition = position;
    }

    private void setTypeFace(View convertView, int style) {
        TextView name = (TextView) convertView.findViewById(R.id.name_ledger);
        name.setTypeface(null, style);
    }
}

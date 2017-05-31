package com.example.zeeshan.expensecalculator;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.widget.DatePicker;
import android.widget.EditText;

import java.util.Calendar;

public class toDatePickerLedger extends DialogFragment
        implements DatePickerDialog.OnDateSetListener {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        return new DatePickerDialog(getActivity(), this, year, month, day);
    }

    public void onDateSet(DatePicker view, int year, int month, int day) {
        if(view.isShown()) {
            EditText toDate = (EditText) getActivity().findViewById(R.id.to_date_edit_text_ledger);
            String date = String.valueOf(new StringBuilder().append(day<10?"0"+day:day).append("/").append(month+1<10?"0"+month+1:month+1).append("/").append(year));
            toDate.setText(date);
        }

    }
}
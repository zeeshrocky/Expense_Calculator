package com.example.zeeshan.expensecalculator;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.graphics.Typeface;
import android.os.Bundle;
import android.widget.DatePicker;
import android.widget.TextView;

import java.util.Calendar;

public class FromDatePicker extends DialogFragment
        implements DatePickerDialog.OnDateSetListener {

    String keyDate;
    getFromDate getDate;


    public interface getFromDate{
        void fromDate(String fromDate);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            getDate = (getFromDate) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString());
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        return new DatePickerDialog(getActivity(), this, year, month, day);
    }

    public void onDateSet(DatePicker view, int year, int mont, int day) {
        if(view.isShown()) {
            int month = mont +1;
            keyDate = String.valueOf(new StringBuilder().append(day<10?"0"+day:day).append(month<10?"0"+month:month).append(year));
            getDate.fromDate(keyDate);
            TextView fromDate = (TextView) getActivity().findViewById(R.id.from_date);
            String date = String.valueOf(new StringBuilder().append(day).append("/").append(month).append("/").append(year));
            fromDate.setText(date);
            fromDate.setTypeface(null, Typeface.BOLD);

            SessionManager sessionManager = new SessionManager();
            final String PREFERENCES_FILTER = "filter";
            final String DATE_KEY_PREFERENCES_FROM = "key_date_from";
            final String MONTH_KEY_PREFERENCES_FROM = "key_month_from";
            final String YEAR_KEY_PREFERENCES_FROM = "key_year_from";
            sessionManager.setDatePreferences(getActivity(), PREFERENCES_FILTER, DATE_KEY_PREFERENCES_FROM, String.valueOf(day));
            sessionManager.setDatePreferences(getActivity(), PREFERENCES_FILTER, MONTH_KEY_PREFERENCES_FROM, String.valueOf(month+1));
            sessionManager.setDatePreferences(getActivity(), PREFERENCES_FILTER, YEAR_KEY_PREFERENCES_FROM, String.valueOf(year));
        }

    }
}
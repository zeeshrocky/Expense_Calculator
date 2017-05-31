package com.example.zeeshan.expensecalculator;


import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class DateFragment extends Fragment{
    String keyDate = null;
    getDateFromDateFragment getDate;
    DatePicker datePicker;
    SessionManager sessionManager;
    final String PREFERENCES_FILTER = "filter";
    final String KEY_DATE_PREFERENCES = "date_key_date";
    final String KEY_MONTH_PREFERENCES = "month_key_date";
    final String KEY_YEAR_PREFERENCES = "year_key_date";
    List<String> list = new ArrayList<>();


    public interface getDateFromDateFragment{
        void getDate(String date);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            getDate = (getDateFromDateFragment) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.date_fragment, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        sessionManager = new SessionManager();

        datePicker = (DatePicker) getActivity().findViewById(R.id.date_picker_filter);

        SharedPreferences editor = getActivity().getSharedPreferences(PREFERENCES_FILTER, Context.MODE_PRIVATE);
        if(editor.contains(KEY_DATE_PREFERENCES) && editor.contains(KEY_MONTH_PREFERENCES) && editor.contains(KEY_YEAR_PREFERENCES)) {
            String d = sessionManager.getDatePreferences(getActivity(), PREFERENCES_FILTER, KEY_DATE_PREFERENCES);
            String m = sessionManager.getDatePreferences(getActivity(), PREFERENCES_FILTER, KEY_MONTH_PREFERENCES);
            String y = sessionManager.getDatePreferences(getActivity(), PREFERENCES_FILTER, KEY_YEAR_PREFERENCES);
            datePicker.init(Integer.valueOf(y), Integer.valueOf(m), Integer.valueOf(d), new DatePicker.OnDateChangedListener() {
                @Override
                public void onDateChanged(DatePicker datePicker, int y, int m, int d) {
                    onDateChange();
                }
            });
        } else {
            resetState();
        }
    }

    private void onDateChange() {
        int day = datePicker.getDayOfMonth();
        int month = datePicker.getMonth() + 1;
        int year = datePicker.getYear();

        keyDate = String.valueOf(new StringBuilder().append(day<10?"0"+day:day).append(month<10?"0"+month:month).append(year));
        getDate.getDate(keyDate);

        sessionManager.setDatePreferences(getActivity(), PREFERENCES_FILTER, KEY_DATE_PREFERENCES, String.valueOf(day));
        sessionManager.setDatePreferences(getActivity(), PREFERENCES_FILTER, KEY_MONTH_PREFERENCES, String.valueOf(month));
        sessionManager.setDatePreferences(getActivity(), PREFERENCES_FILTER, KEY_YEAR_PREFERENCES, String.valueOf(year));
    }

    void resetState() {
        datePicker = (DatePicker) getActivity().findViewById(R.id.date_picker_filter);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        datePicker.init(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker datePicker, int y, int m, int d) {
                onDateChange();
            }
        });
    }
}



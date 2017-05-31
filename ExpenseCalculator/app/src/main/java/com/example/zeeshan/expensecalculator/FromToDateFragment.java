package com.example.zeeshan.expensecalculator;

import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class FromToDateFragment extends Fragment implements View.OnClickListener{
    SessionManager sessionManager;
    TextView fromDateTextView;
    TextView toDateTextView;
    final String PREFERENCES_FILTER = "filter";
    final String DATE_KEY_PREFERENCES_FROM = "key_date_from";
    final String MONTH_KEY_PREFERENCES_FROM = "key_month_from";
    final String YEAR_KEY_PREFERENCES_FROM = "key_year_from";
    final String DATE_KEY_PREFERENCES_TO = "key_date_to";
    final String MONTH_KEY_PREFERENCES_TO = "key_month_to";
    final String YEAR_KEY_PREFERENCES_TO = "key_year_to";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.from_to_date_fragment, container, false);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        FragmentManager manager = getFragmentManager();
        switch (id) {
            case R.id.from_date:
                DialogFragment from = new FromDatePicker();
                from.show(manager, "fromDatePicker");
                break;
            case R.id.to_date:
                DialogFragment to = new ToDatePicker();
                to.show(manager, "toDatePicker");
                break;
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        sessionManager = new SessionManager();

        fromDateTextView = (TextView) getActivity().findViewById(R.id.from_date);
        fromDateTextView.setText(Utility.currentTimeInDateFormat());
        fromDateTextView.setOnClickListener(this);
        toDateTextView = (TextView) getActivity().findViewById(R.id.to_date);
        toDateTextView.setText(Utility.currentTimeInDateFormat());
        toDateTextView.setOnClickListener(this);

        SharedPreferences editor = getActivity().getSharedPreferences(PREFERENCES_FILTER, Context.MODE_PRIVATE);
        if(editor.contains(DATE_KEY_PREFERENCES_FROM) && editor.contains(MONTH_KEY_PREFERENCES_FROM) && editor.contains(YEAR_KEY_PREFERENCES_FROM)) {
            String d = sessionManager.getDatePreferences(getActivity(), PREFERENCES_FILTER, DATE_KEY_PREFERENCES_FROM);
            String m = sessionManager.getDatePreferences(getActivity(), PREFERENCES_FILTER, MONTH_KEY_PREFERENCES_FROM);
            String y = sessionManager.getDatePreferences(getActivity(), PREFERENCES_FILTER, YEAR_KEY_PREFERENCES_FROM);

            String date = String.valueOf(new StringBuilder().append(d).append("/").append(m).append("/").append(y));
            fromDateTextView.setText(date);
        }

        if(editor.contains(DATE_KEY_PREFERENCES_TO) && editor.contains(MONTH_KEY_PREFERENCES_TO) && editor.contains(YEAR_KEY_PREFERENCES_TO)) {
            String d = sessionManager.getDatePreferences(getActivity(), PREFERENCES_FILTER, DATE_KEY_PREFERENCES_TO);
            String m = sessionManager.getDatePreferences(getActivity(), PREFERENCES_FILTER, MONTH_KEY_PREFERENCES_TO);
            String y = sessionManager.getDatePreferences(getActivity(), PREFERENCES_FILTER, YEAR_KEY_PREFERENCES_TO);

            String date = String.valueOf(new StringBuilder().append(d).append("/").append(m).append("/").append(y));
            toDateTextView.setText(date);
        }
    }

    void resetStateOfToDate() {
        toDateTextView.setText(Utility.currentTimeInDateFormat());
        toDateTextView.setTypeface(null, Typeface.NORMAL);
    }

    void resetStateOfFromDate() {
        fromDateTextView.setText(Utility.currentTimeInDateFormat());
        fromDateTextView.setTypeface(null, Typeface.NORMAL);
    }
}



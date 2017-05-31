package com.example.zeeshan.expensecalculator;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import java.util.ArrayList;

public class Filter extends AppCompatActivity implements LeftFragmentFilter.Get,
        TypeFragment.getDataFromTypeFragment, DateFragment.getDateFromDateFragment, View.OnClickListener,
        FromDatePicker.getFromDate, ToDatePicker.getToDate{

    DB db;
    Button applyButton, clearAllButton;
    ArrayList<String> arrayListTypes = new ArrayList<>();
    String date, toDate, fromDate = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        db = new DB(Filter.this);
        applyButton = (Button) findViewById(R.id.apply_button);
        applyButton.setOnClickListener(this);
        clearAllButton = (Button) findViewById(R.id.clear_all_button);
        clearAllButton.setOnClickListener(this);

        removeSharedPreferences();

        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        TypeFragment fragment = new TypeFragment();
        fragmentTransaction.replace(R.id.right_fragment_filter, fragment);
        fragmentTransaction.commit();
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.apply_button:
                if(arrayListTypes.isEmpty() && date==null && toDate==null && fromDate==null) {
                    Utility.shortToast(Filter.this, "Choose a filter or go to back");
                } else {
                    Intent intent = new Intent();
                    Bundle b =new Bundle();
                    b.putStringArrayList("arrayListOfFilter", arrayListTypes);
                    b.putString("date", date);
                    b.putString("toDate", toDate);
                    b.putString("fromDate", fromDate);
                    intent.putExtras(b);
                    setResult(Activity.RESULT_OK, intent);
                    finish();
                    break;
                }
            case R.id.clear_all_button:
                clearStates();
                break;
        }
    }

    private void clearStates() {
        Fragment mFragment = getFragmentManager().findFragmentById(R.id.right_fragment_filter);
        if (mFragment instanceof TypeFragment) {
            TypeFragment typeFragment = (TypeFragment) getFragmentManager().findFragmentById(R.id.right_fragment_filter);
            typeFragment.retainStateListView(false);
        }
        if(mFragment instanceof DateFragment) {
            DateFragment dateFragment = (DateFragment) getFragmentManager().findFragmentById(R.id.right_fragment_filter);
            dateFragment.resetState();
        }
        if(mFragment instanceof FromToDateFragment) {
            FromToDateFragment fromToDateFragment = (FromToDateFragment) getFragmentManager().findFragmentById(R.id.right_fragment_filter);
            fromToDateFragment.resetStateOfToDate();
            fromToDateFragment.resetStateOfFromDate();
        }

        LeftFragmentFilter leftFragment = (LeftFragmentFilter) getFragmentManager().findFragmentById(R.id.left_fragment_filter);
        leftFragment.makeStyleBoldAtFirstPosition(false);
        leftFragment.makeStyleBoldAtSecondPosition(false);
        leftFragment.makeStyleBoldAtThirdPosition(false);

        date = null;
        fromDate = null;
        toDate = null;

        removeSharedPreferences();
    }

    @Override
    public void getData(int s) {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        if(s == 0) {
            TypeFragment fragment = new TypeFragment();
            fragmentTransaction.replace(R.id.right_fragment_filter, fragment);
            fragmentTransaction.commit();
        }
        if(s == 1) {
            DateFragment fragment = new DateFragment();
            fragmentTransaction.replace(R.id.right_fragment_filter, fragment);
            fragmentTransaction.commit();
        }
        if(s == 2) {
            FromToDateFragment fragment = new FromToDateFragment();
            fragmentTransaction.replace(R.id.right_fragment_filter, fragment);
            fragmentTransaction.commit();
        }
    }

    @Override
    public void getTypes(ArrayList<String> data) {
        arrayListTypes = data;
        LeftFragmentFilter fragment = (LeftFragmentFilter) getFragmentManager().findFragmentById(R.id.left_fragment_filter);
        if(data.isEmpty()) {
            fragment.makeStyleBoldAtFirstPosition(false);
        } else {
            fragment.makeStyleBoldAtFirstPosition(true);
            fragment.countSelection(data.size());
        }
    }

    @Override
    public void getDate(String date) {
        try {
            if(fromDate==null || toDate==null) {
                this.date = date;
                LeftFragmentFilter fragment = (LeftFragmentFilter) getFragmentManager().findFragmentById(R.id.left_fragment_filter);
                if(date.equals("")) {
                    fragment.makeStyleBoldAtSecondPosition(false);
                } else {
                    fragment.makeStyleBoldAtSecondPosition(true);
                }
            } else {
                Utility.alertDialog(Filter.this, "Alert!", "You can choose only one from DATE and FROM-TO DATE");
            }
        } catch (Exception e) {
            Log.d("getDate", e.getMessage());
        }

    }

    @Override
    public void fromDate(String fromDate) {
        if(date==null) {
            this.fromDate = fromDate;
            LeftFragmentFilter fragment = (LeftFragmentFilter) getFragmentManager().findFragmentById(R.id.left_fragment_filter);
            if(fromDate.equals("")) {
                fragment.makeStyleBoldAtThirdPosition(false);
            } else if(toDate==null) {

            } else {
                fragment.makeStyleBoldAtThirdPosition(true);
            }
        } else {
            Utility.alertDialog(Filter.this, "Alert!", "You can choose only one from DATE and FROM-TO DATE");
        }
    }

    @Override
    public void toDate(String toDate) {
        if(date==null) {
            this.toDate = toDate;
            LeftFragmentFilter fragment = (LeftFragmentFilter) getFragmentManager().findFragmentById(R.id.left_fragment_filter);
            if(toDate.equals("")) {
                fragment.makeStyleBoldAtThirdPosition(false);
            } else if(fromDate==null) {

            } else {
                fragment.makeStyleBoldAtThirdPosition(true);
            }
        } else {
            Utility.alertDialog(Filter.this, "Alert!", "You can choose only one from DATE and FROM-TO DATE");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.filter_activity, menu);
        return true;
    }

    private void removeSharedPreferences() {
        final String PREFERENCES_FILTER = "filter";
        final String KEY_PREFERENCES = "arrayList";
        final String KEY_DATE_DATE = "date_key_date";
        final String KEY_MONTH_DATE = "month_key_date";
        final String KEY_YEAR_DATE = "year_key_date";
        final String KEY_DATE_FROM = "key_date_from";
        final String KEY_MONTH_FROM = "key_month_from";
        final String KEY_YEAR_FROM = "key_year_from";
        final String KEY_DATE_TO = "key_date_to";
        final String KEY_MONTH_TO = "key_month_to";
        final String KEY_YEAR_TO = "key_year_to";

        SharedPreferences.Editor preferences = getSharedPreferences(PREFERENCES_FILTER, Context.MODE_PRIVATE).edit();
        preferences.remove(KEY_PREFERENCES);

        preferences.remove(KEY_DATE_DATE);
        preferences.remove(KEY_MONTH_DATE);
        preferences.remove(KEY_YEAR_DATE);

        preferences.remove(KEY_DATE_FROM);
        preferences.remove(KEY_MONTH_FROM);
        preferences.remove(KEY_YEAR_FROM);

        preferences.remove(KEY_DATE_TO);
        preferences.remove(KEY_MONTH_TO);
        preferences.remove(KEY_YEAR_TO);

        preferences.apply();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            removeSharedPreferences();
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        removeSharedPreferences();
        finish();
    }
}

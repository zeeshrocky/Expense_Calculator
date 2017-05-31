package com.example.zeeshan.expensecalculator;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

public class AddNew extends AppCompatActivity{

    TabLayout tabLayout;
    final String PREFERENCE_LEDGER = "edit_ledger";
    final String PREFERENCE_EXPENSE = "edit_expense";
    final String PREFERENCE_TYPE = "edit_type";
    final String KEY_ID = "id";
    final String KEY_NAME = "name";
    final String KEY_VALUE = "value";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText("Expense"));
        tabLayout.addTab(tabLayout.newTab().setText("Ledger"));
        tabLayout.addTab(tabLayout.newTab().setText("Type"));

        final ViewPager viewPager = (ViewPager) findViewById(R.id.pager2);
        final TabAdapter adapter = new TabAdapter(getSupportFragmentManager(), tabLayout.getTabCount());

        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        final String PREFERENCE_LEDGER = "edit_ledger";
        final String PREFERENCE_EXPENSE = "edit_expense";
        final String PREFERENCE_TYPE = "edit_type";
        final String KEY_ID = "id";
        final String KEY_NAME = "name";
        final String KEY_VALUE = "value";

        SharedPreferences editorExpense = getSharedPreferences(PREFERENCE_EXPENSE, Context.MODE_PRIVATE);
        if (editorExpense.contains(KEY_ID) && editorExpense.contains(KEY_NAME) && editorExpense.contains(KEY_VALUE)) {
            if (actionBar != null) {
                actionBar.setTitle("Edit");
            }
        }

        SharedPreferences editor = getSharedPreferences(PREFERENCE_LEDGER, Context.MODE_PRIVATE);
        if (editor.contains(KEY_ID) && editor.contains(KEY_NAME)) {
            if (actionBar != null) {
                actionBar.setTitle("Edit");
            }
            viewPager.setCurrentItem(1);

        }

        SharedPreferences editorType = getSharedPreferences(PREFERENCE_TYPE, Context.MODE_PRIVATE);
        if (editorType.contains(KEY_ID) && editorType.contains(KEY_NAME)) {
            if (actionBar != null) {
                actionBar.setTitle("Edit");
            }
            viewPager.setCurrentItem(2);

        }

        tabLayout.addOnTabSelectedListener (new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.finish, menu);
        return true;
    }

    private void removeShearedPreference() {
        SharedPreferences.Editor expense = getSharedPreferences(PREFERENCE_EXPENSE, Context.MODE_PRIVATE).edit();
        SharedPreferences.Editor type = getSharedPreferences(PREFERENCE_TYPE, Context.MODE_PRIVATE).edit();
        SharedPreferences.Editor ledger = getSharedPreferences(PREFERENCE_LEDGER, Context.MODE_PRIVATE).edit();

        final String KEY_TYPE_ID = "typeID";
        final String KEY_LEDGER_ID = "ledgerId";
        expense.remove(KEY_ID);
        expense.remove(KEY_NAME);
        expense.remove(KEY_VALUE);
        expense.remove(KEY_TYPE_ID);
        expense.remove(KEY_LEDGER_ID);
        expense.apply();

        type.remove(KEY_ID);
        type.remove(KEY_NAME);
        type.apply();

        ledger.remove(KEY_ID);
        ledger.remove(KEY_NAME);
        ledger.remove(KEY_VALUE);
        ledger.apply();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            removeShearedPreference();
            Utility.setResultActivity(AddNew.this);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        removeShearedPreference();
        Utility.setResultActivity(AddNew.this);
        finish();
    }



}

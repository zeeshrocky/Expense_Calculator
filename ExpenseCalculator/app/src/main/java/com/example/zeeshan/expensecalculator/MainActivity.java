package com.example.zeeshan.expensecalculator;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity{

    private RecyclerView recyclerView;
    private ArrayList<Object> arrayListExpense;
    private ArrayList<Ledger> arrayListDrawer;
    private DB db;
    private String nameLedger;
    static String ledgerId;
    private static long sum, sumLedger = 0;
    private TextView showValueExpense, showValueIncome, showValueBalance;
    private final int CONFIGURE_DRAWER_REQUEST_CODE = 1;
    private final int FILTER_REQUEST_CODE = 2;
    private final int ADD_NEW_REQUEST_CODE = 4;
    private final int EDIT_EXPENSE_REQUEST_CODE = 5;
    final String ID = "id";
    final String NAME = "name";
    final String VALUE = "value";
    final String DATE = "date";
    final String TYPE_ID = "type_id";
    AdapterDrawerItems adapter = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        removeShearedPreferenceOfEdit();

        db = new DB(MainActivity.this);
        showValueExpense = (TextView) findViewById(R.id.show_value_expense);
        showValueIncome = (TextView) findViewById(R.id.show_value_income);
        showValueBalance = (TextView) findViewById(R.id.show_value_balance);

        viewDrawerItems();
        setSelectedItemPositionOfDrawerItem(adapter, 0);


        if(db.selectLastIdOfLedger() != null) {
            ledgerId = db.selectLastIdOfLedger();
            viewItems(ledgerId);
        }

        Button addNewButton = (Button) findViewById(R.id.add_new_button);
        addNewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utility.startAnActivityForResult(MainActivity.this, MainActivity.this, AddNew.class, ADD_NEW_REQUEST_CODE);
            }
        });

        drawer(toolbar);
    }

    private void viewItems(String incomeId) {
        try {
            getReferencesForViewItemsRecyclerView();
            initializeSumValue();
            Cursor cursorExpense = db.selectExpense(incomeId);
            addValuesToArrayListExpense(cursorExpense);
            selectAndShowLedgerValue(incomeId);
            showBalance();
        } catch (Exception e) {
            Log.d("showItems", " failed " + e.getMessage());
        }
    }

    private void viewDrawerItems() {
        try {
            getReferencesForDrawerItemsListView();

            Cursor cursor = db.selectLedger();
            cursor.moveToFirst();
            if (cursor.getCount() == 0) {
                Utility.shortToast(MainActivity.this, "Empty list");
            } else {
                for (int i = 0; i < cursor.getCount(); i++) {
                    String title = cursor.getString(cursor.getColumnIndex("title"));
                    Ledger ledger = new Ledger(title);
                    arrayListDrawer.add(ledger);
                    cursor.moveToNext();
                }
                cursor.close();
            }
        } catch (Exception e) {
            Log.d("viewDrawerItems", " failed " + e.getMessage());
        }
    }

    private void selectAndShowLedgerValue(String incomeId) {
        Cursor cursor = db.selectLedgerValueById(incomeId);
        cursor.moveToFirst();
        if (cursor.getCount() == 0) {
            Utility.shortToast(MainActivity.this, "Empty list");
        } else {
            for (int i = 0; i < cursor.getCount(); i++) {
                long value = cursor.getLong(cursor.getColumnIndex("starting_balance"));
                showLedger(value);
                cursor.moveToNext();
            }
            cursor.close();
        }
    }

    private void showExpense(long expense) {
        sum = sum + expense;
        String messageSum = getString(R.string.sum, String.valueOf(sum));
        showValueExpense.setText(messageSum);
    }

    private void showLedger(long income) {
        sumLedger = sumLedger + income;
        String messageSum = getString(R.string.sum, String.valueOf(sumLedger));
        showValueIncome.setText(messageSum);
    }

    private void showBalance() {
        long balance = sumLedger - sum;
        String messageSum = getString(R.string.sum, String.valueOf(balance));
        showValueBalance.setText(messageSum);
        if(balance < 0) {
            showValueBalance.setTextColor(Color.RED);
        } else {
            showValueBalance.setTextColor(Color.WHITE);
        }
    }

    private void getReferencesForViewItemsRecyclerView() {
        arrayListExpense = new ArrayList<>();
        recyclerView = (RecyclerView) findViewById(R.id.view_item_recycle_view);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(new Adapter(MainActivity.this, arrayListExpense));
    }

    private void addValuesToArrayListExpense(Cursor cursor) {
        cursor.moveToFirst();
        if (cursor.getCount() == 0) {
            Toast.makeText(this, "Empty list", Toast.LENGTH_SHORT).show();
        } else {
            for (int i = 0; i < cursor.getCount(); i++) {
                String id = cursor.getString(cursor.getColumnIndex(ID));
                String name = cursor.getString(cursor.getColumnIndex(NAME));

                long valueExpense = cursor.getLong(cursor.getColumnIndex(VALUE));
                showExpense(valueExpense);
                String value = String.valueOf(valueExpense);

                String date = Utility.dateFormat(cursor.getLong(cursor.getColumnIndex(DATE)));
                String typeId = cursor.getString(cursor.getColumnIndex(TYPE_ID));
                String type = db.selectTypeById(typeId);

                arrayListExpense.add(new Expense(id, name, value, date, type, typeId, ledgerId));

                cursor.moveToNext();
            }
            cursor.close();
        }
        enableSwipeExpense();
    }

    private void enableSwipeExpense() {
        RecyclerTouchListener onTouchListener = new RecyclerTouchListener(this, recyclerView);
        onTouchListener
                .setSwipeOptionViews(R.id.edit, R.id.delete)
                .setSwipeable(R.id.rowFG, R.id.rowBG, new RecyclerTouchListener.OnSwipeOptionsClickListener() {
                    @Override
                    public void onSwipeOptionClicked(int viewID, int position) {
                        Expense expense = (Expense) arrayListExpense.get(position);
                        if (viewID == R.id.delete) {
                            final String idExpense = expense.getId();
                            deleteExpense(idExpense);

                        } else if (viewID == R.id.edit) {
                            SessionManager preference = new SessionManager();
                            final String PREFERENCES = "edit_expense";
                            preference.setDatePreferences(MainActivity.this, PREFERENCES, "id", expense.getId());
                            preference.setDatePreferences(MainActivity.this, PREFERENCES, "name", expense.getTitle());
                            preference.setDatePreferences(MainActivity.this, PREFERENCES, "value", expense.getValue());
                            preference.setDatePreferences(MainActivity.this, PREFERENCES, "typeID", expense.getTypeId());
                            preference.setDatePreferences(MainActivity.this, PREFERENCES, "ledgerId", expense.getIdLedger());
                            Utility.startAnActivityForResult(MainActivity.this, MainActivity.this, AddNew.class, EDIT_EXPENSE_REQUEST_CODE);
                        }
                    }
                });
        recyclerView.addOnItemTouchListener(onTouchListener);
    }

    private void getReferencesForDrawerItemsListView() {
        arrayListDrawer = new ArrayList<>();
        ListView drawerListView = (ListView) findViewById(R.id.drawer_items_listview);
        adapter = new AdapterDrawerItems(MainActivity.this, arrayListDrawer);
        drawerListView.setAdapter(adapter);

        drawerListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                nameLedger = arrayListDrawer.get(position).getTitle();
                ledgerId = db.selectIdByLedgerName(nameLedger);
                setSelectedItemPositionOfDrawerItem(adapter, position);
                viewItems(ledgerId);
                closeDrawer();
            }

        });
    }

    private void setSelectedItemPositionOfDrawerItem(AdapterDrawerItems adapter, int position) {
        adapter.setSelectedItemPosition(position);
        adapter.notifyDataSetChanged();
    }

    private void initializeSumValue() {
        sum = 0;
        sumLedger = 0;
        String expense = getString(R.string.sum, String.valueOf(sum));
        showValueExpense.setText(expense);
        String ledger = getString(R.string.sum, String.valueOf(sumLedger));
        showValueIncome.setText(ledger);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.configure_drawer) {
            Utility.startAnActivityForResult(MainActivity.this, MainActivity.this, EditTypeAndLedger.class, CONFIGURE_DRAWER_REQUEST_CODE);
            return true;
        }
        if (id == R.id.filter) {
            Utility.startAnActivityForResult(MainActivity.this, MainActivity.this, Filter.class, FILTER_REQUEST_CODE);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    private void closeDrawer() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && data != null) {
            switch (requestCode) {
                case CONFIGURE_DRAWER_REQUEST_CODE:
                    viewDrawerItems();
                    viewItems(ledgerId);
                    break;
                case ADD_NEW_REQUEST_CODE:
                    ledgerId = db.selectLastIdOfLedger();
                    viewDrawerItems();
                    setSelectedItemPositionOfDrawerItem(adapter, 0);
                    viewItems(ledgerId);
                    break;
                case EDIT_EXPENSE_REQUEST_CODE:
                    viewDrawerItems();
                    viewItems(ledgerId);
                    break;
                case FILTER_REQUEST_CODE:
                    @SuppressWarnings("unchecked")
                    ArrayList<String> typeArrayList = (ArrayList<String>) data.getSerializableExtra("arrayListOfFilter");
                    String date = data.getStringExtra("date");
                    String toDate = data.getStringExtra("toDate");
                    String fromDate = data.getStringExtra("fromDate");

                    try {
                        getReferencesForViewItemsRecyclerView();

                        if(date==null && toDate==null && fromDate==null) {
                            String allKeyWord = typeArrayList.get(0);
                            if(typeArrayList.size() == 1 && allKeyWord.equals("all")) {
                                initializeSumValue();
                                Cursor cursor = db.selectExpense(ledgerId);
                                addValuesToArrayListExpense(cursor);
                                selectAndShowLedgerValue(ledgerId);
                                showBalance();
                            } else {
                                initializeSumValue();
                                for (int j = 0; j<typeArrayList.size(); j++) {
                                    Cursor cursor = db.selectExpenseByType(db.getIdByType(typeArrayList.get(j)), ledgerId);
                                    addValuesToArrayListExpense(cursor);
                                }
                                selectAndShowLedgerValue(ledgerId);
                                showBalance();
                            }
                        }

                        if(toDate==null && fromDate==null && typeArrayList.isEmpty()) {
                            initializeSumValue();
                            Cursor cursor = db.selectExpenseByDate(date, ledgerId);
                            addValuesToArrayListExpense(cursor);
                            selectAndShowLedgerValue(ledgerId);
                            showBalance();

                        }

                        if(date==null && typeArrayList.isEmpty()) {
                            initializeSumValue();
                            Cursor cursor = db.selectFromToDate(fromDate, toDate, ledgerId);
                            addValuesToArrayListExpense(cursor);
                            selectAndShowLedgerValue(ledgerId);
                            showBalance();
                        }

                        if(!typeArrayList.isEmpty() && date!=null) {
                            initializeSumValue();
                            for (int i = 0; i<typeArrayList.size(); i++) {
                                Cursor cursor = db.selectExpenseByTypeAndDate(db.getIdByType(typeArrayList.get(i)), date, ledgerId);
                                addValuesToArrayListExpense(cursor);
                            }
                            selectAndShowLedgerValue(ledgerId);
                            showBalance();
                        }

                        if(!typeArrayList.isEmpty() && toDate!=null && fromDate!=null) {
                            initializeSumValue();
                            for (int i = 0; i<typeArrayList.size(); i++) {
                                Cursor cursor = db.selectExpenseByTypeAndFromToDate(db.getIdByType(typeArrayList.get(i)), fromDate, toDate, ledgerId);
                                addValuesToArrayListExpense(cursor);
                            }
                            selectAndShowLedgerValue(ledgerId);
                            showBalance();
                        }

                    } catch (Exception e) {
                        Log.d("showItemsByFilter", " failed " + e.getMessage());
                    }

                    break;
            }

        }
    }



    private void drawer(Toolbar toolbar) {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
    }

    private void deleteExpense(final String idExpense) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
        alertDialogBuilder.setTitle("Confirmation");
        alertDialogBuilder.setMessage("Do you want to delete?");
        alertDialogBuilder.setCancelable(true)
                .setNegativeButton("CANCEL",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        })
                .setPositiveButton("YES",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                if (db.deleteExpense(idExpense)) {
                                    Utility.successSnackBar(recyclerView, "Expense deleted", MainActivity.this);
                                    initializeSumValue();
                                    viewItems(ledgerId);
                                } else if (!db.deleteExpense(idExpense)) {
                                    Utility.failSnackBar(recyclerView, "Error, Expense not deleted, try again", MainActivity.this);
                                }
                            }

                        });
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }

    private void removeShearedPreferenceOfEdit() {

        final String PREFERENCE_LEDGER = "edit_ledger";
        final String PREFERENCE_EXPENSE = "edit_expense";
        final String PREFERENCE_TYPE = "edit_type";
        final String KEY_ID = "id";
        final String KEY_NAME = "name";
        final String KEY_VALUE = "value";
        final String KEY_TYPE_ID = "typeID";
        final String KEY_LEDGER_ID = "ledgerId";

        SharedPreferences.Editor expense = getSharedPreferences(PREFERENCE_EXPENSE, Context.MODE_PRIVATE).edit();
        SharedPreferences.Editor type = getSharedPreferences(PREFERENCE_TYPE, Context.MODE_PRIVATE).edit();
        SharedPreferences.Editor ledger = getSharedPreferences(PREFERENCE_LEDGER, Context.MODE_PRIVATE).edit();

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
}

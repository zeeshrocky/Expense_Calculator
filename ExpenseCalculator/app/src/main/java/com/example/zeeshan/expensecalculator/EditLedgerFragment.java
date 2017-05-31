package com.example.zeeshan.expensecalculator;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

public class EditLedgerFragment extends Fragment {

    private RecyclerView recyclerView;
    ArrayList<Object> arrayListLedger;
    View view;
    private final int EDIT_LEDGER_REQUEST_CODE = 0;
    DB db;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.edit, null);
        db = new DB(getActivity());
        viewDrawerItems();
        return view;
    }


    private void viewDrawerItems() {
        try {
            getReferencesForViewItemsRecyclerView();
            Cursor cursor = db.selectLedger();
            addValuesToArrayListExpense(cursor);
        } catch (Exception e) {
            Log.d("viewDrawerItems", " failed " + e.getMessage());
        }
    }


    private void getReferencesForViewItemsRecyclerView() {
        arrayListLedger = new ArrayList<>();
        recyclerView = (RecyclerView) view.findViewById(R.id.edit_recyclerView);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(new Adapter(getActivity(), arrayListLedger));
    }

    private void addValuesToArrayListExpense(Cursor cursor) {
        cursor.moveToFirst();
        if (cursor.getCount() == 0) {
            Utility.shortToast(getActivity(), "Empty list");
        } else {
            for (int i = 0; i < cursor.getCount(); i++) {
                String id = cursor.getString(cursor.getColumnIndex("id"));
                String title = cursor.getString(cursor.getColumnIndex("title"));
                String value = cursor.getString(cursor.getColumnIndex("starting_balance"));
                arrayListLedger.add(new Ledger(id, title, value));
                cursor.moveToNext();
            }
            cursor.close();
        }
        enableSwipeExpense();
    }

    private void enableSwipeExpense() {
        RecyclerTouchListener onTouchListener = new RecyclerTouchListener(getActivity(), recyclerView);
        onTouchListener
                .setSwipeOptionViews(R.id.edit, R.id.delete)
                .setSwipeable(R.id.rowFG, R.id.rowBG, new RecyclerTouchListener.OnSwipeOptionsClickListener() {
                    @Override
                    public void onSwipeOptionClicked(int viewID, int position) {
                        Ledger ledger = (Ledger) arrayListLedger.get(position);
                        if (viewID == R.id.delete) {
                            final String id = ledger.getId();
                            delete(id);

                        } else if (viewID == R.id.edit) {
                            SessionManager preference = new SessionManager();
                            preference.setDatePreferences(getActivity(), "edit_ledger", "id", ledger.getId());
                            preference.setDatePreferences(getActivity(), "edit_ledger", "name", ledger.getTitle());
                            preference.setDatePreferences(getActivity(), "edit_ledger", "value", ledger.getValue());
                            Intent intent = new Intent(getActivity(), AddNew.class);
                            startActivityForResult(intent, EDIT_LEDGER_REQUEST_CODE);
                        }
                    }
                });
        recyclerView.addOnItemTouchListener(onTouchListener);
    }

    private void delete(final String idLedger) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
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
                                if (db.deleteLedger(idLedger) && db.deleteExpenseByLedgerId(idLedger)) {
                                    Utility.successSnackBar(recyclerView, "Ledger deleted", getActivity());
                                    viewDrawerItems();
                                } else if (!db.deleteExpense(idLedger) && !db.deleteExpenseByLedgerId(idLedger)) {
                                    Utility.failSnackBar(recyclerView, "Error, Expense not deleted, try again", getActivity());
                                }
                            }

                        });
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            switch (requestCode) {
                case EDIT_LEDGER_REQUEST_CODE:
                    viewDrawerItems();
                    break;
            }
        }
    }

}

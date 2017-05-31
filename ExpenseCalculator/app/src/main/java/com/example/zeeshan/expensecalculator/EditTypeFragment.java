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


public class EditTypeFragment extends Fragment {

    private RecyclerView recyclerView;
    ArrayList<Object> arrayList;
    View view;
    private final int EDIT_TYPE_REQUEST_CODE = 0;
    DB db;
    boolean check;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.edit, null);
        db = new DB(getActivity());
        viewTypes();
        return view;
    }

    private void viewTypes() {
        try {
            getReferencesForViewItemsRecyclerView();
            Cursor cursor = db.selectMainType();
            addValuesToArrayListType(cursor);
        } catch (Exception e) {
            Log.d("viewTypes", " failed " + e.getMessage());
        }
    }


    private void getReferencesForViewItemsRecyclerView() {
        arrayList = new ArrayList<>();
        recyclerView = (RecyclerView) view.findViewById(R.id.edit_recyclerView);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(new Adapter(getActivity(), arrayList));
    }

    private void addValuesToArrayListType(Cursor cursor) {
        cursor.moveToFirst();
        if (cursor.getCount() == 0) {
            Utility.shortToast(getActivity(), "Empty list");
        } else {
            for (int i = 0; i < cursor.getCount(); i++) {
                String id = cursor.getString(cursor.getColumnIndex("id"));
                String name = cursor.getString(cursor.getColumnIndex("name"));
                arrayList.add(new Type(id, name));
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
                        Type type = (Type) arrayList.get(position);
                        if (viewID == R.id.delete) {
                            final String id = type.getId();
                            delete(id);

                        } else if (viewID == R.id.edit) {
                            SessionManager preference = new SessionManager();
                            preference.setDatePreferences(getActivity(), "edit_type", "id", type.getId());
                            preference.setDatePreferences(getActivity(), "edit_type", "name", type.getTitle());
                            Intent intent = new Intent(getActivity(), AddNew.class);
                            startActivityForResult(intent, EDIT_TYPE_REQUEST_CODE);
                        }
                    }
                });
        recyclerView.addOnItemTouchListener(onTouchListener);
    }

    private void delete(final String idType) {
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
                                if (db.deleteType(idType) && db.deleteExpenseByTypeId(idType)) {
                                    Utility.successSnackBar(recyclerView, "Type deleted", getActivity());
                                    viewTypes();
                                } else  {
                                    Utility.failSnackBar(recyclerView, "Error, Type not deleted, try again", getActivity());
                                }
                            }

                        });
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        try {
            if (isVisibleToUser) {
                check = true;
            }
        } catch (Exception e) {
            Log.d("setUserVisibleHint", e.getMessage());
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == EDIT_TYPE_REQUEST_CODE && data != null) {
            switch (requestCode) {
                case EDIT_TYPE_REQUEST_CODE:
                    viewTypes();
                    break;
            }
        }
    }

}

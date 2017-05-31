package com.example.zeeshan.expensecalculator;

import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class AddExpenseFragment extends Fragment implements AdapterView.OnItemSelectedListener {

    private TextInputLayout layoutExpenseName, layoutExpenseValue;
    private EditText addNameEditText, addValueEditText;
    private DB db;
    private List<String> arrayListType, arrayListLedger;
    View view;
    private String idType, idLedger;
    private TextView dateTextView;
    SharedPreferences editor;
    SessionManager preference;
    String d;
    boolean check;
    private final String PREFERENCE_EDIT = "edit_expense";
    private final String KEY_ID = "id";
    private final String KEY_NAME = "name";
    private final String KEY_VALUE = "value";
    private final String KEY_TYPE_ID = "typeID";
    private final String KEY_LEDGER_ID = "ledgerId";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.add_expense, container, false);
        db = new DB(getActivity());
        getLayoutReferences();

        preference = new SessionManager();
        editor = getActivity().getSharedPreferences(PREFERENCE_EDIT, Context.MODE_PRIVATE);
        if(editor.contains(KEY_ID) && editor.contains(KEY_NAME) && editor.contains(KEY_VALUE) && editor.contains(KEY_TYPE_ID) && editor.contains(KEY_LEDGER_ID)) {
            if(check) {
                String name = preference.getDatePreferences(getActivity(), PREFERENCE_EDIT, KEY_NAME);
                String value = preference.getDatePreferences(getActivity(), PREFERENCE_EDIT, KEY_VALUE);
                String typeId = preference.getDatePreferences(getActivity(), PREFERENCE_EDIT, KEY_TYPE_ID);
                String ledgerId = preference.getDatePreferences(getActivity(), PREFERENCE_EDIT, KEY_LEDGER_ID);

                EditText nameEditText = (EditText) view.findViewById(R.id.name_expense);
                nameEditText.setText(name);

                EditText valueEditText = (EditText) view.findViewById(R.id.value_expense);
                valueEditText.setText(value);

                arrayListType = new ArrayList<>();
                arrayListType.clear();
                getTypesOrderByTypeId(typeId);
                Spinner typeSpinner = (Spinner) view.findViewById(R.id.type_spinner);
                typeSpinner.setOnItemSelectedListener(this);
                Utility.setSpinnerAdapterByArrayList(typeSpinner, getActivity(), arrayListType);

                arrayListLedger = new ArrayList<>();
                arrayListLedger.clear();
                getLedgerOrderById(ledgerId);
                Spinner ledgerSpinner = (Spinner) view.findViewById(R.id.ledger_expense_spinner);
                ledgerSpinner.setOnItemSelectedListener(this);
                Utility.setSpinnerAdapterByArrayList(ledgerSpinner, getActivity(), arrayListLedger);
            }
        }

        return view;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        try {
            if (isVisibleToUser) {
                check = true;
            }
            if (isVisibleToUser && isResumed()) {
                setLedgerSpinner();
            }
        } catch (Exception e) {
            Log.d("setUserVisibleHint", e.getMessage());
        }
    }

    private void getLayoutReferences() {
        layoutExpenseName = (TextInputLayout) view.findViewById(R.id.text_input_layout_add_expense_name);
        addNameEditText = (EditText) view.findViewById(R.id.name_expense);
        addNameEditText.addTextChangedListener(new addNewItemTextWatcher(addNameEditText));

        layoutExpenseValue = (TextInputLayout) view.findViewById(R.id.text_input_layout_add_expense_value);
        addValueEditText = (EditText) view.findViewById(R.id.value_expense);
        addValueEditText.addTextChangedListener(new addNewItemTextWatcher(addValueEditText));

        dateTextView = (TextView) view.findViewById(R.id.date_expense);
        dateTextView.setText(Utility.currentTimeInDateFormat());
        dateTextView.setOnClickListener(new OnClickListener());

        arrayListType = new ArrayList<>();
        arrayListType.clear();
        getTypes();
        Spinner typeSpinner = (Spinner) view.findViewById(R.id.type_spinner);
        typeSpinner.setOnItemSelectedListener(this);
        Utility.setSpinnerAdapterByArrayList(typeSpinner, getActivity(), arrayListType);

        setLedgerSpinner();

        Button saveButton = (Button) view.findViewById(R.id.save_add_expense);
        saveButton.setOnClickListener(new OnClickListener());
    }

    private void setLedgerSpinner() {
        arrayListLedger = new ArrayList<>();
        arrayListLedger.clear();
        getLedger();
        Spinner ledgerSpinner = (Spinner) view.findViewById(R.id.ledger_expense_spinner);
        ledgerSpinner.setOnItemSelectedListener(this);
        Utility.setSpinnerAdapterByArrayList(ledgerSpinner, getActivity(), arrayListLedger);
    }

    private class OnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            String nameExpense = addNameEditText.getText().toString().trim();
            String valueExpense = addValueEditText.getText().toString().trim();
            String dateExpense = Utility.simpleDateFormat(Utility.dateInMilliSecond(dateTextView.getText().toString()));

            SessionManager preference = new SessionManager();
            String id = preference.getDatePreferences(getActivity(), PREFERENCE_EDIT, KEY_ID);
            if(id != null) {
                if (view.getId() == R.id.save_add_expense) {
                    if (validateInput()) {
                            if(db.updateExpense(Long.valueOf(id), nameExpense, valueExpense, Utility.currentTimeInMillis(), dateExpense, idType, idLedger)) {
                                Utility.successSnackBar(layoutExpenseName, "Updated", getActivity());
                                Utility.setResultActivity(getActivity());
                                removeSharedPreferences();
                                getActivity().finish();
                            } else {
                                Utility.failSnackBar(layoutExpenseName, "Error, try again", getActivity());
                            }
                    } else if (!validateInput()) {
                        Utility.failSnackBar(layoutExpenseName, "Error, fields cannot be empty", getActivity());
                    }
                }
            } else {
                if (view.getId() == R.id.date_expense) {
                    showDatePicker();
                }

                if (view.getId() == R.id.save_add_expense) {
                    if (validateInput()) {
                        if (db.isExpenseExist(nameExpense)) {
                            Utility.failSnackBar(layoutExpenseName, "Error, name already existed", getActivity());
                        } else {
                            if (db.addExpense(nameExpense, valueExpense, Utility.currentTimeInMillis(), dateExpense, idType, idLedger)) {
                                Utility.successSnackBar(layoutExpenseName, "Save", getActivity());
                                addNameEditText.setText("");
                                addValueEditText.setText("");
                                dateTextView.setText(dateTextView.getText().toString());
                                Utility.requestFocus(addNameEditText, getActivity());
                                Utility.hintDisable(addNameEditText, layoutExpenseName);
                                Utility.hintDisable(addValueEditText, layoutExpenseValue);
                            } else {
                                Utility.failSnackBar(layoutExpenseName, "Error, try again", getActivity());
                            }
                        }

                    } else if (!validateInput()) {
                        Utility.failSnackBar(layoutExpenseName, "Error, fields cannot be empty", getActivity());
                    }
                }
            }
            }

    }

    private void showDatePicker() {
        FragmentManager manager = getActivity().getFragmentManager();
        DialogFragment dialog = new ExpenseDatePicker();
        dialog.show(manager, "ExpenseDatePicker");
    }

    private class addNewItemTextWatcher implements TextWatcher {
        private View view;

        private addNewItemTextWatcher(View view) {
            this.view = view;
        }

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            switch (view.getId()) {
                case R.id.name_expense:
                    Utility.validateEditText(addNameEditText, layoutExpenseName, "Enter valid name");
                    break;
                case R.id.value_expense:
                    Utility.validateEditText(addValueEditText, layoutExpenseValue, "Enter valid value");
                    break;
            }

        }
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        Spinner spinner = (Spinner) adapterView;

        if (spinner.getId() == R.id.type_spinner) {
            String type = String.valueOf(adapterView.getItemAtPosition(i));
            idType = db.getIdByType(type);

        } else if (spinner.getId() == R.id.ledger_expense_spinner) {
            String ledger = String.valueOf(adapterView.getItemAtPosition(i));
            idLedger = db.getIdByLedger(ledger);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    private void getTypesOrderByTypeId(String orderId) {
        Cursor cursor = db.selectTypeOrderByTypeId(orderId);
        cursor.moveToFirst();
        for (int i = 0; i < cursor.getCount(); i++) {
            arrayListType.add(cursor.getString(cursor.getColumnIndex("name")));
            cursor.moveToNext();
        }
        cursor.close();
    }

    private void getTypes() {
        Cursor cursor = db.selectMainType();
        cursor.moveToFirst();
        for (int i = 0; i < cursor.getCount(); i++) {
            arrayListType.add(cursor.getString(cursor.getColumnIndex("name")));
            cursor.moveToNext();
        }
        cursor.close();
    }

    private void getLedger() {
        Cursor cursor = db.selectLedger();
        cursor.moveToFirst();
        for (int i = 0; i < cursor.getCount(); i++) {
            arrayListLedger.add(cursor.getString(cursor.getColumnIndex("title")));
            cursor.moveToNext();
        }
        cursor.close();
    }

    private void getLedgerOrderById(String id) {
        Cursor cursor = db.selectLedgerOrderById(id);
        cursor.moveToFirst();
        for (int i = 0; i < cursor.getCount(); i++) {
            arrayListLedger.add(cursor.getString(cursor.getColumnIndex("title")));
            cursor.moveToNext();
        }
        cursor.close();
    }

    private boolean validateInput() {
        return Utility.validateEditText(addNameEditText, layoutExpenseName, "Enter valid name") &&
                Utility.validateEditText(addValueEditText, layoutExpenseValue, "Enter valid value") &&
                idType!=null && idLedger!=null;
    }

    private void removeSharedPreferences() {
        SharedPreferences.Editor preferences = getActivity().getSharedPreferences(PREFERENCE_EDIT, Context.MODE_PRIVATE).edit();
        preferences.remove(KEY_ID);
        preferences.remove(KEY_NAME);
        preferences.remove(KEY_VALUE);
        preferences.remove(KEY_TYPE_ID);
        preferences.remove(KEY_LEDGER_ID);
        preferences.apply();
    }
}

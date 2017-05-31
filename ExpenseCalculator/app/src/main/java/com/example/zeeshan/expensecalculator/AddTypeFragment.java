package com.example.zeeshan.expensecalculator;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

public class AddTypeFragment extends Fragment{

    private TextInputLayout layoutAddType;
    private EditText addType;

    private DB db;
    View view;
    SharedPreferences editor;
    SessionManager preference;
    String d;
    boolean check;
    private final String PREFERENCE_EDIT = "edit_type";
    private final String KEY_ID = "id";
    private final String KEY_NAME = "name";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.add_type, container, false);
        db = new DB(getActivity());
        getLayoutReferences();

        preference = new SessionManager();
        editor = getActivity().getSharedPreferences(PREFERENCE_EDIT, Context.MODE_PRIVATE);
        if(editor.contains(KEY_ID) && editor.contains(KEY_NAME)) {
            if(check) {
                String name = preference.getDatePreferences(getActivity(), PREFERENCE_EDIT, KEY_NAME);
                EditText nameEditText = (EditText) view.findViewById(R.id.add_main_type);
                nameEditText.setText(name);
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
        } catch (Exception e) {
            Log.d("setUserVisibleHint", e.getMessage());
        }
    }

    private void getLayoutReferences() {
        layoutAddType = (TextInputLayout) view.findViewById(R.id.text_input_layout_add_main_type);
        addType = (EditText) view.findViewById(R.id.add_main_type);
        addType.addTextChangedListener(new addNewItemTextWatcher(addType));
        Button addTypeButton = (Button) view.findViewById(R.id.add_type_button);
        addTypeButton.setOnClickListener(new OnClickListener());
    }

    private class OnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            int id = view.getId();
            switch (id) {
                case R.id.add_type_button:
                    SessionManager preference = new SessionManager();
                    String idType = preference.getDatePreferences(getActivity(), PREFERENCE_EDIT, KEY_ID);
                    if(idType!=null) {
                        if (Utility.validateEditText(addType, layoutAddType, "Enter valid type")) {
                            String type = addType.getText().toString();
                            db.updateType(Long.valueOf(idType), type);
                            Utility.successSnackBar(layoutAddType, "Type updated", getActivity());
                            Utility.setResultActivity(getActivity());
                            removeSharedPreferences();
                            getActivity().finish();
                        } else if (!Utility.validateEditText(addType, layoutAddType, "Enter valid type")) {
                            Utility.failSnackBar(layoutAddType, "Error, field cannot be empty, try again", getActivity());
                        }
                    } else {
                        if (Utility.validateEditText(addType, layoutAddType, "Enter valid type")) {
                            String type = addType.getText().toString();

                            if (db.isTypeExisted(type)) {
                                Utility.failSnackBar(layoutAddType, "Error, type already existed. try another", getActivity());
                            } else {
                                db.addType(type);
                                addType.setText("");
                                Utility.hintDisable(addType, layoutAddType);
                                Utility.successSnackBar(layoutAddType, "Type added", getActivity());
                            }
                        } else if (!Utility.validateEditText(addType, layoutAddType, "Enter valid type")) {
                            Utility.failSnackBar(layoutAddType, "Error, field cannot be empty, try again", getActivity());
                        }
                    }

            }
        }
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
                case R.id.add_main_type:
                    Utility.validateEditText(addType, layoutAddType, "Enter valid type");
                    break;
            }

        }
    }

    private void removeSharedPreferences() {
        SharedPreferences.Editor preferences = getActivity().getSharedPreferences(PREFERENCE_EDIT, Context.MODE_PRIVATE).edit();
        preferences.remove(KEY_ID);
        preferences.remove(KEY_NAME);
        preferences.apply();
    }
}

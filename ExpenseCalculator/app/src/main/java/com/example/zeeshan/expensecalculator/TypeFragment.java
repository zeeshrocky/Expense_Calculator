package com.example.zeeshan.expensecalculator;


import android.app.Activity;
import android.app.ListFragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class TypeFragment extends ListFragment implements AdapterView.OnItemClickListener{

    ArrayList<String> arrayList;
    DB db;
    getDataFromTypeFragment getData;
    View view;
    ListView listView;
    ArrayAdapter<String> adapter;
    ArrayList<String> selectedItemsList;
    SessionManager sessionManager;
    List<String> selectedPositionList;
    final String PREFERENCES_FILTER = "filter";
    final String KEY_PREFERENCES = "arrayList";

    public interface getDataFromTypeFragment{
        void getTypes(ArrayList<String> data);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            getData = (getDataFromTypeFragment) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.type_list_fragment, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        arrayList = new ArrayList<>();
        db = new DB(getActivity());
        listView = (ListView) view.findViewById(android.R.id.list);
        sessionManager = new SessionManager();

        viewTypes();

        adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_multiple_choice, arrayList);
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(this);

        retainStateListView(true);
    }

    public void retainStateListView(boolean state) {
        List<String> stateList;
        SessionManager sessionManager = new SessionManager();
        SharedPreferences editor = getActivity().getSharedPreferences(PREFERENCES_FILTER, Context.MODE_PRIVATE);
        if(editor.contains(KEY_PREFERENCES)) {
            if(sessionManager.getPreferences(getActivity(), PREFERENCES_FILTER, KEY_PREFERENCES).isEmpty()) {
                Utility.shortToast(getActivity(), String.valueOf("state empty"));
            } else {
                stateList = sessionManager.getPreferences(getActivity(), PREFERENCES_FILTER, KEY_PREFERENCES);
                if(stateList.isEmpty()) {
                    Utility.shortToast(getActivity(), "empty");
                } else {
                    for(int j = 0; j<stateList.size(); j++) {
                        listView.setItemChecked(Integer.valueOf(stateList.get(j)), state);
                    }
                }
            }
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        SparseBooleanArray checked = listView.getCheckedItemPositions();
        selectedPositionList = new ArrayList<>();
        selectedItemsList = new ArrayList<>();
        int position;
        for (int j = 0; j < checked.size(); j++) {
            position = checked.keyAt(j);
            if (checked.valueAt(j)) {
                selectedItemsList.add(adapter.getItem(position));
                selectedPositionList.add(String.valueOf(position));
            }
        }
        getData.getTypes(selectedItemsList);

        saveState();
    }

    private void viewTypes() {
        try {
            Cursor cursor = db.selectAllMainTypes();
            cursor.moveToFirst();
            if (cursor.getCount() == 0) {
                Toast.makeText(getActivity(), "Empty list", Toast.LENGTH_SHORT).show();
            } else {
                for (int i = 0; i < cursor.getCount(); i++) {
                    arrayList.add(cursor.getString(cursor.getColumnIndex("name")));
                    cursor.moveToNext();
                }
                cursor.close();
            }
        } catch (Exception e) {
            Log.d("viewTypes", " failed " + e.getMessage());
        }
    }

    private void saveState() {
        sessionManager.setPreferences(getActivity(), PREFERENCES_FILTER, KEY_PREFERENCES, selectedPositionList);
    }
}


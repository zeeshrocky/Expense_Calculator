package com.example.zeeshan.expensecalculator;

import android.app.Activity;
import android.app.ListFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

public class LeftFragmentFilter extends ListFragment implements AdapterView.OnItemClickListener {

    Get get;
    View view, prevSelectedView;
    ListView listView;
    AdapterLeftFragment adapter;

    public interface Get {
        void getData(int s);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            get = (Get) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.list_fragment, container, false);
        prevSelectedView = view;
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ArrayList<String> arrayList = new ArrayList<>();
        listView = (ListView) view.findViewById(android.R.id.list);

        arrayList.add("Type");
        arrayList.add("Date");
        arrayList.add("From-to Date");

        adapter = new AdapterLeftFragment(getActivity(), arrayList);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);

        adapter.setSelectedItemPosition(0);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        adapter.setSelectedItemPosition(position);
        adapter.notifyDataSetChanged();
        get.getData(position);
    }

    void makeStyleBoldAtFirstPosition(boolean check) {
        adapter.makeStyleBoldAtFirstPosition(check);
        adapter.notifyDataSetChanged();
    }

    void makeStyleBoldAtSecondPosition(boolean check) {
        adapter.makeStyleBoldAtSecondPosition(check);
        adapter.notifyDataSetChanged();
    }

    void makeStyleBoldAtThirdPosition(boolean check) {
        adapter.makeStyleBoldAtThirdPosition(check);
        adapter.notifyDataSetChanged();
    }

    void countSelection(int count) {
        adapter.countSelection(count);
        adapter.notifyDataSetChanged();
    }


}


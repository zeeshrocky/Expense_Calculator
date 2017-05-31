package com.example.zeeshan.expensecalculator;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


class SessionManager {

    void setPreferences(Context context, String name, String key, List<String> arrayList) {
        SharedPreferences.Editor editor = context.getSharedPreferences(name, Context.MODE_PRIVATE).edit();
        Set<String> set = new HashSet<>();
        set.addAll(arrayList);
        editor.putStringSet(key, set);
        editor.apply();
    }

    void setDatePreferences(Context context, String name, String key, String value) {
        SharedPreferences.Editor editor = context.getSharedPreferences(name, Context.MODE_PRIVATE).edit();
        editor.putString(key, value);
        editor.apply();
    }

    List<String> getPreferences(Context context, String name, String key) {
        SharedPreferences prefs = context.getSharedPreferences(name,Context.MODE_PRIVATE);
        Set<String> set = prefs.getStringSet(key, null);
        List<String> list = new ArrayList<>(set);
        return list;
    }

    String getDatePreferences(Context context, String name, String key) {
        SharedPreferences prefs = context.getSharedPreferences(name,Context.MODE_PRIVATE);
        return prefs.getString(key, null);
    }
}

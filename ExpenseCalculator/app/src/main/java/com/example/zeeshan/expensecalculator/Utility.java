package com.example.zeeshan.expensecalculator;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

class Utility {

    static String dateFormat(long date) {
        Date dateF = new Date(date);
        return new SimpleDateFormat("dd-MM-yyyy hh:mm a", Locale.US).format(dateF);
    }

    static void setResultActivity(Activity activity) {
        Intent intent = new Intent();
        activity.setResult(activity.RESULT_OK, intent);
    }
    static String currentTimeInMillis() {
        Calendar calendar = Calendar.getInstance();
        return String.valueOf(calendar.getTimeInMillis());
    }

    static String currentTimeInDateFormat() {
        Calendar c = Calendar.getInstance();
        c.getTimeInMillis();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        return String.valueOf(new StringBuilder().append(day<10?"0"+day:day).append("/").append(month+1<10?"0"+month+1:month+1).append("/").append(year));
    }

    static String TimefutureThirtyDays() {
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DATE, 30);
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        return String.valueOf(new StringBuilder().append(day<10?"0"+day:day).append("/").append(month+1<10?"0"+month+1:month+1).append("/").append(year));
    }

    static String simpleDateFormat(long timeInMills) {
        SimpleDateFormat formatter = new SimpleDateFormat("ddMMyyyy", Locale.US);
        return formatter.format(new Date(timeInMills));
    }

    static long dateInMilliSecond(String date) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
        try {
            Date mDate = sdf.parse(date);
            return mDate.getTime();
        }
        catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }

    static void shortToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    static void alertDialog(Context context, String title, String message) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        alertDialog.setTitle(title);
        alertDialog.setCancelable(true);
        alertDialog.setMessage(message);
        alertDialog.create();
        alertDialog.show();
    }

    static void failSnackBar(View view, String message, Context context) {
        Snackbar snackbar = Snackbar.make(view, message, Snackbar.LENGTH_LONG);
        View view1 = snackbar.getView();
        view1.setBackgroundColor(context.getResources().getColor(R.color.failure));
        TextView textView = (TextView) view1.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(Color.WHITE);
        snackbar.show();
    }

    static void successSnackBar(View view, String message, Context context) {
        Snackbar snackbar = Snackbar.make(view, message, Snackbar.LENGTH_LONG);
        View view1 = snackbar.getView();
        view1.setBackgroundColor(context.getResources().getColor(R.color.success));
        TextView textView = (TextView) view1.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(Color.WHITE);
        snackbar.show();
    }

//    static void startAnActivity(Context context, Class<?> className) {
//        Intent intent = new Intent(context, className);
//        context.startActivity(intent);
//    }

    static void startAnActivityForResult(Activity activity, Context context, Class<?> className, int requestCode) {
        Intent intent = new Intent(context, className);
        activity.startActivityForResult(intent, requestCode);
    }
    static boolean hintDisable(EditText editText, TextInputLayout textInputLayout) {
        textInputLayout.setErrorEnabled(false);
        textInputLayout.setHintEnabled(true);
        return true;
    }

    static boolean validateEditText(EditText editText, TextInputLayout textInputLayout, String errorMessage) {
            if (editText.getText().toString().trim().isEmpty()) {
                textInputLayout.setError(errorMessage);
                textInputLayout.setHintEnabled(false);
                textInputLayout.setHintAnimationEnabled(true);
                return false;
            } else {
                textInputLayout.setHintEnabled(true);
                textInputLayout.setHintAnimationEnabled(true);
                textInputLayout.setErrorEnabled(false);
            }

            return true;
    }
    static void requestFocus(View view, Context context) {
        if (view.requestFocus()) {
            ((Activity) context).getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }
    static void setSpinnerAdapterByArrayList(Spinner spinner, Context context, List<String> list) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, list);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }
}

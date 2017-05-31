package com.example.zeeshan.expensecalculator;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

class DB extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "expense_calculator.db";

    private final String TABLE_MAIN_TYPE = "main_type";
    private final String ID_MAIN_TYPE = "id";
    private final String NAME_MAIN_TYPE = "name";
    private final String KEY_SEARCH = "key_search";

    private final String CREATE_MAIN_TYPE_TABLE = "CREATE TABLE " + TABLE_MAIN_TYPE
            + "(" + ID_MAIN_TYPE + " INTEGER PRIMARY KEY AUTOINCREMENT DEFAULT 0,"
            + NAME_MAIN_TYPE + " TEXT,"
            + KEY_SEARCH + " TEXT,"
            + "UNIQUE(" + NAME_MAIN_TYPE + "))";

    private final String TABLE_EXPENSE = "expense";
    private final String ID_EXPENSE = "id";
    private final String NAME_EXPENSE = "name";
    private final String VALUE_EXPENSE = "value";
    private final String TYPE_ID_EXPENSE = "type_id";
    private final String DATE_EXPENSE = "date";
    private final String KEY_SEARCH_DATE = "key_search_date";
    private final String FOREIGN_KEY_LEDGER = "foreign_key_ledger";

    private final String CREATE_EXPENSE_TABLE = "CREATE TABLE " + TABLE_EXPENSE
            + "(" + ID_EXPENSE + " INTEGER PRIMARY KEY AUTOINCREMENT DEFAULT 0,"
            + NAME_EXPENSE + " TEXT,"
            + VALUE_EXPENSE + " TEXT,"
            + DATE_EXPENSE + " TEXT,"
            + KEY_SEARCH_DATE + " TEXT,"
            + TYPE_ID_EXPENSE + " TEXT,"
            + FOREIGN_KEY_LEDGER + " TEXT)";

    private final String TABLE_LEDGER = "ledger";
    private final String ID_LEDGER = "id";
    private final String TITLE_LEDGER = "title";
    private final String STARTING_BALANCE_LEDGER = "starting_balance";
    private final String DATE_LEDGER = "date";
    private final String FROM_DATE_LEDGER = "from_date";
    private final String TO_DATE_LEDGER = "to_date";

    private final String CREATE_LEDGER_TABLE = "CREATE TABLE " + TABLE_LEDGER
            + "(" + ID_LEDGER + " INTEGER PRIMARY KEY AUTOINCREMENT DEFAULT 0,"
            + TITLE_LEDGER + " TEXT,"
            + STARTING_BALANCE_LEDGER + " TEXT,"
            + DATE_LEDGER + " TEXT,"
            + FROM_DATE_LEDGER + " TEXT,"
            + TO_DATE_LEDGER + " TEXT)";

    // Constructor for creating database
    DB(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            db.execSQL(CREATE_MAIN_TYPE_TABLE);
            db.execSQL(CREATE_EXPENSE_TABLE);
            db.execSQL(CREATE_LEDGER_TABLE);
            db.execSQL("INSERT INTO main_type(name) VALUES('all');");
        } catch (Exception e) {
            Log.d("Creating Database:", "error " + e.getMessage());
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + CREATE_MAIN_TYPE_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + CREATE_EXPENSE_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + CREATE_LEDGER_TABLE);
        onCreate(db);
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        db.execSQL("PRAGMA foreign_keys = ON;");
        super.onOpen(db);
    }

//    public void getWritableDB() throws SQLException {
//        db = this.getWritableDatabase();
//    }
//
//    public void closeDB() {
//        if (myDBHelper != null) {
//            myDBHelper.close();
//        }
//    }

    Cursor selectExpense(String incomeId) {
        Cursor cursor = null;
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            cursor = db.rawQuery("SELECT " + ID_EXPENSE + "," + NAME_EXPENSE + "," + VALUE_EXPENSE
                    + "," + DATE_EXPENSE + "," + TYPE_ID_EXPENSE + " FROM " + TABLE_EXPENSE
                    + " WHERE " + FOREIGN_KEY_LEDGER + "='" + incomeId + "'"
                    + " ORDER BY " + DATE_EXPENSE + " DESC", null);
        } catch (Exception e) {
            Log.d("selectExpenses", " error " + e.getMessage());
        }

        return cursor;
    }

    Cursor selectExpenseByTypeAndDate(String type, String date, String idLedger) {
        Cursor cursor = null;
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            cursor = db.rawQuery("SELECT " + ID_EXPENSE + ","
                    + NAME_EXPENSE + ","
                    + VALUE_EXPENSE + ","
                    + DATE_EXPENSE + ","
                    + KEY_SEARCH_DATE + ","
                    + TYPE_ID_EXPENSE + " FROM " + TABLE_EXPENSE
                    + " WHERE " + TYPE_ID_EXPENSE + "='" + type + "' AND "
                    + KEY_SEARCH_DATE + "='" + date + "' AND " + FOREIGN_KEY_LEDGER + "='" + idLedger + "'"
                    + " ORDER BY " + DATE_EXPENSE + " DESC", null);
        } catch (Exception e) {
            Log.d("selectExpenses", " error " + e.getMessage());
        }

        return cursor;
    }

    Cursor selectExpenseByTypeAndFromToDate(String type, String fromDate, String toDate, String idLedger) {
        Cursor cursor = null;
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            cursor = db.rawQuery("SELECT " + ID_EXPENSE + ","
                    + NAME_EXPENSE + ","
                    + VALUE_EXPENSE + ","
                    + DATE_EXPENSE + ","
                    + KEY_SEARCH_DATE + ","
                    + TYPE_ID_EXPENSE + " FROM " + TABLE_EXPENSE
                    + " WHERE " + TYPE_ID_EXPENSE + "='" + type + "' AND "
                    + KEY_SEARCH_DATE + " BETWEEN '" + fromDate + "' AND '" + toDate + "' AND " + FOREIGN_KEY_LEDGER + "='" + idLedger + "'"
                    + " ORDER BY " + DATE_EXPENSE + " DESC", null);
        } catch (Exception e) {
            Log.d("selectExpenses", " error " + e.getMessage());
        }

        return cursor;
    }

    Cursor selectLedger() {
        Cursor cursor = null;
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            cursor = db.rawQuery("SELECT " + ID_LEDGER + "," + TITLE_LEDGER  + "," + STARTING_BALANCE_LEDGER + " FROM " + TABLE_LEDGER
                    + " ORDER BY " + ID_LEDGER + " DESC", null);
        } catch (Exception e) {
            Log.d("selectLedger", " error " + e.getMessage());
        }

        return cursor;
    }

    Cursor selectLedgerOrderById(String id) {
        Cursor cursor = null;
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            cursor = db.rawQuery("SELECT " + ID_LEDGER + "," + TITLE_LEDGER  + "," + STARTING_BALANCE_LEDGER + " FROM " + TABLE_LEDGER
                    + " ORDER BY " + ID_LEDGER + "='" + id + "' DESC", null);
        } catch (Exception e) {
            Log.d("selectLedger", " error " + e.getMessage());
        }

        return cursor;
    }

    Cursor selectLedgerValueById(String id) {
        Cursor cursor = null;
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            cursor = db.rawQuery("SELECT " + STARTING_BALANCE_LEDGER  + " FROM " + TABLE_LEDGER
                    + " WHERE "+ID_LEDGER+"='" +id+"'", null);
        } catch (Exception e) {
            Log.d("selectLedgerValueById", " error " + e.getMessage());
        }

        return cursor;
    }

    Cursor selectFromToDate(String from, String to, String idLedger) {
        Cursor cursor = null;
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            cursor = db.rawQuery("SELECT " + ID_EXPENSE + ","
                    + NAME_EXPENSE + ","
                    + VALUE_EXPENSE + ","
                    + DATE_EXPENSE + ","
                    + KEY_SEARCH_DATE + ","
                    + TYPE_ID_EXPENSE + " FROM " + TABLE_EXPENSE
                    + " WHERE " + KEY_SEARCH_DATE + " BETWEEN '" + from + "' AND '" + to
                    + "' AND " + FOREIGN_KEY_LEDGER + "='" + idLedger + "'", null);
        } catch (Exception e) {
            Log.d("selectFromToDate", " error " + e.getMessage());
        }

        return cursor;
    }

    String selectIdByLedgerName(String name) {
        Cursor cursor = null;
        String id = null;
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            cursor = db.rawQuery("SELECT " + ID_LEDGER + " FROM " + TABLE_LEDGER + " WHERE " + TITLE_LEDGER + "='" + name + "'", null);
            cursor.moveToFirst();
        } catch (Exception e) {
            Log.d("selectIdByMainTypeName", " error is " + e.getMessage());
        }
        if(cursor != null && cursor.moveToFirst()) {
            id = cursor.getString(cursor.getColumnIndex(ID_LEDGER));
            cursor.close();
        }
        return id;
    }

    String selectLastIdOfLedger() {
        Cursor cursor;
        String id = null;
        SQLiteDatabase db = this.getWritableDatabase();
        cursor = db.rawQuery("SELECT " + ID_LEDGER + " FROM " + TABLE_LEDGER, null);
        if(cursor!=null && cursor.moveToLast()) {
            cursor.moveToLast();
            id = cursor.getString(cursor.getColumnIndex(ID_LEDGER));
            cursor.close();
        }
        return id;
    }

    Cursor selectExpenseByType(String type, String idLedger) {
        Cursor cursor = null;
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            cursor = db.rawQuery("SELECT " + ID_EXPENSE + ","
                    + NAME_EXPENSE + ","
                    + VALUE_EXPENSE + ","
                    + DATE_EXPENSE + ","
                    + TYPE_ID_EXPENSE + " FROM " + TABLE_EXPENSE
                    + " WHERE " + TYPE_ID_EXPENSE + "='" + type + "' AND " + FOREIGN_KEY_LEDGER + "='" + idLedger + "'"
                    + " ORDER BY " + DATE_EXPENSE + " DESC", null);
        } catch (Exception e) {
            Log.d("selectExpenseByType", " error is " + e.getMessage());
        }
        return cursor;
    }

    Cursor selectExpenseByDate(String date, String idLedger) {
        Cursor cursor = null;
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            cursor = db.rawQuery("SELECT " + ID_EXPENSE + ","
                    + NAME_EXPENSE + ","
                    + VALUE_EXPENSE + ","
                    + DATE_EXPENSE + ","
                    + TYPE_ID_EXPENSE + ","
                    + KEY_SEARCH_DATE +" FROM " + TABLE_EXPENSE
                    + " WHERE " + KEY_SEARCH_DATE + "='" + date + "' AND " + FOREIGN_KEY_LEDGER + "='" + idLedger + "'"
                    + " ORDER BY " + DATE_EXPENSE + " DESC", null);
        } catch (Exception e) {
            Log.d("selectDateOfExpense", " error " + e.getMessage());
        }

        return cursor;
    }

    boolean addType(String mainType) {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues contentValues = new ContentValues();

            contentValues.put(NAME_MAIN_TYPE, mainType);
            db.insert(TABLE_MAIN_TYPE, null, contentValues);

        } catch (Exception e) {
            Log.d("addMainType", " error " + e.getMessage());
        }
        return true;
    }

    boolean addExpense(String name, String value, String date, String keyDate, String type, String incomeId) {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put(NAME_EXPENSE, name);
            contentValues.put(VALUE_EXPENSE, value);
            contentValues.put(DATE_EXPENSE, date);
            contentValues.put(KEY_SEARCH_DATE, keyDate);
            contentValues.put(TYPE_ID_EXPENSE, type);
            contentValues.put(FOREIGN_KEY_LEDGER, incomeId);
            db.insert(TABLE_EXPENSE, null, contentValues);

        } catch (Exception e) {
            Log.d("addExpense", " error " + e.getMessage());
        }
        return true;
    }

    boolean addLedger(String title, String startingBalance, String date, String fromDate, String toDate) {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues contentValues = new ContentValues();

            contentValues.put(TITLE_LEDGER, title);
            contentValues.put(STARTING_BALANCE_LEDGER, startingBalance);
            contentValues.put(DATE_LEDGER, date);
            contentValues.put(FROM_DATE_LEDGER, fromDate);
            contentValues.put(TO_DATE_LEDGER, toDate);
            db.insert(TABLE_LEDGER, null, contentValues);

        } catch (Exception e) {
            Log.d("addLedger", " error " + e.getMessage());
        }
        return true;
    }

    boolean isLedgerExist(String title) {
        Cursor cursor;
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            cursor = db.rawQuery("SELECT " + TITLE_LEDGER + " FROM " + TABLE_LEDGER
                    + " WHERE " + TITLE_LEDGER + "='" + title + "' COLLATE NOCASE", null);
            if(cursor != null && cursor.moveToFirst()) {
                cursor.getString(cursor.getColumnIndex(TITLE_LEDGER));
                cursor.close();
                return true;
            }
        } catch (Exception e) {
            Log.d("isLedgerExist", " error " + e.getMessage());
        }
        return false;
    }

    boolean isExpenseExist(String title) {
        Cursor cursor;
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            cursor = db.rawQuery("SELECT " + NAME_EXPENSE + " FROM " + TABLE_EXPENSE
                    + " WHERE " + NAME_EXPENSE + "='" + title + "' COLLATE NOCASE", null);
            if(cursor != null && cursor.moveToFirst()) {
                cursor.getString(cursor.getColumnIndex(NAME_EXPENSE));
                cursor.close();
                return true;
            }
        } catch (Exception e) {
            Log.d("isExpenseExist", " error " + e.getMessage());
        }
        return false;
    }

    boolean deleteExpense(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_EXPENSE + " WHERE " + ID_EXPENSE + "='" + id + "'");
        return true;
    }


    boolean deleteLedger(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_LEDGER + " WHERE " + ID_LEDGER + "='" + id + "'");
        return true;
    }

    boolean deleteExpenseByLedgerId(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_EXPENSE + " WHERE " + FOREIGN_KEY_LEDGER + "='" + id + "'");
        return true;
    }

    boolean deleteExpenseByTypeId(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_EXPENSE + " WHERE " + TYPE_ID_EXPENSE + "='" + id + "'");
        return true;
    }

    boolean deleteType(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_MAIN_TYPE + " WHERE " + ID_MAIN_TYPE + "='" + id + "'");
        return true;
    }

    boolean updateExpense(long id, String name, String value, String date, String keyDate, String type, String ledgerId) {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put(NAME_EXPENSE, name);
            contentValues.put(VALUE_EXPENSE, value);
            contentValues.put(DATE_EXPENSE, date);
            contentValues.put(KEY_SEARCH_DATE, keyDate);
            contentValues.put(TYPE_ID_EXPENSE, type);
            contentValues.put(FOREIGN_KEY_LEDGER, ledgerId);
            db.update(TABLE_EXPENSE, contentValues, ID_EXPENSE + "= ?", new String[]{Long.toString(id)});
            return true;
        } catch (Exception e) {
            Log.d("updateExpense", e.getMessage());
        }
        return false;
    }

//    boolean updateType(long id, String name) {
//        try {
//            SQLiteDatabase db = this.getWritableDatabase();
//            ContentValues contentValues = new ContentValues();
//            contentValues.put(NAME_EXPENSE, name);
//            db.update(TABLE_EXPENSE, contentValues, ID_EXPENSE + "= ?", new String[]{Long.toString(id)});
//            return true;
//        } catch (Exception e) {
//            Log.d("updateExpense", e.getMessage());
//        }
//        return false;
//    }

    boolean updateLedger(long id, String name, String value, String fromDate, String toDate) {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put(TITLE_LEDGER, name);
            contentValues.put(STARTING_BALANCE_LEDGER, value);
            contentValues.put(FROM_DATE_LEDGER, fromDate);
            contentValues.put(TO_DATE_LEDGER, toDate);
            db.update(TABLE_LEDGER, contentValues, ID_LEDGER + "= ?", new String[]{Long.toString(id)});
            return true;
        } catch (Exception e) {
            Log.d("updateLedger", e.getMessage());
        }
        return false;
    }

    boolean updateType(long id, String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(NAME_MAIN_TYPE, name);
        db.update(TABLE_MAIN_TYPE, contentValues, ID_MAIN_TYPE + "= ?", new String[]{Long.toString(id)});
        return true;
    }

    boolean isTypeExisted(String inputMainType) {
        Cursor cursor;
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            cursor = db.rawQuery("SELECT " + NAME_MAIN_TYPE + " FROM " + TABLE_MAIN_TYPE
                    + " WHERE " + NAME_MAIN_TYPE + "='" + inputMainType + "' COLLATE NOCASE", null);
            if(cursor != null && cursor.moveToFirst()) {
                cursor.getString(cursor.getColumnIndex(NAME_MAIN_TYPE));
                cursor.close();
                return true;
            }
        } catch (Exception e) {
            Log.d("isMainTypeExisted", " error is " + e.getMessage());
        }
        return false;
    }

    Cursor selectMainType() {
        Cursor cursor = null;
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            cursor = db.rawQuery("SELECT " + ID_MAIN_TYPE + "," + NAME_MAIN_TYPE + " FROM " + TABLE_MAIN_TYPE
                    + " WHERE " + ID_MAIN_TYPE + " NOT IN (SELECT " + ID_MAIN_TYPE + " FROM " + TABLE_MAIN_TYPE
                    + " WHERE " + ID_MAIN_TYPE + "= '1')"
                    + " ORDER BY " + ID_MAIN_TYPE + " DESC", null);
        } catch (Exception e) {
            Log.d("selectMainType", " error is " + e.getMessage());
        }
        return cursor;
    }

    Cursor selectTypeOrderByTypeId(String orderId) {
        Cursor cursor = null;
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            cursor = db.rawQuery("SELECT " + ID_MAIN_TYPE + "," + NAME_MAIN_TYPE + " FROM " + TABLE_MAIN_TYPE
                    + " WHERE " + ID_MAIN_TYPE + " NOT IN (SELECT " + ID_MAIN_TYPE + " FROM " + TABLE_MAIN_TYPE
                    + " WHERE " + ID_MAIN_TYPE + "= '1')"
                    + " ORDER BY " + ID_MAIN_TYPE + " ='+" + orderId + "' DESC", null);
        } catch (Exception e) {
            Log.d("selectTypeOrderByEditId", " error is " + e.getMessage());
        }
        return cursor;
    }

    Cursor selectAllMainTypes() {
        Cursor cursor = null;
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            cursor = db.rawQuery("SELECT " + ID_MAIN_TYPE + "," + NAME_MAIN_TYPE + " FROM " + TABLE_MAIN_TYPE
                    + " ORDER BY " + ID_MAIN_TYPE + " DESC", null);
        } catch (Exception e) {
            Log.d("selectAllMainTypes", " error is " + e.getMessage());
        }
        return cursor;
    }

    String selectTypeById(String id) {
        Cursor cursor = null;
        String name = null;
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            cursor = db.rawQuery("SELECT " + NAME_MAIN_TYPE + " FROM " + TABLE_MAIN_TYPE + " WHERE " + ID_MAIN_TYPE + "='" + id + "'", null);
        } catch (Exception e) {
            Log.d("selectMainType", " error is " + e.getMessage());
        }
        if(cursor != null && cursor.moveToFirst()) {
            name = cursor.getString(cursor.getColumnIndex(NAME_MAIN_TYPE));
            cursor.close();
        }
        return name;
    }

    String getIdByType(String type) {
        Cursor cursor = null;
        String id = null;
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            cursor = db.rawQuery("SELECT " + ID_MAIN_TYPE + " FROM " + TABLE_MAIN_TYPE + " WHERE " + NAME_MAIN_TYPE + "='" + type + "'", null);
            cursor.moveToFirst();
        } catch (Exception e) {
            Log.d("getIdByType", " error is " + e.getMessage());
        }
        if(cursor != null && cursor.moveToFirst()) {
            id = cursor.getString(cursor.getColumnIndex(ID_MAIN_TYPE));
            cursor.close();
        }
        return id;
    }

    String getIdByLedger(String ledger) {
        Cursor cursor = null;
        String id = null;
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            cursor = db.rawQuery("SELECT " + ID_LEDGER + " FROM " + TABLE_LEDGER + " WHERE " + TITLE_LEDGER + "='" + ledger + "'", null);
            cursor.moveToFirst();
        } catch (Exception e) {
            Log.d("getIdByType", " error is " + e.getMessage());
        }
        if(cursor != null && cursor.moveToFirst()) {
            id = cursor.getString(cursor.getColumnIndex(ID_LEDGER));
            cursor.close();
        }
        return id;
    }
}
package com.example.myfirebaseapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DBhandler extends SQLiteOpenHelper {

    private static final String DB_NAME = "SQLiteDB_mad";
    private static final int DB_VERSION = 1;
    private static final String TABLE_NAME = "users";
    private static final String ID_COL = "id";
    private static final String FIRSTNAME = "fname";
    private static final String LASTNAME = "lname";
    private static final String EMAIL = "mail";
    private static final String DOB = "dob";
    private static final String GENDER = "gender";
    private static final String MOBILE = "mobile";
    private static final String PASSWORD = "password";
    public DBhandler(@Nullable Context context) {
        super(context,DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE " + TABLE_NAME + " ("
                + ID_COL + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + FIRSTNAME + " TEXT, "
                + LASTNAME + " TEXT, "
                + EMAIL + " TEXT, "
                + DOB + " TEXT, "
                + GENDER + " TEXT, "
                + MOBILE + " TEXT, "
                + PASSWORD + " TEXT )";
        db.execSQL(query);
    }

    public void addNewUSER(String textFirstName, String textLastName,String textEmail, String textDOB, String textGender, String textMobile, String textPwd) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(FIRSTNAME, textFirstName);
        values.put(LASTNAME, textLastName);
        values.put(EMAIL, textEmail);
        values.put(DOB, textDOB);
        values.put(GENDER, textGender);
        values.put(MOBILE, textMobile);
        values.put(PASSWORD, textPwd);

        db.insert(TABLE_NAME, null, values);
        db.close();
    }

    public boolean verifyUSER(String email, String pwd) {

        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME + " WHERE "
                + EMAIL + " = '" + email + "' AND "
                + PASSWORD + " = '" + pwd + "'";
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.getCount() > 0) {
            cursor.close();
            return true;
        } else {
            cursor.close();
            return false;
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }
}
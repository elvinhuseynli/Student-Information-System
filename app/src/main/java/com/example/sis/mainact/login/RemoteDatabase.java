package com.example.sis.mainact.login;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class RemoteDatabase extends SQLiteOpenHelper {

    public RemoteDatabase(Context context) {
        super(context, "loginData.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase MyDB) {
        MyDB.execSQL("create Table data(idNumber TEXT primary key, password TEXT, position TEXT, signedIn TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase MyDB, int i, int j) {
        MyDB.execSQL("drop Table if exists data");
    }

    public void insertData(String idNumber, String password, String position, String signedIn) {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("idNumber", idNumber);
        contentValues.put("password", password);
        contentValues.put("position", position);
        contentValues.put("signedIn", signedIn);
        database.insert("data", null, contentValues);
    }

    public void deleteData() {
        SQLiteDatabase database = this.getWritableDatabase();
        database.delete("data", null, null);
    }

    public Boolean checkDatabase() {
        SQLiteDatabase database = this.getWritableDatabase();
        Cursor result = database.rawQuery("select * from data where signedIn=?", new String[]{"true"});
        return result.getCount()>0;
    }

    public String getUsername() {
        SQLiteDatabase database = this.getWritableDatabase();
        Cursor result = database.rawQuery("select idNumber from data", null);
        result.moveToNext();
        return result.getString(0);
    }

    public Cursor getData() {
        SQLiteDatabase database = this.getWritableDatabase();
        Cursor result = database.rawQuery("select * from data", null);
        return result;
    }

}
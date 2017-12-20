package com.example.ameerthehacker.olastudios;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by ameerthehacker on 20/12/17.
 */

public class History extends SQLiteOpenHelper {
    public History(Context context) {
        super(context, "LibraryDatabase", null, 1);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE IF NOT EXISTS history( id INTEGER PRIMARY KEY AUTOINCREMENT, activity TEXT )";
        db.execSQL(query);
    }
    public void insert(String activity) {
        String query = "INSERT INTO history (activity) VALUES(?)";
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(query, new String [] { activity });
    }

    public Cursor getHistory() {
        String query = "SELECT activity FROM history";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        return cursor;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}

}
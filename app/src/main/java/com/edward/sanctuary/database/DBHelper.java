package com.edward.sanctuary.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by edward on 3/24/17.
 */

public class DBHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 10;
    public static final String DATABASE_NAME = "Sanctuary.db";
    private static DBHelper instance;

    public static synchronized DBHelper getInstance(Context context){
        if(instance == null){
            instance = new DBHelper(context.getApplicationContext());
        }
        return instance;
    }

    private DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        if (!db.isReadOnly()) {
            // Enable foreign key constraints
            db.execSQL("PRAGMA foreign_keys=ON;");
        }
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(SQLCommands.SQL_CREATE_ENTRIES_USER);
        db.execSQL(SQLCommands.SQL_CREATE_ENTRIES_CARD);
        db.execSQL(SQLCommands.SQL_CREATE_ENTRIES_CARD_CARD);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if(oldVersion == 8){
            db.execSQL(SQLCommands.SQL_CREATE_ENTRIES_CARD_CARD);
        }
        else if(oldVersion == 9){
            db.execSQL(SQLCommands.SQL_DELETE_ENTRIES_CARD_CARD);
            db.execSQL(SQLCommands.SQL_CREATE_ENTRIES_CARD_CARD);
            System.out.println("DB Update executed");
        }
        else {
            db.execSQL(SQLCommands.SQL_DELETE_ENTRIES_USER);
            db.execSQL(SQLCommands.SQL_DELETE_ENTRIES_CARD);
            db.execSQL(SQLCommands.SQL_DELETE_ENTRIES_CARD_CARD);
            db.execSQL(SQLCommands.SQL_CREATE_ENTRIES_USER);
            db.execSQL(SQLCommands.SQL_CREATE_ENTRIES_CARD);
            db.execSQL(SQLCommands.SQL_CREATE_ENTRIES_CARD_CARD);
        }
    }
}

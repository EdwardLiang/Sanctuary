package com.edward.sanctuary.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.Arrays;
import java.util.Date;

/**
 * Created by edward on 3/24/17.
 */

public class Database {

    private Database() {
    }

    public static void addUser(String name, String pwd, Context context){
        // Gets the data repository in write mode
        DBHelper dbHelper = DBHelper.getInstance(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        byte[] salt = PasswordUtils.getNextSalt();
        byte[] hash = PasswordUtils.hash(pwd.toCharArray(), salt);

        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(UserContract.UserEntry.USERNAME, name);
        values.put(UserContract.UserEntry.PASSWORD_HASH, hash);
        values.put(UserContract.UserEntry.SALT, salt);
        values.put(UserContract.UserEntry.DATE_CREATED, persistDate(new Date()));

        // Insert the new row, returning the primary key value of the new row
        long newRowId = db.insert(UserContract.UserEntry.TABLE_NAME, null, values);
    }

    public static boolean isUser(String name, Context context){
        DBHelper dbHelper = DBHelper.getInstance(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String[] projection = {
                UserContract.UserEntry._ID,
                UserContract.UserEntry.SALT,
                UserContract.UserEntry.PASSWORD_HASH
        };
        String selection = UserContract.UserEntry.USERNAME + " = ?";
        String[] selectionArgs = { name };
        String sortOrder = UserContract.UserEntry.USERNAME + " DESC";

        Cursor cursor = db.query(
                UserContract.UserEntry.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );

        if(cursor.getCount() > 0){
            cursor.close();
            return true;
        }
        return false;
    }

    public static boolean verifyUser(String name, String pwd, Context context){
        // Gets the data repository in write mode
        DBHelper dbHelper = DBHelper.getInstance(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String[] projection = {
                UserContract.UserEntry._ID,
                UserContract.UserEntry.SALT,
                UserContract.UserEntry.PASSWORD_HASH
        };
        String selection = UserContract.UserEntry.USERNAME + " = ?";
        String[] selectionArgs = { name };
        String sortOrder = UserContract.UserEntry.USERNAME + " DESC";

        Cursor cursor = db.query(
                UserContract.UserEntry.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );

        cursor.moveToNext();
        if(cursor.getCount() == 0){
            return false;
        }
        if(cursor.getCount() > 1){
            System.out.println("Duplicate usernames in database!");
            return false;
        }
        long itemId = cursor.getLong(cursor.getColumnIndexOrThrow(UserContract.UserEntry._ID));
        byte[] salt = cursor.getBlob(cursor.getColumnIndex(UserContract.UserEntry.SALT));
        byte[] hash = cursor.getBlob(cursor.getColumnIndex(UserContract.UserEntry.PASSWORD_HASH));
        cursor.close();

        byte[] hashParam = PasswordUtils.hash(pwd.toCharArray(), salt);
        if(Arrays.equals(hashParam, hash)){
            return true;
        }
        return false;
    }

    public static Long persistDate(Date date) {
        if (date != null) {
            return date.getTime();
        }
        return null;
    }
}

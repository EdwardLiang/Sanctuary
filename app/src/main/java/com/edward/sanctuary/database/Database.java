package com.edward.sanctuary.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.edward.sanctuary.Card;

import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/**
 * Created by edward on 3/24/17.
 */

public class Database {

    private static final Random RANDOM = new SecureRandom();

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

    public static void addCard(String name, String description, long userId,  Context context){
        // Gets the data repository in write mode
        DBHelper dbHelper = DBHelper.getInstance(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(CardContract.CardEntry.NAME, name);
        values.put(CardContract.CardEntry.DESCRIPTION, description);
        values.put(CardContract.CardEntry.OWNER, userId);
        values.put(CardContract.CardEntry.IN_DRAWER, 0);
        values.put(CardContract.CardEntry.IS_DECK, 0);
        values.put(CardContract.CardEntry.DATE_CREATED, persistDate(new Date()));

        // Insert the new row, returning the primary key value of the new row
        long newRowId = db.insert(CardContract.CardEntry.TABLE_NAME, null, values);
    }

    public static List<Card> getCards(Context context, long userId){
        // Gets the data repository in write mode
        List<Card> cards = new LinkedList<Card>();
        DBHelper dbHelper = DBHelper.getInstance(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String[] projection = {
                CardContract.CardEntry._ID,
                CardContract.CardEntry.NAME,
                CardContract.CardEntry.DESCRIPTION,
                CardContract.CardEntry.DATE_CREATED
        };
        String selection = CardContract.CardEntry.OWNER + " = ?";
        String[] selectionArgs = { Long.toString(userId) };
        String sortOrder = CardContract.CardEntry.DATE_CREATED + " DESC";

        Cursor cursor = db.query(
                CardContract.CardEntry.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );

        while(cursor.moveToNext()) {
            long itemId = cursor.getLong(cursor.getColumnIndexOrThrow(CardContract.CardEntry._ID));
            String name = cursor.getString(cursor.getColumnIndex(CardContract.CardEntry.NAME));
            String desc = cursor.getString(cursor.getColumnIndex(CardContract.CardEntry.DESCRIPTION));
            long date = cursor.getLong(cursor.getColumnIndex(CardContract.CardEntry.DATE_CREATED));
            Card card = new Card(name, desc, date, itemId);
            cards.add(card);
        }
        return cards;
    }

    public static List<Card> getCards(Context context, long userId, int amount){
        // Gets the data repository in write mode
        List<Card> cards = new LinkedList<Card>();
        DBHelper dbHelper = DBHelper.getInstance(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String[] projection = {
                CardContract.CardEntry._ID,
                CardContract.CardEntry.NAME,
                CardContract.CardEntry.DESCRIPTION,
                CardContract.CardEntry.DATE_CREATED
        };
        String selection = CardContract.CardEntry.OWNER + " = ?";
        String[] selectionArgs = { Long.toString(userId) };
        String sortOrder = CardContract.CardEntry.DATE_CREATED + " DESC";

        Cursor cursor = db.query(
                CardContract.CardEntry.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder,
                String.valueOf(amount)
        );

        while(cursor.moveToNext()) {
            long itemId = cursor.getLong(cursor.getColumnIndexOrThrow(CardContract.CardEntry._ID));
            String name = cursor.getString(cursor.getColumnIndex(CardContract.CardEntry.NAME));
            String desc = cursor.getString(cursor.getColumnIndex(CardContract.CardEntry.DESCRIPTION));
            long date = cursor.getLong(cursor.getColumnIndex(CardContract.CardEntry.DATE_CREATED));
            Card card = new Card(name, desc, date, itemId);
            cards.add(card);
        }
        return cards;
    }

    public static boolean newCard(Context context, long userId, String name){
        // Gets the data repository in write mode
        List<Card> cards = new LinkedList<Card>();
        DBHelper dbHelper = DBHelper.getInstance(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String[] projection = {
                CardContract.CardEntry._ID,
                CardContract.CardEntry.NAME,
                CardContract.CardEntry.DESCRIPTION,
                CardContract.CardEntry.DATE_CREATED
        };
        String selection = CardContract.CardEntry.OWNER + " = ? AND " + CardContract.CardEntry.NAME + " = ?";
        String[] selectionArgs = { Long.toString(userId) , name};
        String sortOrder = CardContract.CardEntry.DATE_CREATED + " DESC";

        Cursor cursor = db.query(
                CardContract.CardEntry.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );

        if(cursor.getCount() > 0){
            return false;
        }
        return true;
    }


    public static List<Card> getRandomCards(Context context, long userId, int amount, double seed){
        // Gets the data repository in write mode
        List<Card> cards = new LinkedList<Card>();
        DBHelper dbHelper = DBHelper.getInstance(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String[] projection = {
                CardContract.CardEntry._ID,
                CardContract.CardEntry.NAME,
                CardContract.CardEntry.DESCRIPTION,
                CardContract.CardEntry.DATE_CREATED
        };
        String selection = CardContract.CardEntry.OWNER + " = ?";
        String[] selectionArgs = { Long.toString(userId) };
        //String sortOrder = CardContract.CardEntry.DATE_CREATED + " DESC";
        String sortOrder = "(substr("+ CardContract.CardEntry._ID +" * " + seed + ", length("+ CardContract.CardEntry._ID + ") + 2))";

        Cursor cursor = db.query(
                CardContract.CardEntry.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder,
                String.valueOf(amount)
        );

        while(cursor.moveToNext()) {
            long itemId = cursor.getLong(cursor.getColumnIndexOrThrow(CardContract.CardEntry._ID));
            String name = cursor.getString(cursor.getColumnIndex(CardContract.CardEntry.NAME));
            String desc = cursor.getString(cursor.getColumnIndex(CardContract.CardEntry.DESCRIPTION));
            long date = cursor.getLong(cursor.getColumnIndex(CardContract.CardEntry.DATE_CREATED));
            Card card = new Card(name, desc, date, itemId);
            cards.add(card);
        }
        return cards;
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

    public static long getUserId(String name, Context context){
        // Gets the data repository in write mode
        DBHelper dbHelper = DBHelper.getInstance(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String[] projection = {
                UserContract.UserEntry._ID,
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
            return -1;
        }
        if(cursor.getCount() > 1){
            System.out.println("Duplicate usernames in database!");
            return -1;
        }
        long itemId = cursor.getLong(cursor.getColumnIndexOrThrow(UserContract.UserEntry._ID));
        return itemId;
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
    public static double generateSeed(){
        double seed = RANDOM.nextDouble();
        return seed;
    }
}

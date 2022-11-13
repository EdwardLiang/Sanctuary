package com.edward.sanctuary.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.edward.sanctuary.Card;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
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
        values.put(UserContract.UserEntry.SECURITY_ENABLED, 0);
        values.put(UserContract.UserEntry.DARK_MODE, 0);


        // Insert the new row, returning the primary key value of the new row
        long newRowId = db.insert(UserContract.UserEntry.TABLE_NAME, null, values);
    }

    public static void changeUsername(long userId, String newName, Context context){
        // New value for one column
        DBHelper dbHelper = DBHelper.getInstance(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(UserContract.UserEntry.USERNAME, newName);

// Which row to update, based on the title
        String selection = UserContract.UserEntry._ID + " = ?";
        String[] selectionArgs = { String.valueOf(userId) };

        int count = db.update(
                UserContract.UserEntry.TABLE_NAME,
                values,
                selection,
                selectionArgs);
    }

    public static void changeCardName(Card c, String newName, Context context){
        // New value for one column
        DBHelper dbHelper = DBHelper.getInstance(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(CardContract.CardEntry.NAME, newName);

// Which row to update, based on the title
        String selection = CardContract.CardEntry._ID + " = ?";
        String[] selectionArgs = { String.valueOf(c.getCard_id()) };

        int count = db.update(
                CardContract.CardEntry.TABLE_NAME,
                values,
                selection,
                selectionArgs);
    }
    public static void changeCardDescription(Card c, String newDesc, Context context){
        // New value for one column
        DBHelper dbHelper = DBHelper.getInstance(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(CardContract.CardEntry.DESCRIPTION, newDesc);

// Which row to update, based on the title
        String selection = CardContract.CardEntry._ID + " = ?";
        String[] selectionArgs = { String.valueOf(c.getCard_id()) };

        int count = db.update(
                CardContract.CardEntry.TABLE_NAME,
                values,
                selection,
                selectionArgs);
    }


    public static void changePassword(long userId, String password, Context context){
        // New value for one column
        DBHelper dbHelper = DBHelper.getInstance(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        byte[] salt = PasswordUtils.getNextSalt();
        byte[] hash = PasswordUtils.hash(password.toCharArray(), salt);

        ContentValues values = new ContentValues();
        values.put(UserContract.UserEntry.PASSWORD_HASH, hash);
        values.put(UserContract.UserEntry.SALT, salt);

        // Which row to update, based on the title
        String selection = UserContract.UserEntry._ID + " = ?";
        String[] selectionArgs = { String.valueOf(userId) };

        int count = db.update(
                UserContract.UserEntry.TABLE_NAME,
                values,
                selection,
                selectionArgs);
    }
    public static void setSecurity(long userId, boolean securityEnabled, Context context){
        // New value for one column
        DBHelper dbHelper = DBHelper.getInstance(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        int value = securityEnabled ? 1 : 0;
        values.put(UserContract.UserEntry.SECURITY_ENABLED, value);

        // Which row to update, based on the title
        String selection = UserContract.UserEntry._ID + " = ?";
        String[] selectionArgs = { String.valueOf(userId) };

        int count = db.update(
                UserContract.UserEntry.TABLE_NAME,
                values,
                selection,
                selectionArgs);
    }
    public static boolean getSecurityEnabled(long userId, Context context){
        // New value for one column
        DBHelper dbHelper = DBHelper.getInstance(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String[] projection = {
                UserContract.UserEntry._ID,
                UserContract.UserEntry.SECURITY_ENABLED
        };
        String selection = UserContract.UserEntry._ID + " = ?";
        String[] selectionArgs = { String.valueOf(userId) };
        String sortOrder = UserContract.UserEntry._ID + " DESC";

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
        long itemId = cursor.getLong(cursor.getColumnIndexOrThrow(UserContract.UserEntry._ID));
        int secEnabled = cursor.getInt(cursor.getColumnIndex(UserContract.UserEntry.SECURITY_ENABLED));
        cursor.close();
        if(secEnabled != 0){
            return true;
        }
        return false;
    }
    public static void setDarkMode(long userId, boolean securityEnabled, Context context){
        // New value for one column
        DBHelper dbHelper = DBHelper.getInstance(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        int value = securityEnabled ? 1 : 0;
        values.put(UserContract.UserEntry.DARK_MODE, value);

        // Which row to update, based on the title
        String selection = UserContract.UserEntry._ID + " = ?";
        String[] selectionArgs = { String.valueOf(userId) };

        int count = db.update(
                UserContract.UserEntry.TABLE_NAME,
                values,
                selection,
                selectionArgs);
    }
    public static boolean getDarkModeEnabled(long userId, Context context){
        // New value for one column
        DBHelper dbHelper = DBHelper.getInstance(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String[] projection = {
                UserContract.UserEntry._ID,
                UserContract.UserEntry.DARK_MODE
        };
        String selection = UserContract.UserEntry._ID + " = ?";
        String[] selectionArgs = { String.valueOf(userId) };
        String sortOrder = UserContract.UserEntry._ID + " DESC";

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
        long itemId = cursor.getLong(cursor.getColumnIndexOrThrow(UserContract.UserEntry._ID));
        int darkEnabled = cursor.getInt(cursor.getColumnIndex(UserContract.UserEntry.DARK_MODE));
        cursor.close();
        if(darkEnabled != 0){
            return true;
        }
        return false;
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

    public static void addCardToDeck(long deck, long card, long userId, Context context){
        // Gets the data repository in write mode
        DBHelper dbHelper = DBHelper.getInstance(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(CardCardContract.CardCardEntry.CARD1, deck);
        values.put(CardCardContract.CardCardEntry.CARD2, card);
        values.put(CardCardContract.CardCardEntry.OWNER, userId);
        values.put(CardCardContract.CardCardEntry.DATE_CREATED, persistDate(new Date()));

        // Insert the new row, returning the primary key value of the new row
        long newRowId = db.insertWithOnConflict(CardCardContract.CardCardEntry.TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_IGNORE);
    }

    public static void setIsDeck(Card c, long userId, boolean bool, Context context){
        long card = c.getCard_id();
        DBHelper dbHelper = DBHelper.getInstance(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        int val = bool ? 1 : 0;
        System.out.println(val);
        values.put(CardContract.CardEntry.IS_DECK, val);

        // Which row to update, based on the title
        String selection = CardContract.CardEntry.OWNER + " = ? AND " +
        CardContract.CardEntry._ID + " = ?";
        System.out.println("User ID: " + userId);
        System.out.println("Card: " + card);
        String[] selectionArgs = { String.valueOf(userId), String.valueOf(card) };

        int count = db.update(
                CardContract.CardEntry.TABLE_NAME,
                values,
                selection,
                selectionArgs);
        System.out.println("Count changed" + count);
    }
    public static void setInDrawer(Card c, long userId, boolean bool, Context context){
        long card = c.getCard_id();
        DBHelper dbHelper = DBHelper.getInstance(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        int val = bool ? 1 : 0;
        values.put(CardContract.CardEntry.IN_DRAWER, val);

        // Which row to update, based on the title
        String selection = CardContract.CardEntry.OWNER + " = ? AND " +
                CardContract.CardEntry._ID + " = ?";
        String[] selectionArgs = { String.valueOf(userId), String.valueOf(card) };

        int count = db.update(
                CardContract.CardEntry.TABLE_NAME,
                values,
                selection,
                selectionArgs);
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

    public static Card getCard(long id, Context context){
        //For when the card is changed
        DBHelper dbHelper = DBHelper.getInstance(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String[] projection = {
                CardContract.CardEntry._ID,
                CardContract.CardEntry.NAME,
                CardContract.CardEntry.DESCRIPTION,
                CardContract.CardEntry.DATE_CREATED
        };
        String selection = CardContract.CardEntry._ID + " = ?";
        String[] selectionArgs = { Long.toString(id) };
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
            return card;
        }
        return null;
    }

    public static boolean cardExists(long id, Context context){
        //For when the card is changed
        DBHelper dbHelper = DBHelper.getInstance(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String[] projection = {
                CardContract.CardEntry._ID,
                CardContract.CardEntry.NAME,
                CardContract.CardEntry.DESCRIPTION,
                CardContract.CardEntry.DATE_CREATED
        };
        String selection = CardContract.CardEntry._ID + " = ?";
        String[] selectionArgs = { Long.toString(id) };
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
            return true;
        }
        return false;
    }

    public static HashMap<String, Card> getCardsInDeck(Context context, long userId, Card card){
        // Gets the data repository in write mode
        HashMap<String, Card> cards = new HashMap<String, Card>();
        DBHelper dbHelper = DBHelper.getInstance(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String[] selectionArgs = { String.valueOf(userId), String.valueOf(card.getCard_id()) };

        Cursor cursor = db.rawQuery(SQLCommands.SQL_CARDS_IN_DECK, selectionArgs);

        while(cursor.moveToNext()) {
            long itemId = cursor.getLong(cursor.getColumnIndexOrThrow(CardContract.CardEntry._ID));
            String name = cursor.getString(cursor.getColumnIndex(CardContract.CardEntry.NAME));
            String desc = cursor.getString(cursor.getColumnIndex(CardContract.CardEntry.DESCRIPTION));
            long date = cursor.getLong(cursor.getColumnIndex(CardContract.CardEntry.DATE_CREATED));
            Card c = new Card(name, desc, date, itemId);
            System.out.println(c.getCard_name() + " added to hashset");
            cards.put(c.getCard_name(), c);
        }
        return cards;
    }
    public static List<Card> getCardsInDeckList(Context context, long userId, Card card){
        // Gets the data repository in write mode
        List<Card> cards = new ArrayList<Card>();
        DBHelper dbHelper = DBHelper.getInstance(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String[] selectionArgs = { String.valueOf(userId), String.valueOf(card.getCard_id()) };

        Cursor cursor = db.rawQuery(SQLCommands.SQL_CARDS_IN_DECK, selectionArgs);

        while(cursor.moveToNext()) {
            long itemId = cursor.getLong(cursor.getColumnIndexOrThrow(CardContract.CardEntry._ID));
            String name = cursor.getString(cursor.getColumnIndex(CardContract.CardEntry.NAME));
            String desc = cursor.getString(cursor.getColumnIndex(CardContract.CardEntry.DESCRIPTION));
            long date = cursor.getLong(cursor.getColumnIndex(CardContract.CardEntry.DATE_CREATED));
            Card c = new Card(name, desc, date, itemId);
            cards.add(c);
        }
        return cards;
    }

    public static List<Card> getCardsInDeckByRandom(Context context, long userId, Card card, int amount, double seed){
        // Gets the data repository in write mode
        List<Card> cards = new ArrayList<Card>();
        DBHelper dbHelper = DBHelper.getInstance(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String[] selectionArgs = { String.valueOf(userId), String.valueOf(card.getCard_id()), String.valueOf(amount) };

        String q = SQLCommands.SQL_CARDS_IN_DECK_RANDOM + seed + SQLCommands.SQL_CARD_IN_DECK_RANDOM_2;

        Cursor cursor = db.rawQuery(q, selectionArgs);

        while(cursor.moveToNext()) {
            long itemId = cursor.getLong(cursor.getColumnIndexOrThrow(CardContract.CardEntry._ID));
            String name = cursor.getString(cursor.getColumnIndex(CardContract.CardEntry.NAME));
            String desc = cursor.getString(cursor.getColumnIndex(CardContract.CardEntry.DESCRIPTION));
            long date = cursor.getLong(cursor.getColumnIndex(CardContract.CardEntry.DATE_CREATED));
            Card c = new Card(name, desc, date, itemId);
            cards.add(c);
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

    public static List<Card> getRandomDecks(Context context, long userId, int amount, double seed){
        // Gets the data repository in write mode
        List<Card> cards = new LinkedList<Card>();
        DBHelper dbHelper = DBHelper.getInstance(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String[] projection = {
                CardContract.CardEntry._ID,
                CardContract.CardEntry.NAME,
                CardContract.CardEntry.DESCRIPTION,
                CardContract.CardEntry.DATE_CREATED,
                CardContract.CardEntry.IS_DECK
        };
        String selection = CardContract.CardEntry.OWNER + " = ? AND " + CardContract.CardEntry.IS_DECK + " = 1";
        //String selection = CardContract.CardEntry.OWNER + " = ?";

        String[] selectionArgs = { Long.toString(userId) };
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
            System.out.println(card.getCard_name());
            cards.add(card);
        }
        return cards;
    }

    public static List<Card> getDrawerDecks(Context context, long userId){
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
        String selection = CardContract.CardEntry.OWNER + " = ? AND " + CardContract.CardEntry.IS_DECK + " = 1 AND "
                + CardContract.CardEntry.IN_DRAWER + " = 1";
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
    public static HashMap<String, Card> getDrawerDecksMap(Context context, long userId){
        // Gets the data repository in write mode
        HashMap<String, Card> cards = new HashMap<String, Card>();
        DBHelper dbHelper = DBHelper.getInstance(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String[] projection = {
                CardContract.CardEntry._ID,
                CardContract.CardEntry.NAME,
                CardContract.CardEntry.DESCRIPTION,
                CardContract.CardEntry.DATE_CREATED
        };
        String selection = CardContract.CardEntry.OWNER + " = ? AND " + CardContract.CardEntry.IS_DECK + " = 1 AND "
                + CardContract.CardEntry.IN_DRAWER + " = 1";
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
            cards.put(card.getCard_name(), card);
        }
        return cards;
    }




    public static List<Card> getCardsSearch(Context context, String term, long userId, int amount){
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
        String[] selectionArgs = { Long.toString(userId), "%" + term + "%", "%" + term + "%", term, term + "%", String.valueOf(amount) };

        Cursor cursor = db.rawQuery(SQLCommands.SQL_QUERY_STRING, selectionArgs);

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

    public static List<Card> getCardsInDeckSearch(Context context, Card car, String term, long userId, int amount){
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
        String[] selectionArgs = { Long.toString(userId), Long.toString(car.getCard_id()), "%" + term + "%", "%" + term + "%", term, term + "%", String.valueOf(amount) };

        Cursor cursor = db.rawQuery(SQLCommands.SQL_CARDS_IN_DECK_QUERY, selectionArgs);

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

    public static boolean parseDump(Context context, String dump){
        int i = 0;
        int k = 0;
        boolean flag = false;
        boolean flag2 = false;
        String[] tables = new String[3];
        //System.out.println(dump);

        for(int j = 0; j < 3; j++){
            while (true) {
                //System.out.println(i);
                if (dump.charAt(i) == '{' && flag == false) {
                    flag = true;
                    i++;
                    continue;
                }
                else if (dump.charAt(i) == '{' && flag == true) {
                    flag2 = true;
                    i++;
                    continue;
                }
                else if (dump.charAt(i) == '}' && flag == true && flag2 == true) {
                    flag2 = false;
                    i++;
                    continue;
                }
                else if (dump.charAt(i) == '}' && flag == true && flag2 == false) {
                    //System.out.println("this2");
                    tables[j] = dump.substring(k, i + 1);
                    k = i + 1;
                    i++;
                    break;
                }
                else{
                    i++;
                }
            }
        }
        /*System.out.println(tables[0]);
        System.out.println(tables[1]);
        System.out.println(tables[2]);*/
        parseUserContractDump(context, tables[0]);
        parseCardContractDump(context, tables[1]);
        parseCardCardContractDump(context, tables[2]);

        return true;
    }
    public static boolean dropTables(Context context){
        //FOR USE IN IMPORTING
        DBHelper dbHelper = DBHelper.getInstance(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        String[] selectionArgs = {};
        db.execSQL(SQLCommands.SQL_CARDCARD_DROP);
        db.execSQL(SQLCommands.SQL_CARD_DROP);
        db.execSQL(SQLCommands.SQL_USER_DROP);
        //cursor = db.rawQuery(SQLCommands.SQL_CARD_DROP, selectionArgs);
        //cursor = db.rawQuery(SQLCommands.SQL_CARDCARD_DROP, selectionArgs);

        return true;
    }
    public static boolean parseUserContractDump(Context context, String dump1){
        DBHelper dbHelper = DBHelper.getInstance(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        String[] selectionArgs = {};
        dump1 = dump1.substring(1, dump1.length() - 1);
        for(int i = 0; i < dump1.length(); i++){
            int j = i;
            int openPlace = 0;
            if(dump1.charAt(j) == '{'){
                openPlace = j;
                while(dump1.charAt(j) != '}' || (j > 0 && dump1.charAt(j) == '}' && dump1.charAt(j - 1) == '\\')){
                    j++;
                }
            }
            else if(dump1.charAt(j) == ','){
                //System.out.println(dump2.charAt(j));
                continue;
            }

            /*if(dump1.charAt(j) == '{'){
                openPlace = j;
                while(dump1.charAt(j) != '}'){
                    j++;
                }
            }
            else if(dump1.charAt(j) == ','){
                //System.out.println(dump2.charAt(j));
                continue;
            }*/
            String o = dump1.substring(openPlace + 1, j);
            //System.out.println(o);

            //https://stackoverflow.com/questions/1757065/java-splitting-a-comma-separated-string-but-ignoring-commas-in-quotes
            String[] lines = o.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);
            //String[] lines = o.split(",");
           /* System.out.println(Arrays.toString(lines));
            for(int k = 0; k < lines.length; k++){
                System.out.println(lines[k]);
            }*/
            lines[0] = lines[0].substring("Q_IDQ: ".length(), lines[0].length());
            lines[1] = lines[1].substring("QUSERNAMEQ: Q".length(), lines[1].length() - 1);
            lines[2] = lines[2].substring("QPASSWORD_HASHQ: Q".length(), lines[2].length() - 1);
            lines[3] = lines[3].substring("QSALTQ: Q".length(), lines[3].length() - 1);
            lines[4] = lines[4].substring("QSECURITY_ENABLEDQ: ".length(), lines[4].length());
            lines[5] = lines[5].substring("QDARK_MODEQ: ".length(), lines[5].length());
            lines[6] = lines[6].substring("QDATE_CREATEDQ: ".length(), lines[6].length());

           for(int k = 0; k < lines.length; k++){
                System.out.println(lines[k]);
            }


            //System.out.println(Arrays.toString(lines));

            //https://stackoverflow.com/questions/4299111/convert-long-to-byte-array-and-add-it-to-another-array
            //byte[] hash1 = ByteBuffer.allocate(Long.BYTES).putLong(Long.parseLong(lines[2])).array();
            //byte[] salt1 = ByteBuffer.allocate(Long.BYTES).putLong(Long.parseLong(lines[3])).array();
            //System.out.println(hash1);
            //System.out.println(salt1);
            //1111110011101100011001011010
            //0101101110111101100011001110

          //  int val = Integer.parseInt(lines[2], 16);
           // BigInteger big = BigInteger.valueOf(val);
           // System.out.println("Hexadecimal String : " + lines[2]);
            //System.out.println("Big String : " + big);
            //byte[] pass1 = big.toByteArray();
            /*byte[] pass11 = new byte[lines[2].length()];
            boolean negative = false;
            boolean negative2 = false;

            for(int p = 0; p < lines[2].length(); p++){
                //System.out.println(Integer.parseInt(lines[2].charAt(p) + "", 16));
                if(lines[2].charAt(p) == '-'){
                    negative = true;
                }
                Integer inter = Integer.parseInt(lines[2].charAt(p) + "", 16);
                pass11[p] = inter.byteValue();
                //System.out.println(inter.byteValue());
            }
            byte[] salt11 = new byte[lines[3].length()];
            for(int p = 0; p < lines[3].length(); p++){
                //System.out.println(Integer.parseInt(lines[2].charAt(p) + "", 16));
                if(lines[2].charAt(p) == '-'){
                    negative2 = true;
                }
                Integer inter = Integer.parseInt(lines[3].charAt(p) + "", 16);
                salt11[p] = inter.byteValue();
                //System.out.println(inter.byteValue());
            }*/

            /*for(int p = 0; p < pass11.length; p++) {
                System.out.println(pass11[p]);
            }*/
            //System.out.println("Hexadecimal String After: " + pass1);
            BigInteger pass11 = new BigInteger(lines[2], 16);
            BigInteger salt11 = new BigInteger(lines[3], 16);
            //System.out.println("Hexadecimal String After2: " + pass11.toString(16));
            //System.out.println("Salt String After2: " + salt11.toString(16));

            byte[] pass111 = pass11.toByteArray();
            byte[] salt111 = salt11.toByteArray();

            /*Integer val = Integer.parseInt(lines[2], 16);
            BigInteger big = BigInteger.valueOf(val);
            byte[] pass1 = big.toByteArray();*/

            /*Integer val2 = Integer.parseInt(lines[3], 16);
            BigInteger big2 = BigInteger.valueOf(val2);
            byte[] salt1 = big2.toByteArray();
            System.out.println("Salt String After2: " + new BigInteger(salt1).toString(16));
*/

            ContentValues values = new ContentValues();
            values.put(UserContract.UserEntry._ID, Integer.parseInt(lines[0]));
            values.put(UserContract.UserEntry.USERNAME, lines[1]);
            //values.put(UserContract.UserEntry.PASSWORD_HASH, lines[2].getBytes());
            //values.put(UserContract.UserEntry.SALT, lines[3].getBytes());
            values.put(UserContract.UserEntry.PASSWORD_HASH, pass111);
            values.put(UserContract.UserEntry.SALT, salt111);
            values.put(UserContract.UserEntry.SECURITY_ENABLED, Integer.parseInt(lines[4]));
            values.put(UserContract.UserEntry.DARK_MODE, Integer.parseInt(lines[5]));
            values.put(UserContract.UserEntry.DATE_CREATED, Long.parseLong(lines[6]));

         /*   System.out.println("dump: ");
            System.out.println(lines[2]);
            System.out.println(lines[3]);*/

             /*"CREATE TABLE " + UserContract.UserEntry.TABLE_NAME + " (" +
                UserContract.UserEntry._ID + " INTEGER PRIMARY KEY," +
                UserContract.UserEntry.USERNAME + " STRING UNIQUE," +
                UserContract.UserEntry.PASSWORD_HASH + " BLOB," +
                UserContract.UserEntry.SALT + " BLOB," +
                UserContract.UserEntry.SECURITY_ENABLED + " INTEGER," +
                UserContract.UserEntry.DARK_MODE + " INTEGER," +
                UserContract.UserEntry.DATE_CREATED + " LONG)";*/

            // Insert the new row, returning the primary key value of the new row
            long newRowId = db.insert(UserContract.UserEntry.TABLE_NAME, null, values);

            i = j;
        }
        //Cursor cursor = db.rawQuery(SQLCommands.SQL_DUMP1, selectionArgs);

        return true;
    }
    public static boolean parseCardContractDump(Context context, String dump2){
        DBHelper dbHelper = DBHelper.getInstance(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        String[] selectionArgs = {};
        dump2 = dump2.substring(1, dump2.length() - 1);
        /*System.out.println("<this9>");
        System.out.println(dump2);*/
        for(int i = 0; i < dump2.length(); i++){
            System.out.println(i);
            int j = i;
            int openPlace = 0;

            if(dump2.charAt(j) == '{'){
                openPlace = j;
                while(dump2.charAt(j) != '}' || (j > 0 && dump2.charAt(j) == '}' && dump2.charAt(j - 1) == '\\')){
                    j++;
                }
            }
            else if(dump2.charAt(j) == ','){
                //System.out.println(dump2.charAt(j));
                continue;
            }
/*
            if(dump2.charAt(j) == '{'){
                openPlace = j;
                while(dump2.charAt(j) != '}'){
                    j++;
                }
            }*/
            String o = dump2.substring(openPlace + 1, j);
            System.out.println(o);
            //String[] lines = o.split(",");
           /* System.out.println("test");
            System.out.println(o);
            System.out.println("test");*/
            String[] lines = o.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);

            //System.out.println("<this>");
            //System.out.println(Arrays.toString(lines));
            /*for(int k = 0; k < lines.length; k++){
                System.out.println(lines[k]);
            }*/
            lines[0] = lines[0].substring("Q_IDQ: ".length(), lines[0].length());
            lines[1] = lines[1].substring("QNAMEQ: Q".length(), lines[1].length() - 1);
            lines[2] = lines[2].substring("QDESCRIPTIONQ: Q".length(), lines[2].length() - 1);
            lines[3] = lines[3].substring("QIN_DRAWERQ: ".length(), lines[3].length());
            lines[4] = lines[4].substring("QIS_DECKQ: ".length(), lines[4].length());
            lines[5] = lines[5].substring("QOWNERQ: ".length(), lines[5].length());
            lines[6] = lines[6].substring("QDATE_CREATEDQ: ".length(), lines[6].length());
            //System.out.println(Arrays.toString(lines));
           /* for(int k = 0; k < lines.length; k++){
                System.out.println(lines[k]);
            }*/


            ContentValues values = new ContentValues();
            values.put(CardContract.CardEntry._ID, Integer.parseInt(lines[0]));
            values.put(CardContract.CardEntry.NAME, lines[1]);
            values.put(CardContract.CardEntry.DESCRIPTION, lines[2]);
            values.put(CardContract.CardEntry.IN_DRAWER, Integer.parseInt(lines[3]));
            values.put(CardContract.CardEntry.IS_DECK, Integer.parseInt(lines[4]));
            values.put(CardContract.CardEntry.OWNER, Integer.parseInt(lines[5]));
            values.put(CardContract.CardEntry.DATE_CREATED, Long.parseLong(lines[6]));

                    /*            "CREATE TABLE " + CardContract.CardEntry.TABLE_NAME + " (" +
                    CardContract.CardEntry._ID + " INTEGER PRIMARY KEY," +
                    CardContract.CardEntry.NAME + " STRING UNIQUE," +
                    CardContract.CardEntry.DESCRIPTION + " STRING," +
                    CardContract.CardEntry.IN_DRAWER + " INTEGER," +
                    CardContract.CardEntry.IS_DECK + " INTEGER," +
                    CardContract.CardEntry.OWNER + " INTEGER," +
                    CardContract.CardEntry.DATE_CREATED + " LONG, " +
                    "FOREIGN KEY (" + CardContract.CardEntry.OWNER + ") REFERENCES " +
                    UserContract.UserEntry.TABLE_NAME + "(" + UserContract.UserEntry._ID + "))";
*/


            // Insert the new row, returning the primary key value of the new row
            long newRowId = db.insert(CardContract.CardEntry.TABLE_NAME, null, values);

            i = j;
        }
        return true;
    }
    public static boolean parseCardCardContractDump(Context context, String dump3){
        DBHelper dbHelper = DBHelper.getInstance(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        String[] selectionArgs = {};
        dump3 = dump3.substring(1, dump3.length() - 1);
        /*System.out.println("<this9>");
        System.out.println(dump2);*/
        for(int i = 0; i < dump3.length(); i++){
            int j = i;
            int openPlace = 0;
            if(dump3.charAt(j) == '{'){
                openPlace = j;
                while(dump3.charAt(j) != '}' || (j > 0 && dump3.charAt(j) == '}' && dump3.charAt(j - 1) == '\\')){
                    j++;
                }
            }
            else if(dump3.charAt(j) == ','){
                //System.out.println(dump2.charAt(j));
                continue;
            }
            String o = dump3.substring(openPlace + 1, j);
            //String[] lines = o.split(",");
           /* System.out.println("test");
            System.out.println(o);
            System.out.println("test");*/
            String[] lines = o.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);

            //System.out.println("<this>");
            //System.out.println(Arrays.toString(lines));
           /* for(int k = 0; k < lines.length; k++){
                System.out.println(lines[k]);
            }*/
            lines[0] = lines[0].substring("QCARD1Q: ".length(), lines[0].length());
            lines[1] = lines[1].substring("QCARD2Q: ".length(), lines[1].length());
            lines[2] = lines[2].substring("QOWNERQ: ".length(), lines[2].length());
            lines[3] = lines[3].substring("QDATE_CREATEDQ: ".length(), lines[3].length());
            //System.out.println(Arrays.toString(lines));
         /*   for(int k = 0; k < lines.length; k++){
                //System.out.println("k: " + k);
                System.out.println(lines[k]);
            }*/


            ContentValues values = new ContentValues();
            values.put(CardCardContract.CardCardEntry.CARD1, Integer.parseInt(lines[0]));
            values.put(CardCardContract.CardCardEntry.CARD2, Integer.parseInt(lines[1]));
            values.put(CardCardContract.CardCardEntry.OWNER, Integer.parseInt(lines[2]));
            values.put(CardCardContract.CardCardEntry.DATE_CREATED, Long.parseLong(lines[3]));

                   /* "CREATE TABLE " + CardCardContract.CardCardEntry.TABLE_NAME + " (" +
                CardCardContract.CardCardEntry.CARD1 + " INTEGER," +
                CardCardContract.CardCardEntry.CARD2 + " INTEGER," +
                CardCardContract.CardCardEntry.OWNER + " INTEGER," +
                CardCardContract.CardCardEntry.DATE_CREATED + " LONG, " +
                "FOREIGN KEY (" + CardCardContract.CardCardEntry.OWNER + ") REFERENCES " +
                UserContract.UserEntry.TABLE_NAME + "(" + UserContract.UserEntry._ID + ")," +
                "FOREIGN KEY (" + CardCardContract.CardCardEntry.CARD1 + ") REFERENCES " +
                CardContract.CardEntry.TABLE_NAME + "(" + CardContract.CardEntry._ID + ") ON DELETE CASCADE," +
                "FOREIGN KEY (" + CardCardContract.CardCardEntry.CARD2 + ") REFERENCES " +
                CardContract.CardEntry.TABLE_NAME + "(" + CardContract.CardEntry._ID + ") ON DELETE CASCADE," +
                "PRIMARY KEY(" + CardCardContract.CardCardEntry.CARD1 + ", " +
                CardCardContract.CardCardEntry.CARD2 + "))";*/

            // Insert the new row, returning the primary key value of the new row
            long newRowId = db.insert(CardCardContract.CardCardEntry.TABLE_NAME, null, values);

            i = j;
        }
        return true;
    }

    public static String sanitize(String str){
        String sanitized = str.replace("{", "\\{");
        String sanitized2 = sanitized.replace("}", "\\}");

        return sanitized2;
    }
    public static String desanitize(String str){
      /*  for(int i = 0; i < str.length(); i++){
            if(str.charAt(i) == '{' || str.charAt(i) == '}'){
            }
        }*/
        String sanitized = str.replace("\\{", "{");
        String sanitized2 = sanitized.replace("\\}", "}");

        return sanitized2;
    }


    public static String getDump(Context context){

      /*  System.out.println(sanitize("a{bcd}e"));
        System.out.println(desanitize(sanitize("a{bcd}e")));
*/

        DBHelper dbHelper = DBHelper.getInstance(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Charset charset = Charset.forName("ISO-8859-1");

        String[] selectionArgs = {};
        Cursor cursor = db.rawQuery(SQLCommands.SQL_DUMP1, selectionArgs);
        String JSON1 = "{";
        while(cursor.moveToNext()) {
            int id1 = cursor.getInt(cursor.getColumnIndex(UserContract.UserEntry._ID));
            String us1 = sanitize(cursor.getString(cursor.getColumnIndex(UserContract.UserEntry.USERNAME)));
            byte[] pass1 = cursor.getBlob(cursor.getColumnIndex(UserContract.UserEntry.PASSWORD_HASH));
            byte[] salt1 = cursor.getBlob(cursor.getColumnIndex(UserContract.UserEntry.SALT));
            int sec1 = cursor.getInt(cursor.getColumnIndex(UserContract.UserEntry.SECURITY_ENABLED));
            int dark1 = cursor.getInt(cursor.getColumnIndex(UserContract.UserEntry.DARK_MODE));
            long date1 = cursor.getLong(cursor.getColumnIndex(UserContract.UserEntry.DATE_CREATED));

            /*ByteBuffer pass1i = ByteBuffer.allocate(Long.SIZE / Byte.SIZE);
            pass1i.put(pass1);
            pass1i.flip();
            long pass11 = pass1i.getLong();

            //long salt11 = new BigInteger(salt1).longValue();
            ByteBuffer salt1i = ByteBuffer.allocate(Long.SIZE / Byte.SIZE);
            salt1i.put(salt1);
            salt1i.flip();
            long salt11 = salt1i.getLong();*/

            /*System.out.println("asdf");
            System.out.println(pass1);
            System.out.println(salt1);
            System.out.println("asdf2");*/

            String pass11 = new BigInteger(pass1).toString(16);
            String salt11 = new BigInteger(salt1).toString(16);
            /*System.out.println("asdf: ");
            System.out.println(pass11);
            System.out.println(salt11);*/

            JSON1 += "\n{\"_ID\"" + ": " + id1 + ",\n" +
                "\"USERNAME\"" + ": " + "\"" + us1 + "\",\n" +
                "\"PASSWORD_HASH\"" + ": " + "\"" + pass11 + "\",\n" +
                "\"SALT\"" + ": " + "\"" + salt11 + "\",\n" +
                "\"SECURITY_ENABLED\"" + ": " + sec1 + ",\n" +
                "\"DARK_MODE\"" + ": " + dark1 + ",\n" +
                "\"DATE_CREATED\"" + ": " + date1 + "\n},";
        }
        JSON1 = JSON1.substring(0,JSON1.length()-1);
        JSON1 += "}";

        cursor.moveToFirst();
        cursor = db.rawQuery(SQLCommands.SQL_DUMP2, selectionArgs);
        String JSON2 = "{";
        while(cursor.moveToNext()) {
            int id2 = cursor.getInt(cursor.getColumnIndex(CardContract.CardEntry._ID));
            String name2 = sanitize(cursor.getString(cursor.getColumnIndex(CardContract.CardEntry.NAME)));
            String description2 = sanitize(cursor.getString(cursor.getColumnIndex(CardContract.CardEntry.DESCRIPTION)));
            int in_drawer2 = cursor.getInt(cursor.getColumnIndex(CardContract.CardEntry.IN_DRAWER));
            int is_deck2 = cursor.getInt(cursor.getColumnIndex(CardContract.CardEntry.IS_DECK));
            int owner2 = cursor.getInt(cursor.getColumnIndex(CardContract.CardEntry.OWNER));
            long date_created2 = cursor.getLong(cursor.getColumnIndex(CardContract.CardEntry.DATE_CREATED));
            JSON2 += "\n{\"_ID\"" + ": " + id2 + ",\n" +
                    "\"NAME\"" + ": " + "\"" + name2 + "\",\n" +
                    "\"DESCRIPTION\"" + ": \"" + description2 + "\",\n" +
                    "\"IN_DRAWER\"" + ": " + in_drawer2 + ",\n" +
                    "\"IS_DECK\"" + ": " + is_deck2 + ",\n" +
                    "\"OWNER\"" + ": " + owner2 + ",\n" +
                    "\"DATE_CREATED\"" + ": " + date_created2 + "\n},";
        }
        JSON2 = JSON2.substring(0,JSON2.length()-1);
        JSON2 += "}";

        cursor = db.rawQuery(SQLCommands.SQL_DUMP3, selectionArgs);
        cursor.moveToFirst();
        String JSON3 = "{";
        while(cursor.moveToNext()) {
            int card1 = cursor.getInt(cursor.getColumnIndex(CardCardContract.CardCardEntry.CARD1));
            int card2 = cursor.getInt(cursor.getColumnIndex(CardCardContract.CardCardEntry.CARD2));
            int owner3 = cursor.getInt(cursor.getColumnIndex(CardCardContract.CardCardEntry.OWNER));
            long date_created3 = cursor.getLong(cursor.getColumnIndex(CardCardContract.CardCardEntry.DATE_CREATED));

            JSON3 += "\n{\"CARD1\"" + ": " + card1 + ",\n" +
                    "\"CARD2\"" + ": " + card2 + ",\n" +
                    "\"OWNER\"" + ": " + owner3 + ",\n" +
                    "\"DATE_CREATED\"" + ": " + date_created3 + "\n},";
        }
        JSON3 = JSON3.substring(0,JSON3.length()-1);
        JSON3 += "}";


       /* "CREATE TABLE " + CardCardContract.CardCardEntry.TABLE_NAME + " (" +
                CardCardContract.CardCardEntry.CARD1 + " INTEGER," +
                CardCardContract.CardCardEntry.CARD2 + " INTEGER," +
                CardCardContract.CardCardEntry.OWNER + " INTEGER," +
                CardCardContract.CardCardEntry.DATE_CREATED + " LONG, " +
                "FOREIGN KEY (" + CardCardContract.CardCardEntry.OWNER + ") REFERENCES " +
                UserContract.UserEntry.TABLE_NAME + "(" + UserContract.UserEntry._ID + ")," +
                "FOREIGN KEY (" + CardCardContract.CardCardEntry.CARD1 + ") REFERENCES " +
                CardContract.CardEntry.TABLE_NAME + "(" + CardContract.CardEntry._ID + ") ON DELETE CASCADE," +
                "FOREIGN KEY (" + CardCardContract.CardCardEntry.CARD2 + ") REFERENCES " +
                CardContract.CardEntry.TABLE_NAME + "(" + CardContract.CardEntry._ID + ") ON DELETE CASCADE," +
                "PRIMARY KEY(" + CardCardContract.CardCardEntry.CARD1 + ", " +
                CardCardContract.CardCardEntry.CARD2 + "))";*/


        /*            "CREATE TABLE " + CardContract.CardEntry.TABLE_NAME + " (" +
                    CardContract.CardEntry._ID + " INTEGER PRIMARY KEY," +
                    CardContract.CardEntry.NAME + " STRING UNIQUE," +
                    CardContract.CardEntry.DESCRIPTION + " STRING," +
                    CardContract.CardEntry.IN_DRAWER + " INTEGER," +
                    CardContract.CardEntry.IS_DECK + " INTEGER," +
                    CardContract.CardEntry.OWNER + " INTEGER," +
                    CardContract.CardEntry.DATE_CREATED + " LONG, " +
                    "FOREIGN KEY (" + CardContract.CardEntry.OWNER + ") REFERENCES " +
                    UserContract.UserEntry.TABLE_NAME + "(" + UserContract.UserEntry._ID + "))";
*/



        /*"CREATE TABLE " + UserContract.UserEntry.TABLE_NAME + " (" +
                UserContract.UserEntry._ID + " INTEGER PRIMARY KEY," +
                UserContract.UserEntry.USERNAME + " STRING UNIQUE," +
                UserContract.UserEntry.PASSWORD_HASH + " BLOB," +
                UserContract.UserEntry.SALT + " BLOB," +
                UserContract.UserEntry.SECURITY_ENABLED + " INTEGER," +
                UserContract.UserEntry.DARK_MODE + " INTEGER," +
                UserContract.UserEntry.DATE_CREATED + " LONG)";*/

        //String dump1 = cursor.
        /*cursor = db.rawQuery(SQLCommands.SQL_DUMP2, selectionArgs);
        String dump2 = cursor.toString();
        cursor = db.rawQuery(SQLCommands.SQL_DUMP3, selectionArgs);
        String dump3 = cursor.toString();*/
        cursor.close();
        return JSON1 + "\n\n" + JSON2 + "\n\n" + JSON3;
    }

    public static List<Card> getDecksSearch(Context context, String term, long userId, int amount){
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
        String[] selectionArgs = { Long.toString(userId), "%" + term + "%", "%" + term + "%", term, term + "%", String.valueOf(amount) };

        Cursor cursor = db.rawQuery(SQLCommands.SQL_QUERY_STRING_DECKS, selectionArgs);

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

    public static int deleteCard(Context context, Card card){
        DBHelper dbHelper = DBHelper.getInstance(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        // Define 'where' part of query.
        String selection = CardContract.CardEntry._ID + " = ?";
        // Specify arguments in placeholder order.
        String[] selectionArgs = { String.valueOf(card.getCard_id()) };
        // Issue SQL statement.
        return db.delete(CardContract.CardEntry.TABLE_NAME, selection, selectionArgs);
    }
    public static void deleteCardFromDeck(Context context, Card deck, Card card){
        DBHelper dbHelper = DBHelper.getInstance(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        // Define 'where' part of query.
        String selection = CardCardContract.CardCardEntry.CARD1 + " = ? AND " +
                CardCardContract.CardCardEntry.CARD2 + " = ?";
        // Specify arguments in placeholder order.
        String[] selectionArgs = { String.valueOf(deck.getCard_id()), String.valueOf(card.getCard_id()) };
        // Issue SQL statement.
        db.delete(CardCardContract.CardCardEntry.TABLE_NAME, selection, selectionArgs);

    }

    public static void deleteDeck(Context context, long userid, Card deck){
        DBHelper dbHelper = DBHelper.getInstance(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        // Define 'where' part of query.
        String selection = CardCardContract.CardCardEntry.CARD1 + " = ?";
        // Specify arguments in placeholder order.
        String[] selectionArgs = { String.valueOf(deck.getCard_id())};
        // Issue SQL statement.
        db.delete(CardCardContract.CardCardEntry.TABLE_NAME, selection, selectionArgs);

        setIsDeck(deck, userid, false, context);
        setInDrawer(deck, userid, false, context);

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
        cursor.close();
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
            cursor.close();
            return -1;
        }
        if(cursor.getCount() > 1){
            cursor.close();
            System.out.println("Duplicate usernames in database!");
            return -1;
        }
        long itemId = cursor.getLong(cursor.getColumnIndexOrThrow(UserContract.UserEntry._ID));
        cursor.close();
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
    public static boolean userExists(Context context){
        // Gets the data repository in write mode
        DBHelper dbHelper = DBHelper.getInstance(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String[] projection = {
                UserContract.UserEntry._ID,
        };

        Cursor cursor = db.query(
                UserContract.UserEntry.TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                null
        );

        cursor.moveToNext();
        if(cursor.getCount() == 0){
            cursor.close();
            return false;
        }
        cursor.close();
        return true;
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

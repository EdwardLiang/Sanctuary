package com.edward.sanctuary.database;

/**
 * Created by edward on 3/24/17.
 */

public final class SQLCommands {
    public static final String SQL_CREATE_ENTRIES_USER =
            "CREATE TABLE " + UserContract.UserEntry.TABLE_NAME + " (" +
                    UserContract.UserEntry._ID + " INTEGER PRIMARY KEY," +
                    UserContract.UserEntry.USERNAME + " STRING UNIQUE," +
                    UserContract.UserEntry.PASSWORD_HASH + " BLOB," +
                    UserContract.UserEntry.SALT + " BLOB," +
                    UserContract.UserEntry.DATE_CREATED + " LONG)";

    public static final String SQL_CREATE_ENTRIES_CARD =
            "CREATE TABLE " + CardContract.CardEntry.TABLE_NAME + " (" +
                    CardContract.CardEntry._ID + " INTEGER PRIMARY KEY," +
                    CardContract.CardEntry.NAME + " STRING UNIQUE," +
                    CardContract.CardEntry.DESCRIPTION + " STRING," +
                    CardContract.CardEntry.IN_DRAWER + " INTEGER," +
                    CardContract.CardEntry.IS_DECK + " INTEGER," +
                    CardContract.CardEntry.OWNER + " INTEGER," +
                    CardContract.CardEntry.DATE_CREATED + " LONG, " +
                    "FOREIGN KEY (" + CardContract.CardEntry.OWNER + ") REFERENCES " +
                    UserContract.UserEntry.TABLE_NAME + "(" + UserContract.UserEntry._ID + "))";

    public static final String SQL_DELETE_ENTRIES_USER =
            "DROP TABLE IF EXISTS " + UserContract.UserEntry.TABLE_NAME;

    public static final String SQL_DELETE_ENTRIES_CARD =
            "DROP TABLE IF EXISTS " + CardContract.CardEntry.TABLE_NAME;


}

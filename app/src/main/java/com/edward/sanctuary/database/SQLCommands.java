package com.edward.sanctuary.database;

/**
 * Created by edward on 3/24/17.
 */

public final class SQLCommands {
    public static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + UserContract.UserEntry.TABLE_NAME + " (" +
                    UserContract.UserEntry._ID + " INTEGER PRIMARY KEY," +
                    UserContract.UserEntry.USERNAME + " STRING UNIQUE," +
                    UserContract.UserEntry.PASSWORD_HASH + " BLOB," +
                    UserContract.UserEntry.SALT + " BLOB," +
                    UserContract.UserEntry.DATE_CREATED + " LONG)";

    public static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + UserContract.UserEntry.TABLE_NAME;

}

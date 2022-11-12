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
                    UserContract.UserEntry.SECURITY_ENABLED + " INTEGER," +
                    UserContract.UserEntry.DARK_MODE + " INTEGER," +
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
    public static final String SQL_CREATE_ENTRIES_CARD_CARD =
            "CREATE TABLE " + CardCardContract.CardCardEntry.TABLE_NAME + " (" +
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
                    CardCardContract.CardCardEntry.CARD2 + "))";


    public static final String SQL_DELETE_ENTRIES_USER =
            "DROP TABLE IF EXISTS " + UserContract.UserEntry.TABLE_NAME;

    public static final String SQL_DELETE_ENTRIES_CARD =
            "DROP TABLE IF EXISTS " + CardContract.CardEntry.TABLE_NAME;

    public static final String SQL_DELETE_ENTRIES_CARD_CARD =
            "DROP TABLE IF EXISTS " + CardCardContract.CardCardEntry.TABLE_NAME;

    public static final String SQL_DUMP1 = "SELECT * FROM " + UserContract.UserEntry.TABLE_NAME;
    public static final String SQL_DUMP2 = "SELECT * FROM " + CardContract.CardEntry.TABLE_NAME;
    public static final String SQL_DUMP3 = "SELECT * FROM " + CardCardContract.CardCardEntry.TABLE_NAME;

    public static final String SQL_QUERY_STRING =
            "SELECT _id, name, description, date_created FROM card WHERE owner = ? AND (name LIKE ? OR description LIKE ?) " +
                    "ORDER BY (CASE WHEN name = ? THEN 1 WHEN name LIKE ? THEN 2 ELSE 3 END),name,description LIMIT ?";

    public static final String SQL_QUERY_STRING_DECKS =
            "SELECT _id, name, description, date_created FROM card WHERE owner = ? AND is_deck = 1 AND (name LIKE ? OR description LIKE ?)" +
                    " ORDER BY (CASE WHEN name = ? THEN 1 WHEN name LIKE ? THEN 2 ELSE 3 END),name,description LIMIT ?";

    public static final String SQL_CARDS_IN_DECK =
            "SELECT c._id, c.name, c.description, c.date_created FROM " +
                    CardCardContract.CardCardEntry.TABLE_NAME + " deck INNER JOIN " + CardContract.CardEntry.TABLE_NAME +
                    " c ON deck.card2 = c._id WHERE deck.owner = ? AND deck.card1 = ?";

    public static final String SQL_CARDS_IN_DECK_QUERY =
            "SELECT c._id, c.name, c.description, c.date_created FROM " +
                    CardCardContract.CardCardEntry.TABLE_NAME + " deck INNER JOIN " + CardContract.CardEntry.TABLE_NAME +
                    " c ON deck.card2 = c._id WHERE deck.owner = ? AND deck.card1 = ? AND (c.name LIKE ? OR c.description LIKE ?)" +
                    " ORDER BY (CASE WHEN c.name = ? THEN 1 WHEN c.name LIKE ? THEN 2 ELSE 3 END),c.name,c.description LIMIT ?";


    public static final String SQL_CARDS_IN_DECK_RANDOM =
            "SELECT c._id, c.name, c.description, c.date_created FROM " +
                    CardCardContract.CardCardEntry.TABLE_NAME + " deck INNER JOIN " + CardContract.CardEntry.TABLE_NAME +
                    " c ON deck.card2 = c._id WHERE deck.owner = ? AND deck.card1 = ? ORDER BY " + "(substr(c._id * ";
    public static final String SQL_CARD_IN_DECK_RANDOM_2 = ", length(c._id) + 2)) LIMIT ?";


}

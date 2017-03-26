package com.edward.sanctuary.database;

import android.provider.BaseColumns;

/**
 * Created by edward on 3/24/17.
 */

public final class UserContract {

    private UserContract() {}

    public static class UserEntry implements BaseColumns {
        public static final String TABLE_NAME = "person";
        public static final String USERNAME = "username";
        public static final String PASSWORD_HASH = "password_hash";
        public static final String SALT = "salt";
        public static final String DATE_CREATED = "date_created";
    }

}

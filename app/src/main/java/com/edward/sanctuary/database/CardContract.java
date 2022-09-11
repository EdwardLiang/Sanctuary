package com.edward.sanctuary.database;

import android.provider.BaseColumns;

/**
 * Created by edward on 3/24/17.
 */

public final class CardContract {

    private CardContract() {}

    public static class CardEntry implements BaseColumns {
        public static final String TABLE_NAME = "card";
        public static final String NAME = "name";
        public static final String DESCRIPTION = "description";
        public static final String OWNER = "owner";
        public static final String IS_DECK = "is_deck";
        public static final String IN_DRAWER = "in_drawer";
        public static final String DATE_CREATED = "date_created";
    }
}

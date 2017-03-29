package com.edward.sanctuary.database;

import android.provider.BaseColumns;

/**
 * Created by edward on 3/28/17.
 */

public final class CardCardContract {
    private CardCardContract() {}

    public static class CardCardEntry implements BaseColumns {
        public static final String TABLE_NAME = "card_card";
        public static final String CARD1 = "card1";
        public static final String CARD2 = "card2";
        public static final String OWNER = "owner";
        public static final String DATE_CREATED = "date_created";
    }

}

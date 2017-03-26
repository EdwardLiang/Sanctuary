package com.edward.sanctuary.database;

/**
 * Created by edward on 3/24/17.
 */

public class Database {
    private static final Database ourInstance = new Database();

    public static Database getInstance() {
        return ourInstance;
    }

    private Database() {

    }
}

package com.edward.sanctuary;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by edward on 3/25/17.
 */

public class Session {
    private SharedPreferences prefs;
    private static Session instance;

    public static Session getInstance(Context context){
        if(instance == null){
            instance = new Session(context.getApplicationContext());
        }
        return instance;
    }
    public static void destroyInstance(){
        if(instance != null){
            instance = null;
        }
    }

    private Session(Context context){
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public void setUsername(String str){
        prefs.edit().putString("username", str).apply();
    }

    public void setUserId(Long id){
        prefs.edit().putLong("id", id).apply();
    }

    public long getUserId(){
        return prefs.getLong("id", -1);
    }

    public String getUsername(){
        return prefs.getString("username", "");
    }
}

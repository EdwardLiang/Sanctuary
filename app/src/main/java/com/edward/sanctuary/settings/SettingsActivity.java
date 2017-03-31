package com.edward.sanctuary.settings;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;

import com.edward.sanctuary.R;

public class SettingsActivity extends AppCompatPreferenceActivity {

    @Override
    public void onCreate(Bundle savedInstanceState){
        if(Session.getInstance(this).darkModeSet()){
            this.setTheme(R.style.Night);
            this.getSupportActionBar().hide();
            this.getActionBar().setDisplayHomeAsUpEnabled(true);
        }
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction().replace(android.R.id.content, new SettingsFragment()).commit();
        checkValues();
        //setupActionBar();
        setTitle("Settings");
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());

    }

    private void checkValues()
    {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        String strUserName = sharedPrefs.getString("username", "NA");
        boolean bAppUpdates = sharedPrefs.getBoolean("applicationUpdates",false);

        String msg = "Cur Values: ";
        msg += "\n userName = " + strUserName;
        msg += "\n bAppUpdates = " + bAppUpdates;
    }

    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }
    @Override
    public void onBackPressed(){
        NavUtils.navigateUpFromSameTask(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }



}

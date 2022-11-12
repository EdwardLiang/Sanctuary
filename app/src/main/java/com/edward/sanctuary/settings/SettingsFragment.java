package com.edward.sanctuary.settings;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.widget.Toast;
import android.content.Intent;

import com.edward.sanctuary.R;
import com.edward.sanctuary.database.Database;

/**
 * Created by edward on 3/21/17.
 */

public class SettingsFragment extends PreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        getPreferenceScreen().findPreference("enablesecurity").setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
                getPreferenceScreen().findPreference("username").setEnabled((Boolean)newValue);
                getPreferenceScreen().findPreference("password").setEnabled((Boolean)newValue);
                Database.setSecurity(Session.getInstance(getActivity()).getUserId(), (Boolean)newValue, getActivity());
                System.out.println(Database.getSecurityEnabled(Session.getInstance(getActivity()).getUserId(), getActivity()));
                System.out.println("Checkbox hit");
                return true;
            }
        });
        getPreferenceScreen().findPreference("darkmode").setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                Database.setDarkMode(Session.getInstance(getActivity()).getUserId(), (Boolean)newValue, getActivity());
                Session.getInstance(getActivity()).setDarkMode((Boolean)newValue);
                return true;
            }
        });

        getPreferenceScreen().findPreference("username").setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
                if(newValue.toString().equals("")){
                    Toast.makeText(getActivity(), "Username must not be empty!", Toast.LENGTH_SHORT).show();
                    return false;
                }else {
                    Database.changeUsername(Session.getInstance(getActivity()).getUserId(), newValue.toString(), getActivity());
                    Toast.makeText(getActivity(), "Username changed!", Toast.LENGTH_SHORT).show();
                    Session.getInstance(getActivity()).setUsername(newValue.toString());
                }
                return true;
            }
        });
        getPreferenceScreen().findPreference("password").setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
                if(newValue.toString().length() <= 4){
                    Toast.makeText(getActivity(), "Password must be >4 chars!", Toast.LENGTH_SHORT).show();
                    return false;
                }

                        Database.changePassword(Session.getInstance(getActivity()).getUserId(), newValue.toString(), getActivity());
                Toast.makeText(getActivity(), "Password changed!", Toast.LENGTH_SHORT).show();
                return true;
            }
        });
        boolean secEnabled = sharedPreferences.getBoolean("enablesecurity", false);
        getPreferenceScreen().findPreference("username").setEnabled(secEnabled);
        getPreferenceScreen().findPreference("password").setEnabled(secEnabled);

        getPreferenceScreen().findPreference("export").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                String dbDump = Database.getDump(getActivity());
                System.out.println("DATABASE DUMP");
                System.out.println(dbDump);
                sendIntent.putExtra(Intent.EXTRA_TEXT, dbDump);
                sendIntent.setType("text/plain");
                Intent shareIntent = Intent.createChooser(sendIntent, null);
                startActivity(shareIntent);
                return true;
            }
        });


    }
}

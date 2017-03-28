package com.edward.sanctuary;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.widget.Toast;

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

    }
}

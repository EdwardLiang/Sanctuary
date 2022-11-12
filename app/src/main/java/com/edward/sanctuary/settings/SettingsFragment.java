package com.edward.sanctuary.settings;

import android.app.Activity;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.widget.Toast;
import android.content.Intent;

import com.edward.sanctuary.R;
import com.edward.sanctuary.database.Database;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Objects;

/**
 * Created by edward on 3/21/17.
 */

public class SettingsFragment extends PreferenceFragment {
    public static final int PICK_TXT_FILE = 1;

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

        getPreferenceScreen().findPreference("import").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("text/plain");
                startActivityForResult(intent, PICK_TXT_FILE);
                return true;
            }
        });
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode,
                                    Intent resultData){
        if(requestCode == SettingsFragment.PICK_TXT_FILE &&
                resultCode == Activity.RESULT_OK){
            Uri uri = null;
            if(resultData != null){
                uri = resultData.getData();
                try {
                    String readin = readTextFromUri(uri);
                    Database.parseDump(getActivity(), readin);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private String readTextFromUri(Uri uri) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        BufferedReader reader = null;
        InputStream inputStream;
        inputStream = getActivity().getContentResolver().openInputStream(uri);
        reader = new BufferedReader(new InputStreamReader(Objects.requireNonNull(inputStream)));
        String line = null;
        while ((line = reader.readLine()) != null) {
            stringBuilder.append(line);
        }
        return stringBuilder.toString();
    }

}

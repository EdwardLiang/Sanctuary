package com.edward.sanctuary;

import android.os.Bundle;
import android.preference.PreferenceFragment;

/**
 * Created by edward on 3/21/17.
 */

public class SettingsFragment extends PreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
    }
}

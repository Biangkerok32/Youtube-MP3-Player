package com.example.daron.youtubemp3player;


import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.support.annotation.Nullable;


public class SettingsFragment extends PreferenceFragment {
    final static String THEME_KEY = "pref_syncTheme";
    final static String FONT_KEY = "pref_syncFont";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
    }

    //TODO: Refresh everything immediately when you click on a preference
}

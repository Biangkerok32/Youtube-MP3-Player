package com.example.daron.youtubemp3player;


import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.annotation.Nullable;


public class SettingsFragment extends PreferenceFragment implements Preference.OnPreferenceChangeListener {
    final static String THEME_KEY = "pref_syncTheme";
    final static String FONT_KEY = "pref_syncFont";


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
        Preference themePreference = getPreferenceManager().findPreference(THEME_KEY);
        Preference fontPreference = getPreferenceManager().findPreference(FONT_KEY);
        if (themePreference != null && fontPreference != null) {
            themePreference.setOnPreferenceChangeListener(this);
            fontPreference.setOnPreferenceChangeListener(this);
        }
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        Intent intent = new Intent(getActivity(), SettingsActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
        return true;
    }
}

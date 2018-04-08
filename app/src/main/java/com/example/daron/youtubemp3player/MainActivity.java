package com.example.daron.youtubemp3player;


import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentTransaction;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;


public class MainActivity extends AppCompatActivity implements ActionListFragment.ActionListListener {

    private final String LOG_TAG = MainActivity.class.getSimpleName();
    private Toolbar myToolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String theme = sharedPreferences.getString(SettingsFragment.THEME_KEY, "Light");
        String font = sharedPreferences.getString(SettingsFragment.FONT_KEY, "Casual");
        Log.e(LOG_TAG, theme);
        setApplicationTheme(theme);
        setApplicationFont(font);
        setContentView(R.layout.activity_main);

        // If it is normal potrait layout, add the list fragment otherwise
        if (findViewById(R.id.portrait_layout) != null) {

            ActionListFragment firstFragment = new ActionListFragment();

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, firstFragment).commit();
        } else {
            ActionListFragment firstFragment = new ActionListFragment();

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.action_list_layout, firstFragment).commit();
        }

        myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

    }

    public void setApplicationTheme(String theme) {
        if (theme.equals("Light")) {
            this.setTheme(R.style.Light);
        } else if (theme.equals("Dark")) {
            this.setTheme(R.style.Dark);
        }
    }

    public void setApplicationFont(String font) {
        if (font.equals("Casual")) {
            this.setTheme(R.style.FontCasual);
        } else if (font.equals("Cursive")) {
            this.setTheme(R.style.FontCursive);
        }
    }


    @Override
    public void itemClicked(long id) {
        // User wants to stream a song otherwise, user wants to see a list of their favorites
        if (id == 0) {
            StreamMusicFragment streamMusicFragment = new StreamMusicFragment();
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            // update screen for portrait layout otherwise update for landscape and large
            if (findViewById(R.id.portrait_layout) != null) {
                ft.replace(R.id.fragment_container, streamMusicFragment);
                ft.addToBackStack(null);
                ft.commit();
            } else {
                ft.replace(R.id.stream_fragment_container, streamMusicFragment);
                ft.addToBackStack(null);
                ft.commit();
            }
        } else {
            FavoritesFragment favoritesFragment = new FavoritesFragment();
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            // update screen for portrait layout otherwise update for landscape and large
            if (findViewById(R.id.portrait_layout) != null) {
                ft.replace(R.id.fragment_container, favoritesFragment);
                ft.addToBackStack(null);
                ft.commit();
            } else {
                ft.replace(R.id.stream_fragment_container, favoritesFragment);
                ft.addToBackStack(null);
                ft.commit();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.actionbar_menu, menu);
        menu.findItem(R.id.action_share).setVisible(false);
        menu.findItem(R.id.action_favorite).setVisible(false);
        menu.findItem(R.id.action_display_favorites).setVisible(false);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                // User chose the "Settings" item, show the app settings UI...
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                return true;

            case R.id.action_display_favorites:
                FavoritesFragment favoritesFragment = new FavoritesFragment();
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                if (findViewById(R.id.portrait_layout) != null) {
                    ft.replace(R.id.fragment_container, favoritesFragment);
                    ft.addToBackStack(null);
                    ft.commit();
                } else {
                    ft.replace(R.id.stream_fragment_container, favoritesFragment);
                    ft.addToBackStack(null);
                    ft.commit();
                }
                return true;
            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }
    }
}

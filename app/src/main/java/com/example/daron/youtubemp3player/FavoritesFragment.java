package com.example.daron.youtubemp3player;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

public class FavoritesFragment extends ListFragment {

    private SQLiteDatabase db;
    private Cursor favoritesCursor;
    SQLiteOpenHelper databaseHelper;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        databaseHelper = new DatabaseHelper(context);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (container != null) {
            container.removeAllViews();
        }
        try {
            db = databaseHelper.getReadableDatabase();
            favoritesCursor = db.query("LINKS",
                    new String[]{"_id", "NAME"},
                    "FAVORITE = 1",
                    null, null, null, null);
            CustomCursorAdapter customCursorAdapter = new CustomCursorAdapter(inflater.getContext(), favoritesCursor, container, getFragmentManager(), getActivity());
            setListAdapter(customCursorAdapter);
        } catch (SQLiteException e) {
            Toast toast = Toast.makeText(inflater.getContext(), "Database unavailable", Toast.LENGTH_SHORT);
            toast.show();
        }
        return super.onCreateView(inflater, container, savedInstanceState);
    }
}

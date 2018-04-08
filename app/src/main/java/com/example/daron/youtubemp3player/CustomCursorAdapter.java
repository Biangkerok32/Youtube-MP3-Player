package com.example.daron.youtubemp3player;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class CustomCursorAdapter extends CursorAdapter implements View.OnClickListener {

    private ViewGroup viewGroup;
    private FragmentManager fragmentManager;
    private Context context;
    private String link;
    private Activity activity;
    public CustomCursorAdapter(Context context, Cursor cursor, ViewGroup viewGroup, FragmentManager fragmentManager, Activity activity) {
        super(context, cursor, 0);
        this.viewGroup = viewGroup;
        this.fragmentManager = fragmentManager;
        this.context = context;
        this.activity = activity;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.listview_favorites, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView linkView = (TextView) view.findViewById(R.id.link);
        link = cursor.getString(cursor.getColumnIndexOrThrow("NAME"));
        linkView.setText(link);
        ImageView deleteView = (ImageView) view.findViewById(R.id.delete);
        deleteView.setVisibility(ImageButton.VISIBLE);
        linkView.setOnClickListener(this);
        deleteView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.link:
                StreamMusicFragment streamMusicFragment = new StreamMusicFragment();
                Bundle bundle = new Bundle();
                bundle.putString("link", link);
                streamMusicFragment.setArguments(bundle);
                FragmentTransaction ft = fragmentManager.beginTransaction();
                ft.replace(viewGroup.getId(), streamMusicFragment);
                ft.addToBackStack(null);
                ft.commit();
                break;
            case R.id.delete:
                new DeleteLinkTask(activity).execute(link);
                fragmentManager.beginTransaction().replace(viewGroup.getId(), new FavoritesFragment()).commit();
        }
    }
    private class DeleteLinkTask extends AsyncTask<String, Void, Boolean> {
        Activity activity;

        public DeleteLinkTask(Activity activity) {
            this.activity = activity;
        }

        protected Boolean doInBackground(String... links) {
            SQLiteOpenHelper databaseHelper = new DatabaseHelper(activity);
            try {
                SQLiteDatabase db = databaseHelper.getWritableDatabase();
                db.delete("LINKS", "name=?", new String[]{links[0]});
                db.close();
                return true;
            } catch (SQLiteException e) {
                return false;
            }
        }

        protected void onPostExecute(Boolean success) {
            if (!success) {
                Toast toast = Toast.makeText(context,
                        "Database unavailable", Toast.LENGTH_SHORT);
                toast.show();
            }
        }
    }
}

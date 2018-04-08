package com.example.daron.youtubemp3player;


import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


public class StreamMusicFragment extends Fragment implements View.OnClickListener, TextWatcher {

    private final String LOG_TAG = StreamMusicFragment.class.getSimpleName();
    final static String apiURL = "https://coolguruji-youtube-to-mp3-download-v1.p.mashape.com/?id=";
    final static String apiKEY = "&mashape-key=8HhJIKEDxJmshduAQML89GLR1unqp1aHPugjsnTZ4pVf9CXtg9";
    private EditText urlView;
    private ShareActionProvider shareActionProvider;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        MenuItem shareItem = menu.findItem(R.id.action_share);
        MenuItem favoriteItem = menu.findItem(R.id.action_favorite);
        MenuItem showfavoritesItem = menu.findItem(R.id.action_display_favorites);
        // only show share if this fragment is visible
        if (this.isVisible()) {
            favoriteItem.setVisible(true);
            shareItem.setVisible(true);
            showfavoritesItem.setVisible(true);
        }

        shareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(shareItem);
        setShareIntent("7a66clRobKI");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_favorite:
                Log.e(LOG_TAG, "triggered");
                String link = urlView.getText().toString();
                if (!link.equals("")) {
                    Log.e(LOG_TAG, link);
                    new UpdateLinkTask(getActivity()).execute(urlView.getText().toString());
                }
                return true;

        }

        return super.onOptionsItemSelected(item);
    }

    private void setShareIntent(String text) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, "youtu.be/" + text);
        shareActionProvider.setShareIntent(intent);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (container != null) {
            container.removeAllViews();
        }


        View view = inflater.inflate(R.layout.fragment_stream_music, container, false);
        urlView = (EditText) view.findViewById(R.id.linkInput);
        urlView.addTextChangedListener(this);

        if (getArguments() != null) {
            Bundle bundle = getArguments();
            String link = bundle.getString("link");
            urlView.setText(link);
        }

        Button playMusic = (Button) view.findViewById(R.id.playMusic);
        playMusic.setOnClickListener(this);
        return view;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
    }

    @Override
    public void afterTextChanged(Editable s) {
        if (shareActionProvider != null) {
            setShareIntent(s.toString());
        }
    }

    @Override
    public void onClick(View v) {
        String defaultURL = "7a66clRobKI";
        String urlText = urlView.getText().toString();
        Log.e(LOG_TAG, "Button pressed");
        Toast.makeText(getContext(), "Song will start shortly", Toast.LENGTH_SHORT).show();
        int id = v.getId();
        if (id == R.id.playMusic) {
            if (urlText.equals("")) {
                new FetchDownloadLink(getActivity()).execute(defaultURL);
            } else {
                new FetchDownloadLink(getActivity()).execute(urlText);
            }
        }
    }

    public void loadWebPage(String url) {
        Log.e(LOG_TAG, "Loading webpage");
        final WebView webview = new WebView(getContext());
        webview.getSettings().setJavaScriptEnabled(true);
        webview.addJavascriptInterface(this, "android");
        webview.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                webview.loadUrl("javascript:window.android.onData" +
                        "(document.getElementById('ytd').href);");
            }
        });
        webview.loadUrl(url);
    }

    @JavascriptInterface
    public void onData(String link) throws IOException {
        Log.e(LOG_TAG, "Loaded webpage");
        Log.e(LOG_TAG, link);

        PlayerFragment playerFragment = new PlayerFragment();
        playerFragment.loadMedia(link, getContext());
        FragmentTransaction ft = getChildFragmentManager().beginTransaction();
        ft.add(R.id.content_frame, playerFragment);
        ft.addToBackStack(null);
        ft.commit();
    }

    // Fetches the download link from the REST API
    private class FetchDownloadLink extends AsyncTask<String, Void, String> {
        Activity activity;

        public FetchDownloadLink(Activity activity) {
            this.activity = activity;
        }

        @Override
        protected String doInBackground(String... strings) {
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String downloadLink = "";
            try {
                URL url = new URL(apiURL + strings[0] + apiKEY);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream in = urlConnection.getInputStream();
                if (in == null)
                    return null;

                reader = new BufferedReader(new InputStreamReader(in));
                String downloadLinkJsonString = getJsonStringFromBuffer(reader);
                downloadLink = getDownloadLink(downloadLinkJsonString);

                Log.e(LOG_TAG, downloadLink);
            } catch (Exception e) {
                Log.e(LOG_TAG + "Error", e.getMessage());
            } finally {

                if (urlConnection != null)
                    urlConnection.disconnect();
                if (reader != null)
                    try {
                        reader.close();
                    } catch (IOException e) {
                        Log.e(LOG_TAG, "Error" + e.getMessage());
                    }
            }
            return downloadLink;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (result != null)
                Log.e(LOG_TAG, result);

            loadWebPage(result);
        }
    }

    // Gets the Json Object from the REST API as a string
    private String getJsonStringFromBuffer(BufferedReader br) throws Exception {
        StringBuffer buffer = new StringBuffer();
        String line;
        while ((line = br.readLine()) != null) {
            buffer.append(line + '\n');
        }
        if (buffer.length() == 0)
            return null;

        return buffer.toString();
    }

    // Converts string to json object and returns the download link
    private String getDownloadLink(String downloadLinkJsonString) throws JSONException {
        JSONObject jsonObject = new JSONObject(downloadLinkJsonString);
        JSONObject downloadLinkJSONObj = jsonObject.getJSONObject("data");
        return downloadLinkJSONObj.getString("html");
    }

    private class UpdateLinkTask extends AsyncTask<String, Void, Boolean> {
        private ContentValues linkValues;
        Activity activity;

        public UpdateLinkTask(Activity activity) {
            this.activity = activity;
        }

        protected void onPreExecute() {
            linkValues = new ContentValues();
            linkValues.put("FAVORITE", true);
        }

        protected Boolean doInBackground(String... links) {
            linkValues.put("NAME", links[0]);
            SQLiteOpenHelper databaseHelper = new DatabaseHelper(activity);
            Log.e(LOG_TAG, links[0]);
            try {
                SQLiteDatabase db = databaseHelper.getWritableDatabase();
                db.insert("LINKS", null, linkValues);
                db.close();
                return true;
            } catch (SQLiteException e) {
                return false;
            }
        }

        protected void onPostExecute(Boolean success) {
            if (!success) {
                Toast toast = Toast.makeText(getContext(),
                        "Database unavailable", Toast.LENGTH_SHORT);
                toast.show();
            }
        }
    }

}

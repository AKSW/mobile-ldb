package org.dbpedia.browser;

import android.app.ActivityOptions;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.Pair;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import org.dbpedia.browser.adapter.RecentSearchListAdapter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;

/**
 * This is the activity for the input
 */

public class BrowseActivity extends AppCompatActivity {

    private static final String TAG = "DBpediaBrowser";
    //Recent searches read from SharedPreferences
    private ArrayList<String> recentList;
    //The background card of recent searches
    private CardView recentCard;
    //This layout is shown if there are no recent searches
    private LinearLayout searchSomethingLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browse);
        recentCard = (CardView) findViewById(R.id.card_search_history);
        searchSomethingLayout = (LinearLayout) findViewById(R.id.search_something_layout);
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        View loadDataBtn = findViewById(R.id.loadDataBtn);
        final EditText inputText = (EditText) findViewById(R.id.inputEditText);
        //Called when enter is pressed on the keyboard
        inputText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_GO) {
                    search(true);
                    return true;
                }
                return false;
            }
        });
        //Called when the white arrow is pressed
        loadDataBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                search(true);
            }
        });

        //Get the recent searches from SharedPreferences
        recentList = new ArrayList<>(PreferenceManager.getDefaultSharedPreferences(this).getStringSet("SEARCH_HISTORY", new HashSet<String>()));
        final ListView recentSearchListView = (ListView) findViewById(R.id.recent_search_list);
        if (recentList.isEmpty()) {
            recentCard.setVisibility(View.GONE);
            searchSomethingLayout.setVisibility(View.VISIBLE);
        }
        //Set the adapter to the recent searches ListView
        recentSearchListView.setAdapter(new RecentSearchListAdapter(this, R.layout.item_recent_search, R.id.recent_search_text, recentList));
        recentSearchListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                inputText.setText(recentList.get(position));
                search(false);
            }
        });

        //Copy an entry from recent searches list to input EditText
        recentSearchListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                inputText.setText(recentList.get(position));
                return true;
            }
        });

        //Clear button
        TextView clearRecentText = (TextView) findViewById(R.id.clear_recent);
        //Clear recent searches and hide
        clearRecentText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recentList.clear();
                ((RecentSearchListAdapter) recentSearchListView.getAdapter()).notifyDataSetChanged();
                AlphaAnimation hideAnim = new AlphaAnimation(1, 0);
                hideAnim.setDuration(300);
                hideAnim.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        recentCard.setVisibility(View.GONE);
                        searchSomethingLayout.setVisibility(View.VISIBLE);
                        AlphaAnimation showAnim = new AlphaAnimation(0, 1);
                        showAnim.setDuration(300);
                        searchSomethingLayout.startAnimation(showAnim);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
                recentCard.startAnimation(hideAnim);
            }
        });
    }

    /**
     * Called when activity is paused
     */
    @Override
    protected void onPause() {
        super.onPause();
        //Save the recent searches to SharedPreferences
        PreferenceManager.getDefaultSharedPreferences(this).edit().putStringSet("SEARCH_HISTORY", new HashSet<String>(recentList)).apply();
    }


    private void search(boolean addToRecentList) {
        EditText inputText = (EditText) findViewById(R.id.inputEditText);
        if (addToRecentList) {
            if (recentList.size() == 5) {
                recentList.remove(0);
            }
            if (!recentList.contains(inputText.getText().toString())) {
                recentList.add(inputText.getText().toString());
            }
        }
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        ((RecentSearchListAdapter) ((ListView) findViewById(R.id.recent_search_list)).getAdapter()).notifyDataSetChanged();
        recentCard.setVisibility(View.VISIBLE);
        searchSomethingLayout.setVisibility(View.GONE);
        if (inputText.getText().toString().startsWith("http://")) {
            Intent intent = new Intent(BrowseActivity.this, DetailActivity.class);
            intent.setData(Uri.parse(inputText.getText().toString()));
            //If Android version is 5.0 or higher make a scene transition animation
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(BrowseActivity.this, new Pair<View, String>(toolbar, "appBar")).toBundle());
            else
                startActivity(intent);
        } else {
            findViewById(R.id.progress).setVisibility(View.VISIBLE);
            findViewById(R.id.loadDataBtn).setVisibility(View.INVISIBLE);
            new AgdistisAsyncTask().execute(inputText.getText().toString());
        }
    }

    class AgdistisAsyncTask extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... params) {
            try {
                String urlString = getResources().getString(R.string.agdistis);
                URL url = new URL(urlString);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);
                connection.setDoInput(true);
                connection.setUseCaches(false);
                connection.setRequestProperty("Accept", "application/json");
                connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
                connection.setRequestProperty("Content-Length", String.valueOf(("text=<entity>" + params[0] + "</entity>&type=agdistis").length()));

                DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
                wr.writeBytes("text=<entity>" + params[0] + "</entity>&type=agdistis");
                wr.flush();
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String returnValue = reader.readLine();
                wr.close();
                reader.close();
                connection.disconnect();
                return returnValue;
            } catch (java.io.IOException e) {
                Log.d(TAG, e.getClass().getName() + " " + e.getLocalizedMessage());
            }
            return "";
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            JSONObject jsonObject;
            findViewById(R.id.progress).setVisibility(View.INVISIBLE);
            findViewById(R.id.loadDataBtn).setVisibility(View.VISIBLE);
            try {
                jsonObject = new JSONArray(s).getJSONObject(0);
                String url = jsonObject.getString("disambiguatedURL");
                Log.d(TAG, url);
                Intent intent = new Intent(BrowseActivity.this, DetailActivity.class);
                intent.setData(Uri.parse(url));
                startActivity(intent);

            } catch (JSONException ignored) {
                Log.e(TAG, ignored.getClass().getName() + " " + ignored.getLocalizedMessage());
            }
        }
    }
}

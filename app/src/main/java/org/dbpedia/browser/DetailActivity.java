package org.dbpedia.browser;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;

import org.dbpedia.browser.adapter.DefaultRecyclerAdapter;
import org.dbpedia.browser.adapter.LocationRecyclerAdapter;
import org.dbpedia.browser.adapter.MessageRecyclerAdapter;
import org.dbpedia.browser.adapter.PersonRecyclerAdapter;
import org.dbpedia.browser.adapter.ReferToPageRecyclerAdapter;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.UnknownHostException;

/**
 * This is the activity for the output
 */

public class DetailActivity extends AppCompatActivity {

    // The tag used for the logcat. Show filtered logcat with
    // $ adb logcat -s DBpediaBrowser
    private static final String TAG = "DBpediaBrowser";

    //The height of the screen used to calculate the maximum height for the picture
    private int displayHeight;

    //The width of the screen to request the thumbnail in the correct size
    private int displayWidth;

    //true when thumbnail is shown in fullscreen. If true pressing device's back key will close thumbnail instead of exiting activity
    private boolean fullThumbnailOpened;

    //Triggered if there is an UnknownHostException while loading data.
    private boolean noNetwork;

    /**
     * Called when activity is created
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        noNetwork = false;
        fullThumbnailOpened = false;
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        displayHeight = size.y;
        displayWidth = size.x;
        setContentView(R.layout.activity_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        String inputText = getIntent().getDataString()
                .replace("http://www.dbpedia.org", "http://dbpedia.org")
                .replace("http://dbpedia.org/page", "http://dbpedia.org/data")
                .replace("http://dbpedia.org/resource", "http://dbpedia.org/data");
        //This is only shown when the user clicked the thumbnail and it expands to fullscreen.
        final ImageView thumbnailFull = (ImageView) findViewById(R.id.thumbnail_full);
        final ImageView thumbnail = (ImageView) findViewById(R.id.thumbnail);
        //When thumbnail is clicked, it will expand to the whole screen size.
        thumbnail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Enter immersive mode on Android >= Lollipop (hide status bar)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                    getWindow().getDecorView().setSystemUiVisibility(
                            View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                                    | View.SYSTEM_UI_FLAG_IMMERSIVE);
                fullThumbnailOpened = true;
                thumbnailFull.setVisibility(View.VISIBLE);
                ResizeAnimation animation = new ResizeAnimation(thumbnailFull, displayHeight);
                animation.setDuration(300);
                thumbnailFull.startAnimation(animation);
            }
        });
        thumbnailFull.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeThumbnail();
            }
        });
        new LoadDataTask().execute(inputText);
    }

    /**
     * close the fullscreen thumbnail if open
     */
    private void closeThumbnail() {
        final ImageView thumbnailFull = (ImageView) findViewById(R.id.thumbnail_full);
        final ImageView thumbnail = (ImageView) findViewById(R.id.thumbnail);
        //Exit immersive mode on Android >= Lollipop
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }
        fullThumbnailOpened = false;
        //Resize thumbnailFull to thumbnail size using an animation so it looks more natural
        ResizeAnimation animation = new ResizeAnimation(thumbnailFull, thumbnail.getHeight());
        animation.setDuration(300);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                thumbnailFull.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        thumbnailFull.startAnimation(animation);
    }

    /**
     * Called when back key is pressed
     */
    @Override
    public void onBackPressed() {
        if (fullThumbnailOpened)
            closeThumbnail();
        else
            super.onBackPressed();
    }

    /**
     * Background task - loads the rdf file from server
     */
    class LoadDataTask extends AsyncTask<String, Integer, RDFReader> {


        @Override
        protected RDFReader doInBackground(String... params) {
            try {
                URL url = new URL(params[0]);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setDoOutput(true);
                urlConnection.setDoInput(true);
                urlConnection.setRequestProperty("Accept", "application/rdf+xml");
                return new RDFReader(DetailActivity.this, urlConnection.getInputStream());
            } catch (UnknownHostException uhe) {
                //UnknownHostException is thrown if no network available.
                noNetwork = true;
                return new RDFReader();
            } catch (IOException ioe) {
                Log.e(TAG, ioe.getClass().getName() + " " + ioe.getLocalizedMessage());
                return new RDFReader();
            }
        }

        @Override
        protected void onPostExecute(RDFReader rdfReader) {
            super.onPostExecute(rdfReader);
            if (noNetwork) {
                RecyclerView recyclerView = (RecyclerView) findViewById(R.id.resultRecyclerView);
                recyclerView.setLayoutManager(new GridLayoutManager(DetailActivity.this, 1));
                recyclerView.setAdapter(new MessageRecyclerAdapter(getResources().getString(R.string.no_network_message), R.drawable.ic_warning, DetailActivity.this));
                ((ImageView) findViewById(R.id.thumbnail)).setImageDrawable(getResources().getDrawable(R.drawable.no_network));
                ((ImageView) findViewById(R.id.thumbnail_full)).setImageDrawable(getResources().getDrawable(R.drawable.no_network));
                findViewById(R.id.progressBar).setVisibility(View.GONE);
                setTitle(null);
                ((CollapsingToolbarLayout) findViewById(R.id.toolbar_layout)).setTitle(null);
                return;
            }
            String thumbnailUri = rdfReader.getResultsForProperty("foaf:depiction") != null ? rdfReader.getResultsForProperty("foaf:depiction").get(0) : null;
            //Start LoadThumbnailAsyncTask to load the thumbnail in background
            new LoadThumbnailAsyncTask().execute(thumbnailUri);
            RecyclerView recyclerView = (RecyclerView) findViewById(R.id.resultRecyclerView);
            recyclerView.setLayoutManager(new GridLayoutManager(DetailActivity.this, 1));
            String label = rdfReader.getResultsForProperty("rdfs:label") != null ? rdfReader.getResultsForProperty("rdfs:label").get(rdfReader.getResultsForProperty("rdfs:label").size() - 1) : null;
            String abstractText = rdfReader.getResultsForProperty("dbo:abstract") != null ? rdfReader.getResultsForProperty("dbo:abstract").get(rdfReader.getResultsForProperty("dbo:abstract").size() - 1) : null;
            RecyclerView.Adapter adapter;
            //Set the correct adapter to the recyclerView
            //In this case nothing is found so we.
            if (label == null)
                adapter = new MessageRecyclerAdapter(getResources().getString(R.string.not_found), R.drawable.ic_warning, DetailActivity.this);
                //In this case the page refers to more than one other pages
            else if (rdfReader.getResultsForProperty("dbo:wikiPageDisambiguates") != null && rdfReader.getResultsForProperty("dbo:wikiPageDisambiguates").size() > 1 && abstractText == null)
                adapter = new ReferToPageRecyclerAdapter(rdfReader.getResultsForProperty("dbo:wikiPageDisambiguates"), DetailActivity.this);
                //If there's no abstract text, show a message
            else if (abstractText == null)
                adapter = new MessageRecyclerAdapter(getResources().getString(R.string.no_content), R.drawable.ic_warning, DetailActivity.this);
                //In this case the object is an organisation
            else if (rdfReader.getTypes().contains("http://dbpedia.org/ontology/Organisation")) {
                String locationCity = rdfReader.getResultsForProperty("dbp:locationCity") != null ? rdfReader.getResultsForProperty("dbp:locationCity").get(0) : null;
                adapter = new DefaultRecyclerAdapter(DetailActivity.this, abstractText, locationCity);
            }
            //In this case the object is a country
            else if (rdfReader.getTypes().contains("http://dbpedia.org/ontology/Country")) {
                String population = rdfReader.getResultsForProperty("dbo:populationTotal") != null ? rdfReader.getResultsForProperty("dbo:populationTotal").get(0) : null;
                String area = rdfReader.getResultsForProperty("dbo:areaTotal") != null ? rdfReader.getResultsForProperty("dbo:areaTotal").get(0) : null;
                String locationCoordinates = rdfReader.getResultsForProperty("georss:point") != null ? rdfReader.getResultsForProperty("georss:point").get(0) : null;
                //Use a LocationRecyclerAdapter showing a map, the population count and the area size. Zoom level is 2.
                adapter = new LocationRecyclerAdapter(abstractText, locationCoordinates, DetailActivity.this, 2, population, area);
            }
            //In this case the object is a populated place but not a country
            else if (rdfReader.getTypes().contains("http://dbpedia.org/ontology/PopulatedPlace")) {
                String population = rdfReader.getResultsForProperty("dbo:populationTotal") != null ? rdfReader.getResultsForProperty("dbo:populationTotal").get(0) : null;
                String area = rdfReader.getResultsForProperty("dbo:areaTotal") != null ? rdfReader.getResultsForProperty("dbo:areaTotal").get(0) : null;
                String locationCoordinates = rdfReader.getResultsForProperty("georss:point") != null ? rdfReader.getResultsForProperty("georss:point").get(0) : null;
                //Use a LocationRecyclerAdapter showing a map, the population count and the area size. Zoom level is 7.
                adapter = new LocationRecyclerAdapter(abstractText, locationCoordinates, DetailActivity.this, 7, population, area);
            }
            //In this case the object is a person
            else if (rdfReader.getTypes().contains("http://dbpedia.org/ontology/Person")) {
                String birthDate = rdfReader.getResultsForProperty("dbo:birthDate") != null ? rdfReader.getResultsForProperty("dbo:birthDate").get(0) : null;
                String deathDate = rdfReader.getResultsForProperty("dbo:deathDate") != null ? rdfReader.getResultsForProperty("dbo:deathDate").get(0) : null;
                //Use a PersonRecyclerAdapter showing date of birth and death if known
                adapter = new PersonRecyclerAdapter(DetailActivity.this, abstractText, birthDate, deathDate);
            }
            //In this case the object is a location but the geo coordinates are usually not known.
            else if (rdfReader.getTypes().contains("http://dbpedia.org/ontology/Location")) {
                adapter = new DefaultRecyclerAdapter(DetailActivity.this, abstractText, label);
            }
            //DefaultRecyclerAdapter showing nothing but the abstract text
            else
                adapter = new DefaultRecyclerAdapter(DetailActivity.this, abstractText, null);
            recyclerView.setNestedScrollingEnabled(true);
            recyclerView.setHasFixedSize(true);
            //Set the adapter
            recyclerView.setAdapter(adapter);
            //Set the title
            setTitle(label);
            CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
            collapsingToolbarLayout.setTitle(label);

        }
    }

    /**
     * Background task to load the thumbnail bitmap from server.
     */
    class LoadThumbnailAsyncTask extends AsyncTask<String, Integer, Bitmap> {

        @Override
        protected Bitmap doInBackground(String... params) {
            try {
                //We use the url from foaf:depiction + ?width=displayWidth because this always refers to a bitmap
                //and not to an svg which Android cannot handle.
                URL url = new URL(params[0] + "?width=" + displayWidth);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoOutput(true);
                connection.setInstanceFollowRedirects(true);
                //We will be redirected at least once
                while (connection.getResponseCode() == 301) {
                    url = new URL(connection.getHeaderField("Location"));
                    connection = (HttpURLConnection) url.openConnection();
                }
                return BitmapFactory.decodeStream(connection.getInputStream());
            } catch (IOException e) {
                Log.d(TAG, e.getClass().getName() + " " + e.getLocalizedMessage());
            }
            return BitmapFactory.decodeResource(getResources(), R.drawable.nothing_found);
        }

        /**
         * This is called when the task is done and the bitmap is loaded
         *
         * @param bitmap
         */
        @Override
        protected void onPostExecute(final Bitmap bitmap) {
            super.onPostExecute(bitmap);
            final ImageView thumbnailFull = (ImageView) findViewById(R.id.thumbnail_full);
            thumbnailFull.setImageBitmap(bitmap);
            final ImageView thumbnail = (ImageView) findViewById(R.id.thumbnail);
            int minimumHeight = (int) getResources().getDimension(R.dimen.thumbnail_min_height);
            int targetHeight = bitmap.getHeight();
            //Thumbnail should not fill more than the half of the screen
            if (targetHeight > displayHeight / 2) {
                targetHeight = displayHeight / 2;
            } else if (targetHeight < minimumHeight) {
                targetHeight = minimumHeight;
            }

            //Animate the thumbnail image
            //Resize the ImageView to the target height
            ResizeAnimation resizeAnimation = new ResizeAnimation(thumbnail, targetHeight);
            resizeAnimation.setDuration(300);
            resizeAnimation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    //Fade in the bitmap
                    AlphaAnimation alphaAnimation = new AlphaAnimation(0, 1);
                    alphaAnimation.setDuration(200);
                    thumbnail.setImageBitmap(bitmap);
                    thumbnail.startAnimation(alphaAnimation);
                    thumbnailFull.getLayoutParams().height = thumbnail.getHeight();
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            thumbnail.startAnimation(resizeAnimation);
            //Set toolbar color according to the bitmap colors using Palette library.
            Palette palette = Palette.from(bitmap).generate();
            palette.getVibrantColor(getResources().getColor(R.color.colorPrimary));
            int collapsingToolBarScrimColor = palette.getVibrantColor(getResources().getColor(R.color.colorPrimary));
            int toolBarColor = palette.getMutedColor(getResources().getColor(R.color.light_gray));
            CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
            collapsingToolbarLayout.setContentScrimColor(collapsingToolBarScrimColor);
            AppBarLayout appBar = (AppBarLayout) findViewById(R.id.app_bar);
            appBar.setBackgroundColor(toolBarColor);
            thumbnailFull.setBackgroundColor(toolBarColor);
            //Hide progressBar
            findViewById(R.id.progressBar).setVisibility(View.GONE);
        }
    }
}
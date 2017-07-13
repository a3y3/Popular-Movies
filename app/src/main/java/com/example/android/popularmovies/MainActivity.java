package com.example.android.popularmovies;


import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.android.popularmovies.data.MovieContract;

import java.net.URL;

public class MainActivity extends AppCompatActivity implements MovieAdapter.OnRecyclerClickListener, LoaderManager.LoaderCallbacks<String[][]> {
    public ProgressBar progressBar;
    public RecyclerView recyclerView;
    public MovieAdapter movieAdapter;
    private TextView noFavouritesTextView;
    private final int LOADER_ID = 111;
    private final int PERMISSIONS_WRITE_STORAGE = 1111;
    public static boolean isConnectedToInternet = false;
    private Cursor mCursor;
    BroadcastReceiver broadcastReceiver;
    private boolean connectionWasDown = false;
    private static Snackbar noInternetSnackbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        checkPermissions();

        progressBar = (ProgressBar) findViewById(R.id.pb_load);
        recyclerView = (RecyclerView) findViewById(R.id.rv_display);
        movieAdapter = new MovieAdapter(this, this);
        noFavouritesTextView = (TextView)findViewById(R.id.tv_no_favourites);

        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

                if (networkInfo != null && networkInfo.isConnected()) {
                    MainActivity.isConnectedToInternet = true;
                    if(connectionWasDown)
                        Snackbar.make(getWindow().getDecorView().getRootView(), "Connection Restored", Snackbar.LENGTH_SHORT).show();
                    noFavouritesTextView.setVisibility(View.INVISIBLE);
                    loadData(true);
                    if(noInternetSnackbar!=null){
                        noInternetSnackbar.dismiss();
                    }
                    ///Log.e("ConnectivityReceiver", "Connection established");
                }
                else {
                    noInternetSnackbar = Snackbar.make(getWindow().getDecorView().getRootView(),"Offline functionality",Snackbar.LENGTH_INDEFINITE);
                    noInternetSnackbar.show();
                    connectionWasDown = true;
                    loadData(false);
                    isConnectedToInternet = false;
                }
            }
        };
        registerReceiver(broadcastReceiver, new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"));

        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(movieAdapter);
        loadData(isConnectedToInternet);
    }

    public void checkPermissions() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                MovieDetails.isExternalStorageGranted = true;
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        PERMISSIONS_WRITE_STORAGE);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            MovieDetails.isExternalStorageGranted = true;
        } else {
            new AlertDialog.Builder(this)
                    .setTitle("Uh oh")
                    .setMessage(R.string.dialog_write_permission_rejected)
                    .setPositiveButton("Sure!", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            checkPermissions();
                        }
                    })
                    .setNegativeButton("Nope", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            MovieDetails.isExternalStorageGranted = false;
                        }
                    })
                    .show();
            MovieDetails.isExternalStorageGranted = false;
        }
    }

    public void loadData(boolean connection) {
        isConnectedToInternet = connection;
        LoaderManager loaderManager = getSupportLoaderManager();
        if (loaderManager.getLoader(LOADER_ID) != null) {
            getSupportLoaderManager().restartLoader(LOADER_ID, null, this);
        }
        getSupportLoaderManager().initLoader(LOADER_ID, null, this);
    }

    @Override
    public Loader<String[][]> onCreateLoader(int id, final Bundle args) {
        return new AsyncTaskLoader<String[][]>(this) {
            String movieIdData[];
            String movieTitleData[];
            String imageURLs[];
            String synopsisData[];
            String userRatingData[];
            String releaseDateData[];

            @Override
            protected void onStartLoading() {
                super.onStartLoading();
                progressBar.setVisibility(View.VISIBLE);
                forceLoad();
            }

            @Override
            public String[][] loadInBackground() {
                URL url = NetworkUtils.buildURL();
                String results;
                if (isConnectedToInternet) {
                    try {
                        results = NetworkUtils.getResponseFromServer(url);
                        movieIdData = OpenMovieJsonUtils.getMovieIdFromJson(results);
                        movieTitleData = OpenMovieJsonUtils.getTitleFromJson(results);
                        imageURLs = OpenMovieJsonUtils.getImageUrlsFromJson(results);
                        synopsisData = OpenMovieJsonUtils.getSynopsisFromJson(results);
                        userRatingData = OpenMovieJsonUtils.getUserRatingFromJson(results);
                        releaseDateData = OpenMovieJsonUtils.getReleaseDateFromJson(results);

                        return new String[][]{movieIdData, movieTitleData, imageURLs, synopsisData, userRatingData, releaseDateData};
                    } catch (Exception e) {
                        Log.e("MainActivity", "ERROR OCCURRED!" + e.toString());
                        return null;
                    }
                } else {
                    mCursor = getContentResolver().query(MovieContract.MovieEntry.CONTENT_URI,
                            null,
                            null,
                            null,
                            null);
                    if(mCursor.getCount() == 0){
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                noFavouritesTextView.setVisibility(View.VISIBLE);
                            }
                        });
                    }else{
                        noFavouritesTextView.setVisibility(View.INVISIBLE);
                    }
                    movieAdapter.setCursor(mCursor);
                    return null;
                }
            }
        };
    }

    @Override
    public void onLoaderReset(Loader loader) {

    }

    @Override
    public void onLoadFinished(Loader<String[][]> loader, String data[][]) {
        progressBar.setVisibility(View.GONE);
        if (isConnectedToInternet) {
            movieAdapter.setMovieIdData(data[0]);
            movieAdapter.setTitleData(data[1]);
            movieAdapter.setImageString(data[2]);
            movieAdapter.setSynopsisData(data[3]);
            movieAdapter.setUserRatingData(data[4]);
            movieAdapter.setReleaseDateData(data[5]);
        }

        recyclerView.setAdapter(movieAdapter);
    }


    @Override
    public void onClick(String id, String title, String synopsis, String imageURL, String releaseDate, String userRating) {
        Intent intent = new Intent(this, MovieDetails.class);
        intent.putExtra("id", id);
        intent.putExtra("title", title);
        intent.putExtra("userRating", userRating);
        intent.putExtra("imageURL", imageURL);
        intent.putExtra("synopsis", synopsis);
        intent.putExtra("releaseDate", releaseDate);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_sort_popularity) {
            movieAdapter.setImageString(null);
            NetworkUtils.SORT_ORDER = "popular";
            loadData(MainActivity.isConnectedToInternet);
            return true;
        }
        if (id == R.id.action_sort_rating) {
            movieAdapter.setImageString(null);
            NetworkUtils.SORT_ORDER = "top_rated";
            loadData(MainActivity.isConnectedToInternet);
            return true;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadData(isConnectedToInternet);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(broadcastReceiver);
        if(mCursor!=null){
            mCursor.close();
        }
    }
}
package com.example.android.popularmovies;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.net.URL;

public class MainActivity extends AppCompatActivity implements MovieAdapter.OnRecyclerClickListener{
    public ProgressBar progressBar;
    public RecyclerView recyclerView;
    public MovieAdapter movieAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        new AlertDialog.Builder(this)
                .setTitle("Disclaimer")
                .setMessage(R.string.header_string)
                .setPositiveButton("I know!", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .show();
        progressBar = (ProgressBar)findViewById(R.id.pb_load);
        recyclerView = (RecyclerView)findViewById(R.id.rv_display);
        movieAdapter = new MovieAdapter(this, this);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(movieAdapter);
        loadData();

    }

    public void loadData(){
        //Construct a URL and fetch data.
        URL url = NetworkUtils.buildURL();
        new QueryTask().execute(url);

    }

    public class QueryTask extends AsyncTask<URL, Void, Void>{
        String movieIdData[];
        String movieTitleData[];
        String imageURLs[];
        String synopsisData[];
        String userRatingData[];
        String releaseDateData[];
        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(URL... params) {
            URL url = params[0];
            String results;
            try{
                results = NetworkUtils.getResponseFromServer(url);
                movieIdData = OpenMovieJsonUtils.getMovieIdFromJson(results);
                movieTitleData = OpenMovieJsonUtils.getTitleFromJson(results);
                imageURLs = OpenMovieJsonUtils.getImageUrlsFromJson(results);
                synopsisData = OpenMovieJsonUtils.getSynopsisFromJson(results);
                userRatingData = OpenMovieJsonUtils.getUserRatingFromJson(results);
                releaseDateData = OpenMovieJsonUtils.getReleaseDateFromJson(results);

            }
            catch(Exception e){
                Log.e("MainActivity","ERROR OCCURED!"+e.toString());
                return null;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void voids) {
            progressBar.setVisibility(View.GONE);
            movieAdapter.setMovieIdData(movieIdData);
            movieAdapter.setImageString(imageURLs);
            movieAdapter.setReleaseDateData(releaseDateData);
            movieAdapter.setSynopsisData(synopsisData);
            movieAdapter.setTitleData(movieTitleData);
            movieAdapter.setUserRatingData(userRatingData);

            recyclerView.setAdapter(movieAdapter);

        }
    }

    @Override
    public void onClick(String id, String title, String synopsis, String imageURL, String releaseDate, String userRating) {
        Intent intent = new Intent(this, MovieDetails.class);
        intent.putExtra("id",id);
        intent.putExtra("title",title);
        intent.putExtra("userRating", userRating);
        intent.putExtra("imageURL",imageURL);
        intent.putExtra("synopsis",synopsis);
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
            loadData();
            return true;
        }
        if (id == R.id.action_sort_rating) {
            movieAdapter.setImageString(null);
            NetworkUtils.SORT_ORDER = "top_rated";
            loadData();
            return true;

        }

        return super.onOptionsItemSelected(item);
    }
}

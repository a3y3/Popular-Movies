package com.example.android.popularmovies;

import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.io.IOException;
import java.net.URL;

public class MovieDetails extends AppCompatActivity implements LoaderManager.LoaderCallbacks<String[]> {
    String id;
    String title;
    String userRating;
    String synopsis;
    String imageURL;
    String releaseDate;
    private int LoaderId = 444;

    private String MOVIE_ID_KEY = "MovieIdKey";

    private TextView titleTextView;
    private TextView userRatingTextView;
    private TextView releaseDateTextView;
    private TextView synopsisTextView;
    private ImageView movieArt;
    private ImageView trailerButton;
    private ConstraintLayout constraintLayout;
    private ProgressBar mProgressBar;
    private TextView runtimeTextView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);
        Intent intent = getIntent();
        id = intent.getStringExtra("id");
        title = intent.getStringExtra("title");
        userRating = intent.getStringExtra("userRating");
        synopsis = intent.getStringExtra("synopsis");
        imageURL = intent.getStringExtra("imageURL");
        releaseDate = intent.getStringExtra("releaseDate");

        titleTextView = (TextView) findViewById(R.id.banner_movie_name_top);
        userRatingTextView = (TextView) findViewById(R.id.tv_user_rating);
        releaseDateTextView = (TextView) findViewById(R.id.tv_release_date);
        synopsisTextView = (TextView) findViewById(R.id.tv_synopsis);
        movieArt = (ImageView) findViewById(R.id.iv_movie_art);
        constraintLayout = (ConstraintLayout) findViewById(R.id.detail_layout);
        mProgressBar = (ProgressBar)findViewById(R.id.pb_movie_details);
        trailerButton = (ImageView)findViewById(R.id.trailer_iv_buton);
        runtimeTextView = (TextView)findViewById(R.id.tv_runtime);

        //Bundle for loader
        Bundle queryBundle = new Bundle();
        queryBundle.putString(MOVIE_ID_KEY, id);
        titleTextView.setText(title);
        userRatingTextView.setText(userRating);
        userRatingTextView.append("/10");
        synopsisTextView.setText(synopsis);
        String releaseYear = releaseDate.substring(0,4);
        releaseDateTextView.setText(releaseYear);
        Picasso.with(this).load("http://image.tmdb.org/t/p/w185/" + imageURL).into(movieArt);

        getSupportLoaderManager().initLoader(LoaderId, queryBundle, this);

    }

    @Override
    public Loader<String[]> onCreateLoader(int id, final Bundle args) {
        return new AsyncTaskLoader<String[]>(this) {
            URL trailerURL;
            URL runtimeURL;
            String innerClassId = args.getString(MOVIE_ID_KEY);

            @Override
            protected void onStartLoading() {
                super.onStartLoading();
                mProgressBar.setVisibility(View.VISIBLE);
                constraintLayout.setVisibility(View.INVISIBLE);
                forceLoad();
            }

            @Override
            public String[] loadInBackground() {
                trailerURL = NetworkUtils.buildDetailsURL(innerClassId);
                runtimeURL = NetworkUtils.buildUrlForSpecificMovie(innerClassId);
                String data[] = new String [2];
                String trailerKey;
                String runtime;
                try {
                    String jsonTrailerData = NetworkUtils.getResponseFromServer(trailerURL);
                    String jsonRuntimeData = NetworkUtils.getResponseFromServer(runtimeURL);
                    trailerKey = OpenMovieJsonUtils.getTrailersFromJson(jsonTrailerData);
                    runtime = OpenMovieJsonUtils.getRuntimeFromJson(jsonRuntimeData);
                    data[0]=trailerKey;
                    data[1]=runtime;
                } catch (Exception exception) {
                    Log.e("MovieDetails", exception.toString());
                }
                return data;
            }
        };
    }

    @Override
    public void onLoaderReset(Loader loader) {

    }

    @Override
    public void onLoadFinished(Loader<String[]> loader, final String data[]) {
        mProgressBar.setVisibility(View.INVISIBLE);
        constraintLayout.setVisibility(View.VISIBLE);
        if(data[0] == null){
            //No trailer found, hide trailers play button.
            trailerButton.setVisibility(View.INVISIBLE);
        }
        trailerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent openInYoutube = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com/watch?v="+data));
                startActivity(openInYoutube);
            }
        });
        runtimeTextView.setText(data[1]);
        runtimeTextView.append("min");
    }
}

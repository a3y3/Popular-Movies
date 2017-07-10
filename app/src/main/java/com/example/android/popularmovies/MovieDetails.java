package com.example.android.popularmovies;

import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.ContextCompat;
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
    private int LoaderId = 112;

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
    private TextView review1TextView;
    private TextView review2TextView;
    private TextView review3TextView;


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
        trailerButton = (ImageView)findViewById(R.id.trailer_iv_button);
        runtimeTextView = (TextView)findViewById(R.id.tv_runtime);
        review1TextView = (TextView)findViewById(R.id.tv_review1);
        review2TextView = (TextView)findViewById(R.id.tv_review2);
        review3TextView = (TextView)findViewById(R.id.tv_review3);

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

    private void onLoading(){
        mProgressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public Loader<String[]> onCreateLoader(int id, final Bundle args) {
        return new AsyncTaskLoader<String[]>(this) {
            URL trailerURL;
            URL runtimeURL;
            URL reviewURL;
            String innerClassId = args.getString(MOVIE_ID_KEY);

            @Override
            protected void onStartLoading() {
                super.onStartLoading();
                onLoading();
                forceLoad();
            }

            @Override
            public String[] loadInBackground() {
                trailerURL = NetworkUtils.buildDetailsURL(innerClassId);
                runtimeURL = NetworkUtils.buildUrlForSpecificMovie(innerClassId);
                reviewURL = NetworkUtils.buildUrlForReviews(innerClassId);
                String data[] = new String[5];
                String trailerKey;
                String runtime;
                String reviews[];
                try {
                    String jsonTrailerData = NetworkUtils.getResponseFromServer(trailerURL);
                    String jsonRuntimeData = NetworkUtils.getResponseFromServer(runtimeURL);
                    String jsonReviewData = NetworkUtils.getResponseFromServer(reviewURL);
                    trailerKey = OpenMovieJsonUtils.getTrailersFromJson(jsonTrailerData);
                    runtime = OpenMovieJsonUtils.getRuntimeFromJson(jsonRuntimeData);
                    reviews = OpenMovieJsonUtils.getReviewsFromJson(jsonReviewData);
                    data[0]=trailerKey;
                    data[1]=runtime;
                    data[2]=reviews[0];
                    data[3]=reviews[1];
                    data[4]=reviews[2];
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
        if(data[0] == null){
            //No trailer found, hide trailers play button.
            trailerButton.setVisibility(View.INVISIBLE);
        }
        trailerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent openInYoutube = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com/watch?v="+data[0]));
                startActivity(openInYoutube);
            }
        });
        runtimeTextView.setText(data[1]);
        runtimeTextView.append("min");
        if(data[2]!=null){
            review1TextView.setText(R.string.review_1);
            review1TextView.setTextColor(ContextCompat.getColor(this, R.color.linkColor));
            review1TextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent openWebpageIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(data[2]));
                    startActivity(openWebpageIntent);
                }
            });
        }
        if(data[3]!=null){
            review2TextView.setText(R.string.review_2);
            review2TextView.setTextColor(ContextCompat.getColor(this, R.color.linkColor));
            review2TextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent openWebpageIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(data[3]));
                    startActivity(openWebpageIntent);
                }
            });
        }
        if(data[4]!=null){
            review3TextView.setText(R.string.review_3);
            review3TextView.setTextColor(ContextCompat.getColor(this, R.color.linkColor));
            review3TextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent openWebpageIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(data[4]));
                    startActivity(openWebpageIntent);
                }
            });
        }
    }
}

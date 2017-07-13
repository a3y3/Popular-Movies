package com.example.android.popularmovies;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
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
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.DownloadListener;
import com.example.android.popularmovies.data.MovieContract;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.io.File;
import java.io.IOException;
import java.net.URL;

public class MovieDetails extends AppCompatActivity implements LoaderManager.LoaderCallbacks<String[]> {
    String movieId;
    String title;
    String userRating;
    String synopsis;
    String imageURL;
    String runtime;
    String releaseDate;
    private int LoaderId = 112;
    private boolean starClicked;
    protected static boolean isExternalStorageGranted;

    private final String MOVIE_ID_KEY = "MovieIdKey";
    public final static String PATH_TO_MOVIE_IMAGE = Environment.getExternalStorageDirectory() + "/Popular_Movies/Images/";

    private TextView titleTextView;
    private TextView userRatingTextView;
    private TextView releaseDateTextView;
    private TextView synopsisTextView;
    private ImageView movieArt;
    private ImageView trailerButton;
    private ProgressBar mProgressBar;
    private TextView runtimeTextView;
    private TextView review1TextView;
    private TextView review2TextView;
    private TextView review3TextView;
    private ImageView starMovie;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);
        Intent intent = getIntent();
        movieId = intent.getStringExtra("id");
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
        mProgressBar = (ProgressBar) findViewById(R.id.pb_movie_details);
        trailerButton = (ImageView) findViewById(R.id.trailer_iv_button);
        runtimeTextView = (TextView) findViewById(R.id.tv_runtime);
        review1TextView = (TextView) findViewById(R.id.tv_review1);
        review2TextView = (TextView) findViewById(R.id.tv_review2);
        review3TextView = (TextView) findViewById(R.id.tv_review3);
        starMovie = (ImageView) findViewById(R.id.star_movie);

        Picasso.with(MovieDetails.this).load("http://image.tmdb.org/t/p/w185/" + imageURL).into(movieArt);


        //Bundle for loader
        Bundle queryBundle = new Bundle();
        queryBundle.putString(MOVIE_ID_KEY, movieId);
        titleTextView.setText(title);
        userRatingTextView.setText(userRating);
        userRatingTextView.append("/10");
        synopsisTextView.setText(synopsis);
        String releaseYear = releaseDate.substring(0, 4);
        releaseDateTextView.setText(releaseYear);

        getSupportLoaderManager().initLoader(LoaderId, queryBundle, this);

    }

    public void handleStarredMovie(boolean ifClicked) {
        starClicked = ifClicked;
        if (!starClicked) {
            starClicked = true;
            starMovie.setImageResource(R.drawable.ic_star_white_24px);
        } else {
            starClicked = false;
            starMovie.setImageResource(R.drawable.ic_star_border_white_24px);
        }
        starMovie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!starClicked) {
                    starClicked = true;
                    starMovie.setImageResource(R.drawable.ic_star_white_24px);
                    ContentValues contentValues = new ContentValues();
                    contentValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_ID, movieId);
                    contentValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_TITLE, title);
                    contentValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_RELEASE_DATE, releaseDate);
                    contentValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_USER_RATING, userRating);
                    contentValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_RUNTIME, runtime);
                    contentValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_SYNOPSIS, synopsis);
                    contentValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_IMAGE_URL, imageURL);
                    getContentResolver().insert(MovieContract.MovieEntry.CONTENT_URI, contentValues);


                    if (isExternalStorageGranted && MainActivity.isConnectedToInternet) {
                        AndroidNetworking.initialize(MovieDetails.this);
                        String path = PATH_TO_MOVIE_IMAGE;
                        File file = new File(path, movieId + ".png");
                        if (file.length() == 0) {
                            String fileName = movieId + ".png";
                            AndroidNetworking.download("http://image.tmdb.org/t/p/w185/" + imageURL, path, fileName)
                                    .build()
                                    .startDownload(new DownloadListener() {
                                        @Override
                                        public void onDownloadComplete() {
                                            Log.i("MovieDetails", "Download Complete");
                                        }

                                        @Override
                                        public void onError(ANError anError) {
                                            Log.e("MovieDetais", "Download Error" + anError.toString());
                                        }
                                    });
                        } else {
                            Log.i("MovieDetails", "File exists");
                        }
                    }

                    Snackbar.make(view, R.string.yay_offline, Snackbar.LENGTH_LONG)
                            .setAction("Cancel", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    getContentResolver().delete(MovieContract.MovieEntry.CONTENT_URI,
                                            MovieContract.MovieEntry.COLUMN_MOVIE_ID + "=?",
                                            new String[]{movieId});
                                    starClicked = false;
                                    starMovie.setImageResource(R.drawable.ic_star_border_white_24px);
                                }
                            }).show();

                } else {
                    getContentResolver().delete(MovieContract.MovieEntry.CONTENT_URI,
                            MovieContract.MovieEntry.COLUMN_MOVIE_ID + "=?",
                            new String[]{movieId});
                    starClicked = false;
                    starMovie.setImageResource(R.drawable.ic_star_border_white_24px);
                }
            }
        });
    }

    private void onLoading() {
        titleTextView.setVisibility(View.INVISIBLE);
        mProgressBar.setVisibility(View.VISIBLE);
        starMovie.setVisibility(View.INVISIBLE);
    }

    private void onStopLoading() {
        titleTextView.setVisibility(View.VISIBLE);
        mProgressBar.setVisibility(View.GONE);
        starMovie.setVisibility(View.VISIBLE);
    }

    @Override
    public Loader<String[]> onCreateLoader(final int id, final Bundle args) {
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
                String data[] = new String[6];
                String trailerKey;
                String runtime;
                String reviews[];
                if (MainActivity.isConnectedToInternet) {
                    try {
                        String jsonTrailerData = NetworkUtils.getResponseFromServer(trailerURL);
                        String jsonRuntimeData = NetworkUtils.getResponseFromServer(runtimeURL);
                        String jsonReviewData = NetworkUtils.getResponseFromServer(reviewURL);
                        trailerKey = OpenMovieJsonUtils.getTrailersFromJson(jsonTrailerData);
                        runtime = OpenMovieJsonUtils.getRuntimeFromJson(jsonRuntimeData);
                        reviews = OpenMovieJsonUtils.getReviewsFromJson(jsonReviewData);

                        data[0] = trailerKey;
                        data[1] = runtime;
                        data[2] = reviews[0];
                        data[3] = reviews[1];
                        data[4] = reviews[2];
                    } catch (Exception exception) {
                        Log.e("MovieDetails", exception.toString());
                    }
                }
                Cursor cursor = getContentResolver().query(MovieContract.MovieEntry.CONTENT_URI,
                        null,
                        MovieContract.MovieEntry.COLUMN_MOVIE_ID + "=?",
                        new String[]{String.valueOf(movieId)},
                        null);
                if (cursor.getCount() == 0)
                    data[5] = "no";
                else
                    data[5] = "yes";
                cursor.close();
                return data;
            }
        };
    }

    @Override
    public void onLoaderReset(Loader loader) {

    }

    @Override
    public void onLoadFinished(Loader<String[]> loader, final String data[]) {
        onStopLoading();
        mProgressBar.setVisibility(View.INVISIBLE);
        if (MainActivity.isConnectedToInternet) {
            if (data[0] == null) {
                //No trailer found, hide trailers play button.
                trailerButton.setVisibility(View.INVISIBLE);
            }
            trailerButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent openInYoutube = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com/watch?v=" + data[0]));
                    startActivity(openInYoutube);
                }
            });
            runtime = data[1];
            runtimeTextView.setText(runtime);
            runtimeTextView.append("min");
            if (data[2] != null) {
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
            if (data[3] != null) {
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
            if (data[4] != null) {
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
        if (data[5].equals("no")) {
            handleStarredMovie(true);
        } else {
            handleStarredMovie(false);
        }
    }
}
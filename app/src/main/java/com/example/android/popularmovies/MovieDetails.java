package com.example.android.popularmovies;

import android.content.Intent;
import android.media.Image;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class MovieDetails extends AppCompatActivity {
    String title;
    String userRating ;
    String synopsis;
    String imageURL;
    String releaseDate;
    private TextView titleTextView;
    private TextView userRatingTextView;
    private TextView releaseDateTextView;
    private TextView synopsisTextView;
    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);
        Intent intent = getIntent();
        title = intent.getStringExtra("title");
        userRating = intent.getStringExtra("userRating");
        synopsis = intent.getStringExtra("synopsis");
        imageURL = intent.getStringExtra("imageURL");
        releaseDate = intent.getStringExtra("releaseDate");

        titleTextView = (TextView)findViewById(R.id.tv_title);
        userRatingTextView = (TextView)findViewById(R.id.tv_user_rating);
        releaseDateTextView = (TextView)findViewById(R.id.tv_release_date);
        synopsisTextView = (TextView)findViewById(R.id.tv_synopsis);
        imageView = (ImageView)findViewById(R.id.iv_movie_art);

        titleTextView.setText(title);
        userRatingTextView.setText(R.string.tv_append_user_rating);
        userRatingTextView.append(" "+ userRating);
        synopsisTextView.setText(synopsis);
        releaseDateTextView.setText(R.string.tv_append_release_date);
        releaseDateTextView.append(" "+releaseDate);
        Picasso.with(this).load("http://image.tmdb.org/t/p/w185/"+imageURL).into(imageView);

    }
}

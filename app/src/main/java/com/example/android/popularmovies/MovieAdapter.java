package com.example.android.popularmovies;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.popularmovies.data.MovieContract;
import com.example.android.popularmovies.data.MovieDBHelper;
import com.squareup.picasso.Picasso;

import java.io.File;

/**
 * Created by Soham on 27-May-17.
 */

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieAdapterViewHolder> {
    private String movieIdData[];
    private String movieTitleData[];
    private String synopsisData[];
    private String userRatingData[];
    private String releaseDateData[];
    private final OnRecyclerClickListener onRecyclerClickListener;
    private String[] mImageUrls;
    private Context mContext;
    private Cursor cursor;

    public interface OnRecyclerClickListener{
        void onClick(String id, String title, String synopsis, String imageURL, String releaseDate, String userRating);
    }
    public MovieAdapter(Context context, OnRecyclerClickListener onRecyclerClickListener){
        this.mContext = context;
        this.onRecyclerClickListener = onRecyclerClickListener;
    }

    public class MovieAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public ImageView imageView;
        public MovieAdapterViewHolder(View itemView){
            super(itemView);
            itemView.setOnClickListener(this);
            imageView = itemView.findViewById(R.id.rv_image_view);
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            String id,title,userRating,synopsis,imageURL,releaseDate;
            if(MainActivity.isConnectedToInternet && MainActivity.simulatedConnection) {
                id = movieIdData[adapterPosition];
                title = movieTitleData[adapterPosition];
                userRating = userRatingData[adapterPosition];
                synopsis = synopsisData[adapterPosition];
                imageURL = mImageUrls[adapterPosition];
                releaseDate = releaseDateData[adapterPosition];
            }
            else{
                cursor.moveToPosition(adapterPosition);
                id = cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_MOVIE_ID));
                title = cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_MOVIE_TITLE));
                userRating = cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_MOVIE_USER_RATING));
                synopsis = cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_MOVIE_SYNOPSIS));
                imageURL = cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_MOVIE_IMAGE_URL));
                releaseDate = cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_MOVIE_RELEASE_DATE));
            }
            onRecyclerClickListener.onClick(id, title, synopsis, imageURL, releaseDate, userRating);
        }
    }

    @Override
    public int getItemCount() {
        if(MainActivity.isConnectedToInternet && MainActivity.simulatedConnection) {
            if (mImageUrls == null) {
                return 0;
            }
            return mImageUrls.length;
        }
        else{
            if(cursor == null){
                return 0;
            }
            Log.d("112","Returned "+cursor.getCount()+" in getCount()");
            return cursor.getCount();
        }
    }

    @Override
    public MovieAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_movie, parent, false);
        return new MovieAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MovieAdapterViewHolder holder, int position) {
        if(MainActivity.isConnectedToInternet && MainActivity.simulatedConnection){
            String URL = mImageUrls[position];
            Picasso.with(mContext).load("http://image.tmdb.org/t/p/w185/"+URL).into(holder.imageView);
        }
        else {
            cursor.moveToPosition(position);
            String movieId = cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_MOVIE_ID));
            String path = MovieDetails.PATH_TO_MOVIE_IMAGE + movieId + ".png";
            File f = new File(path);
            if (MovieDetails.isExternalStorageGranted) {
                Picasso.with(mContext).load(f).into(holder.imageView);
                Log.d("112", "Loading " + MovieDetails.PATH_TO_MOVIE_IMAGE + movieId + ".png");
            } else {
                Picasso.with(mContext).load(R.drawable.ugly_movie).into(holder.imageView);
            }
        }
    }

    public void setImageString(String [] imageString){
        this.mImageUrls = imageString;
        notifyDataSetChanged();
    }

    public void setMovieIdData(String[] id){
        this.movieIdData = id;
    }

    public void setTitleData(String[] titleString) {
        this.movieTitleData = titleString;
    }

    public void setUserRatingData(String[] userRatingData){
        this.userRatingData = userRatingData;
    }

    public void setSynopsisData(String synopsisData[]){
        this.synopsisData = synopsisData;
    }

    public void setReleaseDateData(String[] releaseDateData){
        this.releaseDateData = releaseDateData;
    }
    public void setCursor(Cursor c){
        cursor = c;
        cursor.moveToFirst();
    }
}

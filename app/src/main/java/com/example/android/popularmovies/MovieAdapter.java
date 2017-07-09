package com.example.android.popularmovies;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

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
            imageView = (ImageView)itemView.findViewById(R.id.rv_image_view);
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            String id = movieIdData[adapterPosition];
            String title = movieTitleData[adapterPosition];
            String userRating = userRatingData[adapterPosition];
            String synopsis = synopsisData[adapterPosition];
            String imageURL = mImageUrls[adapterPosition];
            String releaseDate = releaseDateData[adapterPosition];
            onRecyclerClickListener.onClick(id, title, synopsis, imageURL, releaseDate, userRating);
        }
    }

    @Override
    public int getItemCount() {
        if(mImageUrls == null){
            return 0;
        }
        return mImageUrls.length;
    }

    @Override
    public MovieAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_movie, parent, false);
        return new MovieAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MovieAdapterViewHolder holder, int position) {
        String URL = mImageUrls[position];
        Picasso.with(mContext).load("http://image.tmdb.org/t/p/w185/"+URL).into(holder.imageView);
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
}

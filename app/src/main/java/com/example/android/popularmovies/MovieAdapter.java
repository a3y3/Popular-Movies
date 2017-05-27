package com.example.android.popularmovies;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

/**
 * Created by Soham on 27-May-17.
 */

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieAdapterViewHolder> {
    private String[] mImageUrls;
    private Context mContext;
    public MovieAdapter(){}
    public MovieAdapter(Context context){
        this.mContext = context;
    }

    public class MovieAdapterViewHolder extends RecyclerView.ViewHolder{
        public ImageView imageView;
        public MovieAdapterViewHolder(View itemView){
            super(itemView);
            imageView = (ImageView)itemView.findViewById(R.id.rv_image_view);
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
        Log.e("111","Loading http://image.tmdb.org/t/p/w342/"+URL);
        Picasso.with(mContext).load("http://image.tmdb.org/t/p/w185/"+URL).into(holder.imageView);
    }

    public void setImageString(String [] imageString){
        this.mImageUrls = imageString;
        notifyDataSetChanged();
    }
}

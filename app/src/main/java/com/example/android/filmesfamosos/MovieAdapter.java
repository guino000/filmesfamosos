package com.example.android.filmesfamosos;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.filmesfamosos.model.Movie;
import com.squareup.picasso.Picasso;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Gui on 2/15/2018.
 */

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieAdapterViewHolder> {

    private ArrayList<Movie> mMovieData;

    public class MovieAdapterViewHolder extends RecyclerView.ViewHolder{
        public final ImageView mMiniatureImageView;
        public final TextView mMiniatureTextView;
        public Context mContext;

        public MovieAdapterViewHolder(Context context, View itemView) {
            super(itemView);
            mMiniatureImageView = itemView.findViewById(R.id.iv_miniature_img);
            mMiniatureTextView = itemView.findViewById(R.id.tv_miniature_text);
            mContext = context;
        }
    }

    public void setMovieData(ArrayList<Movie> movieData){
        mMovieData = movieData;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        if(mMovieData == null) return 0;
        return mMovieData.size();
    }

    @Override
    public MovieAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutIdForGridItem = R.layout.main_movie_miniature;
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(layoutIdForGridItem, parent, false);

        return new MovieAdapterViewHolder(parent.getContext(), view);
    }

    @Override
    public void onBindViewHolder(MovieAdapterViewHolder holder, int position) {
        Movie movie = mMovieData.get(position);
        holder.mMiniatureTextView.setText(movie.getTitle().getTitle());
        Picasso.with(holder.mContext).load(movie.getPosterPath()).into(holder.mMiniatureImageView);
    }
}

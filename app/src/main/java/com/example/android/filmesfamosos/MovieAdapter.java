package com.example.android.filmesfamosos;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.android.filmesfamosos.model.Movie;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Gui on 2/15/2018.
 */

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieAdapterViewHolder> {

    private ArrayList<Movie> mMovieData;
    private int mCurrentPage;
    private final MovieAdapterOnClickHandler mClickHandler;

    public interface MovieAdapterOnClickHandler{
        void onClick(Movie clickedMovie);
    }

    public MovieAdapter(MovieAdapterOnClickHandler clickHandler){
        mMovieData = new ArrayList<>();
        mClickHandler = clickHandler;
    }

    public class MovieAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public final ImageView mMiniatureImageView;
        public final TextView mMiniatureTextView;
        public final ProgressBar mProgressBar;
        public Context mContext;

        public MovieAdapterViewHolder(Context context, View itemView) {
            super(itemView);
            mMiniatureImageView = itemView.findViewById(R.id.iv_miniature_img);
            mMiniatureTextView = itemView.findViewById(R.id.tv_miniature_text);
            mProgressBar = itemView.findViewById(R.id.pb_miniature_placeholder);
            mContext = context;

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            Movie clickedMovie = mMovieData.get(position);
            mClickHandler.onClick(clickedMovie);
        }
    }

    public void addMovieData(Movie movie){
        mMovieData.add(movie);
        notifyDataSetChanged();
    }

    public void addMovieData(ArrayList<Movie> movieData){
        mMovieData.addAll(movieData);
        notifyDataSetChanged();
    }

    public void setMovieData(ArrayList<Movie> movieData){
        mMovieData = movieData;
        notifyDataSetChanged();
    }

    public void eraseMovieData(){
        mMovieData = new ArrayList<>();
        notifyDataSetChanged();
    }

    public int getCurrentPage(){
        return mCurrentPage;
    }

    public void setCurrentPage(int page){
        mCurrentPage = page;
    }

    public ArrayList<Movie> getMovieData(){
        return mMovieData;
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
    public void onBindViewHolder(final MovieAdapterViewHolder holder, int position) {
        Movie movie = mMovieData.get(position);
        holder.mMiniatureTextView.setText(movie.getTitle().getTitle());
        Picasso.with(holder.mContext)
                .load(movie.getPosterPath())
                .into(holder.mMiniatureImageView, new Callback() {
                    @Override
                    public void onSuccess() {
                        holder.mProgressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError() {

                    }
                });
    }
}

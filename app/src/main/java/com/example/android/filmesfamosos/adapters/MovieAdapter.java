package com.example.android.filmesfamosos.adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.android.filmesfamosos.R;
import com.example.android.filmesfamosos.model.Movie;
import com.example.android.filmesfamosos.model.MoviesContract;
import com.example.android.filmesfamosos.utilities.ModelUtils;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Guilherme Canalli on 2/15/2018.
 */

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieAdapterViewHolder> {

    private ArrayList<Movie> mMovieData;
    private Cursor mMovieCursor;
    private int mCurrentDatasetType;
    private final MovieAdapterOnClickHandler mClickHandler;
//    Dataset type keys
    public static final int DATASET_LIST = 0;
    public static final int DATASET_CURSOR = 1;

    public interface MovieAdapterOnClickHandler{
        void onClick(Movie clickedMovie);
    }

    public MovieAdapter(MovieAdapterOnClickHandler clickHandler, int currentDataset){
        mMovieData = new ArrayList<>();
        mClickHandler = clickHandler;
        mCurrentDatasetType = currentDataset;
    }

    public void swapCursor(Cursor newCursor){
        mMovieCursor = newCursor;
        notifyDataSetChanged();
    }

    public void setMovieData(ArrayList<Movie> newMovies){
        mMovieData = newMovies;
        notifyDataSetChanged();
    }
    public ArrayList<Movie> getMovieData(){
        return mMovieData;
    }

    public void setCurrentDatasetType(int datasetType){
        mCurrentDatasetType = datasetType;
    }

    public int getCurrentDatasetType(){
        return mCurrentDatasetType;
    }

    @Override
    public int getItemCount() {
        switch (mCurrentDatasetType){
            case DATASET_LIST:
                if(mMovieData == null) return 0;
                return mMovieData.size();
            case DATASET_CURSOR:
                if(mMovieCursor == null) return 0;
                return mMovieCursor.getCount();
            default:
                return 0;
        }
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
        switch (mCurrentDatasetType){
            case DATASET_LIST:
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
                break;
            case DATASET_CURSOR:
                mMovieCursor.moveToPosition(position);
                holder.mMiniatureTextView.setText(mMovieCursor.getString(mMovieCursor.getColumnIndex(MoviesContract.MovieEntry.COLUMN_TITLE)));
                Picasso.with(holder.mContext)
                        .load(mMovieCursor.getString(mMovieCursor.getColumnIndex(MoviesContract.MovieEntry.COLUMN_POSTER_PATH)))
                        .into(holder.mMiniatureImageView, new Callback() {
                            @Override
                            public void onSuccess() {
                                holder.mProgressBar.setVisibility(View.GONE);
                            }

                            @Override
                            public void onError() {

                            }
                        });
                break;
            default:
                throw new UnsupportedOperationException("Not supported yet.");
        }
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
            switch (mCurrentDatasetType){
                case DATASET_LIST:
                    mClickHandler.onClick(mMovieData.get(position));
                    break;
                case DATASET_CURSOR:
                    mMovieCursor.moveToPosition(position);
                    mClickHandler.onClick(ModelUtils.getMovieFromCursor(mMovieCursor));
                    break;
            }
        }
    }
}

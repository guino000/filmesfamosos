package com.example.android.filmesfamosos.adapters;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.VideoView;

import com.example.android.filmesfamosos.R;
import com.example.android.filmesfamosos.model.Movie;
import com.example.android.filmesfamosos.model.MoviesContract;
import com.example.android.filmesfamosos.model.Trailer;
import com.example.android.filmesfamosos.utilities.ModelUtils;

import java.net.URI;
import java.util.ArrayList;

public class TrailerAdapter extends RecyclerView.Adapter<TrailerAdapter.TrailerAdapterViewHolder> {

    private Cursor mTrailerCursor;
    private ArrayList<Trailer> mTrailerData;
    private TrailerAdapterClickHandler mClickHandler;
    private int mCurrentDatasetType;
//    Dataset type keys
    public static final int DATASET_LIST = 0;
    public static final int DATASET_CURSOR = 1;

    public interface TrailerAdapterClickHandler{
        void onClick(Trailer clickedTrailer);
    }

    public TrailerAdapter(TrailerAdapterClickHandler clickHandler, int currentDataset){
        mClickHandler = clickHandler;
        mCurrentDatasetType = currentDataset;
    }

    public void swapCursor(Cursor newCursor){
        mTrailerCursor = newCursor;
        notifyDataSetChanged();
    }

    public void setTrailerData(ArrayList<Trailer> newData){
        mTrailerData = newData;
        notifyDataSetChanged();
    }

    public ArrayList<Trailer> getTrailerData() {
        return mTrailerData;
    }

    public void setCurrentDatasetType(int datasetType){
        mCurrentDatasetType = datasetType;
    }

    public int getCurrentDatasetType(){
        return mCurrentDatasetType;
    }

    @NonNull
    @Override
    public TrailerAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        Gets application context
        Context context = parent.getContext();

//        Inflate the layout of the trailer miniature into a view
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.trailer_miniature, parent, false);

//        Create and return new view holder
        return new TrailerAdapterViewHolder(context, view);
    }

    @Override
    public void onBindViewHolder(@NonNull TrailerAdapterViewHolder holder, int position) {
        switch (mCurrentDatasetType){
            case DATASET_LIST:
                Trailer currentTrailer = mTrailerData.get(position);
                holder.mTrailerNameTextView.setText(currentTrailer.getName());
                break;
            case DATASET_CURSOR:
                mTrailerCursor.moveToPosition(position);
                holder.mTrailerNameTextView.setText(mTrailerCursor.getString(
                        mTrailerCursor.getColumnIndex(MoviesContract.TrailerEntry.COLUMN_NAME)));
                break;
            default:
                throw new UnsupportedOperationException("Not supported yet.");
        }
    }

    @Override
    public int getItemCount() {
        switch (mCurrentDatasetType){
            case DATASET_LIST:
                if(mTrailerData == null) return 0;
                return mTrailerData.size();
            case DATASET_CURSOR:
                if(mTrailerCursor == null) return 0;
                return mTrailerCursor.getCount();
            default:
                return 0;
        }
    }

    public class TrailerAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public final TextView mTrailerNameTextView;
        public Context mContext;

        public TrailerAdapterViewHolder(Context context, View itemView) {
            super(itemView);
            mTrailerNameTextView = itemView.findViewById(R.id.tv_trailer_miniature_name);
            mContext = context;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();

            switch (mCurrentDatasetType){
                case DATASET_LIST:
                    mClickHandler.onClick(mTrailerData.get(position));
                    break;
                case DATASET_CURSOR:
                    mTrailerCursor.moveToPosition(position);
                    mClickHandler.onClick(ModelUtils.getTrailerFromCursor(mTrailerCursor));
                    break;
                default:
                    throw new UnsupportedOperationException("Not supported yet.");
            }
        }
    }
}

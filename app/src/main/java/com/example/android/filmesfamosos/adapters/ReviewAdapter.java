package com.example.android.filmesfamosos.adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.filmesfamosos.R;
import com.example.android.filmesfamosos.model.MoviesContract;
import com.example.android.filmesfamosos.model.Review;

import java.util.ArrayList;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewAdapterViewHolder>{

    private Cursor mReviewCursor;
    private ArrayList<Review> mReviewData;
    private int mCurrentDatasetType;
//    Dataset type keys
    public static final int DATASET_LIST = 0;
    public static final int DATASET_CURSOR = 1;

    public ReviewAdapter(int currentDataset){
        mCurrentDatasetType = currentDataset;
    }

    public void swapCursor(Cursor newCursor){
        mReviewCursor = newCursor;
        notifyDataSetChanged();
    }

    public ArrayList<Review> getReviewData(){
        return mReviewData;
    }

    public void setReviewData(ArrayList<Review> newData){
        mReviewData = newData;
        notifyDataSetChanged();
    }

    public void setCurrentDatasetType(int datasetType){
        mCurrentDatasetType = datasetType;
    }

    public int getCurrentDatasetType(){
        return mCurrentDatasetType;
    }

    @NonNull
    @Override
    public ReviewAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.review_miniature, parent,false);
        return new ReviewAdapterViewHolder(parent.getContext(), view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewAdapterViewHolder holder, int position) {
        switch (mCurrentDatasetType){
            case DATASET_CURSOR:
                mReviewCursor.moveToPosition(position);
                holder.tvReviewAuthor.setText(mReviewCursor.getString(
                        mReviewCursor.getColumnIndex(MoviesContract.ReviewEntry.COLUMN_AUTHOR)));
                holder.tvReviewContent.setText(mReviewCursor.getString(
                        mReviewCursor.getColumnIndex(MoviesContract.ReviewEntry.COLUMN_CONTENT)));
                break;
            case DATASET_LIST:
                Review currentReview = mReviewData.get(position);
                holder.tvReviewAuthor.setText(currentReview.getAuthor());
                holder.tvReviewContent.setText(currentReview.getReviewContent());
                break;
            default:
                throw new UnsupportedOperationException("Not supported yet.");
        }
    }

    @Override
    public int getItemCount() {
        switch (mCurrentDatasetType){
            case DATASET_LIST:
                if(mReviewData == null) return 0;
                return mReviewData.size();
            case DATASET_CURSOR:
                if(mReviewCursor == null) return 0;
                return mReviewCursor.getCount();
            default:
                return 0;
        }
    }

    public class ReviewAdapterViewHolder extends RecyclerView.ViewHolder{
        public final TextView tvReviewAuthor;
        public final TextView tvReviewContent;
        public Context mContext;

        public ReviewAdapterViewHolder(Context context, View itemView) {
            super(itemView);
            tvReviewAuthor = itemView.findViewById(R.id.tv_review_author_name);
            tvReviewContent = itemView.findViewById(R.id.tv_review_content);
            mContext = context;
        }
    }
}

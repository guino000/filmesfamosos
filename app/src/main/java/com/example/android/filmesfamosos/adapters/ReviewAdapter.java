package com.example.android.filmesfamosos.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.filmesfamosos.R;
import com.example.android.filmesfamosos.model.Review;

import java.util.ArrayList;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewAdapterViewHolder>{

    private ArrayList<Review> mReviewData;

    public ArrayList<Review> getReviewData(){
        return mReviewData;
    }

    public void setReviewData(ArrayList<Review> newReviews){
        if(newReviews == null)
            mReviewData = new ArrayList<>();
        else
            mReviewData = newReviews;
        notifyDataSetChanged();
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
        Review selectedReview = mReviewData.get(position);
        holder.tvReviewAuthor.setText(selectedReview.getAuthor());
        holder.tvReviewContent.setText(selectedReview.getReviewContent());
    }

    @Override
    public int getItemCount() {
        if(mReviewData == null) return 0;
        return mReviewData.size();
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

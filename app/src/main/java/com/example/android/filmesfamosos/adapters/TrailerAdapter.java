package com.example.android.filmesfamosos.adapters;

import android.content.Context;
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
import com.example.android.filmesfamosos.model.Trailer;

import java.net.URI;
import java.util.ArrayList;

public class TrailerAdapter extends RecyclerView.Adapter<TrailerAdapter.TrailerAdapterViewHolder> {

    private static final String YOUTUBE_WATCH_PATH = "watch";
    private static final String YOUTUBE_VIDEO_PARAMETER = "v";
    private ArrayList<Trailer> mTrailerData;

    public TrailerAdapter(){
        mTrailerData = new ArrayList<>();
    }

    public void setTrailerData(ArrayList<Trailer> trailerData){
        if(trailerData == null)
            mTrailerData = new ArrayList<>();
        else
            mTrailerData = trailerData;
        notifyDataSetChanged();
    }

    public ArrayList<Trailer> getTrailerData(){
        return mTrailerData;
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
        Trailer selectedTrailer = mTrailerData.get(position);
//        Set Trailer Title to text view
        holder.mTrailerNameTextView.setText(selectedTrailer.getName());
    }

    @Override
    public int getItemCount() {
        if(mTrailerData == null) return 0;
        return mTrailerData.size();
    }

    public class TrailerAdapterViewHolder extends RecyclerView.ViewHolder{

        public final TextView mTrailerNameTextView;
        public Context mContext;

        public TrailerAdapterViewHolder(Context context, View itemView) {
            super(itemView);
            mTrailerNameTextView = itemView.findViewById(R.id.tv_trailer_miniature_name);
            mContext = context;
        }
    }
}

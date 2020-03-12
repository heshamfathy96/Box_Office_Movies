package com.esso1996gmail.hesham.popularmoviesstage1;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import model.Trailer;

public class RecyclerViewTrailersAdapter extends RecyclerView.Adapter<RecyclerViewTrailersAdapter.ViewHolder> {
    private final List<Trailer> mData;
    private final LayoutInflater mInflater;
    private final Context mcontext;
    private ItemClickListener mClickListener;

    // data is passed into the constructor
    public RecyclerViewTrailersAdapter(Context context, List<Trailer> data, ItemClickListener itemClickListener) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
        this.mcontext = context;
        this.mClickListener = itemClickListener;
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.recyclerview_trailer_items, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String name = mData.get(position).getName();
        String key = mData.get(position).getKey();
        holder.trailerTextView.setText(name);
        String ImagePath = mcontext.getString(R.string.base_image_youtube_url) + key
                + mcontext.getString(R.string.remain_image_youtube_url);
        Picasso.with(mcontext).load(ImagePath).into(holder.trailerImageView);
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return mData.size();
    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        final TextView trailerTextView;
        final ImageView trailerImageView;
        ViewHolder(View itemView) {
            super(itemView);
            trailerTextView = itemView.findViewById(R.id.trailer_name_tv);
            trailerImageView = itemView.findViewById(R.id.trailer_image_iv);
            itemView.setOnClickListener(this);
        }
        @Override
        public void onClick(View view) {
            if (mClickListener != null)
                mClickListener.onItemTrailerClick(view, getAdapterPosition());
        }
    }

    // convenience method for getting data at click position
    Trailer getItem(int id) {
        return mData.get(id);
    }

    // allows clicks events to be caught
    void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }
    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemTrailerClick(View view, int position);
    }
}

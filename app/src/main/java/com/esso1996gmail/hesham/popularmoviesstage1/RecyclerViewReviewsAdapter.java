package com.esso1996gmail.hesham.popularmoviesstage1;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import java.util.List;

import model.Review;

public class RecyclerViewReviewsAdapter extends RecyclerView.Adapter<RecyclerViewReviewsAdapter.ViewHolder> {
    private final List<Review> mData;
    private final LayoutInflater mInflater;
    private final Context mcontext;
    private ItemClickListener mClickListener;

    // data is passed into the constructor
    public RecyclerViewReviewsAdapter(Context context, List<Review> data, ItemClickListener itemClickListener) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
        this.mcontext = context;
        this.mClickListener = itemClickListener;
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.recyclerview_review_items, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String author = mData.get(position).getAuthor();
        String content = mData.get(position).getContent();
        String url = mData.get(position).getUrl();
        holder.authorTextView.setText(author);
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return mData.size();
    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        final TextView authorTextView;
        ViewHolder(View itemView) {
            super(itemView);
            authorTextView = itemView.findViewById(R.id.author_tv);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null)
                mClickListener.onItemReviewClick(view, getAdapterPosition());
        }
    }

    // convenience method for getting data at click position
    Review getItem(int id) {
        return mData.get(id);
    }

    // allows clicks events to be caught
    void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemReviewClick(View view, int position);
    }
}

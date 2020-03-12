package com.esso1996gmail.hesham.popularmoviesstage1;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.List;

import model.Movie;

public class GridViewAdapter extends ArrayAdapter<Movie> {

    private final List<Movie> movieList;
    private final Context mcontext;

    public GridViewAdapter(List<Movie> movieList, Context mcontext) {
        super(mcontext, R.layout.list_items, movieList);
        this.movieList = movieList;
        this.mcontext = mcontext;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(mcontext);
        View listViewItem = inflater.inflate(R.layout.list_items, null, true);
        ImageView ImageViewPosterImage = listViewItem.findViewById(R.id.poster_image);
        Movie movie = movieList.get(position);
        String ImagePath = mcontext.getString(R.string.base_image_url) + mcontext.getString(R.string.size_image_url)
                + movie.getPoster_path();
        Picasso.with(mcontext).load(ImagePath).into(ImageViewPosterImage);
        return listViewItem;
    }
}

package com.esso1996gmail.hesham.popularmoviesstage1;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;

import android.os.Build;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import java.util.List;

import model.Movie;
import model.Review;
import model.Trailer;
import utils.DatabaseUtils;
import utils.JsonUtils;
import utils.TLSSocketFactory;

public class DetailsActivity extends AppCompatActivity implements RecyclerViewTrailersAdapter.ItemClickListener,
        RecyclerViewReviewsAdapter.ItemClickListener {

    private RecyclerViewTrailersAdapter trailersAdapter;
    private RecyclerViewReviewsAdapter reviewsAdapter;
    private List<Trailer> trailerList;
    private List<Review> reviewList;
    private RecyclerView trailersRecyclerView;
    private RecyclerView reviewsRecyclerView;
    private final Context context = this;
    private Movie movie;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        setTitle(getString(R.string.details_activity));
        final Intent intentThatStartedThisActivity = getIntent();

        if ((intentThatStartedThisActivity != null) && (intentThatStartedThisActivity.hasExtra(getString(R.string.model_class_name)))) {
            movie = (Movie) intentThatStartedThisActivity.getSerializableExtra(getString(R.string.model_class_name));
            setupUI(movie);
            if (isConnected()) {
                setupTrailers(movie.getId());
                setupReviews(movie.getId());
            } else {
                LinearLayout linearLayout = findViewById(R.id.details_ll);
                Snackbar.make(linearLayout, getString(R.string.no_internet_connection_details), Snackbar.LENGTH_LONG)
                        .setAction(getString(R.string.reload_views), new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                finish();
                                startActivity(intentThatStartedThisActivity);
                            }
                        }).show();
            }
        }
        ToggleButton favouriteToggleButton = findViewById(R.id.favouriteToggleButton);
        if (DatabaseUtils.favouriteIds.contains(movie.getId()))
            favouriteToggleButton.setChecked(true);
        else
            favouriteToggleButton.setChecked(false);

        favouriteToggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    DatabaseUtils.insertToDatabase(movie, getApplicationContext());
                    DatabaseUtils.favouriteIds.add(movie.getId());
                } else {
                    DatabaseUtils.deleteFromDatabase(movie, getApplicationContext());
                    DatabaseUtils.favouriteIds.remove((Integer) movie.getId());
                }
            }
        });
    }

    private void setupTrailers(int id) {
        String URL = getString(R.string.trailer_url) + id + getString(R.string.remain_trailer_url) + MainActivity.API_Key;
        trailersRecyclerView = findViewById(R.id.trailers_rv);
        trailersRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        trailerList = JsonUtils.parseTrailersJson(response);
                        trailersAdapter = new RecyclerViewTrailersAdapter(context, trailerList, (RecyclerViewTrailersAdapter.ItemClickListener) context);
                        trailersRecyclerView.setAdapter(trailersAdapter);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

        try {
            HurlStack stack;
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP) {
                // Use a socket factory that removes sslv3 and add TLS1.2
                stack = new HurlStack(null, new TLSSocketFactory());
            } else {
                stack = new HurlStack();
            }
            //creating a request queue
            RequestQueue requestQueue = Volley.newRequestQueue(this, stack);
            //adding the string request to request queue
            requestQueue.add(stringRequest);
        } catch (Exception e) {
            Log.i("NetworkClient", "can no create custom socket factory");
        }
    }

    private void setupReviews(int id) {
        String URL = getString(R.string.trailer_url) + id + getString(R.string.remain_review_url) + MainActivity.API_Key;
        reviewsRecyclerView = findViewById(R.id.reviews_rv);
        reviewsRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        reviewList = JsonUtils.parseReviewsJson(response);
                        reviewsAdapter = new RecyclerViewReviewsAdapter(context, reviewList, (RecyclerViewReviewsAdapter.ItemClickListener) context);
                        reviewsRecyclerView.setAdapter(reviewsAdapter);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

        try {
            HurlStack stack;
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP) {
                // Use a socket factory that removes sslv3 and add TLS1.2
                stack = new HurlStack(null, new TLSSocketFactory());
            } else {
                stack = new HurlStack();
            }
            //creating a request queue
            RequestQueue requestQueue = Volley.newRequestQueue(this, stack);
            //adding the string request to request queue
            requestQueue.add(stringRequest);
        } catch (Exception e) {
            Log.i("NetworkClient", "can no create custom socket factory");
        }
    }

    @Override
    public void onItemTrailerClick(View view, int position) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW,
                Uri.parse(getString(R.string.watch_trailer_url) + trailerList.get(position).getKey()));
        startActivity(browserIntent);
    }

    @Override
    public void onItemReviewClick(View view, final int position) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle(getString(R.string.content_label)).setMessage(reviewList.get(position).getContent())
                .setNegativeButton(getString(R.string.cancel_label), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    }
                }).setPositiveButton(getString(R.string.reference_label), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(reviewList.get(position).getUrl()));
                startActivity(intent);
            }
        }).show();
    }

    private void setupUI(Movie movie) {
        String ImagePath = getString(R.string.base_image_url) + getString(R.string.size_image_url)
                + movie.getPoster_path();
        TextView original_title_tv = findViewById(R.id.original_title_tv);
        TextView plot_synopsis_tv = findViewById(R.id.plot_synopsis_tv);
        TextView user_rating_tv = findViewById(R.id.user_rating_tv);
        TextView release_date_tv = findViewById(R.id.release_date_tv);
        ImageView poster_image = findViewById(R.id.image_iv);

        original_title_tv.setText(movie.getOriginal_title());
        plot_synopsis_tv.setText(movie.getOverview());
        user_rating_tv.setText(String.valueOf(movie.getVote_average()));
        release_date_tv.setText(movie.getRelease_date());
        Picasso.with(this).load(ImagePath).into(poster_image);
    }

    private boolean isConnected() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }
}

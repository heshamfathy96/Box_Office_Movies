package com.esso1996gmail.hesham.popularmoviesstage1;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.ArrayList;
import java.util.List;

import database.MovieEntry;
import model.Movie;
import utils.DatabaseUtils;
import utils.JsonUtils;
import utils.TLSSocketFactory;

public class MainActivity extends AppCompatActivity {

    public static final String API_Key = BuildConfig.API_Key;
    private GridView gridView;
    private static List<Movie> movieList;
    private GridViewAdapter adapter;
    private static final String POPULAR_OPTION = "popular_option";
    private boolean popularOption;
    private static final String GRIDVIEW_INDEX = "gridview_index";
    private static int gridViewIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        gridView = findViewById(R.id.movie_gridView);
        movieList = new ArrayList<>();

        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(POPULAR_OPTION))
                popularOption = savedInstanceState.getBoolean(POPULAR_OPTION);
            if (savedInstanceState.containsKey(GRIDVIEW_INDEX))
                gridViewIndex = savedInstanceState.getInt(GRIDVIEW_INDEX, 0);
        }

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intentToStartDetailsActivity = new Intent(MainActivity.this, DetailsActivity.class);
                intentToStartDetailsActivity.putExtra(getString(R.string.model_class_name), movieList.get(position));
                startActivity(intentToStartDetailsActivity);
            }
        });
    }

    private void setupViewModel() {
        MainViewModel viewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        viewModel.getMovies().observe(this, new Observer<List<MovieEntry>>() {
            @Override
            public void onChanged(@Nullable List<MovieEntry> movieEntries) {
                movieList = DatabaseUtils.parseEntriesToMovies(movieEntries);
                adapter = new GridViewAdapter(movieList, getApplicationContext());
                gridView.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (DatabaseUtils.databaseOption) {
            setupViewModel();
            setTitle(getString(R.string.favourite_item));
        } else if (popularOption) {
            if (isOnline()) {
                loadMovies(getString(R.string.most_popular_url) + API_Key);
                setTitle(getString(R.string.most_popular_item));
            }
        } else {
            if (isOnline()) {
                loadMovies(getString(R.string.highest_rated_url) + API_Key);
                setTitle(getString(R.string.highest_rated_item));
            }
        }
    }

    @Override
    public void onResume() {
        gridView.setSelection(gridViewIndex);
        super.onResume();
    }

    @Override
    public void onPause() {
        gridViewIndex = gridView.getFirstVisiblePosition();
        super.onPause();
    }

    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putBoolean(POPULAR_OPTION, popularOption);
        savedInstanceState.putInt(GRIDVIEW_INDEX, gridViewIndex);
    }

    private void loadMovies(String URL) {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        movieList = JsonUtils.parseMoviesJson(response);
                        adapter = new GridViewAdapter(movieList, getApplicationContext());
                        gridView.setAdapter(adapter);
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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.settings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int sortOptionId = item.getItemId();
        if (sortOptionId == R.id.popular) {
            DatabaseUtils.databaseOption = false;
            popularOption = true;
            if (isOnline()) {
                loadMovies(getString(R.string.most_popular_url) + API_Key);
                setTitle(getString(R.string.most_popular_item));
            }
            return true;
        } else if (sortOptionId == R.id.highest_rated) {
            DatabaseUtils.databaseOption = false;
            popularOption = false;
            if (isOnline()) {
                loadMovies(getString(R.string.highest_rated_url) + API_Key);
                setTitle(getString(R.string.highest_rated_item));
            }
            return true;
        } else if (sortOptionId == R.id.favourite) {
            DatabaseUtils.databaseOption = true;
            popularOption = false;
            setTitle(getString(R.string.favourite_item));
            setupViewModel();
        } else if (sortOptionId == R.id.about) {
            new AlertDialog.Builder(this)
                    .setTitle(R.string.app_name)
                    .setMessage(R.string.about_content)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // You don't have to do anything here if you just
                            // want it dismissed when clicked
                        }
                    }).show();
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean isOnline() {
        boolean online;
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        online = netInfo != null && netInfo.isConnectedOrConnecting();
        if (online)
            return true;
        Snackbar.make(gridView, getString(R.string.no_internet_connection), Snackbar.LENGTH_INDEFINITE)
                .setAction(getString(R.string.reload_views), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                        startActivity(getIntent());
                    }
                }).show();
        return false;
    }
}

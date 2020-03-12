package utils;

import android.content.Context;
import com.esso1996gmail.hesham.popularmoviesstage1.AppExecutors;
import java.util.ArrayList;
import java.util.List;
import database.AppDatabase;
import database.MovieEntry;
import model.Movie;

public final class DatabaseUtils {
    public static List<Integer> favouriteIds = new ArrayList<>();
    public static boolean databaseOption=true;
    private static AppDatabase mDb;

    private DatabaseUtils() {}

    public static List<Movie> parseEntriesToMovies(List<MovieEntry> movieEntries) {
        List<Movie> movieList = new ArrayList<>();
        for(MovieEntry movieEntry : movieEntries) {
            Movie movie = new Movie();
            movie.setId(movieEntry.getId());
            movie.setPoster_path(movieEntry.getPoster_path());
            movie.setOriginal_title(movieEntry.getOriginal_title());
            movie.setOverview(movieEntry.getOverview());
            movie.setRelease_date(movieEntry.getRelease_date());
            movie.setVote_average(movieEntry.getVote_average());

            favouriteIds.add(movieEntry.getId());
            movieList.add(movie);
        }
        return movieList;
    }

    public static void insertToDatabase(Movie movie, Context context) {
        mDb = AppDatabase.getInstance(context);
        final MovieEntry movieEntry = new MovieEntry(movie.getId(),movie.getPoster_path(),movie.getOriginal_title(),
                movie.getOverview(),movie.getRelease_date(),movie.getVote_average());
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                mDb.movieDao().insertMovie(movieEntry);
            }
        });
    }

    public static void deleteFromDatabase(Movie movie, Context context) {
        mDb = AppDatabase.getInstance(context);
        final MovieEntry movieEntry = new MovieEntry(movie.getId(),movie.getPoster_path(),movie.getOriginal_title(),
                movie.getOverview(),movie.getRelease_date(),movie.getVote_average());
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                mDb.movieDao().deleteMovie(movieEntry);
            }
        });
    }
}

package utils;

import android.util.Log;

import org.json.*;

import java.util.ArrayList;
import java.util.List;

import model.Movie;
import model.Review;
import model.Trailer;

public final class JsonUtils {
    private JsonUtils() {}

    public static List<Movie> parseMoviesJson(String json) {
        final String MOVIES_RESULTS = "results";
        List<Movie> movies = new ArrayList<>();
        try {
            JSONObject movieDBJson = new JSONObject(json);
            JSONArray moviesArrayJson = movieDBJson.getJSONArray(MOVIES_RESULTS);
            List<JSONObject> moviesJsonObjects = parseJSONArrayToListOfObjects(moviesArrayJson);
            for (JSONObject movieJson : moviesJsonObjects)
                movies.add(parseJsonObjectToMovie(movieJson));
        } catch (JSONException e) {
            Log.e("JsonUtilsClass", e.getMessage(), e);
        }
        return movies;
    }

    public static List<Trailer> parseTrailersJson(String json){
        final String TRAILER_RESULTS = "results";
        List<Trailer> trailerList = new ArrayList<>();
        try {
            JSONObject trailerDBJson = new JSONObject(json);
            JSONArray trailerArrayJson = trailerDBJson.getJSONArray(TRAILER_RESULTS);
            List<JSONObject> trailerJsonObjects = parseJSONArrayToListOfObjects(trailerArrayJson);
            for (JSONObject trailerJson : trailerJsonObjects)
                trailerList.add(parseJsonObjectToTrailer(trailerJson));
        } catch (JSONException e) {
            Log.e("JsonUtilsClass", e.getMessage(), e);
        }
        return trailerList;
    }

    public static List<Review> parseReviewsJson(String json){
        final String REVIEW_RESULTS = "results";
        List<Review> reviewList = new ArrayList<>();
        try {
            JSONObject reviewDBJson = new JSONObject(json);
            JSONArray reviewArrayJson = reviewDBJson.getJSONArray(REVIEW_RESULTS);
            List<JSONObject> reviewJsonObjects = parseJSONArrayToListOfObjects(reviewArrayJson);
            for (JSONObject reviewJson : reviewJsonObjects)
                reviewList.add(parseJsonObjectToReview(reviewJson));
        } catch (JSONException e) {
            Log.e("JsonUtilsClass", e.getMessage(), e);
        }
        return reviewList;
    }

    private static Movie parseJsonObjectToMovie(JSONObject jsonObject) {
        final String VOTE_COUNT = "vote_count";
        final String ID = "id";
        final String VIDEO = "video";
        final String VOTE_AVERAGE = "vote_average";
        final String TITLE = "title";
        final String POPULARITY = "popularity";
        final String POSTER_PATH = "poster_path";
        final String ORIGINAL_LANGUAGE = "original_language";
        final String ORIGINAL_TITLE = "original_title";
        final String GENRE_IDS = "genre_ids";
        final String BACKDROP_PATH = "backdrop_path";
        final String ADULT = "adult";
        final String OVERVIEW = "overview";
        final String RELEASE_DATE = "release_date";
        Movie movie = new Movie();
        try {
            movie.setVote_count(jsonObject.getInt(VOTE_COUNT));
            movie.setId(jsonObject.getInt(ID));
            movie.setVideo(jsonObject.getBoolean(VIDEO));
            movie.setVote_average(jsonObject.getDouble(VOTE_AVERAGE));
            movie.setTitle(jsonObject.getString(TITLE));
            movie.setPopularity(jsonObject.getDouble(POPULARITY));
            movie.setPoster_path(jsonObject.getString(POSTER_PATH));
            movie.setOriginal_language(jsonObject.getString(ORIGINAL_LANGUAGE));
            movie.setOriginal_title(jsonObject.getString(ORIGINAL_TITLE));
            movie.setGenre_ids(parseJSONArrayToListOfIntegers(jsonObject.getJSONArray(GENRE_IDS)));
            movie.setBackdrop_path(jsonObject.getString(BACKDROP_PATH));
            movie.setAdult(jsonObject.getBoolean(ADULT));
            movie.setOverview(jsonObject.getString(OVERVIEW));
            movie.setRelease_date(jsonObject.getString(RELEASE_DATE));
        } catch (JSONException e) {
            Log.e("JsonUtilsClass", e.getMessage(), e);
        }
        return movie;
    }

    private static Trailer parseJsonObjectToTrailer(JSONObject jsonObject){
        final String KEY = "key";
        final String NAME = "name";
        Trailer trailer = new Trailer();
        try {
            trailer.setKey(jsonObject.getString(KEY));
            trailer.setName(jsonObject.getString(NAME));
        } catch (JSONException e) {
            Log.e("JsonUtilsClass", e.getMessage(), e);
        }
        return trailer;
    }

    private static Review parseJsonObjectToReview(JSONObject jsonObject){
        final String AUTHOR = "author";
        final String CONTENT = "content";
        final String URL = "url";
        Review review = new Review();
        try {
            review.setAuthor("written by "+jsonObject.getString(AUTHOR));
            review.setContent(jsonObject.getString(CONTENT));
            review.setUrl(jsonObject.getString(URL));
        } catch (JSONException e) {
            Log.e("JsonUtilsClass", e.getMessage(), e);
        }
        return review;
    }

    private static List<Integer> parseJSONArrayToListOfIntegers(JSONArray jsonArray) throws JSONException {
        List<Integer> listOfIntegers = new ArrayList<>();
        if (jsonArray != null)
            for (int i = 0; i < jsonArray.length(); i++)
                listOfIntegers.add(jsonArray.getInt(i));
        return listOfIntegers;
    }

    private static List<JSONObject> parseJSONArrayToListOfObjects(JSONArray jsonArray) throws JSONException {
        List<JSONObject> listOfObjects = new ArrayList<>();
        if (jsonArray != null)
            for (int i = 0; i < jsonArray.length(); i++)
                listOfObjects.add(jsonArray.getJSONObject(i));
        return listOfObjects;
    }

}

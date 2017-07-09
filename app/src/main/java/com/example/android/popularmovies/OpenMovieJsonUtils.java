package com.example.android.popularmovies;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Soham on 27-May-17.
 */

public class OpenMovieJsonUtils {

    public static String[] getMovieIdFromJson(String jsonData) throws JSONException {
        String ids[];
        JSONObject jsonObject = new JSONObject(jsonData);
        JSONArray idArray = jsonObject.getJSONArray("results");
        ids = new String[idArray.length()];
        for (int i = 0; i < idArray.length(); i++) {
            JSONObject jsonObject1 = idArray.getJSONObject(i);
            String idData = jsonObject1.getString("id");
            ids[i] = idData;
        }
        return ids;
    }

    public static String[] getTitleFromJson(String jsonData) throws JSONException {
        String titles[];
        JSONObject jsonObject = new JSONObject(jsonData);
        JSONArray movieArray = jsonObject.getJSONArray("results");
        titles = new String[movieArray.length()];
        for (int i = 0; i < movieArray.length(); i++) {
            JSONObject movieData = movieArray.getJSONObject(i);
            String titleData = movieData.getString("title");
            titles[i] = titleData;
        }
        return titles;
    }

    public static String[] getImageUrlsFromJson(String jsonData) throws JSONException {
        String URLs[];
        JSONObject jsonObject = new JSONObject(jsonData);
        JSONArray movieArray = jsonObject.getJSONArray("results");
        URLs = new String[movieArray.length()];
        for (int i = 0; i < movieArray.length(); i++) {
            JSONObject movieData = movieArray.getJSONObject(i);
            URLs[i] = movieData.getString("poster_path");
        }
        return URLs;
    }

    public static String[] getSynopsisFromJson(String jsonData) throws JSONException {
        String synopsis[];
        JSONObject jsonObject = new JSONObject(jsonData);
        JSONArray movieArray = jsonObject.getJSONArray("results");
        synopsis = new String[movieArray.length()];
        for (int i = 0; i < movieArray.length(); i++) {
            JSONObject movieData = movieArray.getJSONObject(i);
            synopsis[i] = movieData.getString("overview");
        }
        return synopsis;
    }

    public static String[] getUserRatingFromJson(String jsonData) throws JSONException {
        String userRating[];
        JSONObject jsonObject = new JSONObject(jsonData);
        JSONArray movieArray = jsonObject.getJSONArray("results");
        userRating = new String[movieArray.length()];
        for (int i = 0; i < movieArray.length(); i++) {
            JSONObject movieData = movieArray.getJSONObject(i);
            userRating[i] = movieData.getString("vote_average");
        }
        return userRating;
    }

    public static String[] getReleaseDateFromJson(String jsonData) throws JSONException {
        String releaseDate[];
        JSONObject jsonObject = new JSONObject(jsonData);
        JSONArray movieArray = jsonObject.getJSONArray("results");
        releaseDate = new String[movieArray.length()];
        for (int i = 0; i < movieArray.length(); i++) {
            JSONObject movieData = movieArray.getJSONObject(i);
            releaseDate[i] = movieData.getString("release_date");
        }
        return releaseDate;
    }

    public static String getTrailersFromJson(String jsonData) throws JSONException {
        String trailerKey = null;
        JSONObject jsonObject = new JSONObject(jsonData);
        JSONArray trailerArray = jsonObject.getJSONArray("results");
        for (int i = 0; i < trailerArray.length(); i++) {
            JSONObject trailerObject = trailerArray.getJSONObject(i);
            String isTrailer = trailerObject.getString("type");
            if (isTrailer.equals("Trailer")) {
                trailerKey = trailerObject.getString("key");
            }
        }
        return trailerKey;
    }

    public static String getRuntimeFromJson(String jsonData) throws JSONException{
        String runtime;
        JSONObject jsonObject = new JSONObject(jsonData);
        runtime = jsonObject.getString("runtime");
        return runtime;
    }
}
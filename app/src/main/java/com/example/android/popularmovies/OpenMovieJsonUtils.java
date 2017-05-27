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
    public static String[] getTitleFromJson(String jsonData)throws JSONException{
        String titles[];
        JSONObject jsonObject = new JSONObject(jsonData);
        JSONArray movieArray = jsonObject.getJSONArray("results");
        titles = new String[movieArray.length()];
        for(int i = 0; i < movieArray.length(); i++) {
            JSONObject movieData = movieArray.getJSONObject(i);
            String titleData = movieData.getString("title");
            titles[i] = titleData;
        }
        return titles;
    }
}

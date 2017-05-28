package com.example.android.popularmovies;

import android.net.Uri;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

/**
 * Created by Soham on 27-May-17.
 */

public class NetworkUtils {
    //TODO insert API Key below
    private final static String API_KEY = "1d551bb865d207218a13edf0a6f2f2ec";
    public final static String QUERY_PARAM = "api_key";
    public static String SORT_ORDER = "popular";
    public static String BASE_URL = "http://api.themoviedb.org/3/movie/"+NetworkUtils.SORT_ORDER;

    public static URL buildURL() {
        BASE_URL = "http://api.themoviedb.org/3/movie/"+NetworkUtils.SORT_ORDER;
        Uri builtUri = Uri.parse(NetworkUtils.BASE_URL).buildUpon()
                .appendQueryParameter(QUERY_PARAM, API_KEY)
                .build();
        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            Log.e("NetworksUtils", e.toString());
        }
        return url;
    }

    public static String getResponseFromServer(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }
}
//Source: Udacity ABND Earthquake app/
package com.example.android.worldcupnews;

import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public final class QueryUtils {

    /**
     * Tag for the log messages
     */
    private static final String LOG_TAG = QueryUtils.class.getSimpleName();

    private QueryUtils() {
    }

    public static List<WorldCup> fetchWorldCupData(String requestUrl) {
        // Create URL object
        URL url = createUrl( requestUrl );

        // Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest( url );
        } catch (IOException e) {
            Log.e( LOG_TAG, "Problem making the HTTP request.", e );
        }

        // Extract relevant fields from the JSON response and create a list of news' link.
        List<WorldCup> worldCup = extractFeatureFromJson( jsonResponse );

        // Return the list of news' link
        return worldCup;
    }

    /**
     * Returns new URL object from the given string URL.
     */
    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL( stringUrl );
        } catch (MalformedURLException e) {
            Log.e( LOG_TAG, "Problem building the URL ", e );
        }
        return url;
    }

    /**
     * Make an HTTP request to the given URL and return a String as the response.
     */
    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";


        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout( 10000 /* milliseconds */ );
            urlConnection.setConnectTimeout( 15000 /* milliseconds */ );
            urlConnection.setRequestMethod( "GET" );
            urlConnection.connect();


            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream( inputStream );
            } else {
                Log.e( LOG_TAG, "Error response code: " + urlConnection.getResponseCode() );
            }
        } catch (IOException e) {
            Log.e( LOG_TAG, "Problem retrieving the World cup news JSON results.", e );
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {

                inputStream.close();
            }
        }
        return jsonResponse;
    }

    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader( inputStream, Charset.forName( "UTF-8" ) );
            BufferedReader reader = new BufferedReader( inputStreamReader );
            String line = reader.readLine();
            while (line != null) {
                output.append( line );
                line = reader.readLine();
            }
        }
        return output.toString();
    }


    private static List<WorldCup> extractFeatureFromJson(String worldCupJSON) {


        if (TextUtils.isEmpty( worldCupJSON )) {
            return null;
        }


        List<WorldCup> worldCup = new ArrayList<>();

        try {

            JSONObject baseJsonResponse = new JSONObject( worldCupJSON );

            JSONObject worldCupResponse = baseJsonResponse.getJSONObject( "response" );

            JSONArray worldCupArray = worldCupResponse.getJSONArray( "results" );


            for (int i = 0; i < worldCupArray.length(); i++) {

                JSONObject currentWorldCup = worldCupArray.getJSONObject( i );


                String type = currentWorldCup.getString( "type" );


                String title = currentWorldCup.getString( "webTitle" );


                String date = currentWorldCup.getString( "webPublicationDate" );


                String url = currentWorldCup.getString( "webUrl" );


                WorldCup worldCupNews = new WorldCup( type, title, date, url );


                worldCup.add( worldCupNews );
            }

        } catch (JSONException e) {

            Log.e( "QueryUtils", "Problem parsing the earthquake JSON results", e );
        }

        return worldCup;
    }

}


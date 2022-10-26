package com.example.theguardiannews;


import static androidx.core.content.PackageManagerCompat.LOG_TAG;

import android.annotation.SuppressLint;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class Utilities {

    /**
     * This class is only meant to hold static variables and methods, which can be accessed
     * directly from the class name Utilities (and an object instance of Utilities is not needed).
     */
    private Utilities() {
    }

    /**
     * Query the Guardian dataset and return a list of {@link News} objects.
     */
    @SuppressLint("RestrictedApi")
    public static List<News> fetchNewsData(String requestUrl) {
        // Create URL object
        URL url = createUrl(requestUrl);

        // Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem making the HTTP request.", e);
        }

        // Extract relevant fields from the JSON response and create a list of {@link News}s
        List<News> news = extractNews(jsonResponse);

        // Return the list of {@link News}s
        return news;
    }

    /**
     * Returns new URL object from the given string URL.
     */
    @SuppressLint("RestrictedApi")
    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Problem building the URL ", e);
        }
        return url;
    }

    /**
     * Make an HTTP request to the given URL and return a String as the response.
     */
    @SuppressLint("RestrictedApi")
    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        // If the URL is null, then return early.
        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // If the request was successful (response code 200),
            // then read the input stream and parse the response.
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the news JSON results.", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                // Closing the input stream could throw an IOException, which is why
                // the makeHttpRequest(URL url) method signature specifies than an IOException
                // could be thrown.
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    /**
     * Convert the {@link InputStream} into a String which contains the
     * whole JSON response from the server.
     */
    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    /**
     * Return a list of {@link News} objects that has been built up from
     * parsing a JSON response.
     */
    public static ArrayList<News> extractNews(String newsJSON) {
        // If the JSON string is empty or null, then return early.
        if (TextUtils.isEmpty(newsJSON)) {
            return null;
        }

        // Create an empty ArrayList that we can start adding news to
        ArrayList<News> news = new ArrayList<News>();

        // Try to parse the JSON data. If there's a problem with the way the JSON
        // is formatted, a JSONException exception object will be thrown.
        // Catch the exception so the app doesn't crash, and print the error message to the logs.
        try {

            // build up a list of News objects with the corresponding data.
            JSONObject root = new JSONObject(newsJSON);

            JSONObject newsOject = root.getJSONObject("response");

            JSONArray newsArray = newsOject.getJSONArray("results");

            for (int i = 0; i < newsArray.length(); i++){

                JSONObject currentNews = newsArray.getJSONObject(i);
                // extracting title and truncating it to remove the author
                String title = currentNews.getString("webTitle");
                int index = title.indexOf("|");
                if (index != -1) {
                    title = title.substring(0, index - 1);
                }

                // extracting section name
                String section = currentNews.getString("sectionName");

                // sorting out the date and time
                String date = currentNews.getString("webPublicationDate");
                index = date.indexOf("Z");
                date = date.substring(0, index);
                date = timeConversion(date);

                // getting link to article
                String link = currentNews.getString("webUrl");

                // extracting the name of the author if there is one
                JSONArray tagsForName = currentNews.getJSONArray("tags");
                for (int j = 0; j < tagsForName.length(); j++) {
                    JSONObject currentNewsAuthor = tagsForName.getJSONObject(j);
                    String author = currentNewsAuthor.getString("webTitle");

                    // creating new NewsObject object
                    News item = new News(section, title, author, date, link);
                    news.add(item);
                }

            }
        } catch (JSONException e) {
            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message
            // with the message from the exception.
            Log.e("QueryUtils", "Problem parsing the earthquake JSON results", e);
        }

        // Return the list of news
        return news;
    }


    // method to convert Guardian's time stamp to format suitable for UI
    private static String timeConversion(String jsonTime) {
        long milliSeconds;
        @SuppressLint("SimpleDateFormat") SimpleDateFormat guardianTime = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        try {
            Date date = guardianTime.parse(jsonTime);
            milliSeconds = date.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
            return "";
        }
        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateConverter = new SimpleDateFormat("MMM d yyyy");
        @SuppressLint("SimpleDateFormat") SimpleDateFormat timeConverter = new SimpleDateFormat("h:mm a");
        String articleDate = dateConverter.format(milliSeconds);
        String articleTime = timeConverter.format(milliSeconds);
        return articleDate + "\n" + articleTime;
    }

}

package com.example.qlove.popularmovies;

import android.os.AsyncTask;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;


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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FetchMovieTask extends AsyncTask<String, Void, String> {
    private GridView gridView;
    private ImageAdapter imageAdapter;
    private Context mContext;
    private final String LOG_TAG = FetchMovieTask.class.getSimpleName();
    private List<MovieDataHolder> films = new ArrayList<MovieDataHolder>();

    public static interface itemClicked{
        void itemClicked();
    }


    public FetchMovieTask(Context mContext, GridView gridView, ImageAdapter imageAdapter) {
        this.imageAdapter = imageAdapter;
        this.mContext = mContext;
        this.gridView = gridView;
    }

    @Override
    protected String doInBackground(String... params) {
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String movieJsonStr = null;
        if (params.length == 0) {
            return null;
        }
        try {
            URL url = getUrl(params[0]);


            //Create request to TheMovieDB, and open connection
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            //Read the input stream into a String

            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = reader.readLine()) != null) {
                // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                // But it does make debugging a *lot* easier if you print out the completed
                // buffer for debugging.
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                // Stream was empty.  No point in parsing.
                return null;
            }
            movieJsonStr = buffer.toString();
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error " + e, e);
            return null;
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(LOG_TAG, "Error closing stream ", e);
                }
            }
        }
        Log.v(LOG_TAG, movieJsonStr);
        try {
            getDataFromJson(movieJsonStr);
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }


        Log.v(LOG_TAG, Arrays.toString(films.toArray()));
        return null;
    }

    @NonNull
    private URL getUrl(String sortingType) throws MalformedURLException {
        // Construct the URL for the OpenWeatherMap query
        // Possible parameters are avaiable at OWM's forecast API page, at
        // http://api.themoviedb.org/3/movie/popular?api_key=
        final String THEMOVIEDB_BASE_URL = "http://api.themoviedb.org/3/movie/" + sortingType;
        final String APPID_PARAM = "api_key";

        Uri builtUri = Uri.parse(THEMOVIEDB_BASE_URL).buildUpon()
                .appendQueryParameter(APPID_PARAM, BuildConfig.THE_MOVIE_DB_API_KEY)
                .build();

        return new URL(builtUri.toString());
    }


    @Override
    protected void onPostExecute(String s) {
        imageAdapter = new ImageAdapter(mContext, films);
        gridView.setAdapter(imageAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(mContext, DetailActivity.class);
                intent.putExtra("MovieDataHolder", films.get(position));
                mContext.startActivity(intent);
            }
        });
    }

    private void getDataFromJson(String movieJsonStr) throws JSONException {
        final String LINK_FOR_POSTER = "http://image.tmdb.org/t/p/w185";
        final String LINK_FOR_BACKDROP = "http://image.tmdb.org/t/p/w780";
        final String TMDB_RESULTS = "results";
        final String POSTER_PATH = "poster_path";
        final String TITLE = "title";
        final String OVERVIEW = "overview";
        final String RELEASE_DATE = "release_date";
        final String BACKDROP_PATH = "backdrop_path";
        final String RATING = "vote_average";
        final String ID = "id";
        String posterPath, title, overview, releaseDate, backdropPath, rating, id;

        JSONObject jsonObject = new JSONObject(movieJsonStr);
        JSONArray films = jsonObject.getJSONArray(TMDB_RESULTS);

        this.films.clear();

        for (int i = 0; i < films.length(); i++) {
            jsonObject = films.getJSONObject(i);
            posterPath = jsonObject.getString(POSTER_PATH);
            title = jsonObject.getString(TITLE);
            overview = jsonObject.getString(OVERVIEW);
            releaseDate = jsonObject.getString(RELEASE_DATE);
            backdropPath = jsonObject.getString(BACKDROP_PATH);
            rating = jsonObject.getString(RATING);
            id = jsonObject.getString(ID);

            this.films.add(new MovieDataHolder(releaseDate, overview, LINK_FOR_POSTER + posterPath, title, LINK_FOR_BACKDROP + backdropPath, rating, id));
        }
    }
}
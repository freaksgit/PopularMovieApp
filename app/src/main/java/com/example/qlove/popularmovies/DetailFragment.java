package com.example.qlove.popularmovies;

import android.app.Fragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

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

/**
 * Created by qlove on 08.04.2016.
 */
public class DetailFragment extends Fragment {
    private MovieDataHolder movieDataHolder;
    private TextView title, overview, runtime, rating, year;
    private CustomAdapter customAdapter;
    private ListView listView;

    public DetailFragment() {
    }
    @Override
    public void onStart() {
        super.onStart();
        refresh();
    }

    private void refresh() {
        new MoviesConnector().execute(movieDataHolder.getId());
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        movieDataHolder = (MovieDataHolder) getActivity().getIntent().getSerializableExtra("MovieDataHolder");
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

        Picasso.with(getActivity()).load(movieDataHolder.getPosterPath()).into((ImageView) rootView.findViewById(R.id.backdrop_imageview));
        title = (TextView) rootView.findViewById(R.id.detail_title_textview);
        overview = (TextView) rootView.findViewById(R.id.detail_overview_textview);
        runtime = (TextView) rootView.findViewById(R.id.detail_runtime_textview);
        rating = (TextView) rootView.findViewById(R.id.detail_rating_textview);
        year = (TextView) rootView.findViewById(R.id.detail_year_textview);
        listView = (ListView) rootView.findViewById(R.id.detail_trailers_listview);

        title.setText(movieDataHolder.getTitle());
        overview.setText(movieDataHolder.getOverview());
        runtime.setText("runtime");
        rating.setText(movieDataHolder.getRating() + "/10");
        year.setText(movieDataHolder.getDate().substring(0,4));

        return rootView;
    }




    class MoviesConnector extends AsyncTask<String, Void, String> {

        private final String LOG_TAG = MoviesConnector.class.getSimpleName();
        List<Trailer> trailers = new ArrayList<Trailer>();


        @Override
        protected String doInBackground(String... params) {
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String movieJsonStr = null;
            if (params.length == 0){
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
                if (inputStream == null){return null;}
                reader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while ((line = reader.readLine()) != null){
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0){
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                movieJsonStr = buffer.toString();
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error " + e, e);
                return null;
            }finally {
                if(urlConnection != null){
                    urlConnection.disconnect();
                }
                if(reader != null){
                    try{
                        reader.close();
                    }catch (final IOException e){
                        Log.e(LOG_TAG, "Error closing stream ", e);
                    }
                }
            }
            Log.v(LOG_TAG, movieJsonStr);
            try {
                getDataFromJson(movieJsonStr);
            }catch (JSONException e){
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();}


            Log.v(LOG_TAG, Arrays.toString(trailers.toArray()));
            return null;
        }

        @NonNull
        private URL getUrl(String id) throws MalformedURLException {
            // Construct the URL for the OpenWeatherMap query
            // Possible parameters are avaiable at OWM's forecast API page, at
            //http://api.themoviedb.org/3/movie/157336/videos?api_key=f56a57c7bfafd4cb229d41f39ea01f91
            // https://www.youtube.com/watch?v=SUXWAEX2jlg
            final String THEMOVIEDB_BASE_URL = "http://api.themoviedb.org/3/movie/" + id + "/videos?";
            final String APPID_PARAM = "api_key";

            Uri builtUri = Uri.parse(THEMOVIEDB_BASE_URL).buildUpon()
                    .appendQueryParameter(APPID_PARAM, BuildConfig.THE_MOVIE_DB_API_KEY)
                    .build();

            return new URL(builtUri.toString());
        }


        @Override
        protected void onPostExecute(String s) {
            customAdapter = new CustomAdapter(getActivity(), trailers);
            listView.setAdapter(customAdapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com/watch?v=" + trailers.get(position).getKey())));
                }
            });
        }

        private void getDataFromJson(String movieJsonStr) throws JSONException{

            final String TMDB_RESULTS = "results";

            final String KEY = "key";
            final String NAME = "name";
            String trailerURL, name;
            JSONObject jsonObject = new JSONObject(movieJsonStr);
            JSONArray films = jsonObject.getJSONArray(TMDB_RESULTS);

            this.trailers.clear();

            for (int i = 0; i < films.length(); i++) {
                jsonObject = films.getJSONObject(i);

                trailerURL = jsonObject.getString(KEY);
                name = jsonObject.getString(NAME);
                Log.e(LOG_TAG, trailerURL);
                this.trailers.add(new Trailer(trailerURL, name));
            }
        }
    }
}
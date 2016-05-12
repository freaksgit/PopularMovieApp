package com.example.qlove.popularmovies;

import android.util.Log;

import java.io.Serializable;

/**
 * Created by qlove on 07.04.2016.
 */
public class MovieDataHolder implements Serializable{
    private String posterPath;
    private String title;
    private String overview;
    private String date;
    private String backdropPath;
    private String rating;
    private String id;
    private String length;

    private final String LOG_TAG = MovieDataHolder.class.getSimpleName().toString();
    public MovieDataHolder(String date, String overview, String posterPath, String title, String backdropPath, String rating, String id) {
        this.date = date;
        this.overview = overview;
        this.posterPath = posterPath;
        this.title = title;
        this.backdropPath = backdropPath;
        this.rating = rating;
        this.id = id;

        Log.e(LOG_TAG,"date = " + date);
        Log.e(LOG_TAG,"overview = " + overview);
        Log.e(LOG_TAG,"posterPath = " + posterPath);
        Log.e(LOG_TAG,"title = " + title);
        Log.e(LOG_TAG,"backdropPath = " + backdropPath);
    }

    public String getDate() {
        return date;
    }

    public String getOverview() {
        return overview;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public String getTitle() {
        return title;
    }

    public String getBackdropPath() {
        return backdropPath;
    }

    public String getId() {
        return id;
    }


    public String getRating() {
        return rating;
    }

    public String getLength() {
        return length;
    }
}

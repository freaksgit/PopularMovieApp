package com.example.qlove.popularmovies;

/**
 * Created by qlove on 09.04.2016.
 */
public class Trailer {
    private String name;
    private String key;

    public Trailer(String key, String name) {
        this.key = key;
        this.name = name;
    }

    public String getKey() {
        return key;
    }

    public String getName() {
        return name;
    }
}

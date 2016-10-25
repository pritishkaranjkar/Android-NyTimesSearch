package com.example.kapritish.nytimessearch;

/**
 * Created by kapritish on 10/22/16.
 */
public class Multimedia {
    String url;
    int height;
    int width;

    public Multimedia(String url) {
        this.url = url;
    }

    public Multimedia(String url, int height, int width) {
        this.url = url;
        this.height = height;
        this.width = width;
    }
}
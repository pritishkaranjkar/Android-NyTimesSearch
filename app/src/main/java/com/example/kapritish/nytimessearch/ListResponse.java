package com.example.kapritish.nytimessearch;

/**
 * Created by kapritish on 10/22/16.
 */

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;

public class ListResponse {

    @SerializedName("response")
    SingleArticle singleArticle;

    public SingleArticle getSingleArticle() {
        return singleArticle;
    }

    public static ListResponse parseJSON(String response) {
        Gson gson = new GsonBuilder().create();
        ListResponse listResponse = gson.fromJson(response, ListResponse.class);
        return listResponse;
    }
}
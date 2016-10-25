package com.example.kapritish.nytimessearch;

/**
 * Created by kapritish on 10/22/16.
 */

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class SingleArticle {

    @SerializedName("docs")
    ArrayList<Article> articleList;

    public ArrayList<Article> getArticleList() {
        return articleList;
    }

    // public constructor is necessary for collections
    public SingleArticle() {
        articleList = new ArrayList<Article>();
    }
}

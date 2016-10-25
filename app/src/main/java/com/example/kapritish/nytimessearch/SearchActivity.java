package com.example.kapritish.nytimessearch;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cz.msebera.android.httpclient.Header;

public class SearchActivity extends AppCompatActivity {



        private final String API_KEY = "9d3465e84fd04a0e934cfa17c51770b4";
        private final String BASE_URL = "http://api.nytimes.com/svc/search/v2/articlesearch.json";
        private int pageCount = 0;
        private final int SETTINGS_RESULT = 10;
        List<Article> articleList;
        RecyclerView rvArticles;
        RecyclerView.Adapter articlesAdapter;
        MenuItem miActionProgressItem;

        private String searchQuery;
        private String beginDate;
        private String sortString;
        private ArrayList<String> newsList;
        HashMap<String, Object> extraParams;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            newsList = new ArrayList<>();
            extraParams  = new HashMap<>();
           /*if (AppStatus.getInstance(this).isOnline()) {

                Toast.makeText(this,"You are online!!!!",8000).show();

            } else {

                Toast.makeText(this,"You are not online!!!!",8000).show();
                Log.v("Home", "############################You are not online!!!!");
            }*/
            if(isOnline()){
                Toast.makeText(this,"You are online!!!!",8000).show();

            } else {

                Toast.makeText(this,"You are not online!!!!",8000).show();

            }

            setContentView(R.layout.activity_search);
            rvArticles = (RecyclerView) findViewById(R.id.rvArticles);
            articleList = new ArrayList<Article>();
            articlesAdapter = new ArticlesAdapter(this, articleList);
            rvArticles.setAdapter(articlesAdapter);
            GridLayoutManager layoutManager = new GridLayoutManager(this, 3);
            rvArticles.setLayoutManager(layoutManager);
            rvArticles.addOnScrollListener(new EndlessRecyclerViewScrollListener(layoutManager) {
                @Override
                public void onLoadMore(int page, int totalItemsCount) {
                    // Triggered only when new data needs to be appended to the list
                    // Add whatever code is needed to append new items to the bottom of the list
                    fetchMore();
                }
            });
        }

        @Override
        public boolean onCreateOptionsMenu(Menu menu) {
            // Inflate the menu; this adds items to the action bar if it is present.
            getMenuInflater().inflate(R.menu.menu_main, menu);
            MenuItem searchItem = menu.findItem(R.id.action_search);
            final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);

            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    pageCount = 0;
                    searchQuery = query;
                    // perform query here
                    fetchArticlesWithSearchQuery(query);

                    // workaround to avoid issues with some emulators and keyboard devices firing twice if a keyboard enter is used
                    // see https://code.google.com/p/android/issues/detail?id=24599
                    searchView.clearFocus();

                    return true;

                }

                @Override
                public boolean onQueryTextChange(String newText) {

                    return false;
                }
            });

            // Store instance of the menu item containing progress
            miActionProgressItem = menu.findItem(R.id.miActionProgress);
            // Extract the action-view from the menu item
            ProgressBar v =  (ProgressBar) MenuItemCompat.getActionView(miActionProgressItem);

            return super.onCreateOptionsMenu(menu);
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            // Handle action bar item clicks here. The action bar will
            // automatically handle clicks on the Home/Up button, so long
            // as you specify a parent activity in AndroidManifest.xml.

            if (item.getItemId() == R.id.action_settings) {
                openSettings();
            }
            return super.onOptionsItemSelected(item);
        }

        // Networking
        private void fetchArticlesWithSearchQuery(String query) {
            AsyncHttpClient client = new AsyncHttpClient();
            String url = BASE_URL;
            RequestParams params = new RequestParams();
            params.put("q", query);
            params.put("page", pageCount);
            params.put("api-key", API_KEY);
            if (extraParams != null)  {
                for (Map.Entry<String, Object> entry : extraParams.entrySet()) {
                    String key = entry.getKey();
                    Object object = entry.getValue();
                    params.put(key, object);
                }
            }
            client.get(url, params, new TextHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, String response) {
                    // Root JSON in response is an dictionary i.e { "data : [ ... ] }
                    // Handle resulting parsed JSON response here
                    Gson gson = new GsonBuilder().create();
                    if (pageCount == 0) {
                        articleList.clear();
                    }
                    articleList.addAll(ListResponse.parseJSON(response).singleArticle.getArticleList());
                    articlesAdapter.notifyDataSetChanged();
                }

                @Override
//            public void onFailure(int statusCode, Header[] headers, Throwable t, JSONObject object) {
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable t) {
//                public void onFailure(int statusCode, Header[] headers, String failure, Throwable t) {
                    // called when response HTTP status is "4XX" (eg. 401, 403, 404)
//                System.out.println("failure: " + failure);

                }
            });
        }

        public Article getArticleAtPosition(int position) {
            return articleList.get(position);
        }

        public void openSettings() {
            Intent i = new Intent(this, FilterActivity.class);
            startActivityForResult(i, SETTINGS_RESULT);
        }

        public void onActivityResult(int requestCode, int resultCode, Intent data) {
            super.onActivityResult(requestCode, resultCode, data);
            if (resultCode == Activity.RESULT_OK) {

                if (data.getStringExtra("beginDate") != null) {
                    beginDate = data.getStringExtra("beginDate");
                }
                if (data.getStringExtra("sort") != null) {
                    sortString = data.getStringExtra("sort");
                }
                if (beginDate != null) {
                    extraParams.put("begin_date", beginDate);
                }
                if (sortString != null) {
                    extraParams.put("sort", sortString);
                }

                String[] newsArray = data.getStringArrayExtra("news");
                for (String newsString : newsArray) {
                    if (!newsList.contains(newsString)) {
                        newsList.add(newsString);
                    }
                }

                if (newsList.size() > 0) {
                    String filters = android.text.TextUtils.join(" ", newsList);
                    String newsString = "news_desk:(" + filters + ")";
                    extraParams.put("fq", newsString);
                }
                fetchArticlesWithSearchQuery(searchQuery);
            }
        }

    private Boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }


    public boolean isOnline() {
        Runtime runtime = Runtime.getRuntime();
        try {
            Process ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8");
            int     exitValue = ipProcess.waitFor();
            return (exitValue == 0);
        } catch (IOException e)          { e.printStackTrace(); }
        catch (InterruptedException e) { e.printStackTrace(); }
        return false;
    }


        public void fetchMore() {
            pageCount++;
            fetchArticlesWithSearchQuery(searchQuery);
        }
    }
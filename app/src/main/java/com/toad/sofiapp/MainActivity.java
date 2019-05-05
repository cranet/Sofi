package com.toad.sofiapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity implements OnListInteractionListener {

    private OkHttpClient httpClient;
    private OnListInteractionListener listener;
    private EndlessRecyclerViewScrollListener scrollListener;
    private String mquery;
    private int page;
    private ArrayList<ImgurImage> images = new ArrayList<>();
    private MainAdapter mainAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listener = this;

        SearchView sv = findViewById(R.id.search);
        sv.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                scrollListener.resetState();
                images.clear();
                Log.d("MAIN ACTIVITY", query);
                mquery = query;
                fetchData();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        RecyclerView rv = findViewById(R.id.rv_of_photos);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rv.setLayoutManager(linearLayoutManager);
        scrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                loadNextDataFromApi();
            }
        };

        rv.addOnScrollListener(scrollListener);
        mainAdapter = new MainAdapter(this, images, listener);
        rv.setAdapter(mainAdapter);

        //todo remove
        mquery = "cat";
        fetchData();
    }

    private void fetchData() {
        if (mquery == null) {
            return;
        }
        httpClient = new OkHttpClient.Builder().build();
        Request request = new Request.Builder()
                .url("https://api.imgur.com/3/gallery/search/time/" + page + "?q=" + mquery)
                .header("Authorization", "Client-ID 126701cd8332f32")
                .build();
        page++;
        final ProgressBar progressBar = findViewById(R.id.progress_circular);
        progressBar.setVisibility(View.VISIBLE);

        httpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.e("MAIN ACTIVITY", "An error has occurred " + e);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {

                if (response.body() != null) {
                    Type listType = new TypeToken<ArrayList<ImgurImage>>() {
                    }.getType();
                    GsonBuilder gsonBuilder = new GsonBuilder();
                    gsonBuilder.registerTypeAdapter(listType, new ImgurDeserializer());
                    Gson gson = gsonBuilder.create();

                    ArrayList<ImgurImage> a = gson.fromJson(response.body().string(), listType);
                    images.addAll(a);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mainAdapter.notifyItemRangeInserted(mainAdapter.getItemCount(), images.size() - 1);
                            progressBar.setVisibility(View.GONE);
                        }
                    });
                }
            }
        });
    }

    @Override
    public void onListInteraction(ImgurImage image) {
        Log.d("MAIN ACTIVITY", "list interaction");

        Intent intent = new Intent(this, ImageActivity.class);
        intent.putExtra("test", image);
        startActivity(intent);

    }

    private void loadNextDataFromApi() {
        Log.d("MAIN ACTIVITY", " loading more data: " + page);
        fetchData();
    }

}

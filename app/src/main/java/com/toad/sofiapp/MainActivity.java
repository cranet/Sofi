package com.toad.sofiapp;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements OnListInteractionListener {

    private static final String TAG = "MAIN_ACTIVITY";
    private EndlessRecyclerViewScrollListener scrollListener;
    private String mQuery;
    private int page;
    private ArrayList<ImgurImage> images = new ArrayList<>();
    private MainAdapter mainAdapter;
    Network network = new Network();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        OnListInteractionListener listener = this;

        SearchView sv = findViewById(R.id.search);
        sv.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                if (!query.isEmpty()) {
                    images.clear();
                    mainAdapter.notifyDataSetChanged();
                    scrollListener.resetState();

                    Log.d(TAG, query);
                    mQuery = query;
                    networkCall();
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        RecyclerView rv = findViewById(R.id.rv_images);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rv.setLayoutManager(linearLayoutManager);
        scrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                Log.d(TAG, " loading more data: " + page);
                networkCall();
            }
        };

        rv.addOnScrollListener(scrollListener);
        mainAdapter = new MainAdapter(this, images, listener);
        rv.setAdapter(mainAdapter);

    }

    private void networkCall() {
        final ProgressBar progressBar = findViewById(R.id.progress_circular);
        progressBar.setVisibility(View.VISIBLE);
        if (mQuery == null) {
            return;
        }
        network.getGallery(page, mQuery, this, new Network.RequestListener<JsonElement>() {
            @Override
            public void onSuccess(JsonElement response) {

                Type listType = new TypeToken<ArrayList<ImgurImage>>() {
                }.getType();
                GsonBuilder gsonBuilder = new GsonBuilder();
                gsonBuilder.registerTypeAdapter(listType, new ImgurDeserializer());
                Gson gson = gsonBuilder.create();

                ArrayList<ImgurImage> a = gson.fromJson(response, listType);
                images.addAll(a);

                runOnUiThread(() -> {
                    mainAdapter.notifyItemRangeInserted(mainAdapter.getItemCount(), images.size() - 1);
                    progressBar.setVisibility(View.GONE);
                    page++;
                });
            }

            @Override
            public void onResponse() {

            }

            @Override
            public void onError() {
                Log.e(TAG, "Error loading data from Imgur");
            }
        });
    }

    @Override
    public void onListInteraction(ImgurImage image) {
        Log.d(TAG, "list interaction");

        Intent intent = new Intent(this, ImageActivity.class);
        intent.putExtra("image", image);
        startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());

    }

}

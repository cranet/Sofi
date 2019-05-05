package com.toad.sofiapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
    private List<ImgurImage> images = new ArrayList<>();
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
                Log.d("MAINACTIVITY", query);
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
            public void onFailure(Call call, IOException e) {
                Log.e("MAIN ACTIVITY", "An error has occurred " + e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                try {
                    JSONObject data = new JSONObject(response.body().string());
                    JSONArray items = data.getJSONArray("data");

                    for (int i = 0; i < items.length(); i++) {
                        JSONObject item = items.getJSONObject(i);
                        ImgurImage image = new ImgurImage();
                        if (item.getBoolean("is_album")) {
                            image.id = item.getString("cover");
                        } else {
                            image.id = item.getString("id");
                        }
                        image.title = item.getString("title");

                        images.add(image); // Add photo to list
                    }

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mainAdapter.notifyItemRangeInserted(mainAdapter.getItemCount(), images.size() - 1);
                            progressBar.setVisibility(View.GONE);
                        }
                    });

                } catch (Exception e) {

                }
            }
        });
    }

    @Override
    public void onListInteraction(ImgurImage image) {
        Log.d("testy", "list interaction");

        Intent intent = new Intent(this, ImageActivity.class);
        intent.putExtra("test", (Parcelable) image);
        startActivity(intent);

    }

    private void loadNextDataFromApi() {
        Log.d("MAINACTIVITY", " loading more data: " + page);
        fetchData();
    }

}

package com.toad.sofiapp;

import android.content.Context;

import androidx.annotation.NonNull;

import com.google.gson.JsonElement;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Path;
import retrofit2.http.Query;

class Network {

    private final ImgurService imgurService;

    Network() {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.imgur.com/3/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        imgurService = retrofit.create(ImgurService.class);
    }

    void getGallery(int page, String query, Context context, RequestListener<JsonElement> listener) {
        imgurService.getGallery(page, query).enqueue(new
                NetworkCallback<>(context, listener));
    }

    public interface ImgurService {
        @Headers("Authorization: Client-ID 126701cd8332f32")
        @GET("gallery/search/time/{page}")
        Call<JsonElement> getGallery(@Path("page") int page, @Query("q") String query);

    }

    public interface RequestListener<T> {
        void onSuccess(T response);

        void onResponse();

        void onError();
    }

    public class NetworkCallback<T> implements Callback<T> {
        RequestListener<T> listener;
        Context context;

        NetworkCallback(Context context, RequestListener<T> listener) {
            this.listener = listener;
            this.context = context;
        }

        @Override
        public void onResponse(@NonNull Call<T> call, @NonNull Response<T> response) {

            listener.onResponse();
            listener.onSuccess(response.body());

        }

        @Override
        public void onFailure(@NonNull Call<T> call, @NonNull Throwable t) {
            listener.onResponse();
            listener.onError();
        }
    }
}

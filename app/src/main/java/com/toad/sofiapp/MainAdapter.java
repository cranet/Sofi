package com.toad.sofiapp;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link ImgurImage} and makes a call to the
 * specified {@link OnListInteractionListener}.
 */
public class MainAdapter extends RecyclerView.Adapter<MainAdapter.ViewHolder> {

    private final List<ImgurImage> listImages;
    private final OnListInteractionListener listener;
    private Context context;

    MainAdapter(Context context, List<ImgurImage> items, OnListInteractionListener listener) {
        this.context = context;
        listImages = items;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {

        holder.image = listImages.get(position);
        holder.tvTitle = holder.mView.findViewById(R.id.item_title);
        holder.ivImage = holder.mView.findViewById(R.id.item_image);

        holder.mView.setOnClickListener(v -> {
            if (null != listener) {
                //notify selection
                listener.onListInteraction(holder.image);
            }
        });

        /*
        load into layout
        try catch to prevent crash on image too large exception
         */
        try {
            Picasso
                    .with(context)
                    .load("https://i.imgur.com/" + listImages.get(position).id + ".jpg")
                    .into(holder.ivImage);
            holder.tvTitle.setText(listImages.get(position).title);
        }catch (Exception e) {
            Log.e("Error loading", e.getMessage());
        }
    }

    @Override
    public int getItemCount() {
        return listImages.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        final View mView;
        ImgurImage image;
        ImageView ivImage;
        TextView tvTitle;

        ViewHolder(View view) {
            super(view);
            mView = view;

        }
    }
}

package com.toad.sofiapp;

import android.content.Context;
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
        holder.title = holder.mView.findViewById(R.id.title);
        holder.photo = holder.mView.findViewById(R.id.photo);

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != listener) {
                    //notify selection
                    listener.onListInteraction(holder.image);
                }
            }
        });

        //load into layout
        Picasso
                .with(context)
                .load("https://i.imgur.com/" + listImages.get(position).id + ".jpg")
                .into(holder.photo);
        holder.title.setText(listImages.get(position).title);
    }

    @Override
    public int getItemCount() {
        return listImages.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        final View mView;
        ImgurImage image;
        ImageView photo;
        TextView title;

        ViewHolder(View view) {
            super(view);
            mView = view;

        }
    }
}

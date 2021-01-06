package com.example.fal;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class PhotoAdapter extends RecyclerView.Adapter<PhotoAdapter.ViewHolder> {
    private ArrayList<Photo> myPhotos;
    private Context mycontext;
    public OnImageClick onImageClick;

    public void ItemClick(OnImageClick onImageClick) {
        this.onImageClick = onImageClick;
    }

    public PhotoAdapter(Context context, ArrayList<Photo> photos){
        mycontext = context;
        myPhotos = photos;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_layout,parent,false);
//
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
//        holder.itemView.setTag(position);
        String url = myPhotos.get(position).getUrl();
        Glide.with(mycontext)
                .load(url)
                .skipMemoryCache(true)
                .into(holder.imageView);


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onImageClick.onImageClick(myPhotos.get(position).getUrl());
            }
        });



    }

    @Override
    public int getItemCount() {
        return myPhotos.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.image);
        }
    }

    interface OnImageClick {
        void onImageClick(String url);
    }

}

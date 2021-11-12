package com.example.somcoco.searchcampus;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.somcoco.R;

import java.util.ArrayList;

public class CampusCarouselAdapter extends RecyclerView.Adapter<CampusCarouselAdapter.ViewHolder> {

    Context context;
    ArrayList<CampusCarouselItem> items = new ArrayList<CampusCarouselItem>();
    OnItemClickListener listener;

    public static interface OnItemClickListener {
        public void onItemClick(ViewHolder holder, View view, int position);
    }

    public CampusCarouselAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemView = inflater.inflate(R.layout.campus_carousel_item, parent, false);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull CampusCarouselAdapter.ViewHolder holder, int position) {
        CampusCarouselItem item = items.get(position);

        Glide.with(holder.itemView)
                .load(item.getCarouselImg())
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .centerCrop()
                .into(holder.campus_carousel);

        holder.setOnItemClickListener(listener);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void addItem(CampusCarouselItem item) {
        items.add(item);
    }

    public void addItems(ArrayList<CampusCarouselItem> items) {
        this.items = items;
    }

    public CampusCarouselItem getItem(int position) {
        return items.get(position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView campus_carousel;

        OnItemClickListener listener;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            campus_carousel = (ImageView) itemView.findViewById(R.id.campus_carousel_item);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();

                    if (listener != null) {
                        listener.onItemClick(ViewHolder.this, view, position);
                    }
                }
            });
        }
        public void setOnItemClickListener(OnItemClickListener listener){
            this.listener = listener;
        }
    }
}

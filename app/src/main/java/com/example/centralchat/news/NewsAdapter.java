package com.example.centralchat.news;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.centralchat.R;

import java.util.List;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.Viewholder> {

    private final List<NewsList> newsLists;
    private final Context context;

    public NewsAdapter(List<NewsList> newsLists, Context context) {
        this.newsLists = newsLists;
        this.context = context;
    }

    @NonNull
    @Override
    public NewsAdapter.Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Viewholder(LayoutInflater.from(parent.getContext()).inflate(R.layout.news_adapter_layout, null));
    }

    @Override
    public void onBindViewHolder(@NonNull NewsAdapter.Viewholder holder, int position) {
        NewsList list2 = newsLists.get(position);

        if (!list2.getNewsImage().isEmpty()) {
            Glide.with(context).load(list2.getNewsImage()).into(holder.newsImage);
        }
        holder.newsContent.setText(list2.getNewsBody());
        holder.newsDate.setText(list2.getNewsDate());

        holder.contentHolder.setOnClickListener(v -> goLink(list2.getNewsLink()));
    }

    @Override
    public int getItemCount() {
        return newsLists.size();
    }

    static class Viewholder extends RecyclerView.ViewHolder {

        private final ImageView newsImage;
        private final TextView newsContent;
        private final TextView newsDate;
        private final LinearLayout contentHolder;

        public Viewholder(@NonNull View itemView) {
            super(itemView);
            newsImage = itemView.findViewById(R.id.news_image);
            newsContent = itemView.findViewById(R.id.news_content);
            newsDate = itemView.findViewById(R.id.news_time);
            contentHolder = itemView.findViewById(R.id.contentHolder);
        }
    }

    public void goLink(String s) {
        Uri uri = Uri.parse(s);
        Intent intent=new Intent(Intent.ACTION_VIEW);
        intent.setData(uri);
        context.startActivity(intent);

    }
}

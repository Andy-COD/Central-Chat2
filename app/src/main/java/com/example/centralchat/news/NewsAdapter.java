package com.example.centralchat.news;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.centralchat.R;
import com.squareup.picasso.Picasso;

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
            Picasso.Builder builder = new Picasso.Builder(context);
            builder.listener((picasso, uri, exception) -> exception.printStackTrace());
            builder.build().load(list2.getNewsImage()).into(holder.newsImage);
        }
        holder.newsContent.setText(list2.getNewsBody());
        holder.newsDate.setText(list2.getNewsDate());
    }

    @Override
    public int getItemCount() {
        return newsLists.size();
    }

    static class Viewholder extends RecyclerView.ViewHolder {

        private final ImageView newsImage;
        private final TextView newsContent;
        private final TextView newsDate;

        public Viewholder(@NonNull View itemView) {
            super(itemView);
            newsImage = itemView.findViewById(R.id.news_image);
            newsContent = itemView.findViewById(R.id.news_content);
            newsDate = itemView.findViewById(R.id.news_time);
        }
    }
}

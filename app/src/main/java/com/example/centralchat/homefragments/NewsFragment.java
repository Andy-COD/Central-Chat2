package com.example.centralchat.homefragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.centralchat.R;
import com.example.centralchat.news.NewsAdapter;
import com.example.centralchat.news.NewsList;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class NewsFragment extends Fragment {
    private final List<NewsList> newsLists = new ArrayList<>();
    View view;
    ImageView newsImage;
    TextView content, date;
    RecyclerView newsRecyclerView;
    DatabaseReference dbReference;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
         view  = inflater.inflate(R.layout.fragment_news, container, false);

         Fresco.initialize(requireActivity().getApplicationContext());

         newsImage = view.findViewById(R.id.news_image);
         content = view.findViewById(R.id.news_content);
         date = view.findViewById(R.id.news_time);
         newsRecyclerView = view.findViewById(R.id.news_recycler_view);
         dbReference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://central-chat-5d62e-default-rtdb.firebaseio.com/");

         newsRecyclerView.setHasFixedSize(true);
         newsRecyclerView.setLayoutManager(new LinearLayoutManager(requireActivity().getApplicationContext()));

        dbReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                newsLists.clear();

                for(DataSnapshot dataSnapshot : snapshot.child("news").getChildren()) {
                    final String getNewsBody = dataSnapshot.child("newsBody").getValue(String.class);
                    final String getNewsImage = dataSnapshot.child("newsImage").getValue(String.class);
                    final String getNewsDate = dataSnapshot.child("newsDate").getValue(String.class);

                    NewsList newsList = new NewsList(getNewsBody, getNewsDate, getNewsImage);
                    newsLists.add(newsList);
                }
                newsRecyclerView.setAdapter(new NewsAdapter(newsLists, getActivity()));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return view;
    }
}
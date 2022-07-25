package com.example.centralchat.homefragments;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.centralchat.MemoryData;
import com.example.centralchat.R;
import com.example.centralchat.messages.MessagesAdapter;
import com.example.centralchat.messages.MessagesList;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatFragment extends Fragment {

    View view;
    private String indexNum;
    RecyclerView messagesRecyclerView;
    DatabaseReference dbReference;
    private final List<MessagesList> messagesLists = new ArrayList<>();
    private int unseenMessages = 0;
    private String lastMessage = "";
    private MessagesAdapter messagesAdapter;
    private String chatKey = "";
    private boolean dataSet = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_chat, container, false);

        messagesRecyclerView = view.findViewById(R.id.messagesRecyclerView);
        CircleImageView profileImg = view.findViewById(R.id.userProfilePicture);

        //get arguments from bundle
        assert getArguments() != null;
        indexNum = getArguments().getString("indexNum");

        messagesRecyclerView.setHasFixedSize(true);
        messagesRecyclerView.setLayoutManager(new LinearLayoutManager(requireActivity()));

        //set adapter to messages recyclerview
        messagesAdapter = new MessagesAdapter(messagesLists, requireActivity());
        messagesRecyclerView.setAdapter(messagesAdapter);

        dbReference = FirebaseDatabase.getInstance()
                .getReferenceFromUrl("https://central-chat-5d62e-default-rtdb.firebaseio.com/");

        ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Loading...");
        progressDialog.show();

        //get profile pic from firebase
        dbReference.child("users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                final String profilePic = snapshot.child(indexNum)
                        .child("profile picture").getValue(String.class);

                assert profilePic != null;
                if (profilePic.equals("default")) {
                    Glide.with(requireActivity()).load(R.drawable.user_icon).into(profileImg);
                }else {
                    Glide.with(requireActivity()).load(profilePic).into(profileImg);
                }
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressDialog.dismiss();
            }
        });

        dbReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                messagesLists.clear();
                lastMessage = "";
                unseenMessages = 0;
                chatKey = "";

                for (DataSnapshot dataSnapshot : snapshot.child("users").getChildren()) {

                    dataSet = false;

                    final String getOtherIndexNum = dataSnapshot.getKey();
                    assert getOtherIndexNum != null;
                    //check if the index numbers being received do not match what's stored in memory
                    if(!getOtherIndexNum.equals(indexNum)) {
                        final String getUserName = dataSnapshot.child("username").getValue(String.class);
                        final String getProfilePic = dataSnapshot.child("profile picture").getValue(String.class);


                        dbReference.child("chat").addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                int getChatCount = (int) snapshot.getChildrenCount();

                                if(getChatCount > 0) {
                                    for (DataSnapshot dataSnapshot1 : snapshot.getChildren()) {
                                        final String getKey = dataSnapshot1.getKey();
                                        chatKey = getKey;

                                        if(dataSnapshot1.hasChild("user_1") && dataSnapshot1.hasChild("user_2") &&
                                                dataSnapshot1.hasChild("messages")) {
                                            final String getUserOne = dataSnapshot1.child("user_1").getValue(String.class);
                                            final String getUserTwo = dataSnapshot1.child("user_2").getValue(String.class);

                                            assert getUserOne != null;
                                            assert getUserTwo != null;


                                            if (getUserOne.equals(getOtherIndexNum) && getUserTwo.equals(indexNum) ||
                                                    getUserOne.equals(indexNum) && getUserTwo.equals(getOtherIndexNum)) {
                                                for (DataSnapshot chatDataSnapshot : dataSnapshot1.child("messages").getChildren()) {
                                                    final long getMessageKey = Long.parseLong(Objects.requireNonNull(chatDataSnapshot.getKey()));
                                                    final long getLastSeenMessage = Long.parseLong(MemoryData.getLastMsgTS(requireActivity(), getKey));

                                                    lastMessage = chatDataSnapshot.child("msg").getValue(String.class);
                                                    //TODO: Fix unseenMessages count
                                                    if (getMessageKey > getLastSeenMessage) {
                                                        unseenMessages++;
                                                    }else {
                                                        unseenMessages = 0;
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                                if (!dataSet) {
                                    dataSet = true;
                                    MessagesList messagesList = new MessagesList(getUserName, getOtherIndexNum, lastMessage, getProfilePic, unseenMessages, chatKey);
                                    messagesLists.add(messagesList);
                                    messagesAdapter.updateData(messagesLists);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        // Inflate the layout for this fragment
        return view;
    }
}
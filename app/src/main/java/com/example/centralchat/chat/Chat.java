package com.example.centralchat.chat;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.centralchat.MemoryData;
import com.example.centralchat.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class Chat extends AppCompatActivity {

    DatabaseReference dbReference;
    private final List<ChatList> chatLists = new ArrayList<>();
    private String chatKey;
    String getUserIndexNum = "";
    private RecyclerView chattingRecyclerView;
    private ChatAdapter chatAdapter;
    private boolean loadingFirstTime = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        final ImageView backBtn = findViewById(R.id.backBtn);
        final TextView userName = findViewById(R.id.userName);
        final EditText messageEditText = findViewById(R.id.messageEditTxt);
        final CircleImageView profilePic = findViewById(R.id.profilePic);
        final ImageView sendBtn = findViewById(R.id.sendBtn);
        chattingRecyclerView = findViewById(R.id.chatsRecyclerView);

        dbReference = FirebaseDatabase.getInstance()
                .getReferenceFromUrl("https://central-chat-5d62e-default-rtdb.firebaseio.com/");

        //get data from messages adapter
        final String getUserName = getIntent().getStringExtra("userName");
        final String getProfilePic = getIntent().getStringExtra("profile_pic");
        chatKey = getIntent().getStringExtra("chat_key");
        final String getOtherIndexNum = getIntent().getStringExtra("indexNum");

        //get user mobile from memory
        getUserIndexNum = MemoryData.getIndexNum(Chat.this);

        userName.setText(getUserName);
        if (getProfilePic.equals("default")) {
            Glide.with(this).load(R.drawable.user_icon).into(profilePic);
        }else {
            Glide.with(this).load(getProfilePic).into(profilePic);
        }

        chattingRecyclerView.setHasFixedSize(true);
        chattingRecyclerView.setLayoutManager(new LinearLayoutManager(Chat.this));

        chatAdapter = new ChatAdapter(chatLists, Chat.this);
        chattingRecyclerView.setAdapter(chatAdapter);


            dbReference.addValueEventListener(new ValueEventListener() {
                @RequiresApi(api = Build.VERSION_CODES.O)
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (chatKey.isEmpty()) {
                        //generate chatKey. By default chatKey is 1
                        chatKey = "0";

                        if (snapshot.hasChild("chat")) {
                            chatKey = String.valueOf(snapshot.child("chat").getChildrenCount() + 1);
                        }
                    }

                    if (snapshot.hasChild("chat")) {
                        if (snapshot.child("chat").child(chatKey).hasChild("messages")) {

                            chatLists.clear();

                            for (DataSnapshot messageSnapshot : snapshot.child("chat").child(chatKey).child("messages").getChildren()) {

                                if (messageSnapshot.hasChild("msg") && messageSnapshot.hasChild("indexNum")) {

                                    final String messageTimeStamps = messageSnapshot.getKey();
                                    final String getIndexNum = messageSnapshot.child("indexNum").getValue(String.class);
                                    final String getMsg = messageSnapshot.child("msg").getValue(String.class);

                                    assert getIndexNum != null;
                                    assert messageTimeStamps != null;

                                    Timestamp timestamp = new Timestamp(Long.parseLong(messageTimeStamps));
                                    LocalDateTime dateM = Instant.ofEpochMilli(timestamp.getTime()).atZone(ZoneId.systemDefault()).toLocalDateTime();
                                    DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
                                    DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("hh:mm a");
                                    String date = dateFormatter.format(dateM);
                                    String time = timeFormatter.format(dateM);
                                    ChatList chatList = new ChatList(getIndexNum, getUserName, getMsg, date, time);
                                    chatLists.add(chatList);


                                    if (loadingFirstTime|| Long.parseLong(messageTimeStamps) < Long.parseLong(MemoryData.getLastMsgTS(Chat.this, chatKey))) {
                                        loadingFirstTime = false;
                                        MemoryData.saveLastMsgTS(messageTimeStamps, chatKey, Chat.this);
                                        chatAdapter.updateChatList(chatLists);
                                        chattingRecyclerView.scrollToPosition(chatLists.size() - 1);
                                    }
                                }
                            }
                        }

                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });


        sendBtn.setOnClickListener(v -> {
            final String getTxtMessage = messageEditText.getText().toString();

            //get currentTimeStamp
            final String currentTimeStamp = String.valueOf(System.currentTimeMillis());
            Log.d("Message", currentTimeStamp);

            dbReference.child("chat").child(chatKey).child("user_1").setValue(getUserIndexNum);
            dbReference.child("chat").child(chatKey).child("user_2").setValue(getOtherIndexNum);
            dbReference.child("chat").child(chatKey).child("messages")
                    .child(currentTimeStamp).child("msg").setValue(getTxtMessage);
            dbReference.child("chat").child(chatKey).child("messages")
                    .child(currentTimeStamp).child("indexNum").setValue(getUserIndexNum);

            //clear textField after text sent and hide keyboard
            closeKeyboard();
            messageEditText.setText("");
        });

        backBtn.setOnClickListener(v -> finish());
    }
    private void closeKeyboard()
    {
        // this will give us the view
        // which is currently focus
        // in this layout
        View view = this.getCurrentFocus();

        // if nothing is currently
        // focus then this will protect
        // the app from crash
        if (view != null) {

            // now assign the system
            // service to InputMethodManager
            InputMethodManager manager
                    = (InputMethodManager)
                    getSystemService(
                            Context.INPUT_METHOD_SERVICE);
            manager
                    .hideSoftInputFromWindow(
                            view.getWindowToken(), 0);
        }
    }
}
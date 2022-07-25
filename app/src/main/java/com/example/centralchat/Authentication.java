package com.example.centralchat;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;


public class Authentication extends AppCompatActivity {

    VideoView videoView;
    Button loginBtn, registerBtn;


    @Override
    protected void onStart() {
        super.onStart();

        if(!MemoryData.getIndexNum(this).isEmpty()) {
            Intent intent = new Intent(Authentication.this, HomePage.class);
            intent.putExtra("index number", MemoryData.getIndexNum(this));
            intent.putExtra("username", MemoryData.getUserName(this));
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authentication);

        registerBtn = findViewById(R.id.signUpBtn);
        loginBtn = findViewById(R.id.loginBtn);

        loginBtn.setOnClickListener(v -> startActivity(new Intent(Authentication.this, Login.class)));

        registerBtn.setOnClickListener(v -> startActivity(new Intent(Authentication.this, SignUp.class)));

        videoView = findViewById(R.id.bg_Video);
        String path = "android.resource://com.example.centralchat/" + R.raw.background_video;
        Uri vid = Uri.parse(path);

        videoView.setVideoURI(vid);
        videoView.start();

        videoView.setOnPreparedListener(mp -> mp.setLooping(true));
    }

    @Override
    protected void onResume() {
        videoView.resume();
        super.onResume();
    }

    @Override
    protected void onPause() {
        videoView.pause();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        videoView.stopPlayback();
        super.onDestroy();
    }
}
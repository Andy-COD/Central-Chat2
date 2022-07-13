package com.example.centralchat;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

public class InitialActivity extends AppCompatActivity {

    ImageView image;
    TextView textView;
    Animation top, bottom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_initial);

        image = findViewById(R.id.central_logo);
        textView = findViewById(R.id.motto);
        top = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.top);
        bottom = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.bottom);

        image.setAnimation(top);
        textView.setAnimation(bottom);

        Thread thread = new Thread(() -> {
            SystemClock.sleep(5000);
            Intent intent = new Intent(this, Authentication.class);
            startActivity(intent);
            finish();
        });
        thread.start();
    }
}
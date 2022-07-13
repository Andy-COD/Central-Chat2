package com.example.centralchat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class Login extends AppCompatActivity {
    EditText indexNum, password;
    Button login;

    DatabaseReference dbReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Login");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setOnClickListener(v -> onBackPressed());

        indexNum = findViewById(R.id.index_num);
        password = findViewById(R.id.password);
        login = findViewById(R.id.btn_login);

        dbReference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://central-chat-5d62e-default-rtdb.firebaseio.com/");

        login.setOnClickListener(v -> {
            String txtIndex = indexNum.getText().toString();
            String txtPassword = password.getText().toString();



            if(TextUtils.isEmpty(txtIndex) || TextUtils.isEmpty(txtPassword)) {
                Toast.makeText(Login.this, "Please enter your index number / password", Toast.LENGTH_SHORT).show();
            }else{
                dbReference.child("users").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        //check if index number exist
                        if(snapshot.hasChild(txtIndex)) {
                            String getPassword = snapshot.child(txtIndex).child("password").getValue(String.class);
                            if(Objects.equals(getPassword, txtPassword)) {
                                Toast.makeText(Login.this, "Successfully logged in", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(Login.this, HomePage.class);
                                intent.putExtra("index number", txtIndex);
                                startActivity(intent);

                                finish();
                            }else {
                                Toast.makeText(Login.this, "Wrong password", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });
    }
}
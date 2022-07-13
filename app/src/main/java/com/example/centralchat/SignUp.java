package com.example.centralchat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;
import java.util.regex.Pattern;

public class SignUp extends AppCompatActivity {

    //Declare variable
    EditText userName, phoneNum, password, email, indexNum;
    Button signUp;

    //regex patterns to verify text format
    String emailRegexPattern;
    String indexRegexPattern;
    String indexRegexPatternSlashes;

    //Firebase initialization
    FirebaseAuth auth;
    DatabaseReference dbReference;

    ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Sign Up");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setOnClickListener(v -> finish());

        //initialize variables
        userName = findViewById(R.id.username);
        phoneNum = findViewById(R.id.phoneNum);
        password = findViewById(R.id.password);
        email = findViewById(R.id.email);
        signUp = findViewById(R.id.btn_register);
        indexNum = findViewById(R.id.index_num);
        emailRegexPattern = "^(.+)@(\\S+)$";
        indexRegexPattern = "[a-zA-Z]+[0-9]+";
        indexRegexPatternSlashes = "[a-zA-Z]+/[0-9]+/[0-9]+/[0-9]+";


        dbReference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://central-chat-5d62e-default-rtdb.firebaseio.com/");
        auth = FirebaseAuth.getInstance();

        //Check if user already signed in
        if(!MemoryData.getIndexNum(this).isEmpty()) {
            Intent intent = new Intent(SignUp.this, HomePage.class);
            intent.putExtra("index number", MemoryData.getIndexNum(this));
            intent.putExtra("username", MemoryData.getUserName(this));
            startActivity(intent);
            finish();
        }

        signUp.setOnClickListener(v -> {
            String txtUsername = Objects.requireNonNull(userName.getText()).toString();
            String txtPhoneNum = Objects.requireNonNull(phoneNum.getText()).toString();
            String txtPassword = Objects.requireNonNull(password.getText()).toString();
            String txtEmail = Objects.requireNonNull(email.getText()).toString();
            String txtIndexNumber = Objects.requireNonNull(indexNum.getText().toString());

            if(TextUtils.isEmpty(txtEmail) || TextUtils.isEmpty(txtPassword) || TextUtils.isEmpty(txtPhoneNum) || TextUtils.isEmpty(txtUsername) || TextUtils.isEmpty(txtIndexNumber)) {
                Toast.makeText(SignUp.this, "All fields are required", Toast.LENGTH_SHORT).show();
            }else if (txtPassword.length() < 6) {
                Toast.makeText(SignUp.this, "Password must be 6 characters or more", Toast.LENGTH_SHORT).show();
            }else if(!emailPatternMatcher(txtEmail, emailRegexPattern)) {
                Toast.makeText(this, "Invalid email address", Toast.LENGTH_SHORT).show();
            }else if (!indexPatternMatcher(txtIndexNumber, indexRegexPattern) || indexPatternMatcher(txtIndexNumber, indexRegexPatternSlashes)) {
                Toast.makeText(this, "Invalid index number", Toast.LENGTH_SHORT).show();
            }else if (txtPhoneNum.length() != 10) {
                Toast.makeText(this, "Invalid phone number", Toast.LENGTH_SHORT).show();
            }
            else {
                register(txtEmail, txtPassword, txtPhoneNum, txtUsername, txtIndexNumber);
            }
        });
    }

    private void register(String txtEmail, String txtPassword, String txtPhoneNum, String txtUsername, String txtIndexNum) {

        progressDialog = new ProgressDialog(SignUp.this);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        auth.createUserWithEmailAndPassword(txtEmail, txtPassword)
                .addOnCompleteListener(task -> {
                    progressDialog.dismiss();
                    if (task.isSuccessful()) {
                        dbReference.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if(dataSnapshot.hasChild(txtIndexNum)) {
                                    Toast.makeText(SignUp.this, "User already exist", Toast.LENGTH_SHORT).show();
                                }else {
                                    //Using the index number as the unique identifier
                                    dbReference.child("users").child(txtIndexNum).child("phone number").setValue(txtPhoneNum);
                                    dbReference.child("users").child(txtIndexNum).child("password").setValue(txtPassword);
                                    dbReference.child("users").child(txtIndexNum).child("username").setValue(txtUsername);
                                    dbReference.child("users").child(txtIndexNum).child("index number").setValue(txtIndexNum);
                                    dbReference.child("users").child(txtIndexNum).child("email").setValue(txtEmail);
                                    //set default profile picture
                                    dbReference.child("users").child(txtIndexNum).child("profile picture").setValue("default");

                                    //Save index number to memory
                                    MemoryData.saveIndexNum(txtIndexNum, SignUp.this);

                                    //save name to memory
                                    MemoryData.saveUserName(txtUsername, SignUp.this);

                                    //Save password to memory
                                    MemoryData.savePassword(txtPassword, SignUp.this);

                                    Toast.makeText(SignUp.this, "USer registered successfully", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(SignUp.this, HomePage.class);
                                    intent.putExtra("index number", MemoryData.getIndexNum(SignUp.this));
                                    intent.putExtra("username", MemoryData.getUserName(SignUp.this));
                                    startActivity(intent);
                                    finish();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                progressDialog.dismiss();
                            }
                        });
                    }
                });
    }
    public boolean emailPatternMatcher(String txtEmail, String txtRegex) {
        return Pattern.compile(txtRegex)
                .matcher(txtEmail)
                .matches();
    }
    public Boolean indexPatternMatcher(String txtIndex, String txtRegex) {
        return Pattern.compile(txtRegex)
                .matcher(txtIndex)
                .matches();
    }
}

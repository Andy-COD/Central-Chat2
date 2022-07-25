package com.example.centralchat.homefragments;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.centralchat.Login;
import com.example.centralchat.MemoryData;
import com.example.centralchat.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.ViewHolder;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;


public class ProfileFragment extends Fragment {

    EditText txtUserName, txtEmail, txtPhoneNum;
    TextView username, email, phoneNum;
    Button logout, edit, update, chooseFile;
    CircleImageView profilePic;
    StorageReference storageReference;
    ActivityResultLauncher<String> launcher;
    Uri imageUri;

    String indexNum;

    DatabaseReference dbReference;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        logout = view.findViewById(R.id.logout);
        edit = view.findViewById(R.id.edit);
        username = view.findViewById(R.id.username);
        email = view.findViewById(R.id.email);
        phoneNum = view.findViewById(R.id.phoneNum);
        profilePic = view.findViewById(R.id.profile_picture);
        final String randomKey = UUID.randomUUID().toString();
        storageReference = FirebaseStorage.getInstance().getReferenceFromUrl("gs://central-chat-5d62e.appspot.com")
                .child("images/" + randomKey);

        launcher = registerForActivityResult(new ActivityResultContracts.GetContent()
                , result ->{
            imageUri = result;
            profilePic.setImageURI(imageUri);

            storageReference.putFile(imageUri).addOnSuccessListener(taskSnapshot -> storageReference.getDownloadUrl()
                    .addOnSuccessListener(uri -> dbReference.child("users").child(indexNum).child("profile picture").setValue(uri.toString() + indexNum)
                            .addOnSuccessListener(unused -> Toast.makeText(requireActivity(), "Image successfully uploaded", Toast.LENGTH_SHORT)
                                    .show())));
        });

        indexNum = MemoryData.getIndexNum(requireActivity());

        dbReference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://central-chat-5d62e-default-rtdb.firebaseio.com/");

        dbReference.child("users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                username.setText(snapshot.child(indexNum).child("username").getValue(String.class));
                email.setText(snapshot.child(indexNum).child("email").getValue(String.class));
                phoneNum.setText(snapshot.child(indexNum).child("phone number").getValue(String.class));
                String profileImage = snapshot.child(indexNum).child("profile picture")
                                .getValue(String.class);
                assert profileImage != null;
                if (profileImage.equals("default")) {
                    Glide.with(requireActivity()).load(R.drawable.user_icon).into(profilePic);
                }else {
                    Glide.with(requireActivity()).load(profileImage).into(profilePic);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        logout.setOnClickListener(v -> {
            if (FirebaseAuth.getInstance().getCurrentUser() != null)
                Log.d("Message tag", String.valueOf(mAuth));
            mAuth.signOut();

            Intent intent = new Intent(getActivity(), Login.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            requireActivity().finish();
        });

        edit.setOnClickListener(v -> {
            final DialogPlus dialogPlus = DialogPlus.newDialog(requireActivity())
                    .setContentHolder(new ViewHolder(R.layout.update_popup))
                    .setExpanded(true, 1200)
                    .create();

            View view1 = dialogPlus.getHolderView();

            txtUserName = view1.findViewById(R.id.editUserName);
            txtEmail = view1.findViewById(R.id.editEmail);
            txtPhoneNum = view1.findViewById(R.id.editPhoneNum);
            update = view1.findViewById(R.id.updateBtn);
            chooseFile = view1.findViewById(R.id.chooseFile);

            chooseFile.setOnClickListener(v12 -> {
                launcher.launch("image/*");
                requireActivity().onBackPressed();
            });


            dialogPlus.show();


            update.setOnClickListener(v1 -> {
                closeKeyboard(requireActivity());
                String edtUserName = txtUserName.getText().toString();
                String edtEmail = txtEmail.getText().toString();
                String edtPhoneNum = txtPhoneNum.getText().toString();

                if(edtUserName.isEmpty() || edtEmail.isEmpty() || edtPhoneNum.isEmpty()) {
                    Toast.makeText(requireActivity(), "All fields are required", Toast.LENGTH_SHORT).show();
                }else {
                    Map<String, Object> map = new HashMap<>();
                    map.put("username", edtUserName);
                    map.put("email", edtEmail);
                    map.put("phone number", edtPhoneNum);

                    dbReference.child("users").child(indexNum).updateChildren(map).addOnSuccessListener(unused -> {
                        Toast.makeText(requireActivity(), "profile updated successfully", Toast.LENGTH_SHORT).show();
                        dialogPlus.dismiss();
                    }).addOnFailureListener(e -> Toast.makeText(requireActivity(), "Failed to update profile", Toast.LENGTH_SHORT).show());

                }
            });

        });

        return view;
    }

    //Function to close virtual keyboard
    public static void closeKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);

        // To get the correct window token, lets first get the currently focused view
        View view = activity.getCurrentFocus();

        // To get the window token when there is no currently focused view, we have a to create a view
        if (view == null) {
            view = new View(activity);
        }

        // hide the keyboard
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}
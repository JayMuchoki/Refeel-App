package com.first.chatapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class setupActivity extends AppCompatActivity {
    private static final int REQUEST_CODE = 101;
    CircleImageView profileImageView;
    EditText inputusername,inputCity,inputCountry,inputProfession;
    Button btnsave;
    Uri imageuri;
    FirebaseAuth mAuth;
    FirebaseUser mUser;
    DatabaseReference mRef;
    StorageReference StorageRef;
    ProgressDialog mLoadingBar;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);
        profileImageView=findViewById(R.id.profile_image);
        inputusername=findViewById(R.id.inputUsername);
        inputCity=findViewById(R.id.inputCity);
        inputCountry=findViewById(R.id.inputCountry);
        inputProfession=findViewById(R.id.inputProfession);
        btnsave=findViewById(R.id.buttonsave);
        mLoadingBar=new ProgressDialog(this);
       toolbar=findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Setup Profile");


        mAuth=FirebaseAuth.getInstance();
        mUser=mAuth.getCurrentUser();
        mRef= FirebaseDatabase.getInstance().getReference().child("Users");
        StorageRef= FirebaseStorage.getInstance().getReference().child("ProfileImages");

        profileImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent,REQUEST_CODE);

            }
        });
        btnsave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SaveData();
            }
        });
    }

    private void SaveData() {
        String username = inputusername.getText().toString();
        String city = inputCity.getText().toString();
        String country = inputCountry.getText().toString();
        String profession = inputProfession.getText().toString();

        if (username.isEmpty() || username.length() < 3) {
            showError(inputusername, "Incognito name is not Valid");
        } else if (city.isEmpty() || city.length() < 3) {
            showError(inputCity, "City name is not valid");
        } else if (country.isEmpty() || country.length() < 3) {
            showError(inputCountry, "Countryname is not valid");
        } else if (profession.isEmpty() || profession.length() < 3) {
            showError(inputProfession, "Profession is not valid");
        } else if (imageuri == null) {
            Toast.makeText(this, "Insert an image", Toast.LENGTH_SHORT).show();
        } else {
            mLoadingBar.setTitle("Setup Profile");
            mLoadingBar.setCanceledOnTouchOutside(false);
            mLoadingBar.show();

            if (mUser != null) { // Check if mUser is not null
                StorageRef.child(mUser.getUid()).putFile(imageuri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()) {
                            StorageRef.child(mUser.getUid()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    HashMap<String, Object> hashMap = new HashMap<>(); // Specify types for HashMap
                                    hashMap.put("username", username);
                                    hashMap.put("city", city);
                                    hashMap.put("country", country); // Fixed typo here, it was "countrty"
                                    hashMap.put("profession", profession);
                                    hashMap.put("profileImage", uri.toString());
                                    hashMap.put("status", "offline");

                                    mRef.child(mUser.getUid()).updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Intent intent = new Intent(setupActivity.this, MainActivity.class);
                                            startActivity(intent);
                                            mLoadingBar.dismiss();
                                            finish();

                                            Toast.makeText(setupActivity.this, "Setup Profile Completed", Toast.LENGTH_SHORT).show();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            mLoadingBar.dismiss();
                                            Toast.makeText(setupActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            });
                        }
                    }
                });
            } else {
                // Handle the case where mUser is null
                Toast.makeText(this, "User is not logged in", Toast.LENGTH_SHORT).show();
                mLoadingBar.dismiss();
            }
        }
    }


    private void showError(EditText input, String s) {
        input.setError(s);
        input.requestFocus();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==REQUEST_CODE && resultCode==RESULT_OK && data!=null){
            imageuri=data.getData();
            profileImageView.setImageURI(imageuri);
        }

    }
}
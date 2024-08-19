package com.first.chatapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {
    CircleImageView profileImageView;
    EditText inputUsername, inputCity;
    EditText inputCountry, inputProfession;
    Button btnUpdates;

    DatabaseReference mUserRef,FriendsRef,PostRef;
    FirebaseAuth mAuth;
    FirebaseUser mUser;

    ProgressDialog mLoadingBar;

    Button MyPosts,MyFriends;

    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri mImageUri;

    private  int countFriends=0,countPost=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mLoadingBar=new ProgressDialog(this);

        profileImageView = findViewById(R.id.circleImageView);
        inputUsername = findViewById(R.id.profileinputUsername);
        inputCity = findViewById(R.id.profileinputCity);
        inputCountry = findViewById(R.id.profileinputCountry);
        inputProfession = findViewById(R.id.profileinputProfession);
        btnUpdates = findViewById(R.id.profilebtnUpdate);
        MyFriends = findViewById(R.id.Friends);
        MyPosts= findViewById(R.id.post);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        mUserRef = FirebaseDatabase.getInstance().getReference().child("Users");
        FriendsRef= FirebaseDatabase.getInstance().getReference().child("Friends");
        PostRef= FirebaseDatabase.getInstance().getReference().child("Posts");

        PostRef.orderByChild("uid").startAt(mUser.getUid()).endAt(mUser.getUid()+"\uf8ff").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    countPost=(int) dataSnapshot.getChildrenCount();
                    MyPosts.setText(Integer.toString(countPost) +"  Posts");

                }else {
                    MyPosts.setText("0 Posts");
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        FriendsRef.child(mUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    countFriends=(int) dataSnapshot.getChildrenCount();
                    MyFriends.setText(Integer.toString(countFriends)+"  Friends");

                }else {
                    MyFriends.setText("0 Friends");
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        MyPosts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(ProfileActivity.this,MyPost_Activity.class);
                startActivity(intent);
            }
        });

        MyFriends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(ProfileActivity.this,FriendActivity.class);
                startActivity(intent);
            }
        });

        mUserRef.child(mUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String profileImageUrl = snapshot.child("profileImage").getValue().toString();
                    String city = snapshot.child("city").getValue().toString();
                    String country = snapshot.child("country").getValue().toString();
                    String profession = snapshot.child("profession").getValue().toString();
                    String username = snapshot.child("username").getValue().toString();

                    Picasso.get().load(profileImageUrl).into(profileImageView);
                    inputCity.setText(city);
                    inputCountry.setText(country);
                    inputProfession.setText(profession);
                    inputUsername.setText(username);
                } else {
                    Toast.makeText(ProfileActivity.this, "Data does not exist", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ProfileActivity.this, "" + error.getMessage().toString(), Toast.LENGTH_SHORT).show();
            }
        });

        profileImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });

        btnUpdates.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateProfile();
                mLoadingBar.setTitle("Updating Profile");
                mLoadingBar.setCanceledOnTouchOutside(false);
                mLoadingBar.show();
            }
        });
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            mImageUri = data.getData();
            profileImageView.setImageURI(mImageUri);
        }
    }

    private void updateProfile() {
        String username = inputUsername.getText().toString().trim();
        String city = inputCity.getText().toString().trim();
        String country = inputCountry.getText().toString().trim();
        String profession = inputProfession.getText().toString().trim();

        if (TextUtils.isEmpty(username)) {
            inputUsername.setError("Username is required");
            inputUsername.requestFocus();
            return;
        }

        HashMap<String, Object> updates = new HashMap<>();
        updates.put("username", username);
        updates.put("city", city);
        updates.put("country", country);
        updates.put("profession", profession);

        if (mImageUri != null) {
            // Upload new profile image to Firebase Storage
            StorageReference profileImageRef = FirebaseStorage.getInstance().getReference("profile_images/" + mUser.getUid());
            profileImageRef.putFile(mImageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            // Get the download URL for the new image
                            profileImageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    // Update profile image URL in the database
                                    updates.put("profileImage", uri.toString());
                                    updateProfileData(updates);
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(ProfileActivity.this, "Failed to upload image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            // No new image selected, update profile information without changing the profile image
            updateProfileData(updates);
        }
    }

    private void updateProfileData(HashMap<String, Object> updates) {
        // Update profile information in the database
        mUserRef.child(mUser.getUid()).updateChildren(updates)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Intent intent=new Intent(ProfileActivity.this,ProfileActivity.class);
                        mLoadingBar.dismiss();

                        Toast.makeText(ProfileActivity.this, "Profile updated successfully", Toast.LENGTH_SHORT).show();


                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ProfileActivity.this, "Failed to update profile: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

}
package com.first.chatapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ViewFriendActivity extends AppCompatActivity {

    DatabaseReference mUserRef,requestRef,friendRef;
    FirebaseAuth mAuth;
    FirebaseUser mUser;
    String profileImageUrl,username,city,country;
    String myprofileImageUrl,myusername,mycity,mycountry,myprofession;

    CircleImageView profileImage;
    TextView Username,address;
    Button btnPerform,btnDecline;
    String CurrentState="Nothing_happened";
    String profession;
    String userID;


    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_friend);

        userID= getIntent().getStringExtra("userKey");
        //Toast.makeText(this, "User ID: " + userID, Toast.LENGTH_SHORT).show();

        mUserRef = FirebaseDatabase.getInstance().getReference().child("Users");
        requestRef = FirebaseDatabase.getInstance().getReference().child("Requests");
        friendRef= FirebaseDatabase.getInstance().getReference().child("Friends");
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        profileImage = findViewById(R.id.viewprofileImagefriend);
        Username = findViewById(R.id.viewusername);
        address = findViewById(R.id.viewaddress);
        btnPerform=findViewById(R.id.btnPerform);
        btnDecline=findViewById(R.id.btnDecline);





        Loaduser();

        LoadMyProfile();

        btnPerform.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PerformAction(userID);
            }
        });
        CheckUserExistance(userID);
        btnDecline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Unfriend(userID);

            }
        });
    }



    private void Unfriend(String userID) {
        if (CurrentState.equals("friend")) {
            // Remove friend relationship
            friendRef.child(mUser.getUid()).child(userID).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        friendRef.child(userID).child(mUser.getUid()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(ViewFriendActivity.this, "You are Unfriend", Toast.LENGTH_SHORT).show();
                                    CurrentState = "Nothing_happened";
                                    btnPerform.setText("Send Friend Request");
                                    btnDecline.setVisibility(View.GONE);
                                }
                            }
                        });
                    } else {
                        Toast.makeText(ViewFriendActivity.this, "Failed to unfriend: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else if (CurrentState.equals("he_sent_pending")) {


            requestRef.child(mUser.getUid()).child(userID).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        requestRef.child(userID).child(mUser.getUid()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(ViewFriendActivity.this, "Friend Request Canceled", Toast.LENGTH_SHORT).show();
                                    CurrentState = "Nothing_happened";
                                    btnPerform.setText("Send Friend Request");
                                    btnDecline.setVisibility(View.GONE);
                                }
                            }
                        });
                    } else {
                        Toast.makeText(ViewFriendActivity.this, "Failed to cancel friend request: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }
    }






    private void CheckUserExistance(String userID) {
        friendRef.child(mUser.getUid()).child(userID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    CurrentState="friend";
                    btnPerform.setText("Send SMS");
                    btnDecline.setText("Unfriend");
                    btnDecline.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        friendRef.child(userID).child(mUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    CurrentState="friend";
                    btnPerform.setText("Send SMS");
                    btnDecline.setText("Unfriend");
                    btnDecline.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        requestRef.child(mUser.getUid()).child(userID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    if (snapshot.child("status").getValue().toString().equals("pending")){
                        CurrentState="I_sent_pending";
                        btnPerform.setText("Cancel Friend Request");
                        btnDecline.setVisibility(View.GONE);

                    }if (snapshot.child("status").getValue().toString().equals("decline")){
                        CurrentState="I_sent_decline";
                        btnPerform.setText("Cancel Friend Request");
                        btnDecline.setVisibility(View.GONE);

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        requestRef.child(userID).child(mUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    if (snapshot.child("status").getValue().toString().equals("pending")){
                        CurrentState="he_sent_pending";
                        btnPerform.setText("Accept Friend Request");
                        btnDecline.setText("Decline Friend");
                        btnDecline.setVisibility(View.VISIBLE);
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        if (CurrentState.equals("Nothing_happened")){
            CurrentState="Nothing_happened";
            btnPerform.setText("Send Friend Request");
            btnDecline.setVisibility(View.GONE);

        }

    }

    private void PerformAction(String userID) {
        if (CurrentState.equals("Nothing_happened")) {
            // Sending friend request



            sendFriendRequest();


        } else if (CurrentState.equals("he_sent_pending")) {
            // Accepting friend request
            requestRef.child(mUser.getUid()).child(userID).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        final HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("status", "friend");
                        hashMap.put("username", username);
                        hashMap.put("profession", profession );
                        hashMap.put("profileImageUrl", profileImageUrl);


                        final HashMap<String, Object> hashMap1 = new HashMap<>();
                        hashMap1.put("status", "friend");
                        hashMap1.put("username", myusername);
                        hashMap1.put("profession", myprofession );
                        hashMap1.put("profileImageUrl", myprofileImageUrl);

                        friendRef.child(mUser.getUid()).child(userID).updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    friendRef.child(userID).child(mUser.getUid()).updateChildren(hashMap1).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            Toast.makeText(ViewFriendActivity.this, "You added Friend", Toast.LENGTH_SHORT).show();
                                            CurrentState = "friend";
                                            btnPerform.setText("Send SMS");
                                            btnDecline.setText("Unfriend");
                                            btnDecline.setVisibility(View.VISIBLE);

                                            // Remove the request
                                            requestRef.child(userID).child(mUser.getUid()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        // Request removed
                                                    } else {
                                                        Toast.makeText(ViewFriendActivity.this, "" + task.getException().toString(), Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });
                                        }
                                    });
                                } else {
                                    Toast.makeText(ViewFriendActivity.this, "" + task.getException().toString(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    } else {
                        Toast.makeText(ViewFriendActivity.this, "" + task.getException().toString(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else if (CurrentState.equals("friend")) {
            Intent intent=new Intent(ViewFriendActivity.this,ChatActivity.class);
            intent.putExtra("OtherUserId",userID);
            startActivity(intent);
            // Already friends, do nothing
        } else if (CurrentState.equals("I_sent_pending") || CurrentState.equals("I_sent_decline")) {
            // Cancel friend request
            requestRef.child(mUser.getUid()).child(userID).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(ViewFriendActivity.this, "You have cancelled Friend Request", Toast.LENGTH_SHORT).show();
                        CurrentState = "Nothing_happened";
                        btnPerform.setText("Send Friend Request");
                        btnDecline.setVisibility(View.GONE);
                    } else {
                        Toast.makeText(ViewFriendActivity.this, "" + task.getException().toString(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

    }

    private void sendFriendRequest() {

        final HashMap<String, Object> newMap = new HashMap<>();
        newMap.put("status", "friend");
        newMap.put("username", username);
        newMap.put("profession", profession );
        newMap.put("profileImageUrl", profileImageUrl);
        newMap.put("status", "pending");


        final HashMap<String, Object> newMap1 = new HashMap<>();
        newMap1.put("status", "friend");
        newMap1.put("username", myusername);
        newMap1.put("profession", myprofession );
        newMap1.put("profileImageUrl", myprofileImageUrl);
        newMap.put("status", "pending");


        requestRef.child(mUser.getUid()).child(userID).updateChildren(newMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if(task.isSuccessful()){
                    requestRef.child(userID).child(mUser.getUid()).updateChildren(newMap1).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (task.isSuccessful()) {


                                Toast.makeText(ViewFriendActivity.this, "You Have sent Friend Request", Toast.LENGTH_SHORT).show();
                                btnDecline.setVisibility(View.GONE);
                                CurrentState = "I_sent_pending";
                                btnPerform.setText("Cancel Friend Request");
                            } else {
                                Toast.makeText(ViewFriendActivity.this, "" + task.getException().toString(), Toast.LENGTH_SHORT).show();
                            }

                        }
                    });
                }

            }
        });


    }


    private void Loaduser() {
        mUserRef.child(userID).addValueEventListener(new ValueEventListener(){
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    profileImageUrl = snapshot.child("profileImage").getValue().toString();
                    username = snapshot.child("username").getValue().toString();
                    city = snapshot.child("city").getValue().toString();
                    country = snapshot.child("country").getValue().toString();
                    profession = snapshot.child("profession").getValue().toString();

                    Picasso.get().load(profileImageUrl).into(profileImage);
                    Username.setText(username);
                    address.setText(city + "," + country);
                } else {
                    Toast.makeText(ViewFriendActivity.this, "User data not found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ViewFriendActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void LoadMyProfile() {
        mUserRef.child(mUser.getUid()).addValueEventListener(new ValueEventListener(){
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    myprofileImageUrl = snapshot.child("profileImage").getValue().toString();
                    myusername = snapshot.child("username").getValue().toString();
                    mycity = snapshot.child("city").getValue().toString();
                    mycountry = snapshot.child("country").getValue().toString();
                    myprofession = snapshot.child("profession").getValue().toString();




                } else {
                    Toast.makeText(ViewFriendActivity.this, "User data not found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ViewFriendActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });



    }



}




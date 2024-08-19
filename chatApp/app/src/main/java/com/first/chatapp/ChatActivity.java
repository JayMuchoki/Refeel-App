package com.first.chatapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.first.chatapp.Utills.Chat;
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

public class ChatActivity extends AppCompatActivity {
    Toolbar toolbar;
    RecyclerView recyclerView;
    EditText inputSms;
    ImageView btnSend;

    CircleImageView userProfileImageAppbar;
    TextView usernameAppBar,status;

    String OtheruserId;

    DatabaseReference mUserRef,smsRef;
    FirebaseAuth mAuth;
    FirebaseUser mUser;

    String OtherUsername,OtherUserProfileImageLink,OtherUserStatus;
    String username;

    FirebaseRecyclerOptions<Chat>options;
    FirebaseRecyclerAdapter<Chat,ChatMyViewHolder>adapter;

    String myProfileImageLink;
    String URL="https://fcm.googleapis.com/fcm/send";
    RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        toolbar=findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);

        requestQueue= Volley.newRequestQueue(this);

        recyclerView=findViewById(R.id.chatrecyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        inputSms=findViewById(R.id.inputSms);
       btnSend=findViewById(R.id.btnSend);

       userProfileImageAppbar=findViewById(R.id.userProfileImageAppbar);
       usernameAppBar=findViewById(R.id.usernameAppBar);
       status=findViewById(R.id.status);

       mUserRef= FirebaseDatabase.getInstance().getReference().child("Users");
       smsRef= FirebaseDatabase.getInstance().getReference().child("Message");
       mAuth=FirebaseAuth.getInstance();
       mUser=mAuth.getCurrentUser();

       OtheruserId=getIntent().getStringExtra("OtherUserId");


       LoadOtherUser();
       LoadMyProfile();

       btnSend.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               SendSMS();

           }
       });

       LoadSMS();

    }


    private void LoadMyProfile() {
        mUserRef.child(mUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    myProfileImageLink=snapshot.child("profileImage").getValue().toString();
                    username=snapshot.child("username").getValue().toString();

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ChatActivity.this,""+error.getMessage(),Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void LoadSMS() {
        DatabaseReference currentUserMessagesRef = smsRef.child(mUser.getUid()).child(OtheruserId);
        DatabaseReference otherUserMessagesRef = smsRef.child(OtheruserId).child(mUser.getUid());

        options = new FirebaseRecyclerOptions.Builder<Chat>()
                .setQuery(currentUserMessagesRef, Chat.class)
                .build();

        adapter = new FirebaseRecyclerAdapter<Chat, ChatMyViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ChatMyViewHolder holder, int position, @NonNull Chat model) {
                if (model.getUserID().equals(mUser.getUid())) {
                    String messageKey= getRef(position).getKey();
                    // Show current user's messages
                    holder.firstUserText.setVisibility(View.GONE);
                    holder.firstUserProfile.setVisibility(View.GONE);
                    holder.secondUserText.setVisibility(View.VISIBLE);
                    holder.secondUserProfile.setVisibility(View.VISIBLE);

                    holder.secondUserText.setText(model.getSms());
                    Picasso.get().load(myProfileImageLink).into(holder.secondUserProfile);
                } else {
                    // Show other user's messages
                    holder.firstUserText.setVisibility(View.VISIBLE);
                    holder.firstUserProfile.setVisibility(View.VISIBLE);
                    holder.secondUserText.setVisibility(View.GONE);
                    holder.secondUserProfile.setVisibility(View.GONE);

                    holder.firstUserText.setText(model.getSms());
                    Picasso.get().load(OtherUserProfileImageLink).into(holder.firstUserProfile);
                }
            }

            @NonNull
            @Override
            public ChatMyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.singleview_sms, parent, false);
                return new ChatMyViewHolder(view);
            }
        };

        adapter.startListening();
        recyclerView.setAdapter(adapter);
    }


    private void SendSMS() {
        String sms=inputSms.getText().toString();
        if (sms.isEmpty()){
            Toast.makeText(this,"Please write something",Toast.LENGTH_SHORT).show();

        }else {
            final HashMap hashMap=new HashMap();
            hashMap.put("sms",sms);
            hashMap.put("status","unseen");
            hashMap.put("userID",mUser.getUid());

            smsRef.child(OtheruserId).child(mUser.getUid()).push().updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if (task.isSuccessful()){
                        smsRef.child(mUser.getUid()).child(OtheruserId).push().updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener() {
                            @Override
                            public void onComplete(@NonNull Task task) {
                                if (task.isSuccessful()){

                                    sendNotification(sms);
                                    inputSms.setText(null);
                                    Toast.makeText(ChatActivity.this,"Sms sent",Toast.LENGTH_SHORT).show();
                                }

                            }
                        });
                    }

                }
            });
        }

    }

    private void sendNotification(String sms) {

        JSONObject jsonObject=new JSONObject();
        try {
            jsonObject.put("to","/topics/"+OtheruserId);
            JSONObject jsonObject1=new JSONObject();
            jsonObject1.put("title", "Message From " + username);

            jsonObject1.put("body",sms);

            JSONObject jsonObject2=new JSONObject();
            jsonObject2.put("userID",mUser.getUid());
            jsonObject2.put("type", "sms");

            jsonObject.put("notification",jsonObject1);
            jsonObject.put("data",jsonObject2);


            JsonObjectRequest request=new JsonObjectRequest(Request.Method.POST,URL, jsonObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {


                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    // Add a log or Toast message here to indicate that there was an error sending the notification
                    Log.e("Notification", "Error sending notification: " + error.getMessage());
                    Toast.makeText(ChatActivity.this, "Error sending notification: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }){
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> map=new HashMap<>();
                    map.put("content-type","application/json");
                    map.put("authorization","key=AAAAiO4dnFE:APA91bGiYkIMqgj5CwAtaCi6iAoNkS4QnTHCqbcI6I8Aqo3JYXtKCqml2OWcekRC7dArOBe6oOunW8d5rlvRZBts_2cYahsSD0fTQzk-6YqQxnTRoynXglATMX9W22XRnS4WELZwpES0");

                    return map;
                }
            };

            requestQueue.add(request);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void LoadOtherUser() {

            mUserRef.child(OtheruserId).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    if (snapshot.exists()) {
                        OtherUsername = snapshot.child("username").getValue().toString();
                        OtherUserProfileImageLink = snapshot.child("profileImage").getValue().toString();
                        OtherUserStatus = snapshot.child("status").getValue().toString();

                        Picasso.get().load(OtherUserProfileImageLink).into(userProfileImageAppbar);
                        usernameAppBar.setText(OtherUsername);
                        status.setText(OtherUserStatus);

                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                    Toast.makeText(ChatActivity.this, "" + error.getMessage(), Toast.LENGTH_SHORT).show();

                }
            });


    }
}
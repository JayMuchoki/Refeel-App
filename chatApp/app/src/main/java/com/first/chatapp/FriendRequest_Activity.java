package com.first.chatapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.first.chatapp.Utills.Friend_Request;
import com.first.chatapp.Utills.Friends;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.squareup.picasso.Picasso;

public class FriendRequest_Activity extends AppCompatActivity {
    FirebaseRecyclerOptions<Friend_Request>options;
    FirebaseRecyclerAdapter<Friend_Request, FriendRequestMyviewHolder> adapter;
    Toolbar toolbar;
    RecyclerView recyclerView;

    FirebaseAuth mAuth;
    DatabaseReference mRef;
    FirebaseUser mUser;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_request);

        toolbar=findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Friend_Requests");

        recyclerView=findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        mAuth=FirebaseAuth.getInstance();
        mUser=mAuth.getCurrentUser();
        mRef= FirebaseDatabase.getInstance().getReference().child("Requests");

        LoadFriendRequest("");
    }

    private void LoadFriendRequest(String s) {
        Query query=mRef.child(mUser.getUid()).orderByChild("username").startAt(s).endAt(s+"\uf8ff");
        options=new FirebaseRecyclerOptions.Builder<Friend_Request>().setQuery(query,Friend_Request.class).build();
        adapter=new FirebaseRecyclerAdapter<Friend_Request, FriendRequestMyviewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull FriendRequestMyviewHolder holder, int position, @NonNull Friend_Request model) {
                Picasso.get().load(model.getProfileImageUrl()).into(holder.profileImageUrl);
                holder.username.setText(model.getUsername());
                holder.profession.setText(model.getProfession());

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Intent intent = new Intent(FriendRequest_Activity.this, ViewFriendActivity.class);
                        intent.putExtra("userKey", getRef(position).getKey());
                        startActivity(intent);

                    }
                });

            }

            @NonNull
            @Override
            public FriendRequestMyviewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.single_view_friendrequest,parent,false);

                return new FriendRequestMyviewHolder(view);
            }
        };
        adapter.startListening();
        recyclerView.setAdapter(adapter);


    }
}

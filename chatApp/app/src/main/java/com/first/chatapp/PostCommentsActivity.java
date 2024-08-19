package com.first.chatapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.first.chatapp.Utills.Comment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

public class PostCommentsActivity extends AppCompatActivity {

    RecyclerView recyclerViewComments;
    FirebaseRecyclerAdapter<Comment, CommentViewHolder> commentAdapter;
    DatabaseReference commentRef;
    String postKey;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_comments);

        recyclerViewComments = findViewById(R.id.recyclerViewComments);
        recyclerViewComments.setLayoutManager(new LinearLayoutManager(PostCommentsActivity.this));

        // Retrieve post key from intent extras
        postKey = getIntent().getStringExtra("postKey");

        // Initialize Firebase Database reference for comments under the specific post key
        commentRef = FirebaseDatabase.getInstance().getReference().child("Comments").child(postKey);
        toolbar=findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("view comments");


        // Load and display comments using FirebaseRecyclerAdapter
        loadComments();
    }

    private void loadComments() {
        FirebaseRecyclerOptions<Comment> commentOptions = new FirebaseRecyclerOptions.Builder<Comment>()
                .setQuery(commentRef, Comment.class)
                .build();

        commentAdapter = new FirebaseRecyclerAdapter<Comment, CommentViewHolder>(commentOptions) {
            @Override
            protected void onBindViewHolder(@NonNull CommentViewHolder holder, int position, @NonNull Comment model) {
                holder.username.setText(model.getUsername());
                holder.comment.setText(model.getComment());
                Picasso.get().load(model.getProfileImageUrl()).into(holder.profileImage);

                // Get the post owner's user ID and the current user's ID
                String postOwnerId = model.getPostOwnerid();
                String commentOwnerId = model.getOwnerId();
                String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

                // Add a long-click listener to the comment item
                holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        // Check if the current user is the owner of the post or the owner of the comment
                        if ((postOwnerId != null && postOwnerId.equals(currentUserId)) || (commentOwnerId != null && commentOwnerId.equals(currentUserId))) {
                            // Show AlertDialog to confirm deletion
                            AlertDialog.Builder builder = new AlertDialog.Builder(holder.itemView.getContext());
                            builder.setMessage("Are you sure you want to delete this comment?")
                                    .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            // User clicked Delete button
                                            String commentKey = getRef(position).getKey();
                                            commentRef.child(commentKey).removeValue();
                                        }
                                    })
                                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            // User clicked Cancel button
                                            dialog.dismiss();
                                        }
                                    });
                            AlertDialog dialog = builder.create();
                            dialog.show();
                        }

                        return true; // Consume the long click
                    }
                });
            }



            @NonNull
            @Override
            public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_view_comment, parent, false);
                return new CommentViewHolder(view);
            }
        };

        commentAdapter.startListening();
        recyclerViewComments.setAdapter(commentAdapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (commentAdapter != null) {
            commentAdapter.startListening();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (commentAdapter != null) {
            commentAdapter.stopListening();
        }
    }
}

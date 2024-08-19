package com.first.chatapp;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatMyViewHolder  extends RecyclerView.ViewHolder {

    CircleImageView firstUserProfile, secondUserProfile;
    TextView firstUserText, secondUserText;

    public ChatMyViewHolder(@NonNull View itemView) {
        super(itemView);

        firstUserProfile = itemView.findViewById(R.id.firstUserProfile);
        secondUserProfile = itemView.findViewById(R.id.secondUserProfile);
        firstUserText = itemView.findViewById(R.id.firstUserText);
        secondUserText = itemView.findViewById(R.id.secondUserText);

        // Long press listener for the message text
        firstUserText.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showDeleteMessageDialog(firstUserText.getText().toString());
                return true;
            }
        });

        secondUserText.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showDeleteMessageDialog(secondUserText.getText().toString());
                return true;
            }
        });
    }

    private void showDeleteMessageDialog(final String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(itemView.getContext());
        builder.setMessage("Delete this message?")
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        DatabaseReference messageRef = FirebaseDatabase.getInstance().getReference().child("messages").child("messagekey");
                        messageRef.removeValue(new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                                if (error != null) {
                                    Toast.makeText(itemView.getContext(), "Failed to delete message", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(itemView.getContext(), "Message deleted", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Cancelled, do nothing
                    }
                })
                .show();
    }
}

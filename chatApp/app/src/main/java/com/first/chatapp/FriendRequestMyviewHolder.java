package com.first.chatapp;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import de.hdodenhof.circleimageview.CircleImageView;

public class FriendRequestMyviewHolder extends RecyclerView.ViewHolder {
    CircleImageView profileImageUrl;
    TextView username,profession;

    public FriendRequestMyviewHolder(@NonNull View itemView) {
        super(itemView);

        profileImageUrl=itemView.findViewById(R.id.viewprofileImagefriend);
        username=itemView.findViewById(R.id.usernamefriend);
        profession=itemView.findViewById(R.id.professionfriend);
    }
}

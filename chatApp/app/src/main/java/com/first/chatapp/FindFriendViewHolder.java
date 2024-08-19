package com.first.chatapp;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import de.hdodenhof.circleimageview.CircleImageView;

public class FindFriendViewHolder  extends RecyclerView.ViewHolder {
    CircleImageView profileImage;
    TextView username,profession;


    public FindFriendViewHolder(@NonNull View itemView) {
        super(itemView);

        profileImage=itemView.findViewById(R.id.viewprofileImagefriend);
        username=itemView.findViewById(R.id.usernamefriend);
        profession=itemView.findViewById(R.id.professionfriend);

    }
}

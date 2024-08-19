package com.first.chatapp.Utills;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.first.chatapp.FriendRequestMyviewHolder;

public class Friend_Request {

    private String profession,username,profileImageUrl,status;

    public Friend_Request(String profession, String username, String profileImageUrl , String status) {
        this.profession = profession;
        this.username = username;
        this.profileImageUrl = profileImageUrl ;
        this.status = status;
    }

    public Friend_Request() {

    }

    public String getProfession() {
        return profession;
    }

    public void setProfession(String profession) {
        this.profession = profession;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getProfileImageUrl() {
        return profileImageUrl
                ;
    }

    public void setProfileImageUrl(String ProfileImageUrl) {
        this.profileImageUrl = ProfileImageUrl;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}

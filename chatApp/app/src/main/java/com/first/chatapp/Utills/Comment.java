package com.first.chatapp.Utills;

public class Comment {
    private  String comment,profileImageUrl,username,postOwnerid,ownerId;

    public Comment() {

    }

    public Comment(String comment, String profileImageUrl, String username,String postOwnrid,String ownerId) {
        this.comment = comment;
        this.profileImageUrl = profileImageUrl;
        this.username = username;
        this.postOwnerid=postOwnerid;
        this.ownerId=ownerId;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public String getPostOwnerid() {
        return postOwnerid;
    }

    public void setPostOwnrid(String postOwnrid) {
        this.postOwnerid = postOwnrid;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}

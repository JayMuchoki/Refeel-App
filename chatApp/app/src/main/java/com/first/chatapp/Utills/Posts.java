package com.first.chatapp.Utills;

public class Posts {

    private String datePost,postDesc,postimageurl,userProfileImageUrl,username,uid;

    public Posts() {
    }

    public Posts(String datePost, String postDesc, String postimageurl, String userProfileImageUrl, String username,String uid) {
        this.datePost = datePost;
        this.postDesc = postDesc;
        this.postimageurl = postimageurl;
        this.userProfileImageUrl = userProfileImageUrl;
        this.username = username;
        this.uid=uid;

    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getDatePost() {
        return datePost;
    }

    public void setDatePost(String datePost) {
        this.datePost = datePost;
    }

    public String getPostDesc() {
        return postDesc;
    }

    public void setPostDesc(String postDesc) {
        this.postDesc = postDesc;
    }

    public String getPostimageurl() {
        return postimageurl;
    }

    public void setPostimageurl(String postimageurl) {
        this.postimageurl = postimageurl;
    }

    public String getUserProfileImageUrl() {
        return userProfileImageUrl;
    }

    public void setUserProfileImageUrl(String userProfileImageUrl) {
        this.userProfileImageUrl = userProfileImageUrl;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }


}

package com.tehilafi.ama.lists;

public class ListView_item_detail {

    int Image;
    String UserName;
    String Date;
    String Content;
    String NumLike;
    String NumDislike;
    int Add_videoID;
    int Add_picID;

    public ListView_item_detail(int image,  String userName, String date, String content, String numLike, String numDislike, int add_videoID,  int add_picID){
        Image = image;
        UserName = userName;
        Date = date;
        Content = content;
        NumLike = numLike;
        NumDislike = numDislike;
    }

    public void setNumLike(String numLike) {
        NumLike = numLike;
    }

    public void setNumDislike(String numDislike) {
        NumDislike = numDislike;
    }

    public String getNumLike() {
        return NumLike;
    }

    public String getNumDislike() {
        return NumDislike;
    }

    public String getDate() {
        return Date;
    }

    public String getUserName() {
        return UserName;
    }

    public String getContent() {
        return Content;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }

    public void setContent(String content) {
        Content = content;
    }

    public void setDate(String date) {
        Date = date;
    }

    public int getImage() {
        return Image;
    }

    public int getAdd_videoID() {
        return Add_videoID;
    }

    public int getAdd_picID() {
        return Add_picID;
    }

    public void setImage(int image) {
        Image = image;
    }

    public void setAdd_videoID(int add_videoID) {
        Add_videoID = add_videoID;
    }

    public void setAdd_picID(int add_picID) {
        Add_picID = add_picID;
    }
}

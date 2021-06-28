package com.tehilafi.ama.lists;

public class ListView_item_detail {

    String Image;
    String UserName;
    String Date;
    String NumA;
    String Content;
    String NumLike;
    int Add_videoID;
    int Add_picID;
    int StarID;

    public ListView_item_detail(String image,  String userName, String date, String numA, String content, String numLike, int add_videoID,  int add_picID, int starID){
        Image = image;
        UserName = userName;
        Date = date;
        NumA = numA;
        Content = content;
        NumLike = numLike;
        Add_videoID = add_videoID;
        Add_picID = add_picID;
        StarID = starID;
    }

    public void setNumLike(String numLike) {
        NumLike = numLike;
    }


    public String getNumLike() {
        return NumLike;
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

    public String getImage() {
        return Image;
    }

    public int getAdd_videoID() {
        return Add_videoID;
    }

    public int getAdd_picID() {
        return Add_picID;
    }

    public void setImage(String image) {
        Image = image;
    }

    public void setAdd_videoID(int add_videoID) {
        Add_videoID = add_videoID;
    }

    public void setAdd_picID(int add_picID) {
        Add_picID = add_picID;
    }

    public int getStarID() {
        return StarID;
    }

    public void setStarID(int starID) {
        StarID = starID;
    }

    public String getNumA() {
        return NumA;
    }
    public void setNumA(String numA) {
        NumA = numA;
    }
}

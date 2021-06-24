package com.tehilafi.ama.lists;

public class ListView_item_my {

    String Image;
    String UserName;
    String Date;
    String Location;
    int With_ans;
    int Star;
    String Mark;

    public ListView_item_my(String image, String userName, String date, String location, int with_ans, int star, String mark){
        Image = image;
        UserName = userName;
        Date = date;
        Location = location;
        With_ans = with_ans;
        Star = star;
        Mark = mark;

    }


    public String getDate() {
        return Date;
    }

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }

    public void setDate(String date) {
        Date = date;
    }

    public String getLocation() {
        return Location;
    }


    public void setLocation(String location) {
        Location = location;
    }


    public String getImage() {
        return Image;
    }

    public int getWith_ans() {
        return With_ans;
    }

    public void setImage(String image) {
        Image = image;
    }

    public void setWith_ans(int with_ans) {
        With_ans = with_ans;
    }

    public int getStar() {
        return Star;
    }

    public void setStar(int star) {
        Star = star;
    }

    public String getMark() {
        return Mark;
    }

    public void setMark(String mark) {
        Mark = mark;
    }
}
